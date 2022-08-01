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
public class SocialSignUpRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(value = "가입자의 이메일 주소", example = "abc123@naver.com", required = true)
    private String userEmail;

    @ApiModelProperty(value = "가입자의 닉네임", example = "abcd1234", required = true)
    private String nickName;

    @ApiModelProperty(value = "소셜 타입", example = "type", required = true)
    private String socialType;

    @ApiModelProperty(value = "소셜 토큰", example = "token", required = true)
    private String accessToken;

}
