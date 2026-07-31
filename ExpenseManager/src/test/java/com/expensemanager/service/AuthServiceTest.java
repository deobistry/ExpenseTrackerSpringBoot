package com.expensemanager.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.expensemanager.dto.request.LoginRequest;
import com.expensemanager.dto.request.SignupRequest;
import com.expensemanager.dto.response.LoginResponse;
import com.expensemanager.entity.User;
import com.expensemanager.exception.UnauthorizedException;
import com.expensemanager.exception.ValidationException;
import com.expensemanager.repository.UserRepository;
import com.expensemanager.security.JwtUtil;



class AuthServiceTest {


    @Mock
    private UserRepository userRepository;


    @Mock
    private JwtUtil jwtUtil;


    @InjectMocks
    private AuthService authService;



    @BeforeEach
    void setup() {

        MockitoAnnotations.openMocks(this);

    }



    @Test
    void signup_shouldCreateUserSuccessfully() {


        SignupRequest request =
                new SignupRequest();


        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setPassword("12345");



        when(
            userRepository.existsByEmail(
                    "john@gmail.com"
            )
        )
        .thenReturn(false);



        authService.signup(request);



        verify(
            userRepository,
            times(1)
        )
        .save(any(User.class));

    }





    @Test
    void signup_shouldFailWhenEmailAlreadyExists() {


        SignupRequest request =
                new SignupRequest();


        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setPassword("12345");



        when(
            userRepository.existsByEmail(
                    "john@gmail.com"
            )
        )
        .thenReturn(true);



        assertThrows(
            ValidationException.class,
            () -> authService.signup(request)
        );



        verify(
            userRepository,
            never()
        )
        .save(any(User.class));

    }





    @Test
    void login_shouldReturnTokenWhenCredentialsAreCorrect() {


        LoginRequest request =
                new LoginRequest();


        request.setEmail("john@gmail.com");
        request.setPassword("12345");



        User user = new User();


        user.setId(1L);
        user.setEmail("john@gmail.com");
        user.setPassword("12345");



        when(
            userRepository.findByEmail(
                    "john@gmail.com"
            )
        )
        .thenReturn(
            Optional.of(user)
        );



        when(
            jwtUtil.generateToken(user)
        )
        .thenReturn(
            "dummy-jwt-token"
        );



        LoginResponse response =
                authService.login(request);



        assertNotNull(response);


        assertEquals(
            "dummy-jwt-token",
            response.getToken()
        );

    }





    @Test
    void login_shouldFailWithWrongPassword() {


        LoginRequest request =
                new LoginRequest();


        request.setEmail("john@gmail.com");
        request.setPassword("wrongpassword");



        User user = new User();


        user.setId(1L);
        user.setEmail("john@gmail.com");
        user.setPassword("12345");



        when(
            userRepository.findByEmail(
                    "john@gmail.com"
            )
        )
        .thenReturn(
                Optional.of(user)
        );



        assertThrows(
            UnauthorizedException.class,
            () -> authService.login(request)
        );

    }

}