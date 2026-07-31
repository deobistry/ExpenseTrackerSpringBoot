package com.expensemanager.dto.request;

public class CategoryCreateRequest {

    private String title;


    public CategoryCreateRequest() {
    }


    public CategoryCreateRequest(String title) {
        this.title = title;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }
}