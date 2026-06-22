package com.example.expenseTracker.viewmodel;

import com.example.expenseTracker.dto.TransactionRequest;
import com.example.expenseTracker.entity.Transaction;
import com.example.expenseTracker.services.TransactionService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.util.Clients;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Getter
@Setter
@Component("transactionViewModel")
@Scope("prototype")
public class TransactionViewModel {
    private final TransactionService transactionService;
    private final DataSource dataSource;
    private List<Transaction> allTransactions = new ArrayList<>();
    private List<Transaction> transactions;

    private String title;
    private Double amount;
    private String category;
    private String localDate;
    private String transactionType = "EXPENSE";

    private Integer selectedTransactionId;

    private Double totalIncome = 0.0;
    private Double totalExpense = 0.0;
    private Double balance = 0.0;

    private String searchText;
    private String filterType = "ALL";


    public TransactionViewModel(TransactionService transactionService, DataSource dataSource) {
        this.transactionService = transactionService;
        this.dataSource = dataSource;
    }

    @Init
    public void init(){
        loadTransactions();
    }

    public void loadTransactions(){
        allTransactions = transactionService.getAllTransactions();
        updateSummary();
        applyCurrentFilters();
    }

    //Add Transaction
    @Command
    @NotifyChange({
            "transactions",
            "totalIncome",
            "totalExpense",
            "balance",
            "title",
            "amount",
            "category",
            "localDate",
            "transactionType"
    })
    public void saveTransaction(){
        TransactionRequest request = new TransactionRequest();

        request.setTitle(title);
        request.setAmount(getSignedAmount());
        request.setCategory(category);
        request.setTransactionDate(LocalDate.parse(localDate));

        transactionService.addTransaction(request);
        clearForm();
        loadTransactions();
    }

    //Update Transaction
    @Command
    @NotifyChange({
            "transactions",
            "totalIncome",
            "totalExpense",
            "balance",
            "title",
            "amount",
            "category",
            "localDate",
            "transactionType",
            "selectedTransactionId"
    })
    public void updateTransaction(){
        TransactionRequest request = new TransactionRequest();
        request.setTitle(title);
        request.setAmount(getSignedAmount());
        request.setCategory(category);
        request.setTransactionDate(LocalDate.parse(localDate));

        transactionService.updateTransaction(
                selectedTransactionId,
                request
        );
        selectedTransactionId = null;

        clearForm();
        loadTransactions();
    }

    @Command
    @NotifyChange({
            "title",
            "amount",
            "category",
            "localDate",
            "transactionType",
            "selectedTransactionId"
    })
    public void editTransaction(
            @BindingParam("transaction")
            Transaction transaction) {

        selectedTransactionId = transaction.getId();
        title = transaction.getTitle();
        amount = Math.abs(transaction.getAmount());
        category = transaction.getCategory();
        localDate = transaction.getTransactionDate() != null
                ? transaction.getTransactionDate().toString()
                : null;
        transactionType = transaction.getAmount() >= 0 ? "INCOME" : "EXPENSE";
    }

    @Command
    @NotifyChange({
            "transactions",
            "totalIncome",
            "totalExpense",
            "balance",
            "title",
            "amount",
            "category",
            "localDate",
            "transactionType",
            "selectedTransactionId"
    })
    public void deleteTransaction(
            @BindingParam("id")
            Integer id) {

        transactionService.deleteTransaction(id);
        if (id.equals(selectedTransactionId)) {
            selectedTransactionId = null;
            clearForm();
        }

        loadTransactions();
    }

    @Command
    @NotifyChange("transactions")
    public void applyFilters(){
        applyCurrentFilters();
    }

    @Command
    @NotifyChange({
            "transactions",
            "searchText",
            "filterType"
    })
    public void clearFilters(){
        searchText = null;
        filterType = "ALL";
        applyCurrentFilters();
    }

    public String getDisplayType(Transaction transaction) {
        return transaction.getAmount() >= 0 ? "Income" : "Expense";
    }

    public Double getDisplayAmount(Transaction transaction) {
        return Math.abs(transaction.getAmount());
    }

    @Command
    public void logout(){
        Sessions.getCurrent().removeAttribute("jwtToken");
        Sessions.getCurrent().removeAttribute("user");
        Clients.showNotification(
                "Logged out successfully",
                "info",
                null,
                "middle_center",
                2000
        );

        Executions.sendRedirect("/login.zul");
    }

    private void clearForm() {
        title = null;
        amount = null;
        category = null;
        localDate = null;
        transactionType = "EXPENSE";
    }

    private Double getSignedAmount() {
        double absoluteAmount = Math.abs(Objects.requireNonNullElse(amount, 0.0));
        return "INCOME".equals(transactionType) ? absoluteAmount : -absoluteAmount;
    }

    private void updateSummary() {
        totalIncome = allTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .filter(value -> value > 0)
                .sum();

        totalExpense = allTransactions.stream()
                .mapToDouble(Transaction::getAmount)
                .filter(value -> value < 0)
                .map(Math::abs)
                .sum();

        balance = totalIncome - totalExpense;
    }

    private void applyCurrentFilters() {
        String normalizedSearch = searchText == null
                ? ""
                : searchText.trim().toLowerCase(Locale.ROOT);

        transactions = allTransactions.stream()
                .filter(transaction -> matchesType(transaction)
                        && matchesSearch(transaction, normalizedSearch))
                .toList();
    }

    private boolean matchesType(Transaction transaction) {
        if (filterType == null || "ALL".equals(filterType)) {
            return true;
        }

        if ("INCOME".equals(filterType)) {
            return transaction.getAmount() > 0;
        }

        if ("EXPENSE".equals(filterType)) {
            return transaction.getAmount() < 0;
        }

        return true;
    }

    private boolean matchesSearch(
            Transaction transaction,
            String normalizedSearch) {

        if (normalizedSearch.isBlank()) {
            return true;
        }

        return contains(transaction.getTitle(), normalizedSearch)
                || contains(transaction.getCategory(), normalizedSearch)
                || contains(String.valueOf(transaction.getAmount()), normalizedSearch)
                || contains(String.valueOf(transaction.getTransactionDate()), normalizedSearch);
    }

    private boolean contains(String value, String normalizedSearch) {
        return value != null
                && value.toLowerCase(Locale.ROOT).contains(normalizedSearch);
    }



}
