package com.project.tesi.facade;
import com.project.tesi.model.Document;
import com.project.tesi.service.ActivityFeedService;
import com.project.tesi.service.DocumentService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;
/**
 * Facade per la gestione dei documenti.
 * Oltre a chiamare DocumentService, orchestra l'ActivityFeedService per tracciare 
 * automaticamente il log di upload, togliendo questa logica dal controller.
 */
@Component
public class DocumentFacade implements IDocumentFacade {
    private final DocumentService documentService;
    private final ActivityFeedService activityFeedService;

    public DocumentFacade(DocumentService documentService, ActivityFeedService activityFeedService) {
        this.documentService = documentService;
        this.activityFeedService = activityFeedService;
    }

    public Map<String, Object> uploadDocumentWithValidation(MultipartFile file, Long clientId, Long uploaderId, String type) {
        Map<String, Object> result = documentService.uploadDocumentWithValidation(file, clientId, uploaderId, type);
        // La Facade brilla qui: coordina il salvataggio e logga l'evento tutto insieme

        activityFeedService.logDocumentUploaded(clientId, uploaderId, type);
        return result;
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
