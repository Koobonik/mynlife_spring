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
import xyz.pwmw.mynlife.util.DateCreator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@Component
public class SmsService {

    private final ApplicationNaverSENS applicationNaverSENS;

    public String getTest(){
        return applicationNaverSENS.getAccesskey();
    }

    public SendSmsResponseDto sendSms(String recipientPhoneNumber, String content) throws ParseException, JsonProcessingException, UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException, URISyntaxException {
        Long time = new DateCreator().getTimestamp().getTime();
        List<MessagesRequestDto> messages = new ArrayList<>();
        // 보내는 사람에게 내용을 보냄.
        messages.add(new MessagesRequestDto(recipientPhoneNumber,content)); // content부분이 내용임

        // 전체 json에 대해 메시지를 만든다.
        SmsRequestDto smsRequestDto = new SmsRequestDto("SMS", "COMM", "82", applicationNaverSENS.getSendfrom(), "MangoLtd", messages);

        // 쌓아온 바디를 json 형태로 변환시켜준다.
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(smsRequestDto);

        // 헤더에서 여러 설정값들을 잡아준다.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-ncp-apigw-timestamp", time.toString());
        headers.set("x-ncp-iam-access-key", applicationNaverSENS.getAccesskey());

        // 제일 중요한 signature 서명하기.
        String sig = makeSignature(time);
        System.out.println("sig -> " + sig);
        headers.set("x-ncp-apigw-signature-v2", sig);

        // 위에서 조립한 jsonBody와 헤더를 조립한다.
        HttpEntity<String> body = new HttpEntity<>(jsonBody, headers);
        System.out.println(body.getBody());

        // restTemplate로 post 요청을 보낸다. 별 일 없으면 202 코드 반환된다.
        RestTemplate restTemplate = new RestTemplate();
        SendSmsResponseDto sendSmsResponseDto = restTemplate.postForObject(new URI("https://sens.apigw.ntruss.com/sms/v2/services/"+applicationNaverSENS.getServiceid()+"/messages"), body, SendSmsResponseDto.class);
        System.out.println(sendSmsResponseDto.getStatusCode());
        return sendSmsResponseDto;
    }

    public String makeSignature(Long time) throws UnsupportedEncodingException, InvalidKeyException, NoSuchAlgorithmException {
        String space = " ";					// one space
        String newLine = "\n";					// new line
        String method = "POST";					// method
        String url = "/sms/v2/services/"+applicationNaverSENS.getServiceid()+"/messages";	// url (include query string)
        String timestamp = time.toString();			// current timestamp (epoch)
        String accessKey = applicationNaverSENS.getAccesskey();			// access key id (from portal or Sub Account)
        String secretKey = applicationNaverSENS.getSecretkey();

        String message = new StringBuilder()
                .append(method)
                .append(space)
                .append(url)
                .append(newLine)
                .append(timestamp)
                .append(newLine)
                .append(accessKey)
                .toString();

        SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(signingKey);

        byte[] rawHmac = mac.doFinal(message.getBytes("UTF-8"));
        String encodeBase64String = Base64.encodeBase64String(rawHmac);

        return encodeBase64String;
    }
}
