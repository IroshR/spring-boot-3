package com.iroshnk.nftraffle.service.impl;

import com.iroshnk.nftraffle.entity.*;
import com.iroshnk.nftraffle.payload.PaginationResponse;
import com.iroshnk.nftraffle.payload.ResetPasswordReq;
import com.iroshnk.nftraffle.payload.UserRegistrationReq;
import com.iroshnk.nftraffle.payload.UserRes;
import com.iroshnk.nftraffle.repository.*;
import com.iroshnk.nftraffle.service.EntitlementService;
import com.iroshnk.nftraffle.service.UserService;
import com.iroshnk.nftraffle.service.impl.validator.UserValidator;
import com.iroshnk.nftraffle.util.ResponseDetails;
import com.iroshnk.nftraffle.util.exception.DataNotAcceptableException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service(value = "userService")
//@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    private final Argon2PasswordEncoder bcryptEncoder;
    private final UserLoginRepository userLoginRepository;
    private final EntitlementService entitlementService;
    private final UserSessionRepository userSessionRepository;
    private final UserHasGroupRepository userHasGroupRepository;
    private final GroupRepository groupRepository;
    private final UserValidator userValidator;
    private final UserRepository userRepository;

    public UserServiceImpl(Argon2PasswordEncoder bcryptEncoder,
                           UserLoginRepository userLoginRepository,
                           EntitlementService entitlementService,
                           UserSessionRepository userSessionRepository,
                           UserHasGroupRepository userHasGroupRepository,
                           GroupRepository groupRepository,
                           UserValidator userValidator,
                           UserRepository userRepository) {
        this.bcryptEncoder = bcryptEncoder;
        this.userLoginRepository = userLoginRepository;
        this.entitlementService = entitlementService;
        this.userSessionRepository = userSessionRepository;
        this.userHasGroupRepository = userHasGroupRepository;
        this.groupRepository = groupRepository;
        this.userValidator = userValidator;
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        var userLogin = userLoginRepository.findByUserName(username);
        if (userLogin.isEmpty()) {
            throw new DataNotAcceptableException(ResponseDetails.E1005);
        }
        return new org.springframework.security.core.userdetails.User(
                userLogin.get().getUserName(),
                userLogin.get().getPassword(),
                getAuthority(userLogin.get().getUser())
        );
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<String> entitlements = entitlementService.getRolesByUser(user.getUserId());
        return entitlements
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public User save(UserRegistrationReq userRegistrationReq) {
        userValidator.validateCreateUser(userRegistrationReq);
        var userLogin = new UserLogin();
        userLogin.setUserName(userRegistrationReq.getEmail());
        userLogin.setPassword(bcryptEncoder.encode(userRegistrationReq.getPassword()));
        userLogin.setNeedToChangePassword(true);
        userLogin.setPasswordLastUpdateTime(LocalDateTime.now());
        userLogin.setUserStatus(UserStatus.ACTIVE);
        var user = new User();
        user.setEmail(userRegistrationReq.getEmail());
        user.setFirstName(userRegistrationReq.getFirstName());
        user.setLastName(userRegistrationReq.getLastName());
        user.setPrimaryPhone(userRegistrationReq.getPrimaryPhone());
        user.setSecondaryPhone(userRegistrationReq.getSecondaryPhone());
        user.setBusinessTitle(userRegistrationReq.getBusinessTitle());
        userLogin.setUser(user);
        var savedUser = userLoginRepository.save(userLogin).getUser();
        List<Group> groups = groupRepository.findByGroupIdIn(userRegistrationReq.getUserGroups());
        List<UserHasGroup> userHasGroups = new ArrayList<>();
        groups.forEach(group -> {
            var userHasGroup = new UserHasGroup(group, savedUser);
            userHasGroups.add(userHasGroup);
        });
        userHasGroupRepository.saveAll(userHasGroups);
        return savedUser;
    }

    @Override
    public Optional<User> getUserByUsername(String userName) {
        return Optional.of(getUserLoginByUsername(userName).getUser());
    }

    @Override
    public UserLogin getUserLoginByUsername(String userName) {
        Optional<UserLogin> userLoginOptional = userLoginRepository.findByUserName(userName);
        if (userLoginOptional.isEmpty()) {
            throw new DataNotAcceptableException(ResponseDetails.E1005);
        }
        return userLoginOptional.get();
    }

    @Override
    public void saveSession(String token, UserLogin userLogin) {
        var userSession = new UserSession(userLogin, token);
        userSessionRepository.save(userSession);
    }

    @Override
    public boolean userSessionHasToken(String username, String token) {
        var userSession = userSessionRepository.getUserSessionByTokenAndUserLoginUserName(token.trim(), username);

        return userSession != null;
    }

    @Override
    public void updateToken(String username, String oldToken, String newToken) {
        var userSessions = userSessionRepository.getUserSessionByTokenAndUserLoginUserName(oldToken, username);
        userSessions.setToken(newToken);
        userSessionRepository.save(userSessions);
    }

    @Override
    public User resetPassword(ResetPasswordReq resetPassword) {
        var userLogin = userLoginRepository.findByUserUserId(resetPassword.getUserId());

        if (bcryptEncoder.matches(resetPassword.getOldPassword(), userLogin.getPassword())) {
            userLogin.setPassword(bcryptEncoder.encode(resetPassword.getNewPassword()));
            userLogin.setNeedToChangePassword(false);
            userLogin.setPasswordLastUpdateTime(LocalDateTime.now());
            userLogin = userLoginRepository.save(userLogin);
            return userLogin.getUser();
        }
        return null;
    }

    @Override
    public List<Group> getGroups() {
        return groupRepository.findAll();
    }

    @Override
    public List<Entitlement> getEntitlements() {
        return entitlementService.getEntitlements();
    }

    @Override
    public PaginationResponse<List<UserRes>> getUsers(Integer page, Integer size) {
        // TODO need to check user state If the system is using other than active or inactive user state
        Page<UserLogin> userPage = userLoginRepository.findAll(PageRequest.of(page, size, Sort.by("userStatus").and(Sort.by("userName"))));
        List<UserRes> userResList = userPage.stream().map(this::mapToUser).collect(Collectors.toList());
        return new PaginationResponse<>(userResList, userPage.getTotalElements(), page, userResList.size());
    }

    @Override
    public UserRes getUserById(Long userId) {
        var user = userValidator.getUserLoginIfEditable(userId);
        return mapToUser(user);
    }

    @Override
    public User updateUser(UserRegistrationReq userRegistrationReq) {
        var user = userValidator.getUserIfUpdatable(userRegistrationReq);
        user.setEmail(userRegistrationReq.getEmail());
        user.setFirstName(userRegistrationReq.getFirstName());
        user.setLastName(userRegistrationReq.getLastName());
        user.setPrimaryPhone(userRegistrationReq.getPrimaryPhone());
        user.setSecondaryPhone(userRegistrationReq.getSecondaryPhone());
        user.setBusinessTitle(userRegistrationReq.getBusinessTitle());
        var userLogin = user.getUserLogin();
        userLogin.setUser(user);
        userLogin.setUserName(userRegistrationReq.getEmail());
        userLogin.setUserStatus(userRegistrationReq.getUserStatus());
        var savedUserLogin = userLoginRepository.save(userLogin);
        List<Group> groups = groupRepository.findByGroupIdIn(userRegistrationReq.getUserGroups());
        List<UserHasGroup> userHasGroups = new ArrayList<>();
        userHasGroupRepository.deleteByUserUserId(savedUserLogin.getUser().getUserId());
        groups.forEach(group -> {
            var userHasGroup = new UserHasGroup(group, savedUserLogin.getUser());
            userHasGroups.add(userHasGroup);
        });
        userHasGroupRepository.saveAll(userHasGroups);
        return savedUserLogin.getUser();
    }


    private UserRes mapToUser(UserLogin userLogin) {
        var userRes = new UserRes();
        var user = userLogin.getUser();
        userRes.setUserId(user.getUserId());
        userRes.setEmail(user.getEmail());
        userRes.setFirstName(user.getFirstName());
        userRes.setLastName(user.getLastName());
        userRes.setPrimaryPhoneNumber(user.getPrimaryPhone());
        userRes.setSecondaryPhoneNumber(user.getSecondaryPhone());
        userRes.setBusinessTitle(user.getBusinessTitle());
        List<UserHasGroup> userHasGroups = userHasGroupRepository.findUserHasGroupByUserUserId(user.getUserId());
        userRes.setGroups(userHasGroups.stream().map(UserHasGroup::getGroup).collect(Collectors.toList()));
        userRes.setUserStatus(userLogin.getUserStatus());
        return userRes;
    }

}
