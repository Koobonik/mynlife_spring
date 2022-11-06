package xyz.pwmw.mynlife.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.json.JSONParser;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.pwmw.mynlife.dto.responseDto.ChatDto;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();


    // 사용자가(브라우저) 웹소켓 서버에 붙게되면 동작하는 메소드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("받음 -> " + session.getAttributes());
        CLIENTS.put(session.getId(), session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String id = session.getId();  //메시지를 보낸 아이디
        System.out.println("message -> " + message.getPayload());
        //2. Parser
        JSONParser jsonParser = new JSONParser(message.getPayload());
        Map<String, Object> map = jsonParser.object();


        //4. To JsonObject
        System.out.println("map -> " + map);

        ObjectMapper objectMapper = new ObjectMapper();
        ChatDto chatDto = objectMapper.convertValue(map, ChatDto.class);
//        Map<String, Object> kakaoAccount = objectMapper.convertValue(map, Map.class);
        System.out.println("chatDto -> " + chatDto.getText());
        System.out.println("받음22 -> " + message.getPayload());
        CLIENTS.entrySet().forEach( arg->{
            if(!arg.getKey().equals(id)) {  //같은 아이디가 아니면 메시지를 전달합니다.
                try {
                    arg.getValue().sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}