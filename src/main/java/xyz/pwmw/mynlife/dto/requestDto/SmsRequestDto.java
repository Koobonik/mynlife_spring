package xyz.pwmw.mynlife.dto.requestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SmsRequestDto {
    // Model 에 보이는 설명들.
    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String content;
    private List<MessagesRequestDto> messages;
}
