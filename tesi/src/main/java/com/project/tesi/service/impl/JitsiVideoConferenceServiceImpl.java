package com.project.tesi.service.impl;

import com.project.tesi.model.Slot;
import com.project.tesi.model.User;
import com.project.tesi.service.VideoConferenceService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class JitsiVideoConferenceServiceImpl implements VideoConferenceService {

    @Override
    public String generateMeetingLink(User user, User professional, Slot slot) {
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        return "https://meet.jit.si/Kore_Consulto_" + user.getId() + "_" + professional.getId() + "_" + uniqueId;
    }
}

