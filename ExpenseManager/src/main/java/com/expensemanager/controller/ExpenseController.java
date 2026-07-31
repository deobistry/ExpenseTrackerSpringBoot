package com.expensemanager.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.expensemanager.dto.request.ExpenseCreateRequest;
import com.expensemanager.dto.request.ExpenseFilterRequest;
import com.expensemanager.dto.request.ExpenseUpdateRequest;
import com.expensemanager.dto.response.ExpenseResponse;
import com.expensemanager.service.ExpenseService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;



@RestController
@RequestMapping("/expenses")
@Tag(
    name = "Expenses",
    description = "Expense tracking APIs"
)
public class ExpenseController {


    private final ExpenseService expenseService;



    public ExpenseController(
            ExpenseService expenseService
    ) {

        this.expenseService = expenseService;
    }





    @Operation(
        summary = "Create expense",
        description = "Creates a new expense for logged-in user"
    )
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody ExpenseCreateRequest request
    ) {

        expenseService.create(request);

        return ResponseEntity.ok().build();
    }





    @Operation(
        summary = "Get latest expenses",
        description = "Returns last 20 active expenses"
    )
    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getLast20() {


        return ResponseEntity.ok(
                expenseService.getLast20()
        );
    }





    @Operation(
        summary = "Filter expenses",
        description = "Filters expenses using category, date and amount"
    )
    @GetMapping("/filter")
    public ResponseEntity<List<ExpenseResponse>> filter(
            ExpenseFilterRequest request
    ) {


        return ResponseEntity.ok(
                expenseService.filter(request)
        );
    }





    @Operation(
        summary = "Update expense",
        description = "Updates expense after ownership verification"
    )
    @PutMapping
    public ResponseEntity<?> update(
            @RequestBody ExpenseUpdateRequest request
    ) {


        expenseService.update(request);

        return ResponseEntity.ok().build();
    }





    @Operation(
        summary = "Soft delete expense",
        description = "Changes expense status to DELETED"
    )
    @PutMapping("/delete/{expenseId}")
    public ResponseEntity<?> delete(
            @PathVariable Long expenseId
    ) {


        expenseService.softDelete(expenseId);

        return ResponseEntity.ok().build();
    }

}