package com.konkuk.kubit.repository;

import com.konkuk.kubit.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
