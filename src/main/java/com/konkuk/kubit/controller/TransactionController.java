package com.konkuk.kubit.controller;


import com.konkuk.kubit.domain.dto.*;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.service.TransactionService;
import com.konkuk.kubit.service.UserService;
import com.konkuk.kubit.utils.GetUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;
    private final UserService userService;

    public TransactionController(TransactionService transactionService,  UserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PostMapping("/fixed/")
    public ResponseEntity<?> requestFixedPriceTransaction(@GetUser User user, @RequestBody @Valid final FixedTransactionRequest dto) {
        TransactionDto transaction = transactionService.fixedPriceTransaction(user, dto.getTransactionType(), dto.getRequestPrice(), dto.getMarketCode(), dto.getQuantity());
        List<WalletDto> wallets = userService.getWalletOverall(user);
        Map<String, Object> result = new HashMap<>();
//        result.put("transaction",transaction);
        result.put("wallet", wallets);
        result.put("money", user.getMoney());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(dto.getTransactionType() + " 주문 완료")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/market/bid/")
    public ResponseEntity<?> bidMarketPriceTransaction(@GetUser User user, @RequestBody @Valid final BidTransactionRequest dto) {
        TransactionDto transaction = transactionService.bidMarketPriceTransaction(user, dto.getMarketCode(), dto.getCurrentPrice(), dto.getTotalPrice());
        List<WalletDto> wallets = userService.getWalletOverall(user);
        Map<String, Object> result = new HashMap<>();
//        result.put("transaction",transaction);
        result.put("wallet", wallets);
        result.put("money", user.getMoney());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg("매수 주문" + transaction.getQuantity() + " 개 거래 완료")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }
    @PostMapping("market/ask/")
    public ResponseEntity<?> askMarketPriceTransaction(@GetUser User user, @RequestBody @Valid final AskTransactionRequest dto){
        TransactionDto transaction = transactionService.askMarketPriceTransaction(user, dto.getMarketCode(), dto.getCurrentPrice(), dto.getQuantity());
        List<WalletDto> wallets = userService.getWalletOverall(user);
        Map<String, Object> result = new HashMap<>();
//        result.put("transaction",transaction);
        result.put("wallet", wallets);
        result.put("money", user.getMoney());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg("매도 주문" + transaction.getQuantity() + " 개 거래 완료")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/completes/")
    public ResponseEntity<?> getCompletedTransactions(@GetUser User user) {
        List<TransactionDto> transactionList = transactionService.getCompletedTransactions(user);
        Map<String, Object> result = new HashMap<>();
        result.put("transactionList", transactionList);
        result.put("userId", user.getUserId());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(user.getUserId() + "의 체결 내역")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/waits/")
    public ResponseEntity<?> getWaitedTransactions(@GetUser User user) {
        List<TransactionDto> transactionList = transactionService.getWaitedTransactions(user);
        Map<String, Object> result = new HashMap<>();
        result.put("transactionList", transactionList);
        result.put("userId", user.getUserId());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(user.getUserId() + "의 미체결 거래 내역")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }


    @GetMapping("/requests/")
    public ResponseEntity<?> getRequestedTransactions(@GetUser User user) {
        List<TransactionDto> transactionList = transactionService.getRequestedTransactions(user);
        Map<String, Object> result = new HashMap<>();
        result.put("transactionList", transactionList);
        result.put("userId", user.getUserId());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(user.getUserId() + "의 거래 내역")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }
    @PutMapping("/requests/")
    public ResponseEntity<?> cancelTransactions(@GetUser User user, @RequestBody @Valid final CancelTransactionList dto){
        List<Long> successList = transactionService.cancelTransactions(user, dto.getTransactionIdList());
        List<TransactionDto> transactionList = transactionService.getRequestedTransactions(user);
        List<WalletDto> wallets = userService.getWalletOverall(user);
        Map<String, Object> result = new HashMap<>();
        result.put("money", user.getMoney());
        result.put("successList" , successList);
        result.put("transactionList", transactionList);
        result.put("wallets",wallets);
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg("거래 취소")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @PutMapping("/requests/{transactionId}/")
    public ResponseEntity<?> cancelRequestedTransaction(@GetUser User user, @PathVariable("transactionId") Long transactionId){
        TransactionDto transaction = transactionService.cancelRequestedTransaction(user, transactionId);
        Map<String, Object> result = new HashMap<>();
        result.put("transaction", transaction);
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg("거래 취소 성공")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }
}
