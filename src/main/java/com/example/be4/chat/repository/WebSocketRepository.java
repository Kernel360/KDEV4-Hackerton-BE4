package com.example.be4.chat.repository;

import com.example.be4.chat.domain.Message;
import com.example.be4.chat.querydsl.MessageRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;


public interface WebSocketRepository extends JpaRepository<Message, Long> , MessageRepositoryCustom {
}
