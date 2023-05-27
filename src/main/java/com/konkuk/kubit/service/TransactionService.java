package com.konkuk.kubit.service;

import com.konkuk.kubit.domain.Market;
import com.konkuk.kubit.domain.Transaction;
import com.konkuk.kubit.domain.dto.CurrentPriceResponse;
import com.konkuk.kubit.repository.TransactionRepository;
import com.konkuk.kubit.utils.UpbitApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private UpbitApiClient upbitApiClient;
    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UpbitApiClient upbitApiClient) {
        this.transactionRepository = transactionRepository;
        this.upbitApiClient = upbitApiClient;
    }

    @Scheduled(fixedRate = 5000)
    public void schedulingGetSnapshot(){

        //transaction 의 대기상태가 wait인 것 들을 찾는다.
        Optional<List<Transaction>> transactionList =  transactionRepository.findAllByResultType("wait");
        if(transactionList.isPresent()){
            List<Transaction> transactions = transactionList.get();
            for (Transaction transaction: transactions) {
                double requestPrice = transaction.getRequestPrice();
                // 순환 참조 오류 발생 가능... main branch 의 user service 코드 참조(dto 만들어서 처리)
                Market market = transaction.getMarketCode();
                String marketCode = market.getMarketCode();
                log.info("calling up-bit market info api about"+marketCode);
                //marketCode로 api불러오기
                CurrentPriceResponse[] response = upbitApiClient.callApi(marketCode);

                double currentPrice = response[0].getTrade_price();

                // 지정가 거래
                if(requestPrice>0){
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

    private void limitTrade(double currentPrice, Transaction transaction){
        if(transaction.getTransactionType().equals("BID")){
            // 매수 성공
            if(transaction.getRequestPrice() > currentPrice) {
                transaction.setCompletePrice(currentPrice);
                transaction.setResultType("SUCCESS");
                transactionRepository.save(transaction);
                //TODO:: user 얻어서 해당 유저와 마켓코드를 가지는 wallet 의 quantity 와 total_price 업데이트
            }
        }else {
            // 매도 성공
            if(transaction.getRequestPrice() < currentPrice) {
                transaction.setCompletePrice(currentPrice);
                transaction.setResultType("SUCCESS");
                transactionRepository.save(transaction);
            }
        }
    }

    private void currentTrade(double currentPrice, Transaction transaction){
        transaction.setCompletePrice(currentPrice);
        transaction.setResultType("SUCCESS");
        transactionRepository.save(transaction);
    }
}
