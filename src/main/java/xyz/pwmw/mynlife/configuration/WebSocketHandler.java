package xyz.pwmw.mynlife.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import xyz.pwmw.mynlife.dto.responseDto.ChatDto;
import xyz.pwmw.mynlife.model.users.Users;
import xyz.pwmw.mynlife.util.jwt.JwtTokenProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<String, Map> CLIENTS = new ConcurrentHashMap<String, Map>();

    private final JwtTokenProvider jwtTokenProvider;


    // 사용자가(브라우저) 웹소켓 서버에 붙게되면 동작하는 메소드
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("받음 -> " + Objects.requireNonNull(session.getHandshakeHeaders().get("token")).get(0));
        Users users = jwtTokenProvider.getUsersFromToken(Objects.requireNonNull(session.getHandshakeHeaders().get("token")).get(0));
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("session" , session);
        map.put("user" , users);


        CLIENTS.put(session.getId(), map);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }

    @Transactional
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String id = session.getId();  //메시지를 보낸 아이디
        JSONParser jsonParser = new JSONParser(message.getPayload());
        Map<String, Object> map = jsonParser.object();
        ObjectMapper objectMapper = new ObjectMapper();
        ChatDto chatDto = objectMapper.convertValue(map, ChatDto.class);
//        Users sendUser = jwtTokenProvider.getUsersFromToken(chatDto.getUserToken());

        System.out.println("받음 -> " + chatDto.getText());
        CLIENTS.forEach((key, value) -> {
                Users user = (Users) value.get("user");
            if (key.equals(id) || Integer.parseInt(chatDto.getReceivedUserId()) == user.getUserId()) {  // 보낸자와 받는자에게만 메시지를 전달 한다.
                try {
                    WebSocketSession session1 = (WebSocketSession) value.get("session");
                    session1.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}