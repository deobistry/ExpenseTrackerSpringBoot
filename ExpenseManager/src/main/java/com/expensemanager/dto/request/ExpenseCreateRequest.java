package com.expensemanager.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;


public class ExpenseCreateRequest {

    private String description;

    private BigDecimal amount;

    private LocalDate date;

    private Long categoryId;


    public ExpenseCreateRequest() {
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


    public Long getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}