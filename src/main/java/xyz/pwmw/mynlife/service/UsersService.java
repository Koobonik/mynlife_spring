package xyz.pwmw.mynlife.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import xyz.pwmw.mynlife.model.EmailAuthCodeRepository;
import xyz.pwmw.mynlife.model.ResetPasswordAuthCode;
import xyz.pwmw.mynlife.model.Users;
import xyz.pwmw.mynlife.model.UsersRepository;
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
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
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
    private final StringRedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailAuthCodeRepository emailAuthCodeRepository;
    private final AES256Cipher aes256Cipher;
    private final EmailAuthService emailAuthService;
    private final ResetPasswordAuthCodeService resetPasswordAuthCodeService;
    private final ApplicationSocialLoginConfigData applicationSocialLoginConfigData;
    public Users findByEmail(String email) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("findByEmail : '{}'", email);
        return usersRepository.findByUserEmail(aes256Cipher.AES_Encode(email));
    }
    public Users findByEmailAndUserNickname(String email, String nickName) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        log.info("findByEmail : '{}'  nickName '{}'", email, nickName);
        return usersRepository.findByUserEmailAndUserNickname(aes256Cipher.AES_Encode(email), nickName);
    }
    private Users findById(long authUserId) {
        log.info("?????? ????????? ????????? '{}'", authUserId);
        return usersRepository.findByUserId(authUserId);
    }
    @Transactional
    public Long save(Users dto){
        return usersRepository.save(dto).getUserId();
    }

    // ???????????? todo:// ????????? SignUpService??? ?????? ??????????????? ???????????? ?????? ?????? ????????? ??????.
    public ResponseEntity<?> signUp(SignUpRequestDto signUpRequestDto) throws NoSuchPaddingException, InvalidAlgorithmParameterException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidKeyException, ParseException {

        // ????????? ???????????? ????????? ????????? ???????????? ???????????? ????????????.
        if(emailAuthService.validateAuthNumber(new ValidateAuthNumberRequestDto(signUpRequestDto.getEmailCode(), signUpRequestDto.getUserEmail())).getStatusCodeValue() != 200){
            return emailAuthService.validateAuthNumber(new ValidateAuthNumberRequestDto(signUpRequestDto.getEmailCode(), signUpRequestDto.getUserEmail()));
        }

        // todo ???????????? ????????? ?????????, ??????????????? ????????? ??????????????? ????????????.
        String encryptedEmail = aes256Cipher.AES_Encode(signUpRequestDto.getUserEmail());
        String password = new PasswordEncoding().encode(signUpRequestDto.getPassword());

        // ????????? ???????????????.
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        Users users = new Users(encryptedEmail, password, signUpRequestDto.getNickName(), roles);

        usersRepository.save(users);
        return new ResponseEntity<>(new DefaultResponseDto(200, "The membership has been registered successfully!"), HttpStatus.OK);
    }

    // ?????????
    public ResponseEntity<?> login(LoginRequestDto loginRequestDto) throws NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, ParseException {
        // ?????? ????????? ?????????.
        log.info("?????? ????????? {}", loginRequestDto.getUserEmail());
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
        log.info("?????? ?????? {}", jwt);
        if(jwtTokenProvider.validateToken(jwt)){
            log.info("??? ?????????");
            Users user = jwtTokenProvider.getUsersFromToken(httpServletRequest);
            ProfileResponseDto profileResponseDto = new ProfileResponseDto(user.getUserId(), user.getUserNickname(), user.getImageUrl(),user.getRoles());
            return new ResponseEntity<>(profileResponseDto, HttpStatus.OK);
        }
        return DefaultResponseDto.canNotFindProfile();
    }

    // ???????????? ????????? ????????? ???????????? ??????
    // ???????????? ?????? ?????? ????????? (???????????? ??????)
    @javax.transaction.Transactional
    public ResponseEntity<?> sendResetPasswordLink(FindPasswordRequestDto findPasswordRequestDto) throws Exception {
        if(!ValidSomething.isValidEmail(findPasswordRequestDto.getUserEmail())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "????????? ????????? ??????????????????."), HttpStatus.CONFLICT);
        }
        Users authUser = findByEmailAndUserNickname(findPasswordRequestDto.getUserEmail(), findPasswordRequestDto.getUserNickname());
        if(authUser == null) return new ResponseEntity<>(new DefaultResponseDto(409, "?????? ????????? ???????????? ????????????."), HttpStatus.CONFLICT);
        UUID uuid = UUID.randomUUID();
        ResetPasswordAuthCode resetPasswordAuthCode = new ResetPasswordAuthCode(uuid.toString(), authUser.getUserId(), new DateCreator().getTimestamp(), true);

        resetPasswordAuthCodeService.save(resetPasswordAuthCode);
        String link = aes256Cipher.getUrl()+"/resetPassword?token="+uuid.toString();
        EmailSenderRequestDto emailSenderRequestDto = new EmailSenderRequestDto(findPasswordRequestDto.getUserEmail(), "?????? ???????????? ????????? ?????? ?????????.",
                "???????????? ???????????? ??????????????? ????????? ????????????.\n" +link);
        Map<String, String> map = new HashMap<>();
        map.put("link", link);
        return emailAuthService.sendEmailForFindPassword(emailSenderRequestDto);
    }

    @javax.transaction.Transactional
    public ResponseEntity<?> confirmLink(@RequestParam String token) throws ParseException {
        // code ??? AES256?????? ??????????????? ????????????.
        log.info("?????? ??????.");
        ResetPasswordAuthCode resetPasswordAuthCode = resetPasswordAuthCodeService.findByCode(token);
        if (resetPasswordAuthCode == null) // ?????? ??????. ????????? 409 ??????
            return new ResponseEntity<>(new DefaultResponseDto(409,  "????????? ???????????? ????????????."), HttpStatus.CONFLICT);
        // ?????? ???????????? ??????????????? ???????????? ????????????
        if(!new DateCreator().getTimestamp().before(new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()))){
            log.info("?????? ???????????????.");
            log.info(new DateCreator().getTimestamp() + " : " + new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()));
            return new ResponseEntity<>(new DefaultResponseDto(409, "??????????????? ?????? ?????? ?????????."), HttpStatus.CONFLICT);
        }
        log.info("?????? ?????? ??????");
        Users authUser = findById(resetPasswordAuthCode.getAuthUserId());
        if (authUser == null) // ?????? ??????. ????????? 409 ??????
            return new ResponseEntity<>(new DefaultResponseDto(409,  "????????? ???????????? ????????????."), HttpStatus.CONFLICT);
        log.info("????????? ?????? ???????????? ??????");

        HttpHeaders headers = new HttpHeaders();
        // ????????? ?????? ???????????? ????????? ????????? ?????? ??????????????? ???????????? ??????.
        headers.setLocation(URI.create("http://carhelper-client.kro.kr:5000/manager/reset/password?token="+token));
        headers.setLocation(URI.create("http://172.30.1.12:5000/manager/reset/password"));
        log.info("????????? ?????? : " + token);
//        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
        return new ResponseEntity<>(new DefaultResponseDto(200, "????????? ?????? ???????????????."), HttpStatus.OK);
    }



    @javax.transaction.Transactional
    public ResponseEntity<?> resetPasswordUsingToken(@RequestBody ResetPasswordRequestDto resetPasswordRequestDto) throws Exception {
        log.info("??????(uuid)?????? ???????????? ???????????? ??????");
        ResetPasswordAuthCode resetPasswordAuthCode = resetPasswordAuthCodeService.findByCode(resetPasswordRequestDto.getToken());

        // ????????? ?????? ??? ??????
        if(resetPasswordAuthCode == null)
            return new ResponseEntity<>(new DefaultResponseDto(409, "????????? ???????????? ????????????."), HttpStatus.CONFLICT);
        // ??? ????????? ????????? ????????? ?????? ??????
        if(!resetPasswordAuthCode.isCanUse())
            return new ResponseEntity<>(new DefaultResponseDto(409, "?????? ????????? ?????? ?????????."), HttpStatus.CONFLICT);

        // ?????? ???????????? ??????????????? ???????????? ????????????
        if(!new DateCreator().getTimestamp().before(new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()))){
            log.info("?????? ???????????????.");
            log.info(new DateCreator().getTimestamp() + " : " + new DateCreator().getAfterOneDay(resetPasswordAuthCode.getCreatedDate()));
            return new ResponseEntity<>(new DefaultResponseDto(409, "??????????????? ?????? ?????? ?????????."), HttpStatus.CONFLICT);
        }

        Users authUser = findById(resetPasswordAuthCode.getAuthUserId());
        if(authUser == null)
            return new ResponseEntity<>(new DefaultResponseDto(409, "???????????? ???????????? ????????????."), HttpStatus.CONFLICT);

        if(!ValidSomething.isValidPassword(resetPasswordRequestDto.getNewPassword())){
            return new ResponseEntity<>(new DefaultResponseDto(409, "???????????? ????????? ??????????????????. 8~32??? ????????? ??????+??????+??????????????? ???????????? ??????????????????"), HttpStatus.CONFLICT);
        }
        resetPasswordAuthCode.setCertifiedDate(new DateCreator().getTimestamp());
        resetPasswordAuthCodeService.save(resetPasswordAuthCode);
        authUser.setUserPassword(new PasswordEncoding().encode(resetPasswordRequestDto.getNewPassword()));
        save(authUser);
        return new ResponseEntity<>(new DefaultResponseDto(200, "??????????????? ?????????????????????."), HttpStatus.OK);
    }

    // ????????????
    @javax.transaction.Transactional
    public ResponseEntity<?> logout(JwtRequestDto jwtRequestDto){
        Users users = (Users) jwtTokenProvider.getAuthentication(jwtRequestDto.getJwt()).getPrincipal();
        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        logoutValueOperations.set(jwtRequestDto.getJwt(), String.valueOf(users.getUserId())); // redis set ?????????
        logoutValueOperations.set(jwtRequestDto.getRefreshJwt(), String.valueOf(users.getUserId())); // redis set ?????????

        log.info("???????????? ?????? ????????? : '{}' , ?????? ?????? : '{}'", users.getUserId(), users.getUserEmail());
        return new ResponseEntity<>(new DefaultResponseDto(200,"???????????? ???????????????."), HttpStatus.OK);
    }

    public ResponseEntity<?> renewalToken(String token) throws ParseException {
        log.info("???????????? ?????? ????????? ?????????! '{}'\n??? ????????? ?????????????! -> '{}'", token.substring(token.length()-3), jwtTokenProvider.validateToken(token));
        if(jwtTokenProvider.validateToken(token)) {
            log.info("?????? ??????");
            Users user = ((Users) jwtTokenProvider.getAuthentication(token).getPrincipal());

            log.info("????????? ????????? ?????? ??????");
            JwtResponseDto jwtResponseDto = jwtTokenProvider.createTokens(user.getUserEmail(), user.getRoles());
            user.setLastLogin(new DateCreator().getTimestamp());
            usersRepository.save(user);
            log.info("???????????? : '{}'\njwt -> '{}'\nrefresh -> '{}'", user.getUserNickname(), jwtResponseDto.getJwt().substring(jwtResponseDto.getJwt().length()-3), jwtResponseDto.getRefreshJwt().substring(jwtResponseDto.getRefreshJwt().length()-3));
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
        log.info("refreshToken ?????? ???????????? ????????? 401 ??????");
        return new ResponseEntity<>( new DefaultResponseDto(401, "????????? ???????????? ????????????."), HttpStatus.UNAUTHORIZED);
    }

    @javax.transaction.Transactional
    public void invalidationToken(String token) throws InterruptedException {
        ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
        Users user = (Users) jwtTokenProvider.getAuthentication(token).getPrincipal();
        logoutValueOperations.set(token, String.valueOf(user.getUserId())); // redis set ?????????

        log.info("?????? ?????????! ?????? ????????? : '{}' , ?????? ?????? : '{}'", user.getUserId(), user.getUserNickname());
    }


    public String getKaKaoAccessToken(String code){
        System.out.println("hi -> " + applicationSocialLoginConfigData.getKakao());
        String access_Token="";
        String refresh_Token ="";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try{
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            //POST ????????? ?????? ???????????? false??? setDoOutput??? true???
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            //POST ????????? ????????? ???????????? ???????????? ???????????? ?????? ??????
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id="+applicationSocialLoginConfigData.getKakao()); // TODO REST_API_KEY ??????
            sb.append("&redirect_uri=http://localhost:8080/api/v1/kakaoLogin"); // TODO ???????????? ?????? redirect_uri ??????
            sb.append("&code=" + code);
            bw.write(sb.toString());
            bw.flush();

            //?????? ????????? 200????????? ??????
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);
            //????????? ?????? ?????? JSON????????? Response ????????? ????????????
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
            System.out.println("response body : " + result);

            //Gson ?????????????????? ????????? ???????????? JSON?????? ?????? ??????
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            access_Token = element.getAsJsonObject().get("access_token").getAsString();
            refresh_Token = element.getAsJsonObject().get("refresh_token").getAsString();

            System.out.println("access_token : " + access_Token);
            System.out.println("refresh_token : " + refresh_Token);

            br.close();
            bw.close();
        }catch (IOException e) {
            e.printStackTrace();
        }

        return access_Token;
    }
}
