package com.project.tesi.controller;

import com.project.tesi.model.Document;
import com.project.tesi.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("clientId") Long clientId,
            @RequestParam("uploaderId") Long uploaderId,
            @RequestParam("type") String type) throws IOException {

        Document doc = documentService.uploadDocument(file, clientId, uploaderId, type);
        return ResponseEntity.ok(toDto(doc));
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable Long id) throws IOException {
        Document doc = documentService.getDocumentById(id);
        byte[] data = documentService.downloadDocument(id);

        String contentType = doc.getContentType() != null ? doc.getContentType() : "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(data);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserDocuments(@PathVariable Long userId) {
        List<Document> docs = documentService.getUserDocuments(userId);
        return ResponseEntity.ok(docs.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}/type/{type}")
    public ResponseEntity<List<Map<String, Object>>> getUserDocumentsByType(
            @PathVariable Long userId, @PathVariable String type) {
        List<Document> docs = documentService.getUserDocumentsByType(userId, type);
        return ResponseEntity.ok(docs.stream().map(this::toDto).collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) throws IOException {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toDto(Document doc) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", doc.getId());
        map.put("fileName", doc.getFileName());
        map.put("contentType", doc.getContentType());
        map.put("type", doc.getType().name());
        map.put("uploadDate", doc.getUploadDate().toString());
        map.put("ownerId", doc.getOwner().getId());
        map.put("ownerName", doc.getOwner().getFirstName() + " " + doc.getOwner().getLastName());
        map.put("uploadedById", doc.getUploadedBy().getId());
        map.put("uploaderName", doc.getUploadedBy().getFirstName() + " " + doc.getUploadedBy().getLastName());
        return map;
    }
}