package xyz.pwmw.mynlife.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import xyz.pwmw.mynlife.configuration.PasswordEncoding;
import xyz.pwmw.mynlife.dto.requestDto.*;
import xyz.pwmw.mynlife.dto.responseDto.DefaultResponseDto;
import xyz.pwmw.mynlife.dto.responseDto.JwtResponseDto;
import xyz.pwmw.mynlife.dto.responseDto.ProfileResponseDto;
import xyz.pwmw.mynlife.model.email.EmailAuthCodeRepository;
import xyz.pwmw.mynlife.model.ResetPasswordAuthCode;
import xyz.pwmw.mynlife.model.users.*;
import xyz.pwmw.mynlife.util.AES256Cipher;
import xyz.pwmw.mynlife.util.DateCreator;
import xyz.pwmw.mynlife.util.ValidSomething;
import xyz.pwmw.mynlife.util.jwt.JwtTokenProvider;
import xyz.pwmw.mynlife.util.yml.ApplicationSocialLoginConfigData;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final UsersHobbyRepository usersHobbyRepository;
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailAuthCodeRepository emailAuthCodeRepository;
    private final AES256Cipher aes256Cipher;
    private final EmailAuthService emailAuthService;
    private final ResetPasswordAuthCodeService resetPasswordAuthCodeService;
    private final ApplicationSocialLoginConfigData applicationSocialLoginConfigData;
    private final KaKaoService kaKaoService;
    public Users findByEmail(String email) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("findByEmail : '{}'", email);
        return usersRepository.findByUserEmail(aes256Cipher.AES_Encode(email));
    }
    public Users findByEmailAndUserNickname(String email, String nickName) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("findByEmail : '{}'  nickName '{}'", email, nickName);
        return usersRepository.findByUserEmailAndUserNickname(aes256Cipher.AES_Encode(email), nickName);
    }
    private Users findById(long authUserId) {
        log.info("찾는 유저의 아이디 '{}'", authUserId);
        return usersRepository.findByUserId(authUserId);
    }
    @Transactional
    public Long save(Users dto){
        return usersRepository.save(dto).getUserId();
    }

    // 회원가입 todo:// 나중에 SignUpService를 따로 만들어줘서 관리하는 것이 편할 것으로 예상.
    public ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {

        // 유저의 이메일과 이메일 코드로 시크릿을 만들어서 검증한다.
        if(emailAuthService.validateAuthNumber(new ValidateAuthNumberRequestDto(signUpRequestDto.getEmailCode(), signUpRequestDto.getUserEmail())).getStatusCodeValue() != 200){
            return emailAuthService.validateAuthNumber(new ValidateAuthNumberRequestDto(signUpRequestDto.getEmailCode(), signUpRequestDto.getUserEmail()));
        }

        // todo 이메일은 양방향 암호화, 비밀번호는 단방향 암호화하여 저장한다.
        String encryptedEmail = aes256Cipher.AES_Encode(signUpRequestDto.getUserEmail());
        String password = new PasswordEncoding().encode(signUpRequestDto.getPassword());

        // 유저를 만들어준다.
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        Users users = new Users(encryptedEmail, password, signUpRequestDto.getNickName(), roles);

        usersRepository.save(users);
        return new ResponseEntity<>(new DefaultResponseDto(200, "The membership has been registered successfully!"), HttpStatus.OK);
    }

    // 회원가입 todo:// 나중에 SignUpService를 따로 만들어줘서 관리하는 것이 편할 것으로 예상.
    public ResponseEntity<?> socialSignUp(SocialSignUpRequestDto socialSignUpRequestDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {

        // todo 이메일은 양방향 암호화, 비밀번호는 단방향 암호화하여 저장한다.
        String encryptedEmail = aes256Cipher.AES_Encode(socialSignUpRequestDto.getUserEmail());
        String password = "unknown";

        // 유저를 만들어준다.
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        Users users2 = usersRepository.findByUserEmail(encryptedEmail);
        if(users2 != null){
            return new ResponseEntity<>(new DefaultResponseDto(409, "이미 가입된 이메일 입니다."), HttpStatus.CONFLICT);
        }

        Users users = new Users(encryptedEmail, password, socialSignUpRequestDto.getNickName(), roles, socialSignUpRequestDto.getSocialType());

        usersRepository.save(users);

        return new ResponseEntity<>(new DefaultResponseDto(200, jwtTokenProvider.createTokens(users.getUserNickname(), roles).getJwt()), HttpStatus.OK);
    }

    // 로그인
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        // 일단 유저를 찾는다.
        log.info("유저 이메일 {}", loginRequestDto.getUserEmail());
        Users users = usersRepository.findByUserEmail(aes256Cipher.AES_Encode(loginRequestDto.getUserEmail()));
        if(users == null) return DefaultResponseDto.canNotFindAccount();
        users.setLastLogin(new DateCreator().getTimestamp());
        usersRepository.save(users);
        if(new PasswordEncoding().matches(loginRequestDto.getUserPassword(), users.getPassword())){
            return new ResponseEntity<>(jwtTokenProvider.createTokens(users.getUserEmail(), users.getRoles()),HttpStatus.OK);
        }
        return DefaultResponseDto.canNotMatchedAccount();
    }

    public ResponseEntity<?> getUserProfile(HttpServletRequest httpServletRequest){
        String jwt = jwtTokenProvider.resolveToken(httpServletRequest);
        log.info("유저 토큰 {}", jwt);
        if(jwtTokenProvider.validateToken(jwt)){
            log.info("잘 들어옴");
            Users user = jwtTokenProvider.getUsersFromToken(httpServletRequest);
            ProfileResponseDto profileResponseDto = new ProfileResponseDto(user.getUserId(), user.getUserNickname(), user.getImageUrl(),user.getRoles());
            return new ResponseEntity<>(profileResponseDto, HttpStatus.OK);
        }
        return DefaultResponseDto.canNotFindProfile();
    }

    // 비밀번호 초기화 링크를 전송해줄 것임
    // 이메일로 인증 코드 보내기 (비밀번호 찾기)
    @javax.transaction.Transactional
    public ResponseEntity<?> sendResetPasswordLink(FindPasswordRequestDto findPasswordRequestDto) throws Exception {
        if(!ValidSomething.isValidEmail(findPasswordRequestDto.getUserEmail())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "이메일 양식을 벗어났습니다."), HttpStatus.CONFLICT);
        }
        Users authUser = findByEmailAndUserNickname(findPasswordRequestDto.getUserEmail(), findPasswordRequestDto.getUserNickname());
        if(authUser == null) return new ResponseEntity<>(new DefaultResponseDto(409, "계정 정보가 유효하지 않습니다."), HttpStatus.CONFLICT);
        UUID uuid = UUID.randomUUID();
        ResetPasswordAuthCode resetPasswordAuthCode = new ResetPasswordAuthCode(uuid.toString(), authUser.getUserId(), new DateCreator().getTimestamp(), true);

        resetPasswordAuthCodeService.save(resetPasswordAuthCode);
        String link = aes256Cipher.getUrl()+"/resetPassword?token="+uuid.toString();
        EmailSenderRequestDto emailSenderRequestDto = new EmailSenderRequestDto(findPasswordRequestDto.getUserEmail(), "차차 비밀번호 재설정 링크 입니다.",
                "이링크를 클릭하여 비밀번호를 재설정 해주세요.\n" +link);
        Map<String, String> map = new HashMap<>();
        map.put("link", link);
        return emailAuthService.sendEmailForFindPassword(emailSenderRequestDto);
    }

    @javax.transaction.Transactional
    public ResponseEntity<?> confirmLink(@RequestParam String token) throws ParseException {
        // code 는 AES256으로 암호화되어 있습니다.
        log.info("코드 왔다.");
        ResetPasswordAuthCode resetPasswordAuthCode = resetPasswordAuthCodeService.findByCode(token);
        if (resetPasswordAuthCode == null) // 코드 검증. 없으면 409 반환
            return new ResponseEntity<>(new DefaultResponseDto(409,  "코드가 유효하지 않습니다."), HttpStatus.CONFLICT);
        // 하루 지났는지 검증해야함 비교값이 잘못된듯
        if(!new DateCreator().getTimestamp().before(new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()))){
            log.info("하루 지났습니다.");
            log.info(new DateCreator().getTimestamp() + " : " + new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()));
            return new ResponseEntity<>(new DefaultResponseDto(409, "사용기한이 지난 코드 입니다."), HttpStatus.CONFLICT);
        }
        log.info("코드 문제 없음");
        Users authUser = findById(resetPasswordAuthCode.getAuthUserId());
        if (authUser == null) // 유저 검증. 없으면 409 반환
            return new ResponseEntity<>(new DefaultResponseDto(409,  "계정이 유효하지 않습니다."), HttpStatus.CONFLICT);
        log.info("유저도 문제 없으므로 반환");

        HttpHeaders headers = new HttpHeaders();
        // 나중에 우리 비밀번호 재설정 페이지 링크 리다이렉트 해주어야 ㅇ함.
        headers.setLocation(URI.create("http://carhelper-client.kro.kr:5000/manager/reset/password?token="+token));
        headers.setLocation(URI.create("http://172.30.1.12:5000/manager/reset/password"));
        log.info("재설정 토큰 : " + token);
//        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        return new ResponseEntity<>(new DefaultResponseDto(200, "인증이 완료 되었습니다."), HttpStatus.OK);
    }



    @javax.transaction.Transactional
    public ResponseEntity<?> resetPasswordUsingToken(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) throws Exception {
        log.info("토큰(uuid)값을 이용하여 비밀번호 리셋");
        ResetPasswordAuthCode resetPasswordAuthCode = resetPasswordAuthCodeService.findByCode(resetPasswordRequestDto.getToken());

        // 코드값 한번 더 검증
        if(resetPasswordAuthCode == null)
            return new ResponseEntity<>(new DefaultResponseDto(409, "코드가 유효하지 않습니다."), HttpStatus.CONFLICT);
        // 이 코드를 썼으면 더이상 사용 불가
        if(!resetPasswordAuthCode.isCanUse())
            return new ResponseEntity<>(new DefaultResponseDto(409, "이미 사용한 코드 입니다."), HttpStatus.CONFLICT);

        // 하루 지났는지 검증해야함 비교값이 잘못된듯
        if(!new DateCreator().getTimestamp().before(new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()))){
            log.info("하루 지났습니다.");
            log.info(new DateCreator().getTimestamp() + " : " + new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()));
            return new ResponseEntity<>(new DefaultResponseDto(409, "사용기한이 지난 코드 입니다."), HttpStatus.CONFLICT);
        }

        Users authUser = findById(resetPasswordAuthCode.getAuthUserId());
        if(authUser == null)
            return new ResponseEntity<>(new DefaultResponseDto(409, "이메일이 유효하지 않습니다."), HttpStatus.CONFLICT);

        if(!ValidSomething.isValidPassword(resetPasswordRequestDto.getNewPassword())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "비밀번호 양식을 벗어났습니다. 8~32자 이내로 영문+숫자+특수문자를 조합하여 입력해주세요"), HttpStatus.CONFLICT);
        }
        resetPasswordAuthCode.setCertifiedDate(new DateCreator().getTimestamp());
        resetPasswordAuthCodeService.save(resetPasswordAuthCode);
        authUser.setUserPassword(new PasswordEncoding().encode(resetPasswordRequestDto.getNewPassword()));
        save(authUser);
        return new ResponseEntity<>(new DefaultResponseDto(200, "비밀번호가 변경되었습니다."), HttpStatus.OK);
    }

    // 로그아웃
    @javax.transaction.Transactional
    public ResponseEntity<?> logout(JwtRequestDto jwtRequestDto){
        Users users = (Users) jwtTokenProvider.getAuthentication(jwtRequestDto.getJwt()).getPrincipal();
        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        logoutValueOperations.set(jwtRequestDto.getJwt(), String.valueOf(users.getUserId())); // redis set 명령어
        logoutValueOperations.set(jwtRequestDto.getRefreshJwt(), String.valueOf(users.getUserId())); // redis set 명령어

        log.info("로그아웃 유저 아이디 : '{}' , 유저 이름 : '{}'", users.getUserId(), users.getUserEmail());
        return new ResponseEntity<>(new DefaultResponseDto(200,"로그아웃 되었습니다."), HttpStatus.OK);
    }

    public ResponseEntity<?> renewalToken(String token) throws ParseException {
        log.info("리뉴얼을 위해 들어온 토큰값! '{}'\n이 토큰이 유효한가?! -> '{}'", token.substring(token.length()-3), jwtTokenProvider.validateToken(token));
        if(jwtTokenProvider.validateToken(token)) {
            log.info("권한 통과");
            Users user = ((Users) jwtTokenProvider.getAuthentication(token).getPrincipal());

            log.info("새로운 리뉴얼 토큰 발행");
            JwtResponseDto jwtResponseDto = jwtTokenProvider.createTokens(user.getUserEmail(), user.getRoles());
            user.setLastLogin(new DateCreator().getTimestamp());
            usersRepository.save(user);
            log.info("유저이름 : '{}'\njwt -> '{}'\nrefresh -> '{}'", user.getUserNickname(), jwtResponseDto.getJwt().substring(jwtResponseDto.getJwt().length()-3), jwtResponseDto.getRefreshJwt().substring(jwtResponseDto.getRefreshJwt().length()-3));
            new Thread(
                    () -> {
                        try {
                            Thread.sleep(10*1000);
                            invalidationToken(token);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
            ).start();

            return new ResponseEntity<>(jwtResponseDto, HttpStatus.OK);
        }
        log.info("refreshToken 토큰 유효하지 않아서 401 반환");
        return new ResponseEntity<>( new DefaultResponseDto(401, "토큰이 유효하지 않습니다."), HttpStatus.UNAUTHORIZED);
    }

    @javax.transaction.Transactional
    public void invalidationToken(String token) throws InterruptedException {
        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        Users user = (Users) jwtTokenProvider.getAuthentication(token).getPrincipal();
        logoutValueOperations.set(token, String.valueOf(user.getUserId())); // redis set 명령어

        log.info("토큰 무효화! 유저 아이디 : '{}' , 유저 이름 : '{}'", user.getUserId(), user.getUserNickname());
    }

    public void createUsersHobby(HttpServletRequest request, long id) {
        Users users = jwtTokenProvider.getUsersFromToken(request);

        // 아이디 생성
        UsersHobbyId usersHobbyId = new UsersHobbyId();
        usersHobbyId.setHobbyId(id);
        usersHobbyId.setUserId(users.getUserId());

        UsersHobby usersHobby = UsersHobby.builder()
                .usersHobbyId(usersHobbyId)
                .build();

        usersHobbyRepository.save(usersHobby);

    }


}
