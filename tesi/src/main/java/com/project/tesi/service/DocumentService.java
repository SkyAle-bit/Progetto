package com.project.tesi.service;

import com.project.tesi.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * Interfaccia del servizio per la gestione dei documenti.
 * Gestisce caricamento, download, eliminazione e aggiornamento note
 * di file associati ai clienti (schede, piani alimentari, certificati, ecc.).
 */
public interface DocumentService {

    /** Carica un documento validando il ruolo dell'uploader rispetto al tipo di file. */
    Map<String, Object> uploadDocumentWithValidation(MultipartFile file, Long clientId, Long uploaderId, String docType);

    /** Scarica il contenuto binario di un documento. */
    byte[] downloadDocument(Long documentId);

    /** Recupera un documento per ID. */
    Document getDocumentById(Long documentId);

    /** Restituisce tutti i documenti di un utente come lista di DTO. */
    List<Map<String, Object>> getUserDocumentsDto(Long userId);

    /** Restituisce i documenti di un utente filtrati per tipo come lista di DTO. */
    List<Map<String, Object>> getUserDocumentsByTypeDto(Long userId, String docType);

    /** Elimina un documento dal database. */
    void deleteDocument(Long documentId);

    /** Aggiorna le note testuali di un documento. */
    Map<String, Object> updateNotes(Long documentId, String notes);

    /** Converte un'entità Document nel DTO mappa per la risposta. */
    Map<String, Object> toDto(Document doc);

    /** Salva un'entità Document nel database. */
    Document saveDocument(Document document);

    /** Recupera tutti i documenti associati a un utente (entità). */
    List<Document> getUserDocuments(Long userId);

    /** Recupera i documenti associati a un utente filtrati per tipo (entità). */
    List<Document> getUserDocumentsByType(Long userId, String docType);

    /** Carica un documento senza validazione del ruolo (operazione base). */
    Document uploadDocument(MultipartFile file, Long clientId, Long uploaderId, String docType);
}