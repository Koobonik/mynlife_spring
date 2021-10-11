package xyz.pwmw.mynlife.dto.requestDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@Service
@Getter
public class SendAuthNumberRequestDto  {
    @ApiModelProperty(example = "userEmail", value = "유저 이메일", required = true)
    private String userEmail;
}
