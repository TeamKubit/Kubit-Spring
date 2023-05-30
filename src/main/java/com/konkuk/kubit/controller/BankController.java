package com.konkuk.kubit.controller;

import com.konkuk.kubit.domain.User;
import com.konkuk.kubit.domain.dto.BankDto;
import com.konkuk.kubit.domain.dto.BankRequest;
import com.konkuk.kubit.domain.dto.ResultResponse;
import com.konkuk.kubit.service.BankService;
import com.konkuk.kubit.utils.GetUser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/bank")
public class BankController {
    private final BankService bankService;

    public BankController(BankService bankService){
        this.bankService = bankService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getBankList(@GetUser User user){
        List<BankDto> banks = bankService.getBanks(user);
        Map<String, Object> result = new HashMap<>();
        result.put("bankList", banks);
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(user.getUsername()+" 입출금 내역")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }
    @PutMapping("/")
    public ResponseEntity<?> createBankRequest(@GetUser User user, @RequestBody @Valid final BankRequest dto){
        BankDto resultBank = bankService.createBank(user, dto.getRequestType(), dto.getMoney());
        Map<String, Object> result = new HashMap<>();
        result.put("bank", resultBank);
        ResultResponse data = ResultResponse.builder()
                .result_code(200)
                .result_msg(user.getUsername()+" 입출금 완료")
                .detail(result)
                .build();
        return ResponseEntity.ok().body(data);
    }
}
