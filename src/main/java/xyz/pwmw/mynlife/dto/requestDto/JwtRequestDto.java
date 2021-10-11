package xyz.pwmw.mynlife.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "jwt", value = "JWT")
    private String jwt;

    @ApiModelProperty(example = "refreshJwt", value = "refreshJWT")
    private String refreshJwt;
}
