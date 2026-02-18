package com.project.tesi.service;

import com.project.tesi.dto.request.PlanRequest;
import com.project.tesi.dto.response.SubscriptionResponse;

public interface SubscriptionService {
    SubscriptionResponse activateSubscription(PlanRequest request);
    SubscriptionResponse getSubscriptionStatus(Long userId);
}