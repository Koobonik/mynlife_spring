package xyz.pwmw.mynlife.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import xyz.pwmw.mynlife.dto.requestDto.*;
import xyz.pwmw.mynlife.dto.responseDto.DefaultResponseDto;
import xyz.pwmw.mynlife.dto.responseDto.JwtResponseDto;
import xyz.pwmw.mynlife.model.users.Users;
import xyz.pwmw.mynlife.service.KaKaoService;
import xyz.pwmw.mynlife.service.SmsService;
import xyz.pwmw.mynlife.service.UsersService;
import xyz.pwmw.mynlife.util.AES256Cipher;
import xyz.pwmw.mynlife.util.jwt.JwtTokenProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RestController
@RequiredArgsConstructor
@Api(value = "API", tags = "유저와 관련된 api")
@RequestMapping("api/users")
public class UsersController {

    private final UsersService usersService;
    private final JwtTokenProvider jwtTokenProvider;
    private final SmsService smsService;
    private final KaKaoService kaKaoService;
    private final AES256Cipher aes256Cipher;

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
    @Transactional
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

    @PatchMapping("updateUserData")
    public ResponseEntity<?> updateUserData(@RequestBody HashMap<String, Object> map,
                                            HttpServletRequest request) {
        return usersService.updateUserProfile(request, map);
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

    @ResponseBody
    @GetMapping("/kakaoLogin")
    public String kakaoLogin(@RequestParam String code) throws IOException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, ParseException, InvalidKeyException {
        System.out.println(code);
        final String access_Token = kaKaoService.getToken(code);
        final Map<String, Object> map = kaKaoService.getUserInfo(access_Token);
//        usersService.socialSignUp(new SocialSignUpRequestDto("", "", "",""));
        return map.toString();
    }

    @PostMapping("/social/access")
    @Transactional
    public ResponseEntity<?> accessSocial(@RequestBody SocialAccessDto socialAccessDto) throws IOException, org.apache.tomcat.util.json.ParseException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        if(socialAccessDto.getSocialType().equals("kakao")){
//            final String access_Token = kaKaoService.getToken(socialAccessDto.getCode());
            final Map<String, Object> map = kaKaoService.getUserInfo(socialAccessDto.getAccessToken());
            Map<String, Object> kakaoAccount = (Map<String, Object>) map.get("kakaoAccount");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            Users users = usersService.findByEmail(kakaoAccount.get("email").toString());
            if(users == null){
                System.out.println("성별 -> " + kakaoAccount.get("gender") + " 생년월일 -> ");
                // 회원가입 로직으로 보내주자
                Users users1 = Users.builder()
                        .email(aes256Cipher.AES_Encode(kakaoAccount.get("email").toString()))
                        .gender((boolean) kakaoAccount.get("has_gender") ? kakaoAccount.get("gender").toString() : "unknown")
                        .imageUrl(profile.get("profile_image_url") != null ? profile.get("profile_image_url").toString() : "")
                        .userNickname(profile.get("nickname").toString())
                        .socialType("kakao")
                        .build();
                ArrayList<String> arrayList = new ArrayList<>(List.of("ROLE_USER"));

                users1.setRoles(arrayList);
                usersService.save(users1);
                Users users2 = usersService.findByEmail(kakaoAccount.get("email").toString());
                // 가입쪽이면 201 코드 반환
                return new ResponseEntity<>(jwtTokenProvider.createToken(users2.getUserEmail(), users2.getRoles()), HttpStatus.CREATED);

            }
            System.out.println("users -> " + users.getUserEmail());

//            usersService.findByEmail(map2.get("email").toString());
            return new ResponseEntity<>(jwtTokenProvider.createToken(users.getUserEmail(), users.getRoles()), HttpStatus.OK);
        }
        else if(socialAccessDto.getSocialType().equals("apple")){
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        else if(socialAccessDto.getSocialType().equals("google")){
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    // 취미 계정을 생성해주는 변수
    @PostMapping("/social/createNewHobbyAccount/{hobbyId}")
    @Transactional
    public ResponseEntity<?> createNewHobbyAccount(
            HttpServletRequest request,
            @PathVariable long hobbyId) {
        return usersService.createUsersHobby(request, hobbyId);
    }
}
