package com.project.tesi.builder;

import com.project.tesi.model.Chat;
import com.project.tesi.model.Message;

import java.time.LocalDateTime;

public interface MessageBuilder {
    MessageBuilder id(Long id);
    MessageBuilder content(String content);
    MessageBuilder timeStamp(LocalDateTime timeStamp);
    MessageBuilder isRead(boolean isRead);
    MessageBuilder sentByUser1(boolean sentByUser1);
    MessageBuilder chat(Chat chat);
    Message build();
}
