package com.project.tesi.service.impl;

import com.project.tesi.enums.DocumentType;
import com.project.tesi.model.Document;
import com.project.tesi.model.User;
import com.project.tesi.repository.DocumentRepository; // Crea anche questo repo!
import com.project.tesi.repository.UserRepository;
import com.project.tesi.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    @Transactional
    public Document uploadDocument(MultipartFile file, Long clientId, Long uploaderId, String docTypeStr) throws IOException {
        User client = userRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente non trovato"));

        User uploader = userRepository.findById(uploaderId)
                .orElseThrow(() -> new RuntimeException("Uploader non trovato"));

        // 1. Salva file su disco
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Impossibile creare la directory di upload: " + uploadDir);
            }
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || !originalName.contains(".")) {
            throw new IllegalArgumentException("Nome file non valido o estensione mancante");
        }
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String storageName = UUID.randomUUID() + extension; // Nome univoco
        Path destinationPath = Paths.get(uploadDir, storageName);

        Files.copy(file.getInputStream(), destinationPath);

        // 2. Salva metadati su DB
        Document doc = Document.builder()
                .fileName(originalName)
                .filePath(destinationPath.toString())
                .contentType(file.getContentType())
                .type(DocumentType.valueOf(docTypeStr))
                .owner(client)
                .uploadedBy(uploader)
                .uploadDate(LocalDateTime.now())
                .build();

        return documentRepository.save(doc);
    }

    @Override
    public byte[] downloadDocument(Long documentId) throws IOException {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Documento non trovato"));

        Path path = Paths.get(doc.getFilePath());
        return Files.readAllBytes(path);
    }

    @Override
    public List<Document> getUserDocuments(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));
        return documentRepository.findByOwner(user);
    }
}