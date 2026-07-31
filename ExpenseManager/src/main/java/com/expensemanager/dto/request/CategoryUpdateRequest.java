package com.expensemanager.dto.request;

public class CategoryUpdateRequest {

    private Long id;

    private String title;


    public CategoryUpdateRequest() {
    }


    public CategoryUpdateRequest(Long id, String title) {
        this.id = id;
        this.title = title;
    }


    public Long getId() {
        return id;
    }


    public void setId(Long id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }
}