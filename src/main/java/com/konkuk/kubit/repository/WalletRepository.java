package com.konkuk.kubit.repository;

import com.konkuk.kubit.domain.Market;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<Wallet> findByMarketCode(Market marketCode);

    Optional<Wallet> findByuIdAndMarketCode(User user, Market marketCode);
}
