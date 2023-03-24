package com.jazzybruno.example.v1.security.jwt;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class JwtUserInfo {
    private Integer integer;
    private String email;
    private String role;

}

