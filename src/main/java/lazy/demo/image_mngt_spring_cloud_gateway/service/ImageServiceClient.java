package lazy.demo.image_mngt_spring_cloud_gateway.service;

import lazy.demo.image_mngt_spring_cloud_gateway.dto.ImageResp;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;

@Service
public class ImageServiceClient {

    private final WebClient webClient;

    public ImageServiceClient() {
        this.webClient = WebClient.create("http://localhost:8082"); // Địa chỉ của Image Service
    }

    public Long extractUserIdFromImageService(ServerWebExchange exchange) {
        String imageId = exchange.getRequest().getQueryParams().getFirst("image_id");
        return webClient
                .get()
                .uri("/image/" + imageId)
                .retrieve()
                .bodyToMono(ImageResp.class)
                .block()
                .getUserId();
    }
}
