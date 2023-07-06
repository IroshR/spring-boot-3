package com.iroshnk.nftraffle.payload;

public class Response<T> {

    private String code;
    private String description;
    private T data;

    public Response() {
    }

    public Response(T data) {
        this("S1000", "Success", data);
    }

    public Response(String code, String description) {
        this(code, description, null);
    }

    public Response(String code, String description, T data) {
        this.code = code;
        this.description = description;
        this.data = data;
    }

    public static <T> Response<T> success(T data) {
        return new Response<>("S1000", "Success", data);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
