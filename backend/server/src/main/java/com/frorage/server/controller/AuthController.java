package com.frorage.server.controller;

import com.frorage.server.model.ConfirmationToken;
import com.frorage.server.model.Email;
import com.frorage.server.model.Token;
import com.frorage.server.payload.JwtAuthenticationResponse;
import com.frorage.server.payload.LoginRequest;
import com.frorage.server.payload.RegisterRequest;
import com.frorage.server.payload.UpdateRequest;
import com.frorage.server.security.CurrentUser;
import com.frorage.server.security.UserPrincipal;
import com.frorage.server.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(value = "user", tags = {"user"})
@RestController
@RequestMapping("/api/user")
public class AuthController {

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @ApiOperation(value = "register user", response = ConfirmationToken.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Created"),
            @ApiResponse(code = 400, message = "Bad request",response = com.frorage.server.payload.ApiResponse.class)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/register", consumes = "application/json")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        return authService.registerUser(registerRequest);
    }

    @ApiOperation(value = "login user",response = JwtAuthenticationResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request")})
    @PostMapping(value = "/login", consumes = "application/json")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        return authService.authenticateUser(loginRequest);
    }

    @ApiOperation(value = "confirm account", response = com.frorage.server.payload.ApiResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request",response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping(value = "/confirm-account", consumes = "application/json")
    public ResponseEntity<?> confirmUserAccount(@RequestBody Token token) {
        return authService.confirmUserAccount(token.getToken());
    }

    @ApiOperation(value = "resend email with confirmation code",response = com.frorage.server.payload.ApiResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request",response = com.frorage.server.payload.ApiResponse.class)})
    @PostMapping(value = "/resend-email", consumes = "application/json")
    public ResponseEntity<?> resentEmailConfirmation(@RequestBody Email email){
        return authService.resentEmailConfirmation(email.getEmail());
    }

    @ApiOperation(value = "delete user", response = com.frorage.server.payload.ApiResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 401, message = "Unauthorized")})
    @DeleteMapping(value = "")
    public ResponseEntity<?> deleteUser(@CurrentUser UserPrincipal userPrincipal) {
        return authService.deleteUser(userPrincipal);
    }

    @ApiOperation(value = "update user password")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "No content"),
            @ApiResponse(code = 400, message = "Bad request", response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized")})
    @PatchMapping(value = "", consumes = "application/json")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateUserPassword(@CurrentUser UserPrincipal userPrincipal, @Valid @RequestBody UpdateRequest updateRequest) {
        return authService.updateUserPassword(userPrincipal,updateRequest);
    }

    @ApiOperation(value = "reset user password",response = com.frorage.server.payload.ApiResponse.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "OK"),
            @ApiResponse(code = 400, message = "Bad request",response = com.frorage.server.payload.ApiResponse.class),
            @ApiResponse(code = 401, message = "Unauthorized")})
    @PostMapping(value = "/reset")
    public ResponseEntity<?> passwordReset(@CurrentUser UserPrincipal userPrincipal){
        return authService.passwordReset(userPrincipal);
    }
}