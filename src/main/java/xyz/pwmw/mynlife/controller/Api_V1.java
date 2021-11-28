package xyz.pwmw.mynlife.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.pwmw.mynlife.dto.requestDto.*;
import xyz.pwmw.mynlife.dto.responseDto.DefaultResponseDto;
import xyz.pwmw.mynlife.dto.responseDto.JwtResponseDto;
import xyz.pwmw.mynlife.service.EmailAuthService;
import xyz.pwmw.mynlife.service.SmsService;
import xyz.pwmw.mynlife.service.UsersService;
import xyz.pwmw.mynlife.util.jwt.JwtTokenProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;

@Log4j2
@RestController
@RequiredArgsConstructor
@Api(value = "API", tags = "유저 정보")
@RequestMapping("api/v1")
public class Api_V1 {

    private final JwtTokenProvider jwtTokenProvider;
    private final UsersService usersService;
    private final EmailAuthService emailAuthService;
    private final SmsService smsService;

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
//    @ApiOperation(value = "비밀번호 재설정 링크 클릭하면 이 API로 요청이 오게 됩니다.\n" +
//            "", notes = "파라미터를 검증하고 올바르다면 재설정 할 수 있는 재설정 링크로 리다이렉트 시켜줍니다.")
//    @ApiResponses({
//            @ApiResponse(code = 200, message = "비밀번호 재설정 페이지로 리다이렉트", response = DefaultResponseDto.class),
//            @ApiResponse(code = 500, message = "서버에러", response = DefaultResponseDto.class)
//    })
//    @GetMapping("/confirmLink")
//    public ResponseEntity<?> confirmLink(@RequestParam String token) throws ParseException {
//        return usersService.confirmLink(token);
//    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 회원가입", response = DefaultResponseDto.class)
    })
    @ApiOperation(value = "메일 인증코드와 함께 입력한 정보로 회원 가입", notes = "")
    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequestDto signUpRequestDto) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        return usersService.signUp(signUpRequestDto);
    }

    @ApiOperation(value = "로그인", notes = "로그인에 대한 요청을 보냅니다.")
    @PostMapping(value = "login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {
        return usersService.login(loginRequestDto);
    }

    @GetMapping(value = "getProfile")
    public ResponseEntity<?> getProfile(HttpServletRequest httpServletRequest){
        return usersService.getUserProfile(httpServletRequest);
    }

    // 토큰 재발급
    //@ApiImplicitParams({@ApiImplicitParam(name = "refreshJwt", value = "로그인후 JWT 토큰을 발급받아야 합니다.", required = true, dataType = "String", paramType = "header")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "JWT (Json Web Token) 발행", response = JwtResponseDto.class),
            @ApiResponse(code = 401, message = "토큰 유효하지 않음", response = DefaultResponseDto.class),
            @ApiResponse(code = 409, message = "유저 계정에 문제가 있을경우", response = DefaultResponseDto.class)
    })
    @ApiOperation(value = "토큰 재발급", notes = "파라미터(token)에 담겨오는 토큰값으로 jwt와 refreshJwt를 재발급 받는다.")
    @GetMapping("renewalToken")
    public ResponseEntity<?> renewalToken(@RequestParam String token) throws ParseException, InterruptedException {
        return usersService.renewalToken(token);
    }

    @PostMapping("jwtValidation")
    public String jwtValidation(@RequestHeader @RequestParam String jwt){
        // 헤더에서 토큰값 추출
        log.info(jwt);
        // 토큰값이 유효한 경우
        if(jwtTokenProvider.validateToken(jwt)) {
            log.info("토큰 유효함");
            // 유저 정보 추출 (아이디)
            log.info(jwtTokenProvider.getUserPk(jwt));
            return "true";
            // 인증 정보 조회
//            log.info(jwtTokenProvider.getAuthentication(jwt).getAuthorities()); // ex ROLE_USER
//            log.info(jwtTokenProvider.getAuthentication(jwt).getCredentials());
//            log.info(jwtTokenProvider.getAuthentication(jwt).getDetails());
//            log.info(((ExampleUser) jwtTokenProvider.getAuthentication(jwt).getPrincipal()).getPassword()); // 유저 클래스를 가져와준다!
//            log.info(jwtTokenProvider.getAuthentication(jwt).getName());
        }
        return "hi";
    }

    @PostMapping("sendSms")
    public void sendSms(@RequestBody MessagesRequestDto messagesRequestDto) throws UnsupportedEncodingException, ParseException, NoSuchAlgorithmException, URISyntaxException, InvalidKeyException, JsonProcessingException {
        String statusCode = smsService.sendSms(messagesRequestDto).getStatusCode();

    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "정상적으로 로그아웃", response = String.class)
    })
    @ApiOperation(value = "로그아웃 api", notes = "헤더에 jwt, refreshJwt를 넣어서 보내주세요.")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody JwtRequestDto jwtRequestDto){
        log.info(jwtRequestDto.getJwt());
        log.info(jwtRequestDto.getRefreshJwt());
        return usersService.logout(jwtRequestDto);
    }

}
