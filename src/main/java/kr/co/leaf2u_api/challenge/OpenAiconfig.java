package kr.co.leaf2u_api.challenge;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "openai.api")
public class OpenAiconfig {
    private String key;
}
