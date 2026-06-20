package com.example.expenseTracker.services;

import com.example.expenseTracker.dto.TransactionRequest;
import com.example.expenseTracker.entity.Transaction;
import com.example.expenseTracker.entity.User;
import com.example.expenseTracker.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private final TransactionRepository transactionRepository;
    private final UserService userService;

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

}
