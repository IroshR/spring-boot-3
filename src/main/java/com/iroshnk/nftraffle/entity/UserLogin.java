package com.iroshnk.nftraffle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Set;

@ToString(exclude = "userSessions")
@EqualsAndHashCode(exclude = "userSessions")
@Data
@Entity
@Table
public class UserLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginId;
    private String userName;
    private String password;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private User user;
    private Integer failAttempts;
    private boolean needToChangePassword = false;
    @Enumerated(EnumType.STRING)
    private UserStatus userStatus = UserStatus.ACTIVE;

    @JsonIgnore
    @OneToMany(mappedBy = "userLogin", orphanRemoval = true)
    private Set<UserSession> userSessions;

    private LocalDateTime passwordLastUpdateTime;
}
