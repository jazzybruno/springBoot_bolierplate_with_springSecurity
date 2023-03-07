package com.jazzybruno.example.v1.controllers;

import com.jazzybruno.example.v1.config.JwtUtils;
import com.jazzybruno.example.v1.dao.UserDao;
import com.jazzybruno.example.v1.dto.AuthenticateDTO;
import com.jazzybruno.example.v1.repositories.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager , UserDao userDao, JwtUtils jwtUtils , UserRepository userRepository) {
        this.userDao = userDao;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(
            @RequestBody AuthenticateDTO authenticateDTO
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
        final UserDetails userDetails = (UserDetails) userRepository.findUserByEmail(authenticateDTO.getEmail()).get();
        if(userDetails != null){
            return ResponseEntity.ok( jwtUtils.generateToken(userDetails));
        }
        return ResponseEntity.status(400).body("Some error has happened");
    }
}
