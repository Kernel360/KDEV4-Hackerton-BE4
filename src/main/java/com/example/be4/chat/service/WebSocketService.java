package com.example.be4.chat.service;

import com.example.be4.chat.domain.Message;


import java.util.List;

public interface WebSocketService {
    public List<Message> getRecentMessages();
    public void sendMessage(Message message);
}
