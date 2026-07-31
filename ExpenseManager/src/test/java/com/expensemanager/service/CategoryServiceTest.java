package com.expensemanager.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.util.Arrays;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.expensemanager.dto.request.CategoryCreateRequest;
import com.expensemanager.dto.request.CategoryUpdateRequest;
import com.expensemanager.entity.Category;
import com.expensemanager.exception.ForbiddenException;
import com.expensemanager.exception.ValidationException;
import com.expensemanager.repository.CategoryRepository;
import com.expensemanager.repository.ExpenseRepository;
import com.expensemanager.security.UserContext;



class CategoryServiceTest {


    @Mock
    private CategoryRepository categoryRepository;


    @Mock
    private ExpenseRepository expenseRepository;


    @Mock
    private UserContext userContext;


    @InjectMocks
    private CategoryService categoryService;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }



    @Test
    void create_shouldSaveCategorySuccessfully(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        CategoryCreateRequest request =
                new CategoryCreateRequest();


        request.setTitle("Food");



        categoryService.create(request);



        verify(
            categoryRepository,
            times(1)
        )
        .save(any(Category.class));

    }





    @Test
    void create_shouldFailWhenTitleIsEmpty(){


        CategoryCreateRequest request =
                new CategoryCreateRequest();


        request.setTitle("");



        assertThrows(
            ValidationException.class,
            () -> categoryService.create(request)
        );



        verify(
            categoryRepository,
            never()
        )
        .save(any(Category.class));

    }





    @Test
    void getAll_shouldReturnUserCategories(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Category category =
                new Category();


        category.setId(1L);
        category.setTitle("Food");
        category.setUserId(1L);



        when(
            categoryRepository.findAllByUserId(1L)
        )
        .thenReturn(
            Arrays.asList(category)
        );



        assertEquals(
            1,
            categoryService.getAll().size()
        );

    }





    @Test
    void update_shouldAllowOwner(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Category category =
                new Category();


        category.setId(1L);
        category.setUserId(1L);
        category.setTitle("Old");



        when(
            categoryRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(category)
        );



        CategoryUpdateRequest request =
                new CategoryUpdateRequest();


        request.setId(1L);
        request.setTitle("New");



        categoryService.update(request);



        assertEquals(
            "New",
            category.getTitle()
        );

    }





    @Test
    void update_shouldBlockDifferentOwner(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Category category =
                new Category();


        category.setId(1L);
        category.setUserId(2L);



        when(
            categoryRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(category)
        );



        CategoryUpdateRequest request =
                new CategoryUpdateRequest();


        request.setId(1L);
        request.setTitle("New");



        assertThrows(
            ForbiddenException.class,
            () ->
                categoryService.update(request)
        );

    }





    @Test
    void delete_shouldFailWhenActiveExpensesExist(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Category category =
                new Category();


        category.setId(1L);
        category.setUserId(1L);
        category.setTitle("Food");



        when(
            categoryRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(category)
        );



        when(
            expenseRepository.existsByCategoryIdAndStatus(
                    1L,
                    "ACTIVE"
            )
        )
        .thenReturn(true);



        assertThrows(
            ValidationException.class,
            () ->
                categoryService.delete(1L)
        );



        verify(
            categoryRepository,
            never()
        )
        .delete(any(Category.class));

    }





    @Test
    void delete_shouldWorkWhenNoActiveExpensesExist(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Category category =
                new Category();


        category.setId(1L);
        category.setUserId(1L);
        category.setTitle("Food");



        when(
            categoryRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(category)
        );



        when(
            expenseRepository.existsByCategoryIdAndStatus(
                    1L,
                    "ACTIVE"
            )
        )
        .thenReturn(false);



        categoryService.delete(1L);



        verify(
            categoryRepository,
            times(1)
        )
        .delete(category);

    }

}