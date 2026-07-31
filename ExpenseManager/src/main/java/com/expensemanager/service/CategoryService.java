package com.expensemanager.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.expensemanager.config.ExpenseStatus;
import com.expensemanager.dto.request.CategoryCreateRequest;
import com.expensemanager.dto.request.CategoryUpdateRequest;
import com.expensemanager.dto.response.CategoryResponse;
import com.expensemanager.entity.Category;
import com.expensemanager.exception.ForbiddenException;
import com.expensemanager.exception.ResourceNotFoundException;
import com.expensemanager.exception.ValidationException;
import com.expensemanager.repository.CategoryRepository;
import com.expensemanager.repository.ExpenseRepository;
import com.expensemanager.security.UserContext;


@Service
public class CategoryService {


    private final CategoryRepository categoryRepository;

    private final ExpenseRepository expenseRepository;

    private final UserContext userContext;



    public CategoryService(
            CategoryRepository categoryRepository,
            ExpenseRepository expenseRepository,
            UserContext userContext
    ) {
        this.categoryRepository = categoryRepository;
        this.expenseRepository = expenseRepository;
        this.userContext = userContext;
    }



    public void create(CategoryCreateRequest request) {


        if(request.getTitle() == null ||
           request.getTitle().trim().isEmpty()) {

            throw new ValidationException(
                    "Category title is required"
            );
        }


        Category category = new Category();

        category.setTitle(request.getTitle());

        category.setUserId(
                userContext.getCurrentUserId()
        );


        categoryRepository.save(category);
    }



    public List<CategoryResponse> getAll() {


        Long userId =
                userContext.getCurrentUserId();


        return categoryRepository
                .findAllByUserId(userId)
                .stream()
                .map(CategoryResponse::new)
                .collect(Collectors.toList());
    }




    public void update(CategoryUpdateRequest request) {


        Category category =
                categoryRepository.findById(request.getId())
                .orElseThrow(
                    () -> new ResourceNotFoundException(
                        "Category not found"
                    )
                );


        checkOwnership(category);


        if(request.getTitle()==null ||
           request.getTitle().trim().isEmpty()) {

            throw new ValidationException(
                    "Category title is required"
            );
        }


        category.setTitle(request.getTitle());

        categoryRepository.save(category);
    }




    public void delete(Long categoryId) {


        Category category =
                categoryRepository.findById(categoryId)
                .orElseThrow(
                    () -> new ResourceNotFoundException(
                        "Category not found"
                    )
                );


        checkOwnership(category);



        boolean exists =
                expenseRepository.existsByCategoryIdAndStatus(
                        categoryId,
                        ExpenseStatus.ACTIVE
                );


        if(exists) {

            throw new ValidationException(
                    "Cannot delete category with active expenses"
            );
        }


        categoryRepository.delete(category);
    }




    private void checkOwnership(Category category) {


        if(!category.getUserId()
                .equals(userContext.getCurrentUserId())) {


            throw new ForbiddenException(
                    "Access denied"
            );
        }
    }
}