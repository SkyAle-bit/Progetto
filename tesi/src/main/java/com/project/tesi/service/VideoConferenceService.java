package com.project.tesi.service;

import com.project.tesi.model.User;
import com.project.tesi.model.Slot;

public interface VideoConferenceService {
    String generateMeetingLink(User user, User professional, Slot slot);
}

