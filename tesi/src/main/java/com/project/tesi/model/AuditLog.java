package com.project.tesi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;

    @Column(name = "user_identity", length = 255)
    private String userIdentity;

    @Column(name = "http_method", length = 10)
    private String httpMethod;

    @Column(name = "http_path", length = 500)
    private String httpPath;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "http_status")
    private Integer httpStatus;

    @Column(name = "request_body", length = 4000)
    private String requestBody;
}
