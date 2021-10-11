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
public class EmailAuthRequestDto extends EmailRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "인증번호", value = "받는 사람의 이메일 주소", required = true)
    private int authNumber;
}
