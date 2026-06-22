package com.example.expenseTracker.services;

import com.example.expenseTracker.dto.TransactionRequest;
import com.example.expenseTracker.entity.Transaction;
import com.example.expenseTracker.entity.User;
import com.example.expenseTracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final UserService userService;

    @CacheEvict(value = "userTransactions", key = "@userService.getCurrentEmail()")
    public String addTransaction(TransactionRequest request){
        logger.info("Transaction add request received: {}", request.getTitle());
        User user = userService.getCurrentUser();

        Transaction transaction = new Transaction();

        transaction.setTitle(request.getTitle());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setUser(user);

        transactionRepository.save(transaction);
        logger.info("Transaction added successfully with Id: {}", transaction.getId());
        return "New Transaction added and id is" + transaction.getId();
    }

    @CacheEvict(value = "userTransactions", key = "@userService.getCurrentEmail()")
    public String deleteTransaction(int transactionId){
        logger.info("Transaction Delete request received with id: {}", transactionId);
        User currentUser = userService.getCurrentUser();

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found!"));

        if (transaction.getUser().getId() != currentUser.getId()) {
            logger.warn("{} user is not authorized to delete this transaction!",currentUser.getEmail());
            throw new RuntimeException("You are not authorized to delete this transaction!");
        }

        transactionRepository.delete(transaction);
        return "Transaction deleted successfully";
    }

    @Cacheable(value = "userTransactions", key = "@userService.getCurrentEmail()")
    public List<Transaction> getAllTransactions(){
        logger.info("Loading transactions from database");
        User currUser = userService.getCurrentUser();
        return transactionRepository.findByUser(currUser);
    }

    public Transaction getTransactionById(Integer transactionId){
        User user = userService.getCurrentUser();

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if(transaction.getUser().getId() != user.getId()){
            throw new RuntimeException(
                    "Access denied");
        }
        return transaction;
    }

    @CacheEvict(value = "userTransactions", key = "@userService.getCurrentEmail()")
    public String updateTransaction(Integer transactionId, TransactionRequest request){
        logger.info("request for update transaction!");
        User user = userService.getCurrentUser();

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));

        if(user.getId() != transaction.getUser().getId()){
            logger.warn("Access denied");
            throw new RuntimeException("Access denied");
        }

        transaction.setTitle(request.getTitle());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory());
        transaction.setTransactionDate(request.getTransactionDate());

        transactionRepository.save(transaction);
        logger.info("Transaction Updated Successfully");
        return "Transaction Updated Successfully";
    }
}
