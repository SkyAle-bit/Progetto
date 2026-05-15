package com.project.tesi.dto.request;

public record ProfileUpdateRequest(
        String firstName,
        String lastName,
        String password,
        String profilePicture) {
}
