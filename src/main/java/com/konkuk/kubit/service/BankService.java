package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.Bank;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.dto.BankDto;
import com.konkuk.kubit.exception.AppException;
import com.konkuk.kubit.exception.ErrorCode;
import com.konkuk.kubit.repository.BankRepository;
import com.konkuk.kubit.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class BankService {
    private final UserRepository userRepository;
    private final BankRepository bankRepository;

    public BankService(UserRepository userRepository, BankRepository bankRepository) {
        this.userRepository = userRepository;
        this.bankRepository = bankRepository;
    }

    public List<BankDto> getBanks(User user) {
        return user.getBanks().stream()
                .map(BankDto::new)
                .collect(Collectors.toList());
    }

    public BankDto createBank(User user, String requestType, int money) {
        if (requestType.equals("DEPOSIT")) {
            // 입금
            // check depositLimit
            if (user.getDepositCount() >= 3) {
                throw new AppException(ErrorCode.LACK_OF_BALANCE, "입금 횟수 제한 3회를 이미 사용하였습니다");
            }
            user.setMoney(user.getMoney() + money);
            user.setDepositCount(user.getDepositCount() + 1);
        } else {
            // 출금
            //check enough balance for withdraw
            if (user.getMoney() < money) {
                throw new AppException(ErrorCode.LACK_OF_BALANCE, "출금할 잔액이 부족합니다");
            }
            user.setMoney(user.getMoney() - money);
        }
        try {
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "user repository exception");
        }
        Bank bank = Bank.builder()
                .uId(user)
                .bankType(requestType)
                .money(money)
                .tradeAt(LocalDateTime.now())
                .build();
        try{
            bankRepository.save(bank);
        }catch (Exception e){
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION,"bank repository exception");
        }
        return new BankDto(bank);
    }

}
