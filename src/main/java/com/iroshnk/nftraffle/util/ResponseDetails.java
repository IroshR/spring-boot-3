package com.iroshnk.nftraffle.util;

public final class ResponseDetails {

    public static final ResponseDetails E1000 = new ResponseDetails("E1000", "Data can not be accepted.");
    public static final ResponseDetails E1005 = new ResponseDetails("E1001", "Invalid username or password.");
    public static final ResponseDetails E1006 = new ResponseDetails("E1002", "User already exist.");
    public static final ResponseDetails E1007 = new ResponseDetails("E1003", "Group already exist.");
    public static final ResponseDetails E1008 = new ResponseDetails("E1004", "User not Available.");
    public static final ResponseDetails E1009 = new ResponseDetails("E1005", "You can only edit Active user.");
    public static final ResponseDetails E1010 = new ResponseDetails("E1006", "Group not exist.");


    private String code;
    private String description;

    private ResponseDetails() {
    }

    private ResponseDetails(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
