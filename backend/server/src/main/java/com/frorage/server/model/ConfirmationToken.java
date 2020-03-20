package com.frorage.server.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "confirmation_token")
public class ConfirmationToken {
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_VERIFIED = "VERIFIED";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String status;

    @Column(name = "expiration_date_time")
    private LocalDateTime expirationDateTime;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private User user;

    public ConfirmationToken(){
        this.token = UUID.randomUUID().toString().substring(20,25);
        this.expirationDateTime = LocalDateTime.now().plusHours(1);
        this.status = STATUS_PENDING;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpirationDateTime() {
        return expirationDateTime;
    }

    public void setExpirationDateTime(LocalDateTime expiredDateTime) {
        this.expirationDateTime = expiredDateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
