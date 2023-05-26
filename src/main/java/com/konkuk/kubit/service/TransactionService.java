package com.konkuk.kubit.service;

import com.konkuk.kubit.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private RestTemplate restTemplate;
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 330)
    public void schedulingGetSnapshot(){
        //transaction 의 대기상태가 wait인 것 들을 찾는다.
        // 1. wait 중 현재가 거래인 것들
        //      - wait중인 것들의 마켓 정보를 가져온다
        //      - 마켓정보를 기반으로 업비트 api를 받아온다.
        //      - 현재가 거래인 것들은 해당 transaction의 complete price에 현재가정보(업비트api)를 바로 준다.
        // 2. wait 중 지정가 거래인 것들
        //      - 마켓정보를 가져온 후 업비트api를 가져와서 가격을본다
        //      - 매수의 경우 사용자 request_price보다 현재가가 더 낮으면 매수로 complete처리
        //      - 매도의 경우 사용자 request_price보다 현재가가 더 크면 매도로 complete처리
        //      - 두가지 경우가 모두 아니면 wait처리
        String response = restTemplate.getForObject("https://api.upbit.com/v1/ticker", String.class);
    }
}
