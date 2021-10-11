package xyz.pwmw.mynlife.service;

import org.springframework.stereotype.Component;
import xyz.pwmw.mynlife.dto.requestDto.EmailSenderRequestDto;
import xyz.pwmw.mynlife.util.yml.ApplicationEmail;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Component
public class EmailService {
    private final ApplicationEmail applicationEmail;

    public EmailService(ApplicationEmail applicationEmail){
        this.applicationEmail = applicationEmail;
    }

    public void sendEmail(EmailSenderRequestDto emailSenderRequestDto) throws Exception{
        //메일 관련 정보
        String host = this.applicationEmail.getHost();
        final String username = this.applicationEmail.getUsername(); //네이버 이메일 주소중 @ naver.com 앞주소만 작성
        final String password = this.applicationEmail.getPassword(); //네이버 이메일 비밀번호를 작성
        int port = this.applicationEmail.getPort();                  //네이버 STMP 포트 번호

        //메일 내용
        String recipient = emailSenderRequestDto.getRecipient(); // 받는 사람의 이메일 주소
        String subject = emailSenderRequestDto.getSubject();     // 메일 발송시 제목을 작성
        String body = emailSenderRequestDto.getBody();           // 메일 발송시 내용 작성

        Properties props = System.getProperties();

        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.trust", host);

        Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
            String un=username;
            String pw=password;
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(un, pw);
            }
        });
        session.setDebug(true); //for debug

        Message mimeMessage = new MimeMessage(session);
        mimeMessage.setFrom(new InternetAddress(this.applicationEmail.getUsername()+"@naver.com"));
        mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
        mimeMessage.setSubject(subject);
        mimeMessage.setText(body);
        Transport.send(mimeMessage);
    }
}