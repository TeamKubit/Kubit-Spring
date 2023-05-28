package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.Market;
import com.konkuk.kubit.domain.Transaction;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.Wallet;
import com.konkuk.kubit.domain.dto.CurrentPriceResponse;
import com.konkuk.kubit.exception.AppException;
import com.konkuk.kubit.exception.ErrorCode;
import com.konkuk.kubit.repository.MarketRepository;
import com.konkuk.kubit.repository.TransactionRepository;
import com.konkuk.kubit.repository.UserRepository;
import com.konkuk.kubit.repository.WalletRepository;
import com.konkuk.kubit.utils.TransactionUtil;
import com.konkuk.kubit.utils.UpbitApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;
    private final WalletRepository walletRepository;
    private UpbitApiClient upbitApiClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, MarketRepository marketRepository, WalletRepository walletRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.marketRepository = marketRepository;
        this.walletRepository = walletRepository;
        this.upbitApiClient = upbitApiClient;

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
            if (quantityBalance <= 0) {
                throw new AppException(ErrorCode.LACK_OF_QUANTITY, "거래 가능한 수량보다 요청 수량이 많아 거래가 불가능합니다.");
            }
            // create transaction
            savedEntity = transactionRequest(user, transactionType, market, requestPrice, quantity, charge, moneyBalance);
            // reduce wallet's quantity_available
            try {
                wallet.setQuantityAvailable(quantityBalance);
                walletRepository.save(wallet);
            } catch (Exception e) {
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

    @Scheduled(fixedRate = 5000)
    public void schedulingGetSnapshot() {

        //transaction 의 대기상태가 wait인 것 들을 찾는다.
        Optional<List<Transaction>> transactionList = transactionRepository.findAllByResultType("wait");
        if (transactionList.isPresent()) {
            List<Transaction> transactions = transactionList.get();
            for (Transaction transaction : transactions) {
                double requestPrice = transaction.getRequestPrice();
                // 순환 참조 오류 발생 가능... main branch 의 user service 코드 참조(dto 만들어서 처리)
                Market market = transaction.getMarketCode();
                String marketCode = market.getMarketCode();
                log.info("calling up-bit market info api about" + marketCode);
                //marketCode로 api불러오기
                CurrentPriceResponse[] response = upbitApiClient.callApi(marketCode);

                double currentPrice = response[0].getTrade_price();

                // 지정가 거래
                if (requestPrice > 0) {
                    limitTrade(currentPrice, transaction);
                }
                // 현재가 거래
                else {
                    currentTrade(currentPrice, transaction);
                }
            }
        }
        // 1. wait 중 현재가 거래인 것들
        //      - wait중인 것들의 마켓 정보를 가져온다
        //      - 마켓정보를 기반으로 업비트 api를 받아온다.
        //      - 현재가 거래인 것들은 해당 transaction의 complete price에 현재가정보(업비트api)를 바로 준다.
        // 2. wait 중 지정가 거래인 것들
        //      - 마켓정보를 가져온 후 업비트api를 가져와서 가격을본다
        //      - 매수의 경우 사용자 request_price보다 현재가가 더 낮으면 매수로 complete처리
        //      - 매도의 경우 사용자 request_price보다 현재가가 더 크면 매도로 complete처리
        //      - 두가지 경우가 모두 아니면 wait처리
    }

    private void limitTrade(double currentPrice, Transaction transaction) {
        if (transaction.getTransactionType().equals("BID")) {
            // 매수 성공
            if (transaction.getRequestPrice() > currentPrice) {
                transaction.setCompletePrice(currentPrice);
                transaction.setResultType("SUCCESS");
                transactionRepository.save(transaction);

                //TODO:: user 얻어서 해당 유저와 마켓코드를 가지는 wallet 의 quantity 와 total_price 업데이트
            }
        } else {
            // 매도 성공
            if (transaction.getRequestPrice() < currentPrice) {
                transaction.setCompletePrice(currentPrice);
                transaction.setResultType("SUCCESS");
                transactionRepository.save(transaction);
            }
        }
    }

    private void currentTrade(double currentPrice, Transaction transaction) {
        transaction.setCompletePrice(currentPrice);
        transaction.setResultType("SUCCESS");
        transactionRepository.save(transaction);
    }
}

