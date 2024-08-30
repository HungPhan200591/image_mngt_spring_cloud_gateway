package lazy.demo.image_mngt_spring_cloud_gateway.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    public Mono<String> fallback() {
        // Trả về một phản hồi đơn giản khi xảy ra lỗi
        return Mono.just("Service is temporarily unavailable. Please try again later.");
    }
}
