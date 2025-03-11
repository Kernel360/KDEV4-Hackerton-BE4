package com.example.be4.restaurant.exception;

public class InvalidPasswordException extends RuntimeException {

    //비밀번호 불일치
    public InvalidPasswordException(String message) {
        super(message);
    }
}