package com.jazzybruno.example.v1.dto.requests;

import com.jazzybruno.example.v1.models.User;
import com.jazzybruno.example.v1.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        try {
            Optional<User> userOptional =  userRepository.findUserByEmail(email);
            if(userOptional.isPresent()){
                return new org.springframework.security.core.userdetails.User( userOptional.get().getEmail() , userOptional.get().getPassword() , new ArrayList<>());
            }else{
                throw new UsernameNotFoundException("username or password incorrect");
            }
        }catch (UsernameNotFoundException e){
            throw new UsernameNotFoundException("Internal server error");
        }
    }
}
