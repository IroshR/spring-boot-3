package com.iroshnk.nftraffle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class UserAction {

    @Id
    @Column(name = "user_action_id")
    private Long userActionId;
    private String event;
    private Date requestTime;
    private Date responseTime;
    private boolean isSuccess;
    @ManyToOne
    @JoinColumn
    private User user;

}
