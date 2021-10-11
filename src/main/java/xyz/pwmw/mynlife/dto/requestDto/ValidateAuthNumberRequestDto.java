package xyz.pwmw.mynlife.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Service
@Getter
@NoArgsConstructor
public class ValidateAuthNumberRequestDto extends SendAuthNumberRequestDto {
    @ApiModelProperty(example = "147258", value = "인증번호")
    private String authSms;


    public ValidateAuthNumberRequestDto(String authSms, String userEmail) {
        this.authSms = authSms;
        super.setUserEmail(userEmail);
    }
}
