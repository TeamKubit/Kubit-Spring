package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.Market;
import com.konkuk.kubit.domain.Transaction;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.Wallet;
import com.konkuk.kubit.domain.dto.CurrentPriceResponse;
import com.konkuk.kubit.domain.dto.TransactionDto;
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
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final MarketRepository marketRepository;
    private final WalletRepository walletRepository;
    private final UpbitApiClient upbitApiClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository, MarketRepository marketRepository, WalletRepository walletRepository, UpbitApiClient upbitApiClient) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.marketRepository = marketRepository;
        this.walletRepository = walletRepository;
        this.upbitApiClient = upbitApiClient;
    }

    public TransactionDto fixedPriceTransaction(User user, String transactionType, double requestPrice, String marketCode, double quantity) {
        //지정가 거래를 처리
        double totalPrice = requestPrice * quantity;
        int charge = TransactionUtil.getCharge(requestPrice, quantity);
        //get market from marketCode
        Market market = marketRepository.findByMarketCode(marketCode)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_MARKETCODE, "마켓 코드가 올바르지 않습니다."));
        Transaction savedEntity;
        // 1. BID : 매수
        if (Objects.equals(transactionType, "BID")) {
            // check for enough money
            double moneyBalance = user.getMoney() - totalPrice;
            if (moneyBalance - charge < 0)
                throw new AppException(ErrorCode.LACK_OF_BALANCE, "거래를 위한 잔액이 부족합니다");
            // create transaction
            savedEntity = transactionRequest(user, transactionType, market, requestPrice, quantity, charge);
            // reduce money of user(charge는 거래 완료 시에)
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
            savedEntity = transactionRequest(user, transactionType, market, requestPrice, quantity, charge);
            // reduce wallet's quantity_available
            try {
                wallet.setQuantityAvailable(quantityBalance);
                walletRepository.save(wallet);
            } catch (Exception e) {
                throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "wallet repository save error");
            }
        }
        System.out.println(savedEntity.getRequestTime());
        return new TransactionDto(savedEntity);
    }

    public TransactionDto bidMarketPriceTransaction(User user, String marketCode, int currentPrice, int totalPrice) {
        // 시장가 매수 거래를 처리 (요청된 현재가로 거래를 완료 처리)

        // 거래 수량 구하기
        double quantity = totalPrice / (double) currentPrice;
        int charge = TransactionUtil.getCharge(currentPrice, quantity);
        //get market from marketCode
        Market market = marketRepository.findByMarketCode(marketCode)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_MARKETCODE, "마켓 코드가 올바르지 않습니다."));
        Transaction savedEntity;
        // check for enough money
        double moneyBalance = user.getMoney() - totalPrice;
        if (moneyBalance - charge < 0)
            throw new AppException(ErrorCode.LACK_OF_BALANCE, "거래를 위한 잔액이 부족합니다");
        // create transaction as complete
        savedEntity = transactionComplete(user, "BID", market, currentPrice, quantity, charge);
        // reduce money of user(totalPrice + charge)
        try {
            user.setMoney(moneyBalance - charge);
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "user repository save error");
        }
        // increase quantity & quantity_available of wallet
        try {
            // wallet 찾아서 수량 추가해주거나 업데이트
            Optional<Wallet> optionalWallet = walletRepository.findByuIdAndMarketCode(user, market);
            Wallet wallet;
            if (optionalWallet.isEmpty()) {
                wallet = Wallet.builder()
                        .uId(user)
                        .marketCode(market)
                        .quantity(quantity)
                        .totalPrice(totalPrice)
                        .quantityAvailable(quantity)
                        .build();
            } else {
                wallet = optionalWallet.get();
                wallet.setQuantity(wallet.getQuantity() + quantity);
                wallet.setQuantityAvailable(wallet.getQuantityAvailable() + quantity);
                wallet.setTotalPrice(wallet.getTotalPrice() + totalPrice);
            }
            walletRepository.save(wallet);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "wallet repository save error");
        }
        return new TransactionDto(savedEntity);
    }

    public TransactionDto askMarketPriceTransaction(User user, String marketCode, int currentPrice, double quantity) {
        // 시장가 매도 거래를 처리
        int totalPrice = (int) (quantity * currentPrice);
        int charge = TransactionUtil.getCharge(currentPrice, quantity);

        //get market from marketCode
        Market market = marketRepository.findByMarketCode(marketCode)
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_MARKETCODE, "마켓 코드가 올바르지 않습니다."));
        Transaction savedEntity;

        Wallet wallet = walletRepository.findByuIdAndMarketCode(user, market)
                .orElseThrow(() -> new AppException(ErrorCode.LACK_OF_QUANTITY, "해당 종목을 소유하고 있지 않아 매도 주문이 불가합니다."));
        double quantityBalance = wallet.getQuantityAvailable() - quantity;
        if (quantityBalance <= 0) {
            throw new AppException(ErrorCode.LACK_OF_QUANTITY, "거래 가능한 수량보다 요청 수량이 많아 거래가 불가능합니다.");
        }
        // create transaction as complete
        savedEntity = transactionComplete(user, "ASK", market, currentPrice, quantity, charge);
        // increase money of user
        try {
            user.setMoney(user.getMoney() + totalPrice - charge);
            userRepository.save(user);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "user repository save error");
        }
        // reduce wallet's quantity_available & quantity & totalPrice
        try {
            wallet.setQuantityAvailable(quantityBalance);
            wallet.setQuantity(quantityBalance);
            wallet.setTotalPrice(totalPrice);
            walletRepository.save(wallet);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "wallet repository save error");
        }
        return new TransactionDto(savedEntity);
    }


    private Transaction transactionRequest(User user, String transactionType, Market market, double requestPrice, double quantity, int charge) {
        // requestPrice로 거래 등록
        Transaction requestTransaction = Transaction.builder()
                .uId(user)
                .resultType("WAIT")
                .transactionType(transactionType)
                .marketCode(market)
                .requestTime(LocalDateTime.now())
                .requestPrice(requestPrice)
                .quantity(quantity)
                .charge(charge)
                .build();
        // save transaction
        try {
            return transactionRepository.save(requestTransaction);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "transaction repository save error");
        }
    }

    private Transaction transactionComplete(User user, String transactionType, Market market, double requestPrice, double quantity, int charge) {
        // requestPrice로 거래 완료 처리
        Transaction requestTransaction = Transaction.builder()
                .uId(user)
                .resultType("SUCCESS")
                .transactionType(transactionType)
                .marketCode(market)
                .requestPrice(requestPrice)
                .completePrice(requestPrice)
                .quantity(quantity)
                .charge(charge)
                .requestTime(LocalDateTime.now())
                .completeTime(LocalDateTime.now())// 현재 시간
                .build();
        // save transaction
        try {
            return transactionRepository.save(requestTransaction);
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "transaction repository save error");
        }
    }

    public List<TransactionDto> getCompletedTransactions(User user) {
        List<TransactionDto> transactions = user.getTransactions().stream()
                .filter(transaction -> transaction.getResultType().equals("SUCCESS"))
                .map(TransactionDto::new)
                .collect(Collectors.toList());
        Collections.reverse(transactions);
        return transactions;
    }

    public List<TransactionDto> getRequestedTransactions(User user) {
        // 모든 거래 내역 반환
        List<TransactionDto> transactions = user.getTransactions().stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
        Collections.reverse(transactions);
        return transactions;
    }

    public List<TransactionDto> getWaitedTransactions(User user) {
        // 미체결 거래 내역 반환
        List<TransactionDto> transactions = user.getTransactions().stream()
                .filter(transaction -> transaction.getResultType().equals("WAIT"))
                .map(TransactionDto::new)
                .collect(Collectors.toList());
        Collections.reverse(transactions);
        return transactions;
    }
    public List<Long> cancelTransactions(User user, Long[] transactionIdList){
        List<Long> cancelTransactionList = new ArrayList<>();
        for(int i=0; i<transactionIdList.length; i++){
            try{
                Transaction t = transactionRepository.findByuIdAndTransactionId(user, transactionIdList[i]).get();
                if(t.getResultType().equals("WAIT")) {
                    if(t.getTransactionType().equals("BID")){
                        //매수 취소
                        user.setMoney(user.getMoney() + t.getQuantity() * t.getRequestPrice());
                        try {
                            userRepository.save(user);
                        } catch (Exception e) {
                            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "user repository save error");
                        }
                    }else{
                        //매도 취소
                        Wallet wallet = walletRepository.findByuIdAndMarketCode(user, t.getMarketCode())
                                .orElseThrow(() -> new AppException(ErrorCode.LOGIC_EXCEPTION, "매도 요청시 잘못되었습니다"));
                        wallet.setQuantityAvailable(wallet.getQuantityAvailable() + t.getQuantity());
                        try {
                            walletRepository.save(wallet);
                        } catch (Exception e) {
                            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "wallet repository save error");
                        }
                    }
                    t.setResultType("CANCEL");
                    t.setCompleteTime(LocalDateTime.now());
                    try{
                        transactionRepository.save(t);
                    }catch(Exception e){
                        throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "transaction repository save error");
                    }
                    cancelTransactionList.add(t.getTransactionId());
                }
            }
            catch (Exception e){
//                transactionIdList[i];
            }
        }
        return cancelTransactionList;
    }


    public TransactionDto cancelRequestedTransaction(User user, Long transactionId) {
        Transaction selectedTransaction = transactionRepository.findByuIdAndTransactionId(user, transactionId)
                .orElseThrow(() -> new AppException(ErrorCode.TRANSACTION_NOTFOUND, "해당하는 거래 내용이 없습니다"));
        if (selectedTransaction.getResultType().equals("SUCCESS"))
            throw new AppException(ErrorCode.TRANSACTION_NOTFOUND, "해당 거래는 이미 완료되어 취소가 불가능합니다");
        if (selectedTransaction.getResultType().equals("CANCEL"))
            throw new AppException(ErrorCode.TRANSACTION_NOTFOUND, "해당 거래는 이미 취소되었습니다");
        if (selectedTransaction.getTransactionType().equals("BID")) {
            // 매수의 경우, user money 업데이트
            user.setMoney(user.getMoney() + selectedTransaction.getQuantity() * selectedTransaction.getRequestPrice());
            try {
                userRepository.save(user);
            } catch (Exception e) {
                throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "user repository save error");
            }
        } else {
            // 매도의 경우, quantity_available 업데이트
            Wallet wallet = walletRepository.findByuIdAndMarketCode(user, selectedTransaction.getMarketCode())
                    .orElseThrow(() -> new AppException(ErrorCode.LOGIC_EXCEPTION, "매도 요청시 잘못되었습니다"));
            wallet.setQuantityAvailable(wallet.getQuantityAvailable() + selectedTransaction.getQuantity());
            try {
                walletRepository.save(wallet);
            } catch (Exception e) {
                throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "wallet repository save error");
            }
        }
        // transaction set
        selectedTransaction.setResultType("CANCEL");
        selectedTransaction.setCompleteTime(LocalDateTime.now());
        try {
            return new TransactionDto(transactionRepository.save(selectedTransaction));
        } catch (Exception e) {
            throw new AppException(ErrorCode.REPOSITORY_EXCEPTION, "transaction repository save error");
        }
    }


    @Scheduled(fixedRate = 5000)
    public void schedulingGetSnapshot() {

        //transaction 의 대기상태가 wait인 것 들을 찾는다.
        Optional<List<Transaction>> transactionList = transactionRepository.findAllByResultType("WAIT");
        if (transactionList.isPresent()) {
            List<Transaction> transactions = transactionList.get();
            for (Transaction transaction : transactions) {
                log.info(transaction.toString());

                double requestPrice = transaction.getRequestPrice();
                Market market = transaction.getMarketCode();
                String marketCode = market.getMarketCode();
                log.info("calling up-bit market info api about " + marketCode);
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
                transaction.setCompleteTime(LocalDateTime.now());
                transactionRepository.save(transaction);

                walletUpdate(transaction, transaction.getUId(), transaction.getMarketCode());
            }
        } else {
            // 매도 성공
            if (transaction.getRequestPrice() < currentPrice) {
                transaction.setCompletePrice(currentPrice);
                transaction.setResultType("SUCCESS");
                transaction.setCompleteTime(LocalDateTime.now());
                transactionRepository.save(transaction);

                walletUpdate(transaction, transaction.getUId(), transaction.getMarketCode());
            }
        }
    }

    private void currentTrade(double currentPrice, Transaction transaction) {
        transaction.setCompletePrice(currentPrice);
        transaction.setResultType("SUCCESS");
        transactionRepository.save(transaction);

        walletUpdate(transaction, transaction.getUId(), transaction.getMarketCode());
    }

    private void walletUpdate(Transaction transaction, User user, Market market) {
        Optional<Wallet> optionalWallet = walletRepository.findByuIdAndMarketCode(user, market);
        Wallet wallet = null;
        if (optionalWallet.isEmpty()) {
            wallet = Wallet.builder()
                    .uId(user)
                    .marketCode(market)
                    .quantity(0)
                    .totalPrice(0)
                    .quantityAvailable(0)
                    .build();

            walletRepository.save(wallet);
        } else {
            wallet = optionalWallet.get();
        }
        double tmp = wallet.getQuantity();


        if (transaction.getTransactionType().equals("BID")) {
            // 매수의 경우
            wallet.setQuantity(tmp + transaction.getQuantity());
            wallet.setQuantityAvailable(wallet.getQuantityAvailable() + transaction.getQuantity());
            wallet.setTotalPrice(wallet.getTotalPrice() + transaction.getQuantity() * transaction.getCompletePrice());

            //기존 request기준으로 매수금액이 빠져나갔는데 complete되면 차액만큼을 돌려준다.
            user.setMoney(user.getMoney() + transaction.getQuantity() * (transaction.getRequestPrice() - transaction.getCompletePrice()));
            //기존 request기준으로 있는 수수료를 환불
            user.setMoney(user.getMoney() + transaction.getCharge());
            //complete기준으로 수수료 다시 계산
            int total_charge = TransactionUtil.getCharge(transaction.getCompletePrice(), transaction.getQuantity());
            user.setMoney(user.getMoney() - total_charge);
            //transaction 수수료도 complete기준으로 수수료로 변경
            transaction.setCharge(total_charge);

        } else {
            // 매도의 경우
            wallet.setQuantity(tmp - transaction.getQuantity());
            int total_charge = TransactionUtil.getCharge(transaction.getCompletePrice(), transaction.getQuantity());
            double transactionTotalPrice = transaction.getQuantity() * transaction.getCompletePrice();

            user.setMoney(user.getMoney() + transactionTotalPrice - total_charge);
            wallet.setTotalPrice(wallet.getTotalPrice() - transactionTotalPrice);
        }
    }
}

