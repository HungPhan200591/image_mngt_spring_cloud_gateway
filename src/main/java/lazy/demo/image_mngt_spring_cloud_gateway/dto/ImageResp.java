package lazy.demo.image_mngt_spring_cloud_gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResp {
    private String imageId;
    private Long userId;
}
