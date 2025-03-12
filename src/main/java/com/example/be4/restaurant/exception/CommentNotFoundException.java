package com.example.be4.restaurant.exception;

public class CommentNotFoundException extends RuntimeException {
    //댓글 못 찾음
    public CommentNotFoundException(String message) {
        super(message);
    }
}