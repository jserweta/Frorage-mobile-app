package com.frorage.server.service;

import com.frorage.server.email.EmailService;
import com.frorage.server.model.ConfirmationToken;
import com.frorage.server.model.User;
import com.frorage.server.payload.ApiResponse;
import com.frorage.server.payload.LoginRequest;
import com.frorage.server.payload.RegisterRequest;
import com.frorage.server.payload.UpdateRequest;
import com.frorage.server.repository.ConfirmationTokenRepository;
import com.frorage.server.repository.UserRepository;
import com.frorage.server.security.JwtTokenProvider;
import com.frorage.server.security.UserPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository mockUserRepository;

    @Mock
    private EmailService mockEmailService;

    @Mock
    private ConfirmationTokenRepository mockConfirmationTokenRepository;

    @Mock
    private PasswordEncoder mockPasswordEncoder;

    @Mock
    private AuthenticationManager mockAuthenticationManager;

    @Mock
    private JwtTokenProvider mockTokenProvider;

    private void exisitngUsername(){
        // Given

        when(mockUserRepository.existsByUsername("username")).thenReturn(true);
        RegisterRequest registerRequest = new RegisterRequest("username", "other@email.com", "password");

        // When
        ResponseEntity<?> responseEntity = authService.registerUser(registerRequest);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();


        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Username is already taken!",apiResponse.getMessage())
        );
    }

    @Test
    void registerUser_ExistingUsername_BadRequest() {
        exisitngUsername();
    }

    @Test
    void registerUser_ExistingEmail_BadRequest() {
        // Given
        when(mockUserRepository.existsByEmail("email@email.com")).thenReturn(true);
        RegisterRequest registerRequest = new RegisterRequest("other", "email@email.com", "password");

        // When
        ResponseEntity<?> responseEntity = authService.registerUser(registerRequest);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Email Address already in use!",apiResponse.getMessage())
        );
    }

    @Test
    void registerUser_ExistingUsernameAndEmail_BadRequest() {
        exisitngUsername();
    }

    @Test
    void registerUser_OkPayload_Created() {
        // Given
        MockHttpServletRequest request = new MockHttpServletRequest(); // Mock ServletRequestAttributes
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        when(mockUserRepository.existsByUsername("username")).thenReturn(false);
        when(mockUserRepository.existsByEmail("email@email.com")).thenReturn(false);

        RegisterRequest registerRequest = new RegisterRequest("username", "email@email.com", "password");
        User user = new User(registerRequest.getUsername(), registerRequest.getEmail(), registerRequest.getPassword());
        user.setId(1L);
        when(mockUserRepository.save(any(User.class))).thenReturn(user);

        // When
        ResponseEntity<?> responseEntity = authService.registerUser(registerRequest);

        String location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/user/{id}")
                .buildAndExpand(user.getId()).toString();

        // Then
        assertAll(
                () -> assertEquals(HttpStatus.CREATED,responseEntity.getStatusCode()), // check status code
                () -> assertEquals(location,responseEntity.getHeaders().get("location").get(0)) //check location header
        );

    }


    @Test
    void authenticateUser_BadCredentials_BadCredentialsException(){
        // Given
        when(mockAuthenticationManager.authenticate(any())).thenThrow(BadCredentialsException.class);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("login");
        loginRequest.setPassword("password");

        // Then
        assertAll(
                () -> assertThrows(BadCredentialsException.class, () -> authService.authenticateUser(loginRequest)) // check exception
        );
    }

    @Test
    void authenticateUser_DisabledAccount_DisabledException(){
        // Given
        when(mockAuthenticationManager.authenticate(any())).thenThrow(DisabledException.class);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("login");
        loginRequest.setPassword("password");

        // Then
        assertAll(
                () -> assertThrows(DisabledException.class, () -> authService.authenticateUser(loginRequest)) // check exception
        );
    }

    @Test
    void confirmUserAccount_wrongConfirmationToken_BadRequest(){
        // Given
        when(mockConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authService.confirmUserAccount("token");
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Wrong confirmation token!",apiResponse.getMessage())
        );
    }

    @Test
    void confirmUserAccount_ExpiredToken_BadRequest(){
        // Given
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setExpirationDateTime(LocalDateTime.now().minusDays(1L));

        when(mockConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));

        // When
        ResponseEntity<?> responseEntity = authService.confirmUserAccount("token");
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Token expired!",apiResponse.getMessage())
        );
    }

    @Test
    void confirmUserAccount_ValidToken_AccountConfirmed(){
        // Given
        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setUser(new User());

        when(mockConfirmationTokenRepository.findByToken(any())).thenReturn(Optional.of(confirmationToken));

        // When
        ResponseEntity<?> responseEntity = authService.confirmUserAccount("token");
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () -> assertEquals("Email confirmed - your account is activated!",apiResponse.getMessage())
        );
    }

    @Test
    void resentEmailConfirmation_IncorrectEmail_BadRequest(){
        // Given
        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authService.resentEmailConfirmation("email");
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Incorrect email",apiResponse.getMessage())
        );
    }

    @Test
    void resentEmailConfirmation_EmailConfirmed_BadRequest(){
        // Given

        User user = new User("username","email@email.com", "password");
        user.setEnabled(true);

        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // When
        ResponseEntity<?> responseEntity = authService.resentEmailConfirmation("email");
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Email is already confirmed",apiResponse.getMessage())
        );
    }

    @Test
    void resentEmailConfirmation_ValidEmail_OK(){
        // Given
        User user = new User("username","email@email.com", "password");

        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.of(user));
        when(mockConfirmationTokenRepository.findByUserId(any())).thenReturn(Optional.of(new ConfirmationToken()));

        // When
        ResponseEntity<?> responseEntity = authService.resentEmailConfirmation("email");
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () -> assertEquals("Email with confirmation code sent",apiResponse.getMessage())
        );
    }

    @Test
    void deleteUser_User_UserDeleted(){
        // Given
        User user = new User("username","email@email.com", "password");

        // When
        ResponseEntity<?> responseEntity = authService.deleteUser(UserPrincipal.create(user));
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () -> assertEquals("User deleted",apiResponse.getMessage())
        );
    }

    @Test
    void updateUserPassword_NotExistingUser_BadRequest(){
        // Given
        User user = new User("username","email@email.com", "password");
        UpdateRequest updateRequest = new UpdateRequest("password","password");
        when(mockUserRepository.findById(any())).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authService.updateUserPassword(UserPrincipal.create(user),updateRequest);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("User doesn't exists",apiResponse.getMessage())
        );
    }

    @Test
    void updateUserPassword_WrongOldPassword_BadRequest(){
        // Given
        User user = new User("username","email@email.com", "password");
        UpdateRequest updateRequest = new UpdateRequest("newPassword","differentPassword");
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(user));

        // When
        ResponseEntity<?> responseEntity = authService.updateUserPassword(UserPrincipal.create(user),updateRequest);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
                () -> assertEquals("Wrong old password",apiResponse.getMessage())
        );
    }

    @Test
    void updateUserPassword_OkPayload_PasswordUpdated(){
        // Given
        User user = new User("username","email@email.com", "password");
        UpdateRequest updateRequest = new UpdateRequest("newPassword","password");
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(user));
        when(mockPasswordEncoder.matches("password","password")).thenReturn(true);

        // When
        ResponseEntity<?> responseEntity = authService.updateUserPassword(UserPrincipal.create(user),updateRequest);
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.NO_CONTENT,responseEntity.getStatusCode())
        );
    }

    @Test
    void passwordReset_NotExistingUser_BadRequest(){
        // Given
        User user = new User("username","email@email.com", "password");
        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.empty());

        // When
        ResponseEntity<?> responseEntity = authService.passwordReset(UserPrincipal.create(user));
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.BAD_REQUEST,responseEntity.getStatusCode()),
            () -> assertEquals("user doesn't exists",apiResponse.getMessage())
        );
    }

    @Test
    void passwordReset_User_OK(){
        // Given
        User user = new User("username","email@email.com", "password");
        when(mockUserRepository.findByEmail(any())).thenReturn(Optional.of(user));

        // When
        ResponseEntity<?> responseEntity = authService.passwordReset(UserPrincipal.create(user));
        ApiResponse apiResponse = (ApiResponse) responseEntity.getBody();

        // Then
        assertAll(
                () ->assertEquals(HttpStatus.OK,responseEntity.getStatusCode()),
                () -> assertEquals("Email with new password send",apiResponse.getMessage())
        );
    }

}