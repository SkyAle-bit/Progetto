package com.project.tesi.repository;

import com.project.tesi.enums.DocumentType;
import com.project.tesi.model.Document;
import com.project.tesi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByOwner(User owner);
    List<Document> findByOwnerAndType(User owner, DocumentType type);
    List<Document> findByOwnerOrderByUploadDateDesc(User owner);
    List<Document> findByOwnerAndTypeOrderByUploadDateDesc(User owner, DocumentType type);

    // Documenti caricati da un professionista dopo una certa data
    @Query("SELECT COUNT(d) FROM Document d WHERE d.uploadedBy = :uploader AND d.uploadDate >= :since")
    int countByUploaderSince(@Param("uploader") User uploader, @Param("since") LocalDateTime since);

    // Trova l'ultimo documento di un certo tipo per un owner
    @Query("SELECT d FROM Document d WHERE d.owner = :owner AND d.type = :type ORDER BY d.uploadDate DESC LIMIT 1")
    Document findLatestByOwnerAndType(@Param("owner") User owner, @Param("type") DocumentType type);

    // Documenti recenti per un cliente (caricati per lui)
    @Query("SELECT d FROM Document d WHERE d.owner = :owner AND d.uploadDate >= :since ORDER BY d.uploadDate DESC")
    List<Document> findRecentByOwner(@Param("owner") User owner, @Param("since") LocalDateTime since);

    // Documenti recenti caricati da un professionista
    @Query("SELECT d FROM Document d WHERE d.uploadedBy = :uploader AND d.uploadDate >= :since ORDER BY d.uploadDate DESC")
    List<Document> findRecentByUploader(@Param("uploader") User uploader, @Param("since") LocalDateTime since);
}