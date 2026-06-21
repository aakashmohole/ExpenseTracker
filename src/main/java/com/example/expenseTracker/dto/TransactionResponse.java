package com.example.expenseTracker.dto;

import java.time.LocalDate;

public class TransactionResponse {
    private Integer id;
    private String title;
    private Double amount;
    private String category;
    private LocalDate transactionDate;
}
