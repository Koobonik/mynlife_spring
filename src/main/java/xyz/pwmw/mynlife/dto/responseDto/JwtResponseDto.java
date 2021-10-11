package xyz.pwmw.mynlife.dto.responseDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JwtResponseDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "jwt", value = "JWT", required = true)
    private String jwt;

    @ApiModelProperty(example = "refreshJwt", value = "refreshJWT", required = true)
    private String refreshJwt;
}
