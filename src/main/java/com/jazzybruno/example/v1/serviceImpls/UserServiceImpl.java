    package com.jazzybruno.example.v1.serviceImpls;

import com.jazzybruno.example.v1.dto.requests.CreateUserDTO;
import com.jazzybruno.example.v1.dto.responses.LoginResponse;
import com.jazzybruno.example.v1.dto.responses.UserDTOMapper;
import com.jazzybruno.example.v1.dto.requests.UserLoginDTO;
import com.jazzybruno.example.v1.exceptions.LoginFailedException;
import com.jazzybruno.example.v1.models.Role;
import com.jazzybruno.example.v1.models.User;
import com.jazzybruno.example.v1.payload.ApiResponse;
import com.jazzybruno.example.v1.repositories.RoleRepository;
import com.jazzybruno.example.v1.repositories.UserRepository;
import com.jazzybruno.example.v1.security.jwt.JwtUtils;
import com.jazzybruno.example.v1.security.user.UserAuthority;
import com.jazzybruno.example.v1.security.user.UserSecurityDetails;
import com.jazzybruno.example.v1.security.user.UserSecurityDetailsService;
import com.jazzybruno.example.v1.services.UserService;
import com.jazzybruno.example.v1.utils.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserDTOMapper userDTOMapper;
    private final RoleRepository roleRepository;
    private final AuthenticationManager authenticationManager;
    private final UserSecurityDetailsService userSecurityDetailsService;
    private final JwtUtils jwtUtils;

    public ResponseEntity<ApiResponse> getAllUsers() throws Exception{
      try {
          List<User> users = userRepository.findAll();
          return  ResponseEntity.ok().body(new ApiResponse(
                  true,
                  "Successfully fetched the users",
                  users.stream().map(userDTOMapper).collect(Collectors.toList())
          ));
      }catch (Exception e){
          return ResponseEntity.status(500).body(new ApiResponse(
                  false,
                  "Failed to fetch the users"
          ));
      }
    }

    public ResponseEntity<ApiResponse> getUserById(Long user_id) throws Exception{
        if(userRepository.existsById(user_id)){
            try {
                Optional<User> user = userRepository.findById(user_id);
                return ResponseEntity.ok().body(new ApiResponse(
                        true,
                        "Successfully fetched the users",
                        user.map(userDTOMapper)
                ));
            }catch (Exception e){
                return ResponseEntity.status(500).body(new ApiResponse(
                        false,
                        "Failed to fetch the user"
                ));
            }
        }else{
            return ResponseEntity.status(404).body(new ApiResponse(
                    false,
                    "The user with the id:" + user_id + " does not exist"
            ));
        }
    }

    public ResponseEntity<ApiResponse> createUser(CreateUserDTO createUserDTO) throws Exception{
         Optional<User> user1 = userRepository.findUserByEmail(createUserDTO.getEmail());
         if(!user1.isPresent()){
             User user = new User(
                     createUserDTO.getEmail(),
                     createUserDTO.getUsername(),
                     createUserDTO.getNational_id(),
                     createUserDTO.getPassword()
             );

             Long id = 4l;
             Optional<Role> roleOptional = roleRepository.findById(id);
             user.setRole(roleOptional.get());

             Hash hash = new Hash();
             user.setPassword(hash.hashPassword(user.getPassword()));
             user.setLastLogin(null);
             try {
                 userRepository.save(user);
                 return ResponseEntity.ok().body(new ApiResponse(
                         true,
                         "Successfully saved the user",
                         user
                 ));
             }catch (HttpServerErrorException.InternalServerError e){
                 return ResponseEntity.status(500).body(new ApiResponse(
                         false,
                         "Failed to create the user"
                 ));
             }
         }else{
             return ResponseEntity.status(404).body(new ApiResponse(
                     false,
                     "The user with the email:" + createUserDTO.getEmail() + " already exists"
             ));
         }
    }

    public void updateUserMapper(Optional<User> user, CreateUserDTO createUserDTO){
        user.get().setEmail(createUserDTO.getEmail());
        user.get().setUsername(createUserDTO.getUsername());
        user.get().setNational_id(createUserDTO.getNational_id());
        user.get().setPassword(createUserDTO.getPassword());
    }

    @Transactional
    public ResponseEntity<ApiResponse> updateUser(Long user_id ,  CreateUserDTO createUserDTO) throws Exception {
        if (userRepository.existsById(user_id)) {
            Optional<User> user = userRepository.findById(user_id);
            updateUserMapper(user, createUserDTO);
            return ResponseEntity.ok().body(new ApiResponse(
                    true,
                    "Successfully updated the user",
                    user.map(userDTOMapper)
            ));
        } else {
            return ResponseEntity.status(404).body(new ApiResponse(
                    false,
                    "The user with the id:" + user_id + " does not exist"
            ));
        }
    }

    public ResponseEntity<ApiResponse> deleteUser(Long user_id) throws Exception{
        if (userRepository.existsById(user_id)) {
            Optional<User> user = userRepository.findById(user_id);
            userRepository.deleteById(user_id);
            return ResponseEntity.ok().body(new ApiResponse(
                    true,
                    "Successfully deleted the user",
                    user.map(userDTOMapper)
            ));
        }else {
            return ResponseEntity.status(404).body(new ApiResponse(
                    false,
                    "The user with the id:" + user_id + " does not exist"
            ));
        }
    }

    @Override
    public ResponseEntity<ApiResponse> authenticateUser(UserLoginDTO userLoginDTO) throws BadCredentialsException , LoginFailedException{
            Optional<User> user = userRepository.findUserByEmail(userLoginDTO.getEmail());
            if(user.isPresent()){
                Hash hash = new Hash();
                if(hash.isTheSame(userLoginDTO.getPassword() , user.get().getPassword())){

                    // do the login stuff as usual
                    UserSecurityDetails userSecurityDetails = (UserSecurityDetails) userSecurityDetailsService.loadUserByUsername(userLoginDTO.getEmail());
                    List<GrantedAuthority> grantedAuthorities = userSecurityDetails.grantedAuthorities;
                    UserAuthority userAuthority = (UserAuthority) grantedAuthorities.get(0);
                    String role = userAuthority.getAuthority();
                    //updating the last login information
                    User userObject = user.get();
                    userObject.setLastLogin(new Date());
                    userRepository.save(userObject);
                    // Todo Add the last login parameter to the table and update it here to keep track of
                    //  the login userSecurityDetails
                    String token = jwtUtils.createToken(user.get().getUser_id(), userLoginDTO.getEmail() , role);
                    return ResponseEntity.ok().body(new ApiResponse(true , "Success in login" , new LoginResponse(token , user.map(userDTOMapper).get())));
                    }else{
                    return ResponseEntity.status(401).body(new ApiResponse(false , "Failed to Login" , new LoginFailedException("Incorrect Email or password").getMessage()));
                }
            }else{
                return ResponseEntity.status(401).body(new ApiResponse(false , "Failed to login" , new LoginFailedException("User does not exist!!").getMessage()));
            }
        }
}
