package com.project.tesi.controller;

import com.project.tesi.enums.Role;
import com.project.tesi.model.Plan;
import com.project.tesi.model.Subscription;
import com.project.tesi.model.User;
import com.project.tesi.repository.PlanRepository;
import com.project.tesi.repository.SubscriptionRepository;
import com.project.tesi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<Map<String, Object>> result = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", u.getId());
            map.put("firstName", u.getFirstName());
            map.put("lastName", u.getLastName());
            map.put("email", u.getEmail());
            map.put("role", u.getRole().name());
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/subscriptions")
    public ResponseEntity<List<Map<String, Object>>> getAllSubscriptions() {
        List<Subscription> subs = subscriptionRepository.findAll();
        List<Map<String, Object>> result = subs.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("userId", s.getUser().getId());
            map.put("userName", s.getUser().getFirstName() + " " + s.getUser().getLastName());
            map.put("planName", s.getPlan() != null ? s.getPlan().getName() : "N/A");
            map.put("active", s.isActive());
            map.put("startDate", s.getStartDate() != null ? s.getStartDate().toString() : null);
            map.put("endDate", s.getEndDate() != null ? s.getEndDate().toString() : null);
            map.put("monthlyPrice", s.getPlan() != null ? s.getPlan().getMonthlyInstallmentPrice() : 0);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }
}
