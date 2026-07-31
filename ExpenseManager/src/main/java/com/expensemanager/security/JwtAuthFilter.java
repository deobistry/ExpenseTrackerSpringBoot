package com.expensemanager.security;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class JwtAuthFilter implements Filter {


    private final JwtUtil jwtUtil;

    private final UserContext userContext;


    public JwtAuthFilter(
            JwtUtil jwtUtil,
            UserContext userContext
    ) {
        this.jwtUtil = jwtUtil;
        this.userContext = userContext;
    }



    @Override
    public void doFilter(
    	     ServletRequest request,
    	        ServletResponse response,
    	        FilterChain chain)
    	        throws IOException, ServletException {

    	    HttpServletRequest httpRequest =
    	            (HttpServletRequest) request;

    	    HttpServletResponse httpResponse =
    	            (HttpServletResponse) response;

    	    if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
    	        httpResponse.setStatus(HttpServletResponse.SC_OK);
    	        chain.doFilter(request, response);
    	        return;
    	    }

        String path =
                httpRequest.getRequestURI();



        // Allow authentication endpoints
        if(path.startsWith("/auth")) {

            chain.doFilter(request, response);
            return;
        }



        String header =
                httpRequest.getHeader("Authorization");



        if(header == null ||
           !header.startsWith("Bearer ")) {


            httpResponse.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }



        String token =
                header.substring(7);



        if(!jwtUtil.validateToken(token)) {


            httpResponse.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            return;
        }



        Long userId =
                jwtUtil.extractUserId(token);



        userContext.setCurrentUserId(userId);
        System.out.println("Logged in user id: " + userId);


        chain.doFilter(request, response);
    }
}