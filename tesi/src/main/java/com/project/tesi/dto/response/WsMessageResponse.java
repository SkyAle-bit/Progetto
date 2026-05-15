package com.project.tesi.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WsMessageResponse {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private String content;
    private String status;
    private String createdAt;
    private String roomId;
}
