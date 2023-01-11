package com.example.dto.response;


public class ServerResponse<T>{
    //SERVER 의 응답값과 필드명이 같아야 하며, 필드명은 소문자로 한정
    private String message;
    private T result;

    public String getMessage() {
        return message;
    }
    public T getResult() {
        return result;
    }
}
