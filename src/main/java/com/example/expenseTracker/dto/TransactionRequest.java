package com.example.expenseTracker.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class TransactionRequest {
    private String title;
    private double amount;
    private String category;
    private LocalDate transactionDate;
}

