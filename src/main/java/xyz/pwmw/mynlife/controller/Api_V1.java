package xyz.pwmw.mynlife.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.pwmw.mynlife.dto.requestDto.*;
import xyz.pwmw.mynlife.dto.responseDto.DefaultResponseDto;
import xyz.pwmw.mynlife.service.EmailAuthService;
import xyz.pwmw.mynlife.service.UsersService;

@Log4j2
@RestController
@RequiredArgsConstructor
@Api(value = "API", tags = "유저 정보")
@RequestMapping("api/v1")
public class Api_V1 {

    private final EmailAuthService emailAuthService;
    private final UsersService usersService;

    @ApiOperation(value = "HTTP GET EXAMPLE", notes = "GET 요청에 대한 예제 입니다.")
    @ApiResponses({
            @ApiResponse(code = 200, message = "성공"),
            @ApiResponse(code = 500, message = "서버에러"),
            @ApiResponse(code = 404, message = "찾을 수 없음")
    })
    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public @ResponseBody String main(@ApiParam(value = "테스트 파라미터_1", required = true, example = "test_parameter_1") @RequestParam String test1,
                                     @ApiParam(value = "테스트 파라미터_2", required = true, example = "test_parameter_2") @RequestParam String test2) {
        return test1 + " : " + test2;
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 이메일 전송", response = DefaultResponseDto.class)
    })
    @ApiOperation(value = "입력한 이메일로 인증 번호 요청 api", notes = "")
    @PostMapping("/sendEmailForAuthEmail")
    public ResponseEntity<?> sendEmailForAuthEmail(@RequestBody EmailRequestDto emailRequestDto){
        return emailAuthService.sendEmailForAuthEmail(emailRequestDto);
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 이메일 전송", response = DefaultResponseDto.class)
    })
    @ApiOperation(value = "입력한 이메일로 인증 번호 요청 api", notes = "")
    @PostMapping("/findPassword")
    public ResponseEntity<?> findPassword(@RequestBody FindPasswordRequestDto findPasswordRequestDto) throws Exception {
        return usersService.sendResetPasswordLink(findPasswordRequestDto);
    }

    // 비밀번호 재설정
    @ApiOperation(value = "비밀번호 '찾기' 를 이용했을 시 사용하는 비밀번호 재설정 API", notes = "이메일로 받았었던 토큰값과")
    @ApiResponses({
            @ApiResponse(code = 200, message = "비밀번호 재설정할 수 있는 링크를 이메일로 전송.", response = DefaultResponseDto.class),
            @ApiResponse(code = 500, message = "서버에러", response = DefaultResponseDto.class),
            @ApiResponse(code = 409, message = "이메일에러", response = DefaultResponseDto.class)
    })
    @PostMapping("/resetPasswordUsingToken")
    public ResponseEntity<?> resetPasswordUsingToken(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) throws Exception {
        return usersService.resetPasswordUsingToken(resetPasswordRequestDto);
    }



}
