    package com.jazzybruno.example.v1.serviceImpls;

import com.jazzybruno.example.v1.dto.requests.CreateUserDTO;
import com.jazzybruno.example.v1.dto.requests.UpdateRoleDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.sql.SQLException;
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

    @PreAuthorize("hasAuthority('Admin')")
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

    @PreAuthorize("hasAuthority('Admin') or #user_id == authentication.principal.grantedAuthorities[0].userId")
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
                     createUserDTO.getGender(),
                     createUserDTO.getPassword()
             );

             Long id = 4l;
             Optional<Role> roleOptional = roleRepository.findById(id);
             user.setRole(roleOptional.get());

             // setting a default avatar
             if(user.getGender().equals("Male")){
                 user.setGender("https://www.google.com/imgres?imgurl=https%3A%2F%2Fus.123rf.com%2F450wm%2Fyupiramos%2Fyupiramos1610%2Fyupiramos161007352%2F64369849-young-man-avatar-isolated-icon-vector-illustration-design.jpg&tbnid=ilUgygO9TcHl4M&vet=12ahUKEwj5x5G_oZv-AhVlmycCHTAsBCkQMygKegUIARDdAQ..i&imgrefurl=https%3A%2F%2Fwww.123rf.com%2Fphoto_64369849_young-man-avatar-isolated-icon-vector-illustration-design.html&docid=_AAMeHi1dhj_1M&w=450&h=450&q=a%20man%20avatar&ved=2ahUKEwj5x5G_oZv-AhVlmycCHTAsBCkQMygKegUIARDdAQ");
             }else{
                 user.setGender("   data:image/png;base64,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");
             }
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

    @PreAuthorize("hasAuthority('Admin') or #user_id == authentication.principal.grantedAuthorities[0].userId")
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

    @PreAuthorize("hasAuthority('Admin') or #user_id == authentication.principal.grantedAuthorities[0].userId")
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



    @PreAuthorize("hasAuthority('Admin')")
    @Transactional
    public ResponseEntity<ApiResponse> updateUserRole(UpdateRoleDTO updateRoleDTO) throws Exception{
        if(userRepository.existsById(updateRoleDTO.getUserId())){
            Optional<User> user = userRepository.findById(updateRoleDTO.getUserId());
            if(roleRepository.existsById(updateRoleDTO.getRoleId())){
                Role role = roleRepository.findById(updateRoleDTO.getRoleId()).get();
                user.get().setRole(role);
                return ResponseEntity.ok().body(new ApiResponse(
                        true,
                        "Successfully updated the user role",
                        user.map(userDTOMapper)
                ));
            }else{
                return ResponseEntity.status(404).body(new ApiResponse(
                        false,
                        "The role with the id:" + updateRoleDTO.getRoleId() + " does not exist try 1,2,3,4"
                ));
            }
        }else{
            return ResponseEntity.status(404).body(new ApiResponse(
                    false,
                    "The user with the id:" + updateRoleDTO.getUserId() + " does not exist"
            ));
        }
    }

    public ResponseEntity<ApiResponse> updatePassword(Long user_id , String newPassword) throws Exception{
        try {
            Optional<User> optionalUser = userRepository.findById(user_id);
            if(optionalUser.isPresent()){
                Hash hash = new Hash();
                String hashedPassword = hash.hashPassword(newPassword);
                optionalUser.get().setPassword(hashedPassword);
                return ResponseEntity.status(200).body(new ApiResponse(
                        true,
                        "Password was reset successfully"
                ));
            }else{
                return ResponseEntity.status(404).body(new ApiResponse(
                        false,
                        "The user with the id:" + user_id + " does not exist"
                ));
            }
        }catch (RuntimeException e){
            return ResponseEntity.status(500).body(new ApiResponse(
                    false,
                "Failed!!"
            ));
        }
    }

}
