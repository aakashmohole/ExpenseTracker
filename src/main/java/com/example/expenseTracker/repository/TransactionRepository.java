package com.example.expenseTracker.repository;

import com.example.expenseTracker.entity.Transaction;
import com.example.expenseTracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    Transaction id(int id);
    List<Transaction> findByUser(User user);

}
