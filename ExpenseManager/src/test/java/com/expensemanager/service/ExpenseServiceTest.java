package com.expensemanager.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import com.expensemanager.config.ExpenseStatus;
import com.expensemanager.dto.request.ExpenseCreateRequest;
import com.expensemanager.dto.request.ExpenseFilterRequest;
import com.expensemanager.dto.request.ExpenseUpdateRequest;
import com.expensemanager.dto.response.ExpenseResponse;
import com.expensemanager.entity.Category;
import com.expensemanager.entity.Expense;
import com.expensemanager.exception.ForbiddenException;
import com.expensemanager.exception.ValidationException;
import com.expensemanager.repository.CategoryRepository;
import com.expensemanager.repository.ExpenseRepository;
import com.expensemanager.security.UserContext;



class ExpenseServiceTest {


    @Mock
    private ExpenseRepository expenseRepository;


    @Mock
    private CategoryRepository categoryRepository;


    @Mock
    private UserContext userContext;


    @InjectMocks
    private ExpenseService expenseService;



    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }



    @Test
    void create_shouldSaveExpenseSuccessfully(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Category category = new Category();

        category.setId(1L);
        category.setUserId(1L);



        when(
            categoryRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(category)
        );



        ExpenseCreateRequest request =
                new ExpenseCreateRequest();


        request.setDescription("Lunch");
        request.setAmount(new BigDecimal("200"));
        request.setDate(LocalDate.now());
        request.setCategoryId(1L);



        expenseService.create(request);



        verify(
            expenseRepository,
            times(1)
        )
        .save(any(Expense.class));

    }





    @Test
    void create_shouldFailForInvalidAmount(){


        ExpenseCreateRequest request =
                new ExpenseCreateRequest();


        request.setDescription("Lunch");
        request.setAmount(BigDecimal.ZERO);
        request.setDate(LocalDate.now());
        request.setCategoryId(1L);



        assertThrows(
            ValidationException.class,
            () ->
                expenseService.create(request)
        );



        verify(
            expenseRepository,
            never()
        )
        .save(any(Expense.class));

    }





    @Test
    void getLast20_shouldReturnExpenses(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        ExpenseResponse response =
                new ExpenseResponse();



        when(
            expenseRepository.findLast20Expenses(
                    anyLong(),
                    any()
            )
        )
        .thenReturn(
            Arrays.asList(response)
        );



        assertEquals(
            1,
            expenseService.getLast20().size()
        );

    }





    @Test
    void filter_shouldReturnFilteredExpenses(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        ExpenseFilterRequest request =
                new ExpenseFilterRequest();


        request.setCategoryId(1L);
        request.setFromDate(
                LocalDate.now().minusDays(5)
        );
        request.setToDate(
                LocalDate.now()
        );



        when(
            expenseRepository.filterExpenses(
                    anyLong(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any()
            )
        )
        .thenReturn(
            Arrays.asList(
                new ExpenseResponse()
            )
        );



        assertEquals(
            1,
            expenseService.filter(request).size()
        );

    }





    @Test
    void update_shouldAllowOwner(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Expense expense = new Expense();

        expense.setId(1L);
        expense.setUserId(1L);



        when(
            expenseRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(expense)
        );



        Category category = new Category();

        category.setId(1L);
        category.setUserId(1L);



        when(
            categoryRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(category)
        );



        ExpenseUpdateRequest request =
                new ExpenseUpdateRequest();


        request.setId(1L);
        request.setDescription("Updated");
        request.setAmount(new BigDecimal("500"));
        request.setDate(LocalDate.now());
        request.setCategoryId(1L);



        expenseService.update(request);



        assertEquals(
            "Updated",
            expense.getDescription()
        );

    }





    @Test
    void update_shouldBlockDifferentOwner(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Expense expense = new Expense();

        expense.setId(1L);
        expense.setUserId(2L);



        when(
            expenseRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(expense)
        );



        ExpenseUpdateRequest request =
                new ExpenseUpdateRequest();


        request.setId(1L);



        assertThrows(
            ForbiddenException.class,
            () ->
                expenseService.update(request)
        );

    }





    @Test
    void softDelete_shouldChangeStatus(){


        when(
            userContext.getCurrentUserId()
        )
        .thenReturn(1L);



        Expense expense = new Expense();

        expense.setId(1L);
        expense.setUserId(1L);
        expense.setStatus(
                ExpenseStatus.ACTIVE
        );



        when(
            expenseRepository.findById(1L)
        )
        .thenReturn(
            Optional.of(expense)
        );



        expenseService.softDelete(1L);



        assertEquals(
            ExpenseStatus.DELETED,
            expense.getStatus()
        );



        verify(
            expenseRepository
        )
        .save(expense);

    }

}