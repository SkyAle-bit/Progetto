package com.project.tesi.builder.impl;

import com.project.tesi.builder.MessageBuilder;
import com.project.tesi.model.Chat;
import com.project.tesi.model.Message;

import java.time.LocalDateTime;
import java.util.Objects;

public class MessageBuilderImpl implements MessageBuilder {

    private static final int MAX_MESSAGE_LENGTH = 2000;

    private Long id;
    private String content;
    private LocalDateTime timeStamp;
    private boolean isRead;
    private boolean sentByUser1;
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
    public MessageBuilder sentByUser1(boolean sentByUser1) {
        this.sentByUser1 = sentByUser1;
        return this;
    }

    @Override
    public MessageBuilder chat(Chat chat) {
        this.chat = chat;
        return this;
    }

    @Override
    public Message build() {
        Objects.requireNonNull(this.chat, "chat è obbligatorio");
        Objects.requireNonNull(this.content, "content è obbligatorio");
        if (this.content.isBlank())
            throw new IllegalArgumentException("content non può essere vuoto");
        if (this.content.length() > MAX_MESSAGE_LENGTH)
            throw new IllegalArgumentException("content non può superare " + MAX_MESSAGE_LENGTH + " caratteri");

        Message obj = new Message();
        obj.setId(this.id);
        obj.setContent(this.content);
        obj.setTimeStamp(this.timeStamp);
        obj.setRead(this.isRead);
        obj.setSentByUser1(this.sentByUser1);
        obj.setChat(this.chat);
        return obj;
    }
}
