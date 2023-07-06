package com.iroshnk.nftraffle.util.exception;

import org.springframework.http.HttpStatus;

public class StandardException extends RuntimeException {

    private final String code;
    private final String description;
    private final HttpStatus httpStatus;

    protected StandardException(String code,
                                String description,
                                HttpStatus httpStatus) {
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    protected StandardException(String code,
                                String description,
                                String message,
                                HttpStatus httpStatus) {
        super(message);
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    protected StandardException(String code,
                                String description,
                                String message,
                                Throwable cause,
                                HttpStatus httpStatus) {
        super(message, cause);
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    protected StandardException(String code,
                                String description,
                                Throwable cause,
                                HttpStatus httpStatus) {
        super(cause);
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    protected StandardException(String code,
                                String description,
                                String message,
                                Throwable cause,
                                boolean enableSuppression,
                                boolean writableStackTrace,
                                HttpStatus httpStatus) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.description = description;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
