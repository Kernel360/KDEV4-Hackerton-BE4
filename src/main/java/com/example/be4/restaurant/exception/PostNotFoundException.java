package com.example.be4.restaurant.exception;

public class PostNotFoundException extends RuntimeException {
    //게시글 못 찾음
    public PostNotFoundException(String message) {
        super(message);
    }
}