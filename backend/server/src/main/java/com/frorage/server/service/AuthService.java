package com.frorage.server.service;

import com.frorage.server.email.EmailService;
import com.frorage.server.model.ConfirmationToken;
import com.frorage.server.model.User;
import com.frorage.server.payload.*;
import com.frorage.server.repository.ConfirmationTokenRepository;
import com.frorage.server.repository.UserRepository;
import com.frorage.server.security.JwtTokenProvider;
import com.frorage.server.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider tokenProvider;

    private EmailService emailService;

    private ConfirmationTokenRepository confirmationTokenRepository;


    public AuthService(AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider, EmailService emailService, ConfirmationTokenRepository confirmationTokenRepository) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
        this.confirmationTokenRepository = confirmationTokenRepository;
    }

    public ResponseEntity<?> registerUser(RegisterRequest registerRequest){
        // checking username and email availability
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return badRequest("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return badRequest("Email Address already in use!");
        }

        // creating user's account
        User user = new User(registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()));

        User result = userRepository.save(user);

        // create confirmationToken
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setToken("123");
        confirmationToken.setUser(user);
        confirmationTokenRepository.save(confirmationToken);

        emailService.sendEmail(user.getEmail(), "Complete Registration!", "To confirm your account, use this code in app: " + confirmationToken.getToken());

        // resource location
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/user/{id}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(confirmationToken);
    }

    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        Optional<User> userOptional = userRepository.findByUsernameOrEmail(loginRequest.getUsernameOrEmail(),loginRequest.getUsernameOrEmail());
        if(userOptional.isPresent()){
            User user = userOptional.get();
            Long id = user.getId();

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            return ResponseEntity.ok(new JwtAuthenticationResponse(jwt,id));
        }

        return badRequest("User doesnt exist!");
    }

    public ResponseEntity<?> confirmUserAccount(String token) {
        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findByToken(token);

        if(confirmationTokens.size()>0) {

            for (int i = 0; i < confirmationTokens.size(); i++) {
                User u = confirmationTokens.get(i).getUser();
                u.setEnabled(true);
                userRepository.save(u);
            }

            return ok("Email confirmed - your account is activated!");
        }
        else {
            return badRequest("Wrong confirmation token!");
        }
    }

    public ResponseEntity<?> resentEmailConfirmation(String email){
        // find email in users
        Optional<User> userOptional = userRepository.findByEmail(email);

        if(userOptional.isPresent()){
            User user = userOptional.get();
            if(!user.isEnabled()){
                // create confirmationToken
                Optional<ConfirmationToken> confirmationTokenOptional = confirmationTokenRepository.findByUserId(user.getId());

                if(confirmationTokenOptional.isPresent()){
                    ConfirmationToken confirmationToken = confirmationTokenOptional.get();
                    confirmationToken.setExpirationDateTime(LocalDateTime.now().plusMinutes(30L)); //30 minutes to confirm
                    confirmationTokenRepository.save(confirmationToken);

                    emailService.sendEmail(user.getEmail(), "Complete Registration!", "To confirm your account, use this code in app: " + confirmationToken.getToken());
                    return ok("Email with confirmation code sent");
                }
            }

            return badRequest("Email is already confirmed");
        }

        return badRequest("Incorrect email");
    }

    public ResponseEntity<?> deleteUser(UserPrincipal userPrincipal) {
        userRepository.deleteUserByUsername(userPrincipal.getUsername());
        return ok("User deleted");
    }

    public ResponseEntity<?> updateUserPassword(UserPrincipal userPrincipal, UpdateRequest updateRequest) {
        Optional<User> userOptional = userRepository.findById(userPrincipal.getId());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            //check oldPassword
            if (passwordEncoder.matches(updateRequest.getOldPassword(), user.getPassword())) {

                //change password to newPassword
                user.setPassword(passwordEncoder.encode(updateRequest.getNewPassword()));
                userRepository.save(user);

                return ResponseEntity.noContent().build();
            } else {
                return badRequest("Wrong old password");
            }
        }

        return badRequest("User doesn't exists");
    }

    public ResponseEntity<?> passwordReset(UserPrincipal userPrincipal){
        Optional<User> userOptional = userRepository.findByEmail(userPrincipal.getEmail());

        if(userOptional.isPresent()){
            User user = userOptional.get();

            String newPassword = UUID.randomUUID().toString().substring(10,20);
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);

            emailService.sendEmail(user.getEmail(), "Password Reset Request!", "Your new password is: " + newPassword);
            return ok("Email with new password send");
        }

        return badRequest("user doesn't exists");
    }


    private ResponseEntity<?> badRequest(String message){
        return new ResponseEntity<>(new ApiResponse(false,message),HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> ok(String message){
        return new ResponseEntity<>(new ApiResponse(true,message),HttpStatus.OK);
    }
}
