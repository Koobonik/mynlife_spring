package xyz.pwmw.mynlife.dto.responseDto;

import lombok.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Data
@Service
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponseDto {
    long id;
    String nickname;
    String imageUrl;
    List<String> roles;
    String gender;
    String birthDay;
}
