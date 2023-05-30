package com.konkuk.kubit.controller;


import com.konkuk.kubit.domain.dto.MarketTransactionRequest;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.dto.FixedTransactionRequest;
import com.konkuk.kubit.domain.dto.ResultResponse;
import com.konkuk.kubit.domain.dto.TransactionDto;
import com.konkuk.kubit.service.TransactionService;
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

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/fixed")
    public ResponseEntity<?> requestFixedPriceTransaction(@GetUser User user, @RequestBody @Valid final FixedTransactionRequest dto) {
        Long transactionId = transactionService.fixedPriceTransaction(user, dto.getTransactionType(), dto.getRequestPrice(), dto.getMarketCode(), dto.getQuantity());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(dto.getTransactionType() + " 주문 완료")
                .detail(transactionId)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @PostMapping("/market")
    public ResponseEntity<?> requestMarketPriceTransaction(@GetUser User user, @RequestBody @Valid final MarketTransactionRequest dto) {
        double transactionResult = transactionService.marketPriceTransaction(user, dto.getTransactionType(), dto.getMarketCode(), dto.getCurrentPrice(), dto.getTotalPrice());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(dto.getTransactionType() + " 주문" + transactionResult + " 개 거래 완료")
                .detail(transactionResult)
                .build();
        return ResponseEntity.ok().body(data);
    }

    @GetMapping("/completes")
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

    @GetMapping("/requests")
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

    @PutMapping("/requests/{transactionId}")
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
