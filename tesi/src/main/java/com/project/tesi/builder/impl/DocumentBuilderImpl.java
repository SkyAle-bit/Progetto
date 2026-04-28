package com.project.tesi.builder.impl;

import com.project.tesi.builder.DocumentBuilder;
import com.project.tesi.enums.DocumentType;
import java.time.LocalDateTime;
import com.project.tesi.model.*;


public class DocumentBuilderImpl implements DocumentBuilder {
    private Long id;
    private String fileName;
    private String filePath;
    private String contentType;
    private DocumentType type;
    private User owner;
    private User uploadedBy;
    private LocalDateTime uploadDate;
    private String notes;

    @Override
    public DocumentBuilder id(Long id) {
        this.id = id;
        return this;
    }
    @Override
    public DocumentBuilder fileName(String fileName) {
        this.fileName = fileName;
        return this;
    }
    @Override
    public DocumentBuilder filePath(String filePath) {
        this.filePath = filePath;
        return this;
    }
    @Override
    public DocumentBuilder contentType(String contentType) {
        this.contentType = contentType;
        return this;
    }
    @Override
    public DocumentBuilder type(DocumentType type) {
        this.type = type;
        return this;
    }
    @Override
    public DocumentBuilder owner(User owner) {
        this.owner = owner;
        return this;
    }
    @Override
    public DocumentBuilder uploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
        return this;
    }
    @Override
    public DocumentBuilder uploadDate(LocalDateTime uploadDate) {
        this.uploadDate = uploadDate;
        return this;
    }
    @Override
    public DocumentBuilder notes(String notes) {
        this.notes = notes;
        return this;
    }

    @Override
    public Document build() {
        Document obj = new Document();
        obj.setId(this.id);
        obj.setFileName(this.fileName);
        obj.setFilePath(this.filePath);
        obj.setContentType(this.contentType);
        obj.setType(this.type);
        obj.setOwner(this.owner);
        obj.setUploadedBy(this.uploadedBy);
        obj.setUploadDate(this.uploadDate);
        obj.setNotes(this.notes);
        return obj;
    }
}
