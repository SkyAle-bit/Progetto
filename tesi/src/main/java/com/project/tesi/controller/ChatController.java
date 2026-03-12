package com.project.tesi.controller;

import com.project.tesi.dto.request.SendMessageRequest;
import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;
import com.project.tesi.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST per la chat (endpoint HTTP).
 * Fornisce le API per inviare messaggi, recuperare la cronologia,
 * ottenere la lista conversazioni e gestire lo stato di lettura.
 * Per l'interazione in tempo reale si usa {@link ChatWebSocketController}.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chat", description = "API per la messaggistica tra utenti")
public class ChatController {

    private final ChatService chatService;

    /** Invia un nuovo messaggio da un utente a un altro. */
    @Operation(summary = "Invia un messaggio")
    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    /** Recupera la cronologia dei messaggi tra due utenti (paginata). */
    @Operation(summary = "Recupera la cronologia messaggi tra due utenti")
    @GetMapping("/conversation/{userId1}/{userId2}")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
            @PathVariable Long userId1,
            @PathVariable Long userId2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(chatService.getConversation(userId1, userId2, page, size));
    }

    /** Recupera la lista di tutte le conversazioni di un utente con anteprima ultimo messaggio. */
    @Operation(summary = "Recupera la lista di tutte le conversazioni di un utente")
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationPreviewResponse>> getUserConversations(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }

    /** Segna come letti tutti i messaggi ricevuti da un certo mittente. */
    @Operation(summary = "Segna come letti tutti i messaggi ricevuti da un utente")
    @PutMapping("/read/{receiverId}/{senderId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long receiverId, @PathVariable Long senderId) {
        chatService.markAsRead(receiverId, senderId);
        return ResponseEntity.ok().build();
    }

    /** Restituisce il conteggio totale dei messaggi non letti per un utente. */
    @Operation(summary = "Conteggio totale messaggi non letti per un utente")
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Integer> getTotalUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getTotalUnreadCount(userId));
    }
}
