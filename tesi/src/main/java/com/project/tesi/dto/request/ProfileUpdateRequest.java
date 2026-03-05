package com.project.tesi.dto.request;

import lombok.Data;

@Data
public class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String password;
    private String profilePicture;
}
