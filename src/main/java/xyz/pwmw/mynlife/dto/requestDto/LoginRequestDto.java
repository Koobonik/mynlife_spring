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
public class LoginRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "test_login_email", value = "로그인 이메일", required = true)
    private String userEmail;

    @ApiModelProperty(example = "test_login_password", value = "로그인 비밀번호", required = true)
    private String userPassword;
}
