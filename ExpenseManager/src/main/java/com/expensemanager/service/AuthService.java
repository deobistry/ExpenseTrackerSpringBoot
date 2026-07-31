package com.expensemanager.service;

import org.springframework.stereotype.Service;

import com.expensemanager.dto.request.LoginRequest;
import com.expensemanager.dto.request.SignupRequest;
import com.expensemanager.dto.response.LoginResponse;
import com.expensemanager.entity.User;
import com.expensemanager.exception.UnauthorizedException;
import com.expensemanager.exception.ValidationException;
import com.expensemanager.repository.UserRepository;
import com.expensemanager.security.JwtUtil;


@Service
public class AuthService {


    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;


    public AuthService(
            UserRepository userRepository,
            JwtUtil jwtUtil
    ) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }


    public void signup(SignupRequest request) {


        if(request.getName()==null ||
           request.getEmail()==null ||
           request.getPassword()==null) {

            throw new ValidationException(
                    "All fields are required"
            );
        }


        if(userRepository.existsByEmail(request.getEmail())) {

            throw new ValidationException(
                    "Email already exists"
            );
        }


        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());


        userRepository.save(user);
    }



    public LoginResponse login(LoginRequest request) {


        User user =
            userRepository.findByEmail(request.getEmail())
            .orElseThrow(
                () -> new UnauthorizedException(
                    "Invalid credentials"
                )
            );


        if(!user.getPassword()
                .equals(request.getPassword())) {

            throw new UnauthorizedException(
                    "Invalid credentials"
            );
        }


        String token =
                jwtUtil.generateToken(user);


        return new LoginResponse(token);
    }
}