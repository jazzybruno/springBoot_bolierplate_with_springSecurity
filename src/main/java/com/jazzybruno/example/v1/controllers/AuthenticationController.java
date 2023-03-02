package com.jazzybruno.example.v1.controllers;

import com.jazzybruno.example.v1.config.JwtUtils;
import com.jazzybruno.example.v1.dao.UserDao;
import com.jazzybruno.example.v1.dto.AuthenticateDTO;
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

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager , UserDao userDao, JwtUtils jwtUtils) {
        this.userDao = userDao;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(
            @RequestBody AuthenticateDTO authenticateDTO
    ){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticateDTO.getEmail(),
                        authenticateDTO.getPassword()
                )
        );
        final UserDetails userDetails = userDao.findUserByEmail(authenticateDTO.getEmail());
        if(userDetails != null){
            return ResponseEntity.ok( jwtUtils.generateToken(userDetails));
        }
        return ResponseEntity.status(400).body("Some error has happened");
    }
}
