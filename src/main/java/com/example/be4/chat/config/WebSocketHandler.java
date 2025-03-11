package com.example.be4.chat.config;

import com.example.be4.chat.domain.Message;
import com.example.be4.chat.service.WebSocketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
@Slf4j
public class WebSocketHandler extends TextWebSocketHandler {

    private final WebSocketService webSocketService;
    private final Set<WebSocketSession> sessions = ConcurrentHashMap.newKeySet();
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("Connected to websocket:" + session.getId());
        sessions.add(session);
        List<Message> messages = webSocketService.getRecentMessages();
        //메시지 순서정렬
        Collections.reverse(messages);

        if(session.isOpen()) {
            for(Message message : messages) {
                session.sendMessage(new TextMessage(toJSON(message)));
            }
        }
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.info("Received message:" + payload+ " from websocket:" + session.getId());
        Message newMessage = Message.builder()
                .senderId(session.getId())
                .payload(payload)
                .build();
        webSocketService.sendMessage(newMessage);

        for(WebSocketSession s : sessions) {
            if(s.isOpen()){
                s.sendMessage(new TextMessage(toJSON(newMessage)));
            }
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("Disconnected from websocket:" + session.getId());
        sessions.remove(session);

    }


    private String toJSON(Message message) throws Exception {
        return objectMapper.writeValueAsString(message);
    }
}
