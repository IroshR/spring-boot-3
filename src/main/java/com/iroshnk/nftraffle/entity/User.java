package com.iroshnk.nftraffle.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String primaryPhone;
    private String secondaryPhone;
    private boolean isEmailConfirmed = false;
    private String businessTitle;
    @JsonIgnore
    @OneToOne(mappedBy = "user")
    private UserLogin userLogin;

}