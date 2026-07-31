package com.expensemanager.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.expensemanager.security.JwtAuthFilter;


@Configuration
public class FilterConfig {


    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter(
            JwtAuthFilter jwtAuthFilter
    ) {


        FilterRegistrationBean<JwtAuthFilter> registration =
                new FilterRegistrationBean<>();


        registration.setFilter(jwtAuthFilter);


        registration.addUrlPatterns(
                "/categories/*",
                "/expenses/*"
        );


        registration.setOrder(1);


        return registration;
    }
}