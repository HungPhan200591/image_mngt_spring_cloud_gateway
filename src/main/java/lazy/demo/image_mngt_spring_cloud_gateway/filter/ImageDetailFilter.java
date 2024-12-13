package lazy.demo.image_mngt_spring_cloud_gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import lazy.demo.image_mngt_spring_cloud_gateway.service.AuthServiceClient;
import lazy.demo.image_mngt_spring_cloud_gateway.service.ImageServiceClient;

import java.nio.charset.StandardCharsets;


@Component
public class ImageDetailFilter extends AbstractGatewayFilterFactory<ImageDetailFilter.Config> {

    private final AuthServiceClient authServiceClient;
    private final ImageServiceClient imageServiceClient;

    public ImageDetailFilter(AuthServiceClient authServiceClient, ImageServiceClient imageServiceClient) {
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
                                    if (!userResp.getUserId().equals(imageUserId)) {
                                        return this.onError(exchange, "User ID mismatch", HttpStatus.FORBIDDEN);
                                    }
                                    // Nếu tất cả kiểm tra hợp lệ, chuyển tiếp yêu cầu
                                    return chain.filter(exchange);
                                });
                    })
                    .onErrorResume(WebClientResponseException.class, e -> {
                        // Trả về mã trạng thái và nội dung lỗi từ service gốc
                        exchange.getResponse().setStatusCode(e.getStatusCode());
                        exchange.getResponse().getHeaders().addAll(e.getHeaders());
                        return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                                .bufferFactory().wrap(e.getResponseBodyAsByteArray())));
                    });
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // Tạo nội dung JSON cho lỗi
        String jsonError = String.format("{\"error\": \"%s\", \"status\": %d}", err, httpStatus.value());
        DataBuffer dataBuffer = exchange.getResponse().bufferFactory().wrap(jsonError.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(dataBuffer));
    }

    public static class Config {
        // Các cấu hình cho bộ lọc (nếu cần)
    }
}
