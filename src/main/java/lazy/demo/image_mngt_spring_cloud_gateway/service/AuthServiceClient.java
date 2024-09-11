package lazy.demo.image_mngt_spring_cloud_gateway.service;

import lazy.demo.image_mngt_spring_cloud_gateway.dto.GenericResponse;
import lazy.demo.image_mngt_spring_cloud_gateway.dto.UserResp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient(WebClient.Builder webClientBuilder,
                             @Value("${spring.auth-service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder
                .baseUrl(authServiceUrl)
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
