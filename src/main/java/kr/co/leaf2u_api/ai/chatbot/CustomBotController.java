package kr.co.leaf2u_api.ai.chatbot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/bot")
public class CustomBotController {
    @Value("${openai.model}")
    private String MODEL;

    @Value("${openai.api.url}")
    private String APIURL;

    @Autowired
    private RestTemplate template;

    @GetMapping("/chat")
    public String chat(@RequestParam(name = "prompt")String prompt) {
        System.out.println("prompt");

        ChatGPTRequest request = new ChatGPTRequest(MODEL, prompt);
        ChatGPTResponse chatGPTResponse = template.postForObject(APIURL, request, ChatGPTResponse.class);

        // 응답 내용 가져와서 줄바꿈 적용
        String responseContent = chatGPTResponse.getChoices().get(0).getMessage().getContent();
        return responseContent.replace("\n", "<br>");

//        줄 바꿈 안할거면 위에 2줄 지우고 이 return 쓰면 됨
//        return chatGPTResponse.getChoices().get(0).getMessage().getContent();
    }
}
