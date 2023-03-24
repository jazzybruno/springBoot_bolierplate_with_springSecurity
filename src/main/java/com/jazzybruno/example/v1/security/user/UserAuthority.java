package com.jazzybruno.example.v1.security.user;

import org.springframework.security.core.GrantedAuthority;

public class UserAuthority implements GrantedAuthority {

    public Long userId;
    public String authority;

    public UserAuthority(Long userId, String authority) {
        this.userId = userId;
        this.authority = authority;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
