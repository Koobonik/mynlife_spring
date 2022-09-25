package xyz.pwmw.mynlife.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import xyz.pwmw.mynlife.dto.requestDto.MessagesRequestDto;
import xyz.pwmw.mynlife.dto.requestDto.SmsRequestDto;
import xyz.pwmw.mynlife.dto.responseDto.SendSmsResponseDto;
import xyz.pwmw.mynlife.model.Hobby;
import xyz.pwmw.mynlife.model.HobbyRepository;
import xyz.pwmw.mynlife.model.Users;
import xyz.pwmw.mynlife.util.DateCreator;
import xyz.pwmw.mynlife.util.yml.ApplicationNaverSENS;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Component
public class HobbyService {

    private final ApplicationNaverSENS applicationNaverSENS;
    private final HobbyRepository hobbyRepository;

    public List<Hobby> findAll(){
        return hobbyRepository.findAll();
    }
}
