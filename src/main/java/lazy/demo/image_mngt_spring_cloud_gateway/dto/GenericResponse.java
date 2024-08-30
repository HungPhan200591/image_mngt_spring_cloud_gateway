package lazy.demo.image_mngt_spring_cloud_gateway.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class GenericResponse<T> {
    private String status;
    private T data;

    public GenericResponse(String status, T data) {
        this.status = status;
        this.data = data;
    }

    public static <T> GenericResponse<T> success(T data) {
        return new GenericResponse<>("success", data);
    }

}