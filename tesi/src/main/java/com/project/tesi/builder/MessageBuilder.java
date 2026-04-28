package com.project.tesi.builder;

import java.time.LocalDateTime;
import com.project.tesi.model.*;


public interface MessageBuilder {
    MessageBuilder id(Long id);
    MessageBuilder content(String content);
    MessageBuilder timeStamp(LocalDateTime timeStamp);
    MessageBuilder isRead(boolean isRead);
    MessageBuilder user(User user);
    MessageBuilder chat(Chat chat);
    Message build();
}
