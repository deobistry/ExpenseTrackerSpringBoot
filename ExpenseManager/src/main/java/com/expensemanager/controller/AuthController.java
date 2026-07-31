package com.expensemanager.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.expensemanager.dto.request.LoginRequest;
import com.expensemanager.dto.request.SignupRequest;
import com.expensemanager.dto.response.LoginResponse;
import com.expensemanager.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/auth")
@Tag(
    name = "Authentication",
    description = "Signup and login APIs"
)
public class AuthController {


    private final AuthService authService;



    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }



    @Operation(
        summary = "Register new user",
        description = "Creates a new user account"
    )
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @RequestBody SignupRequest request
    ) {

        authService.signup(request);

        return ResponseEntity.ok().build();
    }





    @Operation(
        summary = "Login user",
        description = "Returns JWT token after successful login"
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request
    ) {

        return ResponseEntity.ok(
                authService.login(request)
        );
    }

}