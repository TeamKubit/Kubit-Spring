package com.konkuk.kubit.repository;

import com.konkuk.kubit.domain.Bank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankRepository extends JpaRepository<Bank, Long> {
}
