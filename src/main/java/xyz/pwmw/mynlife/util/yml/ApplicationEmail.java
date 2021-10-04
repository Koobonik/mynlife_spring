package xyz.pwmw.mynlife.util.yml;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
// yml 파일에서 가져올 변수 이름을 명시해준다.
@ConfigurationProperties(prefix = "email")
@Setter
@Getter
public class ApplicationEmail {
    private String host;
    private String username;
    private String password;
    private int port;
}