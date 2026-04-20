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

    /** Crea una nuova chat tra due utenti o recupera quella esistente. */
    @Operation(summary = "Crea o recupera la chat tra due utenti")
    @PostMapping("/create/{senderId}/{receiverId}")
    public ResponseEntity<Long> createChat(@PathVariable Long senderId, @PathVariable Long receiverId) {
        return ResponseEntity.ok(chatService.createChat(senderId, receiverId));
    }

    /** Invia un nuovo messaggio da un utente a un altro. */
    @Operation(summary = "Invia un messaggio")
    @PostMapping("/send")
    public ResponseEntity<ChatMessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    /** Recupera la cronologia dei messaggi di una chat tra due utenti (paginata). */
    @Operation(summary = "Recupera la cronologia messaggi di una chat")
    @GetMapping("/conversation/{chatId}/{userId}")
    public ResponseEntity<List<ChatMessageResponse>> getConversation(
            @PathVariable Long chatId,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        return ResponseEntity.ok(chatService.getConversation(chatId, userId, page, size));
    }

    /** Recupera la lista di tutte le conversazioni di un utente con anteprima ultimo messaggio. */
    @Operation(summary = "Recupera la lista di tutte le conversazioni di un utente")
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationPreviewResponse>> getUserConversations(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getUserConversations(userId));
    }

    /** Segna come letti tutti i messaggi ricevuti in una chat. */
    @Operation(summary = "Segna come letti tutti i messaggi ricevuti in una chat")
    @PutMapping("/read/{chatId}/{userId}")
    public ResponseEntity<Void> markAsRead(@PathVariable Long chatId, @PathVariable Long userId) {
        chatService.markAsRead(chatId, userId);
        return ResponseEntity.ok().build();
    }

    /** Restituisce il conteggio totale dei messaggi non letti per un utente. */
    @Operation(summary = "Conteggio totale messaggi non letti per un utente")
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Integer> getTotalUnreadCount(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getTotalUnreadCount(userId));
    }
}
