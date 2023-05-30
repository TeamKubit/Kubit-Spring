package com.konkuk.kubit.event;

import com.konkuk.kubit.domain.Bank;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.repository.BankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserCreatedEventListener {

    @Autowired
    private BankRepository bankRepository;

    @EventListener
    public void handleUserCreatedEvent(UserCreatedEvent event) {
        // UserCreatedEvent 발생시, Bank에 첫 입금 내역 추가
        User user = event.getUser();

        Bank bank = Bank.builder()
                .bankType("DEPOSIT")
                .money(10000000)
                .tradeAt(LocalDateTime.now())
                .uId(user)
                .build();

        bankRepository.save(bank);
    }
}