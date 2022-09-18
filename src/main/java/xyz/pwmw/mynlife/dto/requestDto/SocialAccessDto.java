package xyz.pwmw.mynlife.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SocialAccessDto {
    @ApiModelProperty(value = "회원가입 하려는 사람의 액세스 코드", example = "abc12", required = true)
    private String accessToken;

    @ApiModelProperty(value = "해당 소셜 플랫폼", example = "kakao apple google", required = true)
    private String socialType;
}
