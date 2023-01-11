package com.example.securityjwt.model.entity;


import com.example.securityjwt.model.UserRole;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Getter
public class UserEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) Integer id;
    @Setter @Column(nullable = false) String username;
    @Setter @Column(nullable = false) private String password;
    @Column(nullable = false) @Enumerated(EnumType.STRING) private UserRole role = UserRole.ROLE_USER;
    @Column(name = "register_at", updatable = false) private Timestamp registeredAt;

    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    protected UserEntity() {}

    private UserEntity(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public static UserEntity of(String username, String password) {
        return new UserEntity(username, password);
    }
}
