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
public class ResetPasswordRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "토큰값", required = true)
    private String token;

    @ApiModelProperty(example = "새로운 비밀번호", required = true)
    private String newPassword;
}