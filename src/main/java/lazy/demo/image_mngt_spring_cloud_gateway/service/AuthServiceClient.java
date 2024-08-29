package lazy.demo.image_mngt_spring_cloud_gateway.service;

import lazy.demo.image_mngt_spring_cloud_gateway.dto.UserResp;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AuthServiceClient {

    private final WebClient webClient;

    public AuthServiceClient() {
        this.webClient = WebClient.create("http://localhost:8081"); // Địa chỉ của Auth Service
    }

    public UserResp getUserByTokenWithAuthService(String token) {
        try {
            return webClient
                    .get()
                    .uri("/api/v1/user/profile")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(UserResp.class)
                    .block();

        } catch (Exception e) {
            return null;
        }
    }
}
