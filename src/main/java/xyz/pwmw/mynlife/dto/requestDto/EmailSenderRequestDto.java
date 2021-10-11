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
public class EmailSenderRequestDto {
    // Model 에 보이는 설명들.
    @ApiModelProperty(example = "abcd123@abcd123.com", value = "받는 사람의 이메일 주소", required = true)
    private String recipient;

    @ApiModelProperty(example = "계정을 활성화 하세요!", value = "이메일 제목", required = true)
    private String subject;

    @ApiModelProperty(example = "이메일 인증 링크를 클릭하여 들어가주세요!", value = "이메일 내용", required = true)
    private String body;
}
