package kr.co.leaf2u_api.ai.chatbot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ChatbotConfig {
    @Value("${openai.api.key}")
    private String OPENAIKEY;
    @Bean
    public RestTemplate template() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add("Authorization", "Bearer " + OPENAIKEY);
            return execution.execute(request, body);
        });
        return restTemplate;
    }
}
