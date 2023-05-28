package com.konkuk.kubit.repository;

import com.konkuk.kubit.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
