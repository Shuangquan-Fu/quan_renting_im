package com.quan.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quan.dao.MessageDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
public class MessageHandler extends TextWebSocketHandler {
    @Autowired
    private MessageDao messageDao;
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<Long, WebSocketSession> SESSIONS = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws
            Exception {
        Long uid = (Long)session.getAttributes().get("uid");
        // 将当前用户的session放置到map中，后面会使用相应的session通信
        SESSIONS.put(uid, session);
    }
    
}
