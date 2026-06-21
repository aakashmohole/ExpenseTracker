package com.example.expenseTracker.controller;

import com.example.expenseTracker.dto.TransactionRequest;
import com.example.expenseTracker.entity.Transaction;
import com.example.expenseTracker.services.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/transaction/")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<String>  addTransaction(@RequestBody TransactionRequest request){
        return ResponseEntity.ok(
                transactionService
                        .addTransaction(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(
            @PathVariable int id) {

        return ResponseEntity.ok(
                transactionService
                        .deleteTransaction(id));
    }

    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions(){
        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Integer id){
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateTransaction(@PathVariable Integer id, @RequestBody TransactionRequest request){
        return ResponseEntity.ok(
                transactionService
                        .updateTransaction(id, request)
        );
    }
}
