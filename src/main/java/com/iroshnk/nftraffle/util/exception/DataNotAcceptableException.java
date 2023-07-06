package com.iroshnk.nftraffle.util.exception;

import com.iroshnk.nftraffle.util.ResponseDetails;
import org.springframework.http.HttpStatus;

public class DataNotAcceptableException extends StandardException {

    public DataNotAcceptableException(ResponseDetails responseDetails) {
        super(responseDetails.getCode(), responseDetails.getDescription(), HttpStatus.OK);
    }

    public DataNotAcceptableException(ResponseDetails responseDetails, String msg) {
        super(responseDetails.getCode(), responseDetails.getDescription(), msg, HttpStatus.OK);
    }

    public DataNotAcceptableException(ResponseDetails responseDetails, String msg, Throwable cause) {
        super(responseDetails.getCode(), responseDetails.getDescription(), msg, cause, HttpStatus.OK);
    }

}
