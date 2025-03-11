package com.example.be4.chat.querydsl;

import com.example.be4.chat.domain.Message;

import java.util.List;

public interface MessageRepositoryCustom {
    public List<Message> findRecentMessages(int limit);
}
