package lazy.demo.image_mngt_spring_cloud_gateway.service;

import lazy.demo.image_mngt_spring_cloud_gateway.dto.GenericResponse;
import lazy.demo.image_mngt_spring_cloud_gateway.dto.UserResp;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8081")
                .build();
    }

    public Mono<UserResp> getUserByTokenWithAuthService(String token) {

        return webClient
                .get()
                .uri("/api/v1/user/profile")
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<GenericResponse<UserResp>>() {
                })
                .flatMap(response -> {
                    if ("success".equalsIgnoreCase(response.getStatus())) {
                        return Mono.justOrEmpty(response.getData());
                    } else {
                        return Mono.error(new RuntimeException("Failed to fetch user details"));
                    }
                })
                .doOnError(e -> e.printStackTrace());

    }
}
