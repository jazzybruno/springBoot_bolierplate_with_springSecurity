package com.jazzybruno.example.v1.controllers;

import com.jazzybruno.example.v1.security.jwt.JwtUtils;
import com.jazzybruno.example.v1.dao.UserDao;
import com.jazzybruno.example.v1.dto.UserLoginDTO;
import com.jazzybruno.example.v1.dto.User.CustomUserDetails;
import com.jazzybruno.example.v1.utils.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;

    private UserDao userDao;
    private JwtUtils jwtUtils;
    private final CustomUserDetails userDetails;
    private Hash hash = new Hash();

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager , UserDao userDao, JwtUtils jwtUtils ,CustomUserDetails userDetails) {
        this.userDao = userDao;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userDetails = userDetails;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(
            @RequestBody UserLoginDTO authenticateDTO
    ){
        if(authenticateDTO.getEmail() == null || authenticateDTO.getPassword() == null){
            return ResponseEntity.badRequest().body("PLEASE INPUT VALID EMAIL AND PASSWORD");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateDTO.getEmail(),
                        authenticateDTO.getPassword()
                )
        );
           final UserDetails user = (UserDetails) userDetails.loadUserByUsername(authenticateDTO.getEmail());
        if(!hash.isTheSame(authenticateDTO.getPassword() , user.getPassword())){
            return ResponseEntity.badRequest().body("The passwords or email does not match");
        }
        if(user != null){
            return ResponseEntity.ok( jwtUtils.generateToken(user));
        }
        return ResponseEntity.status(400).body("Some error has happened");
    }
}
