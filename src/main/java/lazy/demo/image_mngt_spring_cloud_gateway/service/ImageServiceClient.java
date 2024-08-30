package lazy.demo.image_mngt_spring_cloud_gateway.service;

import lazy.demo.image_mngt_spring_cloud_gateway.dto.GenericResponse;
import lazy.demo.image_mngt_spring_cloud_gateway.dto.ImageResp;
import lazy.demo.image_mngt_spring_cloud_gateway.dto.UserResp;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

@Service
public class ImageServiceClient {

    private final WebClient webClient;

    public ImageServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8082")
                .build();
    }

    public Mono<Long> extractUserIdFromImageService(ServerWebExchange exchange) {
        PathPatternParser parser = new PathPatternParser();
        PathPattern pattern = parser.parse("/api/v1/file/image/{image_id}");
        ServerHttpRequest request = exchange.getRequest();
        PathPattern.PathMatchInfo pathMatchInfo = pattern.matchAndExtract(request.getPath().pathWithinApplication());

        if (pathMatchInfo == null) {
            return Mono.error(new RuntimeException("Invalid path"));
        }

        String imageId = pathMatchInfo.getUriVariables().get("image_id");

        return webClient
                .get()
                .uri("/api/v1/file/image/" + imageId)
                .retrieve()
                .onStatus(
                        status -> status.isError(), // Kiểm tra mã trạng thái lỗi
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(body -> {
                                    // Trả về lỗi với thông tin mã trạng thái và nội dung body
                                    return Mono.error(new WebClientResponseException(
                                            clientResponse.statusCode().value(),
                                            clientResponse.statusCode().toString(), // Sử dụng mã trạng thái dưới dạng chuỗi
                                            clientResponse.headers().asHttpHeaders(),
                                            body.getBytes(),
                                            null
                                    ));
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<GenericResponse<ImageResp>>() {})
                .flatMap(response -> {
                    if ("success".equalsIgnoreCase(response.getStatus())) {
                        return Mono.justOrEmpty(response.getData());
                    } else {
                        return Mono.error(new RuntimeException("Failed to fetch user details"));
                    }
                })
                .map(ImageResp::getUserId)
                .doOnError(e -> {
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException ex = (WebClientResponseException) e;
                        System.out.println("Error: " + ex.getStatusCode() + ", Body: " + ex.getResponseBodyAsString());
                    } else {
                        e.printStackTrace();
                    }
                });
    }
}
