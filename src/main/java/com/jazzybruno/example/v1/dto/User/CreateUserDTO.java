package com.jazzybruno.example.v1.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.management.relation.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    private Long userId;
    private String email;
    private String username;
    private String national_id;
    private Role role;
    private String password;
}
