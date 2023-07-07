package com.iroshnk.nftraffle.controller;

import com.iroshnk.nftraffle.config.security.JwtTokenHandler;
import com.iroshnk.nftraffle.entity.*;
import com.iroshnk.nftraffle.exception.TokenRefreshException;
import com.iroshnk.nftraffle.log.AuditLog;
import com.iroshnk.nftraffle.payload.*;
import com.iroshnk.nftraffle.service.GroupService;
import com.iroshnk.nftraffle.service.RefreshTokenService;
import com.iroshnk.nftraffle.service.UserActionService;
import com.iroshnk.nftraffle.service.UserService;
import com.iroshnk.nftraffle.service.helper.LoggerHelper;
import com.iroshnk.nftraffle.util.ResponseDetails;
import com.iroshnk.nftraffle.util.UserEvent;
import com.iroshnk.nftraffle.util.exception.DataNotAcceptableException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Tag(name = "User Management", description = "User Management APIs")
@RestController
@RequestMapping("/admin/api")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenHandler jwtTokenUtil;
    private final UserService userService;
    private final LoggerHelper loggerHelper;
    private final GroupService groupService;
    private final RefreshTokenService refreshTokenService;

    private final MessageSource messageSource;

    public UserController(AuthenticationManager authenticationManager,
                          JwtTokenHandler jwtTokenUtil,
                          UserService userService,
                          UserActionService userActionService,
                          LoggerHelper loggerHelper,
                          GroupService groupService,
                          RefreshTokenService refreshTokenService,
                          MessageSource messageSource) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.loggerHelper = loggerHelper;
        this.groupService = groupService;
        this.refreshTokenService = refreshTokenService;
        this.messageSource = messageSource;
    }

    @Operation(
            summary = "Login",
            description = "Login with username/password"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = UserProfile.class), mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema())})})
    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<UserProfile>> generateToken(@RequestHeader(value = "Accept-Language", required = false) String locale,
                                                               @RequestBody LoginReq loginUser, HttpServletResponse response) {
        try {

            var user = userService.getUserByUsername(loginUser.getUsername());
            if (user.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            if (user.get().getUserLogin().getUserStatus().equals(UserStatus.INACTIVE)) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(new Response<>("", "Your account is Deactivated, Please contact system administrator to activate your account"));
            }
            final var authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginUser.getUsername(),
                            loginUser.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            final String token = jwtTokenUtil.generateToken(authentication);
            var userLogin = userService.getUserLoginByUsername(loginUser.getUsername());
            userService.saveSession(token, userLogin);

            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.get().getUserId());

            response.setHeader("token", token);
            response.setHeader("refresh-token", refreshToken.getToken());
            return ResponseEntity.ok(new Response<>(mapToUserProfile(userLogin.getUser())));
        } catch (DataNotAcceptableException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new Response<>(e.getCode(), e.getDescription()));
        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    //.body(new Response<>(ResponseDetails.E1005.getCode(), ResponseDetails.E1005.getDescription()));
                    .body(new Response<>(ResponseDetails.E1005.getCode(), messageSource.getMessage("greeting", null, new Locale(locale))));
        }
    }

    private UserProfile mapToUserProfile(User user) {
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(user.getUserId());
        userProfile.setEmail(user.getEmail());
        userProfile.setBusinessTitle(user.getBusinessTitle());
        userProfile.setFirstName(user.getFirstName());
        userProfile.setLastName(user.getLastName());
        userProfile.setPrimaryPhone(user.getPrimaryPhone());
        userProfile.setSecondaryPhone(user.getSecondaryPhone());
        userProfile.setEmailConfirmed(user.isEmailConfirmed());
        userProfile.setNeedToChangePassword(user.getUserLogin().isNeedToChangePassword());
        return userProfile;
    }

    @PostMapping(value = "/refresh-token", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshReq request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = jwtTokenUtil.generateTokenFromUsername(user.getUserLogin().getUserName());
                    return ResponseEntity.ok(new TokenRefreshRes(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @Secured({"ROLE_ADD_USER"})
    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Response<User>> saveUser(@RequestBody UserRegistrationReq userRegistrationReq,
                                                   @RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.CREATE_USER, "Start Add User Request");
            loggerHelper.saveAuditRecord(token, UserEvent.CREATE_USER);
            var save = userService.save(userRegistrationReq);
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(save));
        } catch (DataNotAcceptableException e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.ok(new Response<>(e.getCode(), e.getDescription()));
        }
    }

    @Secured({"ROLE_LOGIN"})
    @PostMapping(value = "/reset-password", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<User> resetPassword(@RequestBody ResetPasswordReq resetPassword,
                                              @RequestHeader("Authorization") String token) {

        AuditLog.info(UserEvent.RESET_PASSWORD, "Start Reset password");
        loggerHelper.saveAuditRecord(token, UserEvent.RESET_PASSWORD);
        var save = userService.resetPassword(resetPassword);
        if (save != null) {
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(save);
        } else {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_VIEW_GROUP_LIST"})
    @GetMapping(value = "/groups")
    public ResponseEntity<Response<List<Group>>> getGroups(@RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.GET_GROUPS, "Start Get Groups");
            loggerHelper.saveAuditRecord(token, UserEvent.GET_GROUPS);
            List<Group> groups = userService.getGroups();
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(groups));
        } catch (Exception e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_VIEW_ENTITLEMENTS"})
    @GetMapping(value = "/entitlements")
    public ResponseEntity<Response<List<Entitlement>>> getEntitlements(@RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.VIEW_ENTITLEMENTS, "Start Get Groups");
            loggerHelper.saveAuditRecord(token, UserEvent.VIEW_ENTITLEMENTS);
            List<Entitlement> entitlements = userService.getEntitlements();
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(entitlements));
        } catch (Exception e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_ADD_GROUP"})
    @PostMapping(value = "/groups")
    public ResponseEntity<Response<GroupRes>> createGroup(@RequestBody GroupReq groupReq,
                                                          @RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.ADD_GROUP, "Start create Groups");
            loggerHelper.saveAuditRecord(token, UserEvent.ADD_GROUP);
            var group = groupService.createGroup(groupReq);
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(group));
        } catch (DataNotAcceptableException e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.ok(new Response<>(e.getCode(), e.getDescription()));
        } catch (Exception e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_UPDATE_GROUP"})
    @PutMapping(value = "/groups")
    public ResponseEntity<Response<GroupRes>> updateGroup(@RequestBody GroupReq groupReq,
                                                          @RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.ADD_GROUP, "Start update Groups");
            loggerHelper.saveAuditRecord(token, UserEvent.ADD_GROUP);
            var group = groupService.updateGroup(groupReq);
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(group));
        } catch (DataNotAcceptableException e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.ok(new Response<>(e.getCode(), e.getDescription()));
        } catch (Exception e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_UPDATE_USER"})
    @PutMapping(value = "/users")
    public ResponseEntity<Response<User>> updateUser(@RequestBody UserRegistrationReq userRegistrationReq,
                                                     @RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.UPDATE_USER, "Start update user request");
            loggerHelper.saveAuditRecord(token, UserEvent.UPDATE_USER);
            var user = userService.updateUser(userRegistrationReq);
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(user));
        } catch (DataNotAcceptableException e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.ok(new Response<>(e.getCode(), e.getDescription()));
        } catch (Exception e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_VIEW_USER_LIST"})
    @GetMapping(value = "/users")
    public ResponseEntity<PaginationResponse<List<UserRes>>> getUsers(
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.VIEW_USERS, "Start view users");
            loggerHelper.saveAuditRecord(token, UserEvent.VIEW_USERS);
            PaginationResponse<List<UserRes>> users = userService.getUsers(page, size);
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(users);
        } catch (DataNotAcceptableException e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.ok(new PaginationResponse<>(e.getCode(), e.getDescription()));
        } catch (Exception e) {
            LOGGER.error("Some thing went wrong while getting user list [{}]", e);
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_VIEW_USER_DETAILS"})
    @GetMapping(value = "/users/{userId}")
    public ResponseEntity<Response<UserRes>> getUserById(@PathVariable Long userId,
                                                         @RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.VIEW_USER_DETAILS, "Start view user details request");
            loggerHelper.saveAuditRecord(token, UserEvent.VIEW_USER_DETAILS);
            var users = userService.getUserById(userId);
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(users));
        } catch (DataNotAcceptableException e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.ok(new Response<>(e.getCode(), e.getDescription()));
        } catch (Exception e) {
            LOGGER.error("Some thing went wrong while getting user Details [{}]", e);
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

    @Secured({"ROLE_VIEW_GROUP_DETAILS"})
    @GetMapping(value = "/groups/{groupId}")
    public ResponseEntity<Response<GroupRes>> getGroupById(@PathVariable Long groupId,
                                                           @RequestHeader("Authorization") String token) {
        try {
            AuditLog.info(UserEvent.VIEW_GROUP_DETAILS, "Start view group details request");
            loggerHelper.saveAuditRecord(token, UserEvent.VIEW_GROUP_DETAILS);
            var group = groupService.getGroupById(groupId);
            loggerHelper.updateAuditRecord(true);
            return ResponseEntity.ok(new Response<>(group));
        } catch (Exception e) {
            loggerHelper.updateAuditRecord(false);
            return ResponseEntity.badRequest().build();
        }
    }

}
