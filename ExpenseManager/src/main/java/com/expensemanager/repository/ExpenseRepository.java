package com.expensemanager.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.expensemanager.dto.response.ExpenseResponse;
import com.expensemanager.entity.Expense;


public interface ExpenseRepository extends JpaRepository<Expense, Long> {


    boolean existsByCategoryIdAndStatus(
            Long categoryId,
            String status
    );


    @Query("""
            SELECT new com.expensemanager.dto.response.ExpenseResponse(
                e.id,
                e.description,
                e.amount,
                e.date,
                e.userId,
                e.categoryId,
                c.title
            )
            FROM Expense e, Category c
            WHERE e.categoryId = c.id
            AND e.userId = :userId
            AND e.status = 'ACTIVE'
            ORDER BY e.date DESC
            """)
    List<ExpenseResponse> findLast20Expenses(
            @Param("userId") Long userId,
            Pageable pageable
    );



    @Query("""
            SELECT new com.expensemanager.dto.response.ExpenseResponse(
                e.id,
                e.description,
                e.amount,
                e.date,
                e.userId,
                e.categoryId,
                c.title
            )
            FROM Expense e, Category c
            WHERE e.categoryId = c.id
            AND e.userId = :userId
            AND e.status = 'ACTIVE'
            AND (:categoryId IS NULL OR e.categoryId = :categoryId)
            AND (:fromDate IS NULL OR e.date >= :fromDate)
            AND (:toDate IS NULL OR e.date <= :toDate)
            AND (:minAmt IS NULL OR e.amount >= :minAmt)
            AND (:maxAmt IS NULL OR e.amount <= :maxAmt)
            ORDER BY e.date DESC
            """)
    List<ExpenseResponse> filterExpenses(
            @Param("userId") Long userId,
            @Param("categoryId") Long categoryId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("minAmt") java.math.BigDecimal minAmt,
            @Param("maxAmt") java.math.BigDecimal maxAmt
    );

}