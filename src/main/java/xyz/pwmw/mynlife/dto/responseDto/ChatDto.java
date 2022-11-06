package xyz.pwmw.mynlife.dto.responseDto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ChatDto {
    private String userToken;
    private String text;
    private String receivedUserId;
//    private List<String> images = new ArrayList<>();
    private String createdAt;

    public void fromMap(Map<String, Object> map){
        this.userToken = (String) map.get("userToken");
        this.text = (String) map.get("text");
//        this.images = (List<String>) map.get("images");
        this.createdAt = (String) map.get("createdAt");
    }

}
