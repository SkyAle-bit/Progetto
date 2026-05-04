package com.project.tesi.facade;
import com.project.tesi.model.Document;
import com.project.tesi.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
@Component
public class DocumentFacade implements IDocumentFacade {
    private final DocumentService documentService;
    public DocumentFacade(DocumentService documentService) {
        this.documentService = documentService;
    }
    public Map<String, Object> uploadDocumentWithValidation(MultipartFile file, Long clientId, Long uploaderId, String type) {
        return documentService.uploadDocumentWithValidation(file, clientId, uploaderId, type);
    }
    public Document getDocumentById(Long id) {
        return documentService.getDocumentById(id);
    }
    public byte[] downloadDocument(Long id) {
        return documentService.downloadDocument(id);
    }
    public List<Map<String, Object>> getUserDocumentsDto(Long userId) {
        return documentService.getUserDocumentsDto(userId);
    }
    public List<Map<String, Object>> getUserDocumentsByTypeDto(Long userId, String type) {
        return documentService.getUserDocumentsByTypeDto(userId, type);
    }
    public void deleteDocument(Long id) {
        documentService.deleteDocument(id);
    }
    public Map<String, Object> updateNotes(Long id, String notes) {
        return documentService.updateNotes(id, notes);
    }
}
