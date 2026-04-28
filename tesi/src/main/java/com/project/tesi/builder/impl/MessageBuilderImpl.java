package com.project.tesi.builder.impl;

import com.project.tesi.builder.MessageBuilder;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public class MessageBuilderImpl implements MessageBuilder {
    private Long id;
    private String content;
    private LocalDateTime timeStamp;
    private boolean isRead;
    private User user;
    private Chat chat;

    @Override
    public MessageBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public MessageBuilder content(String content) {
        this.content = content;
        return this;
    }
    @Override
    public MessageBuilder timeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
        return this;
    }
    @Override
    public MessageBuilder isRead(boolean isRead) {
        this.isRead = isRead;
        return this;
    }
    @Override
    public MessageBuilder user(User user) {
        this.user = user;
        return this;
    }
    @Override
    public MessageBuilder chat(Chat chat) {
        this.chat = chat;
        return this;
    }

    @Override
    public Message build() {
        Message obj = new Message();
        obj.setId(this.id);
        obj.setContent(this.content);
        obj.setTimeStamp(this.timeStamp);
        obj.setRead(this.isRead);
        obj.setUser(this.user);
        obj.setChat(this.chat);
        return obj;
    }
}
