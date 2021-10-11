package xyz.pwmw.mynlife.dto.responseDto;

import lombok.*;
import org.springframework.stereotype.Service;

@Data
@Service
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExampleUserResponseDto {
    private String encryptedUserId;
    private String encryptedUserPassword;
}