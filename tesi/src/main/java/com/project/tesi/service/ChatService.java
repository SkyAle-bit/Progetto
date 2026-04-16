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

    /** Crea o recupera una chat tra due utenti. */
    Long createChat(Long senderId, Long receiverId);

    /** Invia un messaggio tramite endpoint REST (con validazione permessi). */
    ChatMessageResponse sendMessage(SendMessageRequest request);

    /**
     * Salva direttamente un messaggio (usato dal WebSocket controller).
     * Gestisce la transazione correttamente tramite il proxy Spring.
     *
     * @param chatId     ID della chat
     * @param senderId   ID del mittente
     * @param content    contenuto del messaggio
     */
    void sendMessageDirect(Long chatId, Long senderId, String content);

    /** Recupera la cronologia dei messaggi di una chat (paginata). */
    List<ChatMessageResponse> getConversation(Long chatId, Long userId, int page, int size);

    /** Recupera la lista di tutte le conversazioni di un utente con anteprima. */
    List<ConversationPreviewResponse> getUserConversations(Long userId);

    /** Segna come letti tutti i messaggi ricevuti in una chat. */
    void markAsRead(Long chatId, Long userId);

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

    /**
     * Termina una chat di supporto per un utente.
     * La chat scompare dalla lista dell'utente ma resta visibile per l'operatore
     * con un'indicazione che la conversazione è stata terminata.
     *
     * @param chatId      ID della chat
     */
    void terminateChat(Long chatId, Long userId);

    com.project.tesi.model.Chat getChatEntity(Long chatId);
}
