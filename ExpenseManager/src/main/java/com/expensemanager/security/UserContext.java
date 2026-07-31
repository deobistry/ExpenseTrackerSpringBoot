package com.expensemanager.security;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
public class UserContext {

    private Long currentUserId;


    public Long getCurrentUserId() {
        return currentUserId;
    }


    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }
}