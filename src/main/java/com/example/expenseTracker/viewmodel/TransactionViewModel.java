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
import java.util.List;

@Getter
@Setter
@Component("transactionViewModel")
@Scope("prototype")
public class TransactionViewModel {
    private final TransactionService transactionService;
    private final DataSource dataSource;
    private List<Transaction> transactions;

    private String title;
    private Double amount;
    private String category;
    private String localDate;

    private Integer selectedTransactionId;

    public TransactionViewModel(TransactionService transactionService, DataSource dataSource) {
        this.transactionService = transactionService;
        this.dataSource = dataSource;
    }

    @Init
    public void init(){
        loadTransactions();
    }

    public void loadTransactions(){
        transactions = transactionService.getAllTransactions();
    }

    //Add Transaction
    @Command
    @NotifyChange({
            "transactions",
            "title",
            "amount",
            "category",
            "localDate"
    })
    public void saveTransaction(){
        TransactionRequest request = new TransactionRequest();

        request.setTitle(title);
        request.setAmount(amount);
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
            "title",
            "amount",
            "category",
            "localDate",
            "selectedTransactionId"
    })
    public void updateTransaction(){
        TransactionRequest request = new TransactionRequest();
        request.setTitle(title);
        request.setAmount(amount);
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
            "selectedTransactionId"
    })
    public void editTransaction(
            @BindingParam("transaction")
            Transaction transaction) {

        selectedTransactionId = transaction.getId();
        title = transaction.getTitle();
        amount = transaction.getAmount();
        category = transaction.getCategory();
        localDate = transaction.getTransactionDate() != null
                ? transaction.getTransactionDate().toString()
                : null;
    }

    @Command
    @NotifyChange({
            "transactions",
            "title",
            "amount",
            "category",
            "localDate",
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
    }

}
