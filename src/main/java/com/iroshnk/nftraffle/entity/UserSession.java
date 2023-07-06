package com.iroshnk.nftraffle.entity;

import jakarta.persistence.*;
import lombok.*;

@ToString(exclude = "userLogin")
@EqualsAndHashCode(exclude = "userLogin")
@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;
    @ManyToOne
    @JoinColumn(name = "login_id", nullable = false)
    private UserLogin userLogin;
    @Column(length = 10000)
    private String token;

    public UserSession(UserLogin userLogin, String token) {
        this.userLogin = userLogin;
        this.token = token;
    }
}
