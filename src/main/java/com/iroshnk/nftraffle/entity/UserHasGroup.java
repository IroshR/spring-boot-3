package com.iroshnk.nftraffle.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table
public class UserHasGroup {

    @EmbeddedId
    private UserHasGroupId userHasGroupId = new UserHasGroupId();

    @ManyToOne
    @MapsId("groupId")
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    public UserHasGroup(Group group, User user) {
        this.group = group;
        this.user = user;
    }
}
