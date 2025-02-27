package kr.co.leaf2u_api.ai.challenge;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private String role;
    private List<Map<String, Object>> content;
}
