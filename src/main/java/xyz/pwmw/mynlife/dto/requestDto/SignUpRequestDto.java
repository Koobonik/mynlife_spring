package xyz.pwmw.mynlife.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(value = "가입자의 이메일 주소", example = "abc123@naver.com", required = true)
    private String userEmail;

    @ApiModelProperty(value = "이메일로 전송된 코드", example = "123456", required = true)
    private String emailCode;

    @ApiModelProperty(value = "가입자의 패스워드", example = "abcd1234", required = true)
    private String password;

    @ApiModelProperty(value = "가입자의 닉네임", example = "abcd1234", required = true)
    private String nickName;

    @ApiModelProperty(value = "가입자의 파이어베이스 토큰", example = "abcd1234", required = true)
    private String firebaseToken;

}
