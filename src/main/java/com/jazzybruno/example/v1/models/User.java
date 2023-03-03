package com.jazzybruno.example.v1.models;


import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.management.relation.Role;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long user_id;
    @NotNull
    private String email;
    @NotNull
    private String username;
    @NotNull
    private String national_id;
    @NotNull
    private Role role;
    @NotNull
    private String password;

    public User(String email, String username, String national_id, Role role, String password) {
        this.email = email;
        this.username = username;
        this.national_id = national_id;
        this.role = role;
        this.password = password;
    }
}