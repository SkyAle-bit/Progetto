package com.project.tesi.facade.impl;

import com.project.tesi.dto.response.ActivityFeedItemResponse;
import com.project.tesi.facade.IActivityFeedFacade;
import com.project.tesi.service.ActivityFeedService;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ActivityFeedFacadeImpl implements IActivityFeedFacade {

    private final ActivityFeedService activityFeedService;

    public ActivityFeedFacadeImpl(ActivityFeedService activityFeedService) {
        this.activityFeedService = activityFeedService;
    }

    @Override
    public List<ActivityFeedItemResponse> getActivityFeed(Long userId, int days, int limit) {
        return activityFeedService.getActivityFeed(userId, days, limit);
    }

    @Override
    public void logDocumentUploaded(Long clientId, Long uploaderId, String type) {
        activityFeedService.logDocumentUploaded(clientId, uploaderId, type);
    }
}
