package lazy.demo.image_mngt_spring_cloud_gateway.filter;

import lazy.demo.image_mngt_spring_cloud_gateway.dto.UserResp;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import lazy.demo.image_mngt_spring_cloud_gateway.service.AuthServiceClient;
import lazy.demo.image_mngt_spring_cloud_gateway.service.ImageServiceClient;

import java.util.Objects;


@Component
public class TokenAuthenticationFilter extends AbstractGatewayFilterFactory<TokenAuthenticationFilter.Config> {

    private final AuthServiceClient authServiceClient;
    private final ImageServiceClient imageServiceClient;

    public TokenAuthenticationFilter(AuthServiceClient authServiceClient, ImageServiceClient imageServiceClient) {
        super(Config.class);
        this.authServiceClient = authServiceClient;
        this.imageServiceClient = imageServiceClient;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            System.out.println(authHeader);
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("Authorization header is missing or invalid");
                return this.onError(exchange, "Authorization header is missing or invalid", HttpStatus.UNAUTHORIZED);
            }

            // Gọi Auth Service để lấy thông tin người dùng từ token
            return authServiceClient.getUserByTokenWithAuthService(authHeader)
                    .flatMap(userResp -> {
                        System.out.println(userResp);

                        if (userResp == null) {
                            return this.onError(exchange, "Invalid Token", HttpStatus.UNAUTHORIZED);
                        }

                        // Gọi Image Service để lấy userId từ image
                        return imageServiceClient.extractUserIdFromImageService(exchange)
                                .flatMap(imageUserId -> {
                                    System.out.println(imageUserId);
                                    if (!userResp.getUserId().equals(imageUserId)) {
                                        System.out.println("User ID mismatch");
                                        return this.onError(exchange, "User ID mismatch", HttpStatus.FORBIDDEN);
                                    }
                                    // Nếu tất cả kiểm tra hợp lệ, chuyển tiếp yêu cầu
                                    return chain.filter(exchange);
                                });
                    })
                    .onErrorResume(e -> {
                        e.printStackTrace();
                        return this.onError(exchange, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
                    });
        };
    }


    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
        // Các cấu hình cho bộ lọc (nếu cần)
    }
}
