package com.iroshnk.nftraffle.service.impl;

import com.iroshnk.nftraffle.entity.UserAction;
import com.iroshnk.nftraffle.repository.UserActionRepository;
import com.iroshnk.nftraffle.service.UserActionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Transactional
public class UserActionServiceImpl implements UserActionService {

    private final UserActionRepository userActionRepository;

    public UserActionServiceImpl(UserActionRepository userActionRepository) {
        this.userActionRepository = userActionRepository;
    }

    @Override
    public void save(UserAction userAction) {
        userActionRepository.save(userAction);
    }

    @Override
    public void update(Long id, Date responseDate, boolean isSuccess) {
        userActionRepository.findById(id).ifPresent(userAction -> {
            userAction.setResponseTime(responseDate);
            userAction.setSuccess(isSuccess);
            userActionRepository.save(userAction);
        });
    }
}
