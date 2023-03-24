package com.jazzybruno.example.v1.exceptions;

import org.springframework.http.HttpStatus;

public class LoginFailedException extends Exception{
    private final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
}
