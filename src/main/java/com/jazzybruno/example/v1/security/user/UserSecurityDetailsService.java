package com.jazzybruno.example.v1.security.user;

import com.jazzybruno.example.v1.models.User;
import com.jazzybruno.example.v1.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserSecurityDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if(userOptional.isPresent()){
            return new UserSecurityDetails(userOptional.get());
        }else{
        throw new UsernameNotFoundException("" + email + " was not found");
        }
    }
}
