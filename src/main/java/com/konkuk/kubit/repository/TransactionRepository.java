package com.konkuk.kubit.repository;

import com.konkuk.kubit.domain.Transaction;
import com.konkuk.kubit.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository  extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionType(String type);

    Optional<List<Transaction>> findAllByResultType(String type);

    Optional<Transaction> findByuIdAndTransactionId(User user, Long transactionId);


}
