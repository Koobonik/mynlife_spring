package xyz.pwmw.mynlife.dto.responseDto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DefaultResponseDto {
    @ApiModelProperty(example = "응답 코드 번호", value = "에러 코드값")
    private int code;
    @ApiModelProperty(example = "안내 메시지", value = "메시지")
    private String message;

    public static ResponseEntity<?> canNotFindAccount(){
        return new ResponseEntity<>(new DefaultResponseDto(409, "계정이 일치하지 않습니다."), HttpStatus.CONFLICT);
    }
    public static ResponseEntity<?> canNotMatchedAccount(){
        return new ResponseEntity<>(new DefaultResponseDto(409, "계정이 일치하지 않습니다."), HttpStatus.CONFLICT);
    }
    public static ResponseEntity<?> canNotFindProfile(){
        return new ResponseEntity<>(new DefaultResponseDto(409, "프로필 정보를 가져올 수 없습니다."), HttpStatus.CONFLICT);
    }
    public static ResponseEntity<?> canNotSendResetEmail(){
        return new ResponseEntity<>(new DefaultResponseDto(409, "비밀번호 초기화 링크 전송에 실패했습니다."), HttpStatus.CONFLICT);
    }
    public static ResponseEntity<?> canNotCreateBook(){
        return new ResponseEntity<>(new DefaultResponseDto(409, "책 정보를 생성하는데 실패했습니다."), HttpStatus.CONFLICT);
    }
    public static ResponseEntity<?> canNotFindBook(){
        return new ResponseEntity<>(new DefaultResponseDto(409, "책 정보가 올바르지 않습니다."), HttpStatus.CONFLICT);
    }
}