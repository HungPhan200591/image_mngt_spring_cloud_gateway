package lazy.demo.image_mngt_spring_cloud_gateway.service;

import lazy.demo.image_mngt_spring_cloud_gateway.dto.GenericResponse;
import lazy.demo.image_mngt_spring_cloud_gateway.dto.ImageResp;
import lazy.demo.image_mngt_spring_cloud_gateway.dto.UserResp;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
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
        PathPattern pattern = parser.parse("/image/{image_id}");
        // Lấy đường dẫn của yêu cầu từ exchange
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().pathWithinApplication().value();

        // Khớp pattern với đường dẫn
        PathPattern.PathMatchInfo pathMatchInfo = pattern.matchAndExtract(request.getPath().pathWithinApplication());

        String imageId = pathMatchInfo.getUriVariables().get("image_id");
        System.out.println(imageId);
        return webClient
                .get()
                .uri("/api/v1/file/image/" + imageId)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GenericResponse<ImageResp>>() {})
                .flatMap(response -> {
                    if ("success".equalsIgnoreCase(response.getStatus())) {
                        return Mono.justOrEmpty(response.getData());
                    } else {
                        return Mono.error(new RuntimeException("Failed to fetch user details"));
                    }
                })
                .map(ImageResp::getUserId) // Lấy userId từ ImageResp
                .doOnError(e -> e.printStackTrace());
    }
}
