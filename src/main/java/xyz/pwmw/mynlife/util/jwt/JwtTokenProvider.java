package xyz.pwmw.mynlife.util.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import xyz.pwmw.mynlife.dto.responseDto.JwtResponseDto;
import xyz.pwmw.mynlife.model.Users;
import xyz.pwmw.mynlife.util.PemReader;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
@Log4j2
public class JwtTokenProvider {

    private PrivateKey tokenKey;

    // 토큰 유효시간 30분
    private final long tokenValidTime = 60 * 60 * 24 * 365 * 1000L;

    // 토큰 유효시간 1년
    private long refreshTokenValidTime = 60 * 60 * 24 * 365 * 1000L;

    private final UserDetailsService userDetailsService;
    private final StringRedisTemplate redisTemplate;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() throws IOException, GeneralSecurityException {
        // 30분 단위로 갱신되는 토큰 값.
        Path path = Paths.get("src/main/resources/token_key.pem");
        List<String> reads = Files.readAllLines(path);
        String read = "";
        for (String str : reads){
            read += str+"\n";
        }
        tokenKey = PemReader.getPrivateKeyFromString(read);
    }

    // JWT 토큰 생성
    public String createToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "RS256");
        header.put("typ", "JWT");
        Date now = new Date();
        return Jwts.builder()
                .setHeader(header) // 알고리즘과 토큰 타입을 헤더에 넣어줌
                .setClaims(claims) // 유저의 이름(userPk)등이 담겨있음
                .setIssuedAt(now) // 토큰 발행 시간 정보 iat
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time 언제까지 유효한지.
                .signWith(SignatureAlgorithm.RS256, tokenKey)  // 사용할 암호화 알고리즘과
                .setIssuer("dev_koo")
                // signature 에 들어갈 secret값 세팅
                .compact();
    }
    public String createRefreshToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Map<String, Object> header = new HashMap<>();
        header.put("alg", "RS256");
        header.put("typ", "JWT");
        Date now = new Date();
        // log.info(refreshKey);
        return Jwts.builder()
                .setHeader(header) // 알고리즘과 토큰 타입을 헤더에 넣어줌
                .setClaims(claims) // 유저의 이름(userPk)등이 담겨있음
                .setIssuedAt(now) // 토큰 발행 시간 정보 iat
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime)) // set Expire Time 언제까지 유효한지.
                .signWith(SignatureAlgorithm.RS256, tokenKey)  // 사용할 암호화 알고리즘과
                .setIssuer("dev_koo")
                // signature 에 들어갈 secret값 세팅
                .compact();
    }

    public JwtResponseDto createTokens(String userName, List<String> roles){
        return new JwtResponseDto(createToken(userName, roles), createRefreshToken(userName, roles));
    }

    // JWT 토큰에서 인증 정보 조회
    @Transactional
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        Claims claims = Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token).getBody();
        log.info("토큰값 : '{}'", token);
        return Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "JWT" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(jwtToken);
//            ValueOperations<String, String> logoutValueOperations = redisTemplate.opsForValue();
//            if(logoutValueOperations.get(jwtToken) != null){
//                log.info("로그아웃된 토큰 입니다.");
//                return false;
//            }
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            log.info("validateToken 에러 발생!");
            return false;
        }
    }
    public Users getUsersFromToken(HttpServletRequest httpServletRequest){
        String token = this.resolveToken(httpServletRequest);
        return (Users) this.getAuthentication(token).getPrincipal();
    }
}