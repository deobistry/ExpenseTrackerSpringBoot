package com.expensemanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.expensemanager.config.ExpenseStatus;
import com.expensemanager.dto.request.ExpenseCreateRequest;
import com.expensemanager.dto.request.ExpenseFilterRequest;
import com.expensemanager.dto.request.ExpenseUpdateRequest;
import com.expensemanager.dto.response.ExpenseResponse;
import com.expensemanager.entity.Category;
import com.expensemanager.entity.Expense;
import com.expensemanager.exception.ForbiddenException;
import com.expensemanager.exception.ResourceNotFoundException;
import com.expensemanager.exception.ValidationException;
import com.expensemanager.repository.CategoryRepository;
import com.expensemanager.repository.ExpenseRepository;
import com.expensemanager.security.UserContext;


@Service
public class ExpenseService {


    private final ExpenseRepository expenseRepository;

    private final CategoryRepository categoryRepository;

    private final UserContext userContext;


    public ExpenseService(
            ExpenseRepository expenseRepository,
            CategoryRepository categoryRepository,
            UserContext userContext
    ) {
        this.expenseRepository = expenseRepository;
        this.categoryRepository = categoryRepository;
        this.userContext = userContext;
    }



    public void create(ExpenseCreateRequest request) {


        validateExpense(
                request.getDescription(),
                request.getAmount(),
                request.getDate(),
                request.getCategoryId()
        );


        validateCategoryOwnership(
                request.getCategoryId()
        );


        Expense expense = new Expense();

        expense.setDescription(
                request.getDescription()
        );

        expense.setAmount(
                request.getAmount()
        );

        expense.setDate(
                request.getDate()
        );

        expense.setCategoryId(
                request.getCategoryId()
        );

        expense.setUserId(
                userContext.getCurrentUserId()
        );

        expense.setStatus(
                ExpenseStatus.ACTIVE
        );


        expenseRepository.save(expense);
    }




    public List<ExpenseResponse> getLast20() {


        return expenseRepository.findLast20Expenses(
                userContext.getCurrentUserId(),
                PageRequest.of(0,20)
        );
    }




    public List<ExpenseResponse> filter(
            ExpenseFilterRequest request
    ) {


        validateFilter(request);


        return expenseRepository.filterExpenses(
                userContext.getCurrentUserId(),
                request.getCategoryId(),
                request.getFromDate(),
                request.getToDate(),
                request.getMinAmt(),
                request.getMaxAmt()
        );
    }




    public void update(
            ExpenseUpdateRequest request
    ) {


        Expense expense =
                expenseRepository.findById(request.getId())
                .orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Expense not found"
                    )
                );


        checkOwnership(expense);


        validateExpense(
                request.getDescription(),
                request.getAmount(),
                request.getDate(),
                request.getCategoryId()
        );


        validateCategoryOwnership(
                request.getCategoryId()
        );


        expense.setDescription(
                request.getDescription()
        );

        expense.setAmount(
                request.getAmount()
        );

        expense.setDate(
                request.getDate()
        );

        expense.setCategoryId(
                request.getCategoryId()
        );


        expenseRepository.save(expense);
    }




    public void softDelete(Long expenseId) {


        Expense expense =
                expenseRepository.findById(expenseId)
                .orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Expense not found"
                    )
                );


        checkOwnership(expense);


        expense.setStatus(
                ExpenseStatus.DELETED
        );


        expenseRepository.save(expense);
    }




    private void validateExpense(
            String description,
            BigDecimal amount,
            LocalDate date,
            Long categoryId
    ) {


        if(description == null ||
           description.trim().isEmpty()) {

            throw new ValidationException(
                    "Description is required"
            );
        }


        if(amount == null ||
           amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ValidationException(
                    "Amount must be greater than zero"
            );
        }


        if(date == null) {

            throw new ValidationException(
                    "Date is required"
            );
        }


        if(categoryId == null) {

            throw new ValidationException(
                    "Category is required"
            );
        }
    }




    private void validateCategoryOwnership(
            Long categoryId
    ) {


        Category category =
                categoryRepository.findById(categoryId)
                .orElseThrow(
                    () -> new ResourceNotFoundException(
                            "Category not found"
                    )
                );


        if(!category.getUserId()
                .equals(userContext.getCurrentUserId())) {


            throw new ForbiddenException(
                    "Category does not belong to user"
            );
        }
    }




    private void checkOwnership(
            Expense expense
    ) {


        if(!expense.getUserId()
                .equals(userContext.getCurrentUserId())) {


            throw new ForbiddenException(
                    "Access denied"
            );
        }
    }




    private void validateFilter(
            ExpenseFilterRequest request
    ) {


        if(request.getFromDate()!=null &&
           request.getToDate()!=null &&
           request.getFromDate()
                  .isAfter(request.getToDate())) {


            throw new ValidationException(
                    "Invalid date range"
            );
        }



        if(request.getMinAmt()!=null &&
           request.getMaxAmt()!=null &&
           request.getMinAmt()
                  .compareTo(request.getMaxAmt()) > 0) {


            throw new ValidationException(
                    "Invalid amount range"
            );
        }
    }
}