package com.iroshnk.nftraffle.service;

import com.iroshnk.nftraffle.entity.Entitlement;
import com.iroshnk.nftraffle.entity.Group;
import com.iroshnk.nftraffle.entity.User;
import com.iroshnk.nftraffle.entity.UserLogin;
import com.iroshnk.nftraffle.payload.PaginationResponse;
import com.iroshnk.nftraffle.payload.ResetPasswordReq;
import com.iroshnk.nftraffle.payload.UserRegistrationReq;
import com.iroshnk.nftraffle.payload.UserRes;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(UserRegistrationReq user);

    Optional<User> getUserByUsername(String userName);

    UserLogin getUserLoginByUsername(String userName);

    void saveSession(String token, UserLogin userLogin);

    boolean userSessionHasToken(String username, String token);

    void updateToken(String username, String authToken, String token);

    User resetPassword(ResetPasswordReq resetPassword);

    List<Group> getGroups();

    List<Entitlement> getEntitlements();

    PaginationResponse<List<UserRes>> getUsers(Integer page, Integer size);

    UserRes getUserById(Long userId);

    User updateUser(UserRegistrationReq userRegistrationReq);
}
