//package lazy.demo.image_mngt_spring_cloud_gateway.config;
//
//import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import reactor.core.publisher.Mono;
//
//import java.util.Objects;
//
//@Configuration
//public class RateLimiterConfig {
//    // Định nghĩa KeyResolver để giới hạn theo địa chỉ IP của client
//    @Bean
//    public KeyResolver remoteAddressKeyResolver() {
//        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
//    }
//}
