package com.expensemanager.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;


public class ExpenseResponse {

    private Long expenseId;

    private String description;

    private BigDecimal amount;

    private LocalDate date;

    private Long userId;

    private Long categoryId;

    private String categoryTitle;


    public ExpenseResponse() {
    }


    public ExpenseResponse(
            Long expenseId,
            String description,
            BigDecimal amount,
            LocalDate date,
            Long userId,
            Long categoryId,
            String categoryTitle
    ) {
        this.expenseId = expenseId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryTitle = categoryTitle;
    }


    public Long getExpenseId() {
        return expenseId;
    }


    public void setExpenseId(Long expenseId) {
        this.expenseId = expenseId;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public BigDecimal getAmount() {
        return amount;
    }


    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }


    public LocalDate getDate() {
        return date;
    }


    public void setDate(LocalDate date) {
        this.date = date;
    }


    public Long getUserId() {
        return userId;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public Long getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }


    public String getCategoryTitle() {
        return categoryTitle;
    }


    public void setCategoryTitle(String categoryTitle) {
        this.categoryTitle = categoryTitle;
    }
}