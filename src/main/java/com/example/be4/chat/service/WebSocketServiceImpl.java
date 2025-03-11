package com.example.be4.chat.service;

import com.example.be4.chat.domain.Message;
import com.example.be4.chat.repository.WebSocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {
    private final WebSocketRepository webSocketRepository;

    @Override
    public List<Message> getRecentMessages() {
        return webSocketRepository.findRecentMessages(100);
    }

    @Override
    public void sendMessage(Message message) {
        webSocketRepository.save(message);
    }
}
