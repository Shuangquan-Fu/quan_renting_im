package com.quan.websocket;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quan.dao.MessageDao;
import com.quan.pojo.Message;
import com.quan.pojo.UserData;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.Map;

@Component
@RocketMQMessageListener(topic = "quan-im-send-message-topic",
        consumerGroup = "quan-im-consumer",
        messageModel = MessageModel.BROADCASTING,
        selectorExpression = "SEND_MSG")
public class MessageHandler extends TextWebSocketHandler implements RocketMQListener<String> {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Map<Long, WebSocketSession> SESSIONS = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws
            Exception {
        Long uid = (Long)session.getAttributes().get("uid");
        // 将当前用户的session放置到map中，后面会使用相应的session通信
        SESSIONS.put(uid, session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage Tmessage) throws Exception {
        Long uid  = (Long) session.getAttributes().get("uid");
        JsonNode jsonNode = MAPPER.readTree(Tmessage.getPayload());
        Long toId = jsonNode.get("toId").asLong();
        String msg = jsonNode.get("msg").asText();
        Message message = Message.builder()
                .from(UserData.USER_MAP.get(uid))
                .to(UserData.USER_MAP.get(toId))
                .msg(msg)
                .build();
        message = this.messageDao.saveMessage(message);
        String msgStr = MAPPER.writeValueAsString(message);
        // 判断to用户是否在线
        WebSocketSession toSession = SESSIONS.get(toId);
        if(toSession != null && toSession.isOpen()){
        //TODO 具体格式需要和前端对接
            toSession.sendMessage(new TextMessage(MAPPER.writeValueAsString(message)));
        // 更新消息状态为已读
            this.messageDao.updateMessageState(message.getId(), 2);
        } else {
            org.springframework.messaging.Message mqMessage = MessageBuilder
                    .withPayload(msgStr)
                    .build();
// topic:tags 设置主题和标签
            this.rocketMQTemplate.send("haoke-im-send-message-topic:SEND_MSG",
                    mqMessage);
        }
    }

    @Override
    public void onMessage(String msg) {
        try {
            JsonNode jsonNode = MAPPER.readTree(msg);
            Long toId = jsonNode.get("to").get("id").longValue();
// 判断to用户是否在线
            WebSocketSession toSession = SESSIONS.get(toId);
            if (toSession != null && toSession.isOpen()) {
                toSession.sendMessage(new TextMessage(msg));
// 更新消息状态为已读
                this.messageDao.updateMessageState(new ObjectId(jsonNode.get("id").asText()), 2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
