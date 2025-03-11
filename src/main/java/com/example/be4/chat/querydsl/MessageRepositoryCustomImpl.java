package com.example.be4.chat.querydsl;

import com.example.be4.chat.domain.Message;
import com.example.be4.chat.domain.QMessage;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageRepositoryCustomImpl extends QuerydslRepositorySupport implements MessageRepositoryCustom {

    public MessageRepositoryCustomImpl() {
        super(Message.class);
    }

    @Override
    public List<Message> findRecentMessages(int limit) {
        QMessage message = QMessage.message;
        JPQLQuery<Message> query = from(message);
        query.orderBy(message.timestamp.desc())
                .limit(limit);

        return query
                .fetch();
    }
}
