package com.project.tesi.service;

import com.project.tesi.model.Document;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface DocumentService {
    Document uploadDocument(MultipartFile file, Long clientId, Long uploaderId, String docType) throws IOException;
    byte[] downloadDocument(Long documentId) throws IOException;
    Document getDocumentById(Long documentId);
    List<Document> getUserDocuments(Long userId);
    List<Document> getUserDocumentsByType(Long userId, String docType);
    void deleteDocument(Long documentId) throws IOException;
}