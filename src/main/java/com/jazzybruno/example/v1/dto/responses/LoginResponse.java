package com.jazzybruno.example.v1.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

    public String token;
    public UserDTO userData;
}
