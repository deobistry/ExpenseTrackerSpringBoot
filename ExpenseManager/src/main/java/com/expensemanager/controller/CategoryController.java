package com.expensemanager.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.expensemanager.dto.request.CategoryCreateRequest;
import com.expensemanager.dto.request.CategoryUpdateRequest;
import com.expensemanager.dto.response.CategoryResponse;
import com.expensemanager.service.CategoryService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/categories")
@Tag(
    name = "Categories",
    description = "Category management APIs"
)
public class CategoryController {


    private final CategoryService categoryService;



    public CategoryController(
            CategoryService categoryService
    ) {
        this.categoryService = categoryService;
    }





    @Operation(
        summary = "Create category",
        description = "Creates category for logged-in user"
    )
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CategoryCreateRequest request
    ) {

        categoryService.create(request);

        return ResponseEntity.ok().build();
    }





    @Operation(
        summary = "Get categories",
        description = "Returns categories of current user"
    )
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAll() {


        return ResponseEntity.ok(
                categoryService.getAll()
        );
    }





    @Operation(
        summary = "Update category",
        description = "Updates category after ownership verification"
    )
    @PutMapping
    public ResponseEntity<?> update(
            @RequestBody CategoryUpdateRequest request
    ) {

        categoryService.update(request);

        return ResponseEntity.ok().build();
    }





    @Operation(
        summary = "Delete category",
        description = "Deletes category if no active expenses exist"
    )
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> delete(
            @PathVariable Long categoryId
    ) {

        categoryService.delete(categoryId);

        return ResponseEntity.ok().build();
    }

}