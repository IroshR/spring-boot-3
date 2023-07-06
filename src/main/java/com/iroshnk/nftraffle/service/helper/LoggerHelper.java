package com.iroshnk.nftraffle.service.helper;

import com.iroshnk.nftraffle.config.security.JwtTokenHandler;
import com.iroshnk.nftraffle.entity.UserAction;
import com.iroshnk.nftraffle.service.UserActionService;
import com.iroshnk.nftraffle.service.UserService;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class LoggerHelper {

    private static final String CORRELATION_ID = "correlationId";

    private final UserService userService;
    private final JwtTokenHandler jwtTokenHandler;
    private final UserActionService userActionService;

    public LoggerHelper(UserService userService,
                        JwtTokenHandler jwtTokenHandler,
                        UserActionService userActionService) {
        this.userService = userService;
        this.jwtTokenHandler = jwtTokenHandler;
        this.userActionService = userActionService;
    }

    public UserAction createUserAction(Long uuid, String event, Date requestTime, String token) {
        String username = jwtTokenHandler.getUsernameFromToken(token);
        var user = userService.getUserByUsername(username);
        return new UserAction(uuid, event, requestTime, null, false, user.get());
    }

    public void saveAuditRecord(String token, String event) {
        Long uuid = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        MDC.put(CORRELATION_ID, uuid.toString());
        var userAction = createUserAction(uuid, event, new Date(), token.replace("Bearer", "").trim());
        userActionService.save(userAction);
    }

    public void updateAuditRecord(boolean isSuccess) {
        Long uuid = Long.parseLong(MDC.get(CORRELATION_ID));
        userActionService.update(uuid, new Date(), isSuccess);
    }
}
