//package lazy.demo.image_mngt_spring_cloud_gateway.config;
//
//import lombok.Getter;
//import lombok.Setter;
//import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.Map;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Component
//public class InMemoryRateLimiter implements RateLimiter<InMemoryRateLimiter.Config> {
//
//    private final Map<String, Config> configMap = new ConcurrentHashMap<>();
//    private final ConcurrentHashMap<String, AtomicInteger> requestCounts = new ConcurrentHashMap<>();
//
//    // Lớp Config định nghĩa các cấu hình giới hạn tốc độ
//    @Setter
//    @Getter
//    public static class Config {
//        private int replenishRate; // Số lượng yêu cầu được phép xử lý mỗi giây
//        private int burstCapacity; // Số lượng yêu cầu tối đa được phép trong hàng đợi chờ xử lý
//
//    }
//
//    @Override
//    public Mono<Response> isAllowed(String routeId, String id) {
//        // Lấy cấu hình giới hạn tốc độ cho route tương ứng
//        Config config = configMap.getOrDefault(routeId, newConfig());
//        AtomicInteger currentCount = requestCounts.computeIfAbsent(id, k -> new AtomicInteger(0));
//
//        // Logic kiểm tra xem yêu cầu có được phép không dựa trên cấu hình
//        if (currentCount.incrementAndGet() > config.getBurstCapacity()) {
//            return Mono.just(new Response(false, null)); // Từ chối yêu cầu nếu vượt quá burstCapacity
//        }
//        return Mono.just(new Response(true, null)); // Cho phép yêu cầu
//    }
//
//    @Override
//    public Map<String, Config> getConfig() {
//        return this.configMap;
//    }
//
//    @Override
//    public Class<Config> getConfigClass() {
//        return Config.class;
//    }
//
//    @Override
//    public Config newConfig() {
//        // Khởi tạo cấu hình mặc định cho RateLimiter
//        Config config = new Config();
//        config.setReplenishRate(10); // Ví dụ: 10 yêu cầu mỗi giây
//        config.setBurstCapacity(20); // Ví dụ: tối đa 20 yêu cầu trong hàng đợi
//        return config;
//    }
//}
