package com.project.tesi.facade;

import com.project.tesi.model.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface IDocumentFacade {
    Map<String, Object> uploadDocumentWithValidation(MultipartFile file, Long clientId, Long uploaderId, String type);
    Document getDocumentById(Long id);
    byte[] downloadDocument(Long id);
    List<Map<String, Object>> getUserDocumentsDto(Long userId);
    List<Map<String, Object>> getUserDocumentsByTypeDto(Long userId, String type);
    void deleteDocument(Long id);
    Map<String, Object> updateNotes(Long id, String notes);
}

