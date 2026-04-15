package com.project.tesi.service.impl;

import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import com.project.tesi.service.VideoConferenceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JitsiVideoConferenceServiceImpl implements VideoConferenceService {

    @Value("${jitsi.base-url:https://meet.jit.si/Kore_Consulto_}")
    private String jitsiBaseUrl;

    @Override
    public String generateMeetingLink(User user, User professional, Slot slot) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s%d_%d_%s", jitsiBaseUrl, user.getId(), professional.getId(), uniqueId);
    }
}
