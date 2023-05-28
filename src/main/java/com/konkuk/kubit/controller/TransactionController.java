package com.konkuk.kubit.controller;


import com.konkuk.kubit.domain.Market;
import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.dto.FixedTransactionRequest;
import com.konkuk.kubit.domain.dto.ResultResponse;
import com.konkuk.kubit.exception.AppException;
import com.konkuk.kubit.exception.ErrorCode;
import com.konkuk.kubit.service.TransactionService;
import com.konkuk.kubit.utils.GetUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/transaction")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/fixed")
    public ResponseEntity<?> requestFixedPriceTransaction(@GetUser User user, @RequestBody @Valid final FixedTransactionRequest dto) {
        Long transactionId = transactionService.fixedPriceTransaction(user, dto.getTransactionType() ,dto.getRequestPrice(), dto.getMarketCode(), dto.getQuantity());
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(dto.getTransactionType()+" 주문 완료")
                .detail(transactionId)
                .build();
        return ResponseEntity.ok().body(data);
    }
}
