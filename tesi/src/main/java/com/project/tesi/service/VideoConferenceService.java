package com.project.tesi.service;

import org.springframework.validation.annotation.Validated;

import com.project.tesi.model.User;
import com.project.tesi.model.Slot;

@Validated
public interface VideoConferenceService {
    String generateMeetingLink(User user, User professional, Slot slot);
}

