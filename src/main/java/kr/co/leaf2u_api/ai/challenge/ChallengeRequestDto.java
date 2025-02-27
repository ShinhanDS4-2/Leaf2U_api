package kr.co.leaf2u_api.ai.challenge;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 이미지 업로드
 * */
@Data
public class ChallengeRequestDto {

    private String model;
    private List<Message> messages;
    private String imageUrl;

    public ChallengeRequestDto(String model, String base64Image) {
        this.model = model;
        List<Map<String, Object>> content = new ArrayList<>();

        Map<String, Object> textMap = new HashMap<>();
        textMap.put("type", "text");
        textMap.put("text", "이미지 분석");

        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("url", "data:image/jpeg;base64," + base64Image);

        Map<String, Object> imageMap = new HashMap<>();
        imageMap.put("type", "image_url");
        imageMap.put("image_url", urlMap);

        content.add(textMap);
        content.add(imageMap);

        Message message = new Message();
        message.setRole("user");
        message.setContent(content);
        System.out.println(message);

        messages.add(message);


        System.out.println(messages);



//        // 사용자 메세지 구성
//        List<Content> contentList = new ArrayList<>();
//        contentList.add(new Content("text", "이 이미지는 뭔가요?"));
//
//        // 이미지 URL 추가
//        contentList.add(new Content("image_url", "data:image/jpeg;base64," + base64Image));
//
//        this.messages.add(new Message("user", contentList));

    }


//    /**
//     * GPT-4o에 보낼 메세지 클래스
//     * */
//    @Data
//    static class Message {
//        private String role;
//        private List<Content> content;
//
//        public Message(String role, List<Content> content) {
//            this.role = role;
//            this.content = content;
//        }
//    }
//
//    /**
//     *  텍스트 또는 이미지 URL을 포함하는 컨텐츠 클래스
//     * */
//    @Data
//    static class Content{
//        private String type;
//        private Object content;
//
//        public Content(String type, String text) {
//            this.type = type;
//            this.content = text;
//        }
//
//        public Content(String type, Object image_url) {
//            this.type = type;
//            this.content = image_url;
//        }
//    }
//
//    /**
//     * 이미지 URL을 포함하는 클래스
//     */
//    @Data
//    static class ImageUrl {
//        private String url;
//
//        public ImageUrl(String url) {
//            this.url = url;
//        }
//    }
}
