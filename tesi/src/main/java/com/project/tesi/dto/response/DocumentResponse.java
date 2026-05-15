package com.project.tesi.dto.response;

public record DocumentResponse(
        Long id,
        String fileName,
        String contentType,
        String type,
        String uploadDate,
        Long ownerId,
        String ownerName,
        Long uploadedById,
        String uploaderName,
        String notes
) {}
