package com.expensemanager.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;


public class ExpenseFilterRequest {

    private Long categoryId;

    private LocalDate fromDate;

    private LocalDate toDate;

    private BigDecimal minAmt;

    private BigDecimal maxAmt;


    public Long getCategoryId() {
        return categoryId;
    }


    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }


    public LocalDate getFromDate() {
        return fromDate;
    }


    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }


    public LocalDate getToDate() {
        return toDate;
    }


    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }


    public BigDecimal getMinAmt() {
        return minAmt;
    }


    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
    }


    public BigDecimal getMaxAmt() {
        return maxAmt;
    }


    public void setMaxAmt(BigDecimal maxAmt) {
        this.maxAmt = maxAmt;
    }
}