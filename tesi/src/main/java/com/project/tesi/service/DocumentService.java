package com.project.tesi.service;

import com.project.tesi.model.Document;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface DocumentService {
    // Carica un file
    Document uploadDocument(MultipartFile file, Long clientId, Long uploaderId, String docType) throws IOException;

    // Scarica un file (restituisce il byte array o resource)
    byte[] downloadDocument(Long documentId) throws IOException;

    // Lista documenti di un utente
    List<Document> getUserDocuments(Long userId);
}