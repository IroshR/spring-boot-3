package com.iroshnk.nftraffle.service.impl.validator;

import com.iroshnk.nftraffle.entity.User;
import com.iroshnk.nftraffle.entity.UserLogin;
import com.iroshnk.nftraffle.entity.UserStatus;
import com.iroshnk.nftraffle.payload.UserRegistrationReq;
import com.iroshnk.nftraffle.repository.UserLoginRepository;
import com.iroshnk.nftraffle.repository.UserRepository;
import com.iroshnk.nftraffle.util.ResponseDetails;
import com.iroshnk.nftraffle.util.exception.DataNotAcceptableException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserValidator {

    private final UserRepository userRepository;
    private final UserLoginRepository userLoginRepository;

    public UserValidator(UserRepository userRepository,
                         UserLoginRepository userLoginRepository) {
        this.userRepository = userRepository;
        this.userLoginRepository = userLoginRepository;
    }

    public void validateCreateUser(UserRegistrationReq userRegistrationReq) {
        if (userRegistrationReq.getEmail().trim().equals("") ||
                userRegistrationReq.getFirstName().trim().equals("") ||
                userRegistrationReq.getLastName().equals("") ||
                userRegistrationReq.getPrimaryPhone().trim().equals("") ||
                userRegistrationReq.getPassword().trim().equals("")) {
            throw new DataNotAcceptableException(ResponseDetails.E1000);
        }
        Optional<User> userOptional = userRepository.findByEmail(userRegistrationReq.getEmail());
        if (userOptional.isPresent()) {
            throw new DataNotAcceptableException(ResponseDetails.E1006);
        }
    }

    public UserLogin getUserLoginIfEditable(Long userId) {
        var userLogin = userLoginRepository.findByUserUserId(userId);
        if (userLogin == null) {
            throw new DataNotAcceptableException(ResponseDetails.E1008);
        }
        if (!(userLogin.getUserStatus().equals(UserStatus.ACTIVE) || userLogin.getUserStatus().equals(UserStatus.INACTIVE))) {
            throw new DataNotAcceptableException(ResponseDetails.E1009);
        }

        return userLogin;
    }

    public User getUserIfUpdatable(UserRegistrationReq userRegistrationReq) {
        Optional<User> userOptional = userRepository.findById(userRegistrationReq.getUserId());
        if (userOptional.isEmpty()) {
            throw new DataNotAcceptableException(ResponseDetails.E1008);
        }
        return userOptional.get();
    }
}
