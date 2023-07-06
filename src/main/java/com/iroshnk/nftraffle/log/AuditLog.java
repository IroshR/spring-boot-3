package com.iroshnk.nftraffle.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditLog {

    private static final Logger LOGGER = LoggerFactory.getLogger("com.iroshnk.audit");

    private AuditLog() {
    }

    public static void info(String event, String msg, Object... objects) {
        var logTemplateBuilder = new StringBuilder("{} | {} | ");
        var parameter = new Object[2 + objects.length];
        parameter[0] = event;
        parameter[1] = msg;
        for (var i = 0; i < objects.length; i++) {
            logTemplateBuilder.append("{} | ");
            parameter[2 + i] = objects[i];
        }
        var logTemplate = logTemplateBuilder.toString();
        LOGGER.info(logTemplate, parameter);
    }

    public static void error(String event, String error) {
        LOGGER.error("{} | {} ", event, error);
    }

}
