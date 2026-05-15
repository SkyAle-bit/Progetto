package com.project.tesi.controller;

import com.project.tesi.dto.response.ActivityFeedItemResponse;
import com.project.tesi.facade.UserFacade;
import com.project.tesi.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint REST per il feed attività dell'utente autenticato.
 */
@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
public class ActivityFeedController {

    private final UserFacade userFacade;

    /** Restituisce il feed delle attività recenti dell'utente autenticato. */
    @GetMapping("/feed")
    public ResponseEntity<List<ActivityFeedItemResponse>> getActivityFeed(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "14") int days,
            @RequestParam(defaultValue = "15") int size) {
        return ResponseEntity.ok(userFacade.getActivityFeed(user.getId(), days, size));
    }
}
