package com.example.securityjwt.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response <T>{
    private String message;
    private T result;

    //@Controller 에서 반환할 Response
    public static <T> Response<T> success(T result) {
        return new Response("success", result);
    }

    //@RestControllerAdvice 에서 반환할 Response
    public static Response<Void> error(String message) {
        return new Response(message, null);
    }
}
