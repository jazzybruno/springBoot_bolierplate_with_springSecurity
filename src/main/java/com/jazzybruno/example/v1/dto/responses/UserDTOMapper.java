package com.jazzybruno.example.v1.dto.responses;


import org.springframework.stereotype.Component;
import com.jazzybruno.example.v1.models.User;

import java.text.SimpleDateFormat;
import java.util.function.Function;

@Component
public class UserDTOMapper implements Function<User , UserDTO> {
    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
                user.getUser_id(),
                user.getEmail(),
                user.getUsername(),
                user.getNational_id(),
                user.getLastLogin()
        );
    }
}
