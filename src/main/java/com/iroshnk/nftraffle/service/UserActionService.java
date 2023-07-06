package com.iroshnk.nftraffle.service;

import com.iroshnk.nftraffle.entity.UserAction;

import java.util.Date;

public interface UserActionService {

    void save(UserAction userAction);

    void update(Long id, Date responseDate, boolean isSuccess);
}
