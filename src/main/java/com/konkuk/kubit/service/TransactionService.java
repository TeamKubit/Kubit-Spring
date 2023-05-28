package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.Market;
import com.konkuk.kubit.domain.Transaction;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.Wallet;
import com.konkuk.kubit.exception.AppException;
import com.konkuk.kubit.exception.ErrorCode;
import com.konkuk.kubit.repository.MarketRepository;
import com.konkuk.kubit.repository.TransactionRepository;
import com.konkuk.kubit.repository.UserRepository;
import com.konkuk.kubit.repository.WalletRepository;
import com.konkuk.kubit.utils.TransactionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;
    private final WalletRepository walletRepository;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, MarketRepository marketRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.marketRepository = marketRepository;
        this.walletRepository = walletRepository;
    }

    public Long fixedPriceTransaction(User user, String transactionType, double requestPrice, String marketCode, double quantity) {
        double totalPrice = requestPrice * quantity;
        double charge = TransactionUtil.getCharge(requestPrice, quantity);
        double moneyBalance = user.getMoney() - totalPrice - charge;
        System.out.println(transactionType);
        //get market from marketCode
        Market market = marketRepository.findByMarketCode(marketCode)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_MARKETCODE, "마켓 코드가 올바르지 않습니다."));
        Transaction savedEntity;
        // 1. BID : 매수
        if (Objects.equals(transactionType, "BID")) {
            System.out.println("BIDDID");
            // check for enough money
            if (moneyBalance < 0)
                throw new AppException(ErrorCode.LACK_OF_BALANCE, "거래를 위한 잔액이 부족합니다");
            // create transaction
            savedEntity = transactionRequest(user, transactionType, market, requestPrice, quantity, charge, moneyBalance);
            // reduce money of user(charge?)
            try {
                user.setMoney(moneyBalance);
                userRepository.save(user);
            } catch (Exception e) {
                throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "user repository save error");
            }
        }
        // 2. ASK : 매도
        else {
            // check for enough quantity
            Wallet wallet = walletRepository.findByuIdAndMarketCode(user, market)
                    .orElseThrow(() -> new AppException(ErrorCode.LACK_OF_QUANTITY, "해당 종목을 소유하고 있지 않아 매도 주문이 불가합니다."));
            double quantityBalance = wallet.getQuantityAvailable() - quantity;
            if(quantityBalance <= 0){
                throw new AppException(ErrorCode.LACK_OF_QUANTITY, "거래 가능한 수량보다 요청 수량이 많아 거래가 불가능합니다.");
            }
            // create transaction
            savedEntity = transactionRequest(user, transactionType, market, requestPrice, quantity, charge, moneyBalance);
            // reduce wallet's quantity_available
            try{
                wallet.setQuantityAvailable(quantityBalance);
                walletRepository.save(wallet);
            }catch (Exception e){
                throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "wallet repository save error");
            }
        }
        return savedEntity.getTransactionId();

    }

    private Transaction transactionRequest(User user, String transactionType, Market market, double requestPrice, double quantity, double charge, double balance) {
        Transaction bidTransaction = Transaction.builder()
                .user(user)
                .resultType("WAIT")
                .transactionType(transactionType)
                .marketCode(market)
                .requestPrice(requestPrice)
                .quantity(quantity)
                .charge(charge)
                .build();
        // save transaction
        try {
            return transactionRepository.save(bidTransaction);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "transaction repository save error");
        }
    }
}
