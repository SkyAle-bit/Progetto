package com.project.tesi.controller;

import com.project.tesi.model.Document;
import com.project.tesi.facade.DocumentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentFacade documentFacade;

    /** Carica un documento validando il ruolo dell'uploader rispetto al tipo di file. */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clientId") Long clientId,
            @RequestParam("uploaderId") Long uploaderId,
            @RequestParam("type") String type) {
        return ResponseEntity.ok(documentFacade.uploadDocumentWithValidation(file, clientId, uploaderId, type));
    }

    /** Scarica il contenuto binario di un documento per la visualizzazione inline nel browser. */
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) {
        Document doc = documentFacade.getDocumentById(id);
        byte[] data = documentFacade.downloadDocument(id);
        String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    /** Restituisce tutti i documenti di un utente (qualsiasi tipo). */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserDocuments(@PathVariable Long userId) {
        return ResponseEntity.ok(documentFacade.getUserDocumentsDto(userId));
    }

    /** Restituisce i documenti di un utente filtrati per tipologia. */
    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<Map<String, Object>>> getUserDocumentsByType(
            @PathVariable Long userId, @PathVariable String type) {
        return ResponseEntity.ok(documentFacade.getUserDocumentsByTypeDto(userId, type));
    }

    /** Elimina un documento dal database e dal filesystem. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentFacade.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    /** Aggiorna le note testuali associate a un documento. */
    @PutMapping("/{id}/notes")
    public ResponseEntity<Map<String, Object>> updateNotes(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(documentFacade.updateNotes(id, body.get("notes")));
    }
}
