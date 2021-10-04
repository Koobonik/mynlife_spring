package xyz.pwmw.mynlife.util.yml;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
//value를 통해 값이 있는 위치를 명시해준다.
// @PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
// yml 파일에서 가져올 변수 이름을 명시해준다.
@ConfigurationProperties(prefix = "aes256")
@Setter
@Getter
public class ApplicationAESRead {
    private String secretkey;
    private String iv;
    private String url;
}