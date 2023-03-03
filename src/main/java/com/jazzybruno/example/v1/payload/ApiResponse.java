package com.jazzybruno.example.v1.payload;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiResponse {
    public boolean success;
    public String message;

    public Object data;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
