package com.project.tesi.service;

import com.project.tesi.dto.request.SendMessageRequest;
import com.project.tesi.dto.response.ChatMessageResponse;
import com.project.tesi.dto.response.ConversationPreviewResponse;

import java.util.List;

/**
 * Interfaccia del servizio di messaggistica chat.
 * Gestisce l'invio, il recupero, la lettura dei messaggi
 * e la lista delle conversazioni attive di un utente.
 */
public interface ChatService {

    /** Invia un messaggio tramite endpoint REST (con validazione permessi). */
    ChatMessageResponse sendMessage(SendMessageRequest request);

    /**
     * Salva direttamente un messaggio (usato dal WebSocket controller).
     * Gestisce la transazione correttamente tramite il proxy Spring.
     *
     * @param senderId   ID del mittente
     * @param receiverId ID del destinatario
     * @param content    contenuto del messaggio
     */
    void sendMessageDirect(Long senderId, Long receiverId, String content);

    /** Recupera la cronologia dei messaggi tra due utenti (paginata). */
    List<ChatMessageResponse> getConversation(Long userId1, Long userId2, int page, int size);

    /** Recupera la lista di tutte le conversazioni di un utente con anteprima. */
    List<ConversationPreviewResponse> getUserConversations(Long userId);

    /** Segna come letti tutti i messaggi ricevuti da un certo mittente. */
    void markAsRead(Long receiverId, Long senderId);

    /** Restituisce il conteggio totale dei messaggi non letti per un utente. */
    int getTotalUnreadCount(Long userId);

    /**
     * Restituisce il nome completo di un utente (usato dal WebSocket controller
     * per costruire il DTO del messaggio in tempo reale).
     *
     * @param userId ID dell'utente
     * @return nome completo (nome + cognome)
     */
    String getUserFullName(Long userId);
}
