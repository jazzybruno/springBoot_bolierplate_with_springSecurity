package com.jazzybruno.example.v1.dto.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import javax.management.relation.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class CreateUserDTO {
    private String email;
    private String username;
    private String national_id;
    private String password;
}
