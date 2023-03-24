package com.jazzybruno.example.v1.dto.requests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginDTO {
    private String email;
    private String password;
}

