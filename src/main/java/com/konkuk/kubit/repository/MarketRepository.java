package com.konkuk.kubit.repository;

import com.konkuk.kubit.domain.Market;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketRepository extends JpaRepository<Market, String> {
    Optional<Market> findByMarketCode(String marketCode);
}
