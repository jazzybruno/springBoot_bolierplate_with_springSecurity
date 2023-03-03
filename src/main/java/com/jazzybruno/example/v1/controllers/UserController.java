package com.jazzybruno.example.v1.controllers;


import com.jazzybruno.example.v1.dto.User.CreateUserDTO;
import com.jazzybruno.example.v1.payload.ApiResponse;
import com.jazzybruno.example.v1.serviceImpls.UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final CreateUserDTO createUserDTO;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllUser() throws Exception{
        return userService.getAllUsers();
    }

    @GetMapping("/id/{user_id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long user_id) throws Exception{
        return userService.getUserById(user_id);
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody CreateUserDTO createUserDTO) throws Exception{
        return userService.createUser(createUserDTO);
    }

    @PutMapping("/update/{user_id}")
    public ResponseEntity<ApiResponse> updateUser(@PathVariable Long user_id , @RequestBody CreateUserDTO createUserDTO) throws Exception{
        return userService.updateUser(user_id , createUserDTO);
    }

    @DeleteMapping("/delete/{user_id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long user_id) throws Exception{
        return userService.deleteUser(user_id);
    }

}
