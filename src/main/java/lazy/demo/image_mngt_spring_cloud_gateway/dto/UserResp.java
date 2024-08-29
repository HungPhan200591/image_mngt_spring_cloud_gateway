package lazy.demo.image_mngt_spring_cloud_gateway.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResp {

    private Long userId;
    private String username;
    private String email;
    private boolean isAdmin;
    private String fullName;
    private LocalDate dateOfBirth;

}
