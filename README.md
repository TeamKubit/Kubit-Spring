# Kubit-Spring

`KUBIT` backend server with `Spring Boot` 

# ISSUE



## cloud server setting

- spring build

  gradle -> task -> build -> bootjar 더블클릭하여 jar 파일 빌드 (/build 디렉토리에서 확인)

  ```
  docker buildx build --platform linux/amd64 --load -t choieastsea/kubit-docker ./
  docker push choieastsea/kubit-docker
  ```

- Docker & Docker compose 깔기

  https://choo.oopy.io/5c999170-dde5-4418-addc-00a0d263287c

  `docker-compose.yml`

  ```
  version: "3"
  
  services:
  
          application:
                  image: kubit_server
                  environment:
                          SPRING_DATASOURCE_URL : jdbc:mysql://34.22.70.64:3306/kubit_test
                          SPRING_DATASOURCE_USERNAME : root
                          SPRING_DATASOURCE_PASSWORD : 12341234
                  restart: always
                  container_name : kubit_container
                  ports:
                          - "8080:8080"
  ```

  `Dockerfile`

  ```dockerfile
  FROM openjdk:11-jdk-slim-buster
  COPY build/libs/kubit-0.0.1-SNAPSHOT.jar app.jar
  EXPOSE 8080
  ENTRYPOINT ["java", "-jar", "/app.jar"]
  ```

- 설치 이후 실행 방법

  docker repository를 pull 받아서 컴포즈로 말아서 실행시킨다

  ```shell
  choieastsea@kubit-server:/$ sudo docker pull choieastsea/kubit-docker
  choieastsea@kubit-server:/$ sudo docker tag choieastsea/kubit-docker kubit_server
  choieastsea@kubit-server:/$ sudo docker-compose up
  ```

  

# Schema

https://dbdiagram.io/d/644b7741dca9fb07c43105f5를 참고한다

![schema](schema.png)

# API

인증이 필요한 경우에는 `(T)` 로 표기하겠음!! 해당 경우에는 Request HTTP **Headers**에

- key : `Authorization`

- value :  `Bearer {access_token}`

  을 넣어줘야 **403** 에러가 뜨지 않을 것임!

## Login

로그인 요청시, 성공여부와 함께 (성공시) 토큰 정보를 반환한다.

- url

  `{root}/api/v1/user/login/`

- method

  `POST`

- request body

  | Key      | Type(?) | Description    |
  | -------- | ------- | -------------- |
  | userId   | String  | 사용자 계정 ID |
  | password | String  | 사용자 계정 PW |

- response

  | Key            | Type(?) | Description                                                  |
  | -------------- | ------- | ------------------------------------------------------------ |
  | result_code    | int     | 200 : 성공<br />400 : 입력 잘못한 경우 / <br />404 : 해당하는 사용자가 없는 경우,<br />500 : 버그 |
  | result_message | String  | 200 : 로그인 성공<br />400 : HttpMessageNotReadableException / missing parameters<br />401 : 비밀번호 틀렸습니다 <br />404 : {userId}에 해당하는 사용자가 없습니다<br />500 : ? |
  | access_token   | String? | jwt access token (1시간 만료)                                |
  | refresh_token  | String? | jwt refresh token (7일 만료)                                 |

## Refresh

로그인 되었는데, access_token 만료되어 **403** 리턴시, `refresh_token`으로 토큰 재생성한다

- url

  `{root}/api/v1/user/refresh/`

- method

  `POST`

- request body

  | Key          | Type(?) | Description       |
  | ------------ | ------- | ----------------- |
  | refreshToken | String  | jwt refresh token |

- response

  ```
  {
      "grantType": "Bearer",
      "accessToken": "",
      "refreshToken": ""
  }
  ```

  or 500 error (token 값 이상한 경우)

## 지갑 기본 정보(T)

로그인 성공 이후, 로딩창에서 사용자에게 필요한 정보(잔액 + 지갑 정보) 제공

- url

  `{root}/api/v1/user/wallet_overall`

- method

  `GET`

- response

  quantity : 갖고 있는 수량

  quantityAvailable : 매도 거래 가능한 수량

```json
{
    "result_code": 200,
    "result_msg": "지갑 정보",
    "detail": {
        "wallet": [
            {
                "marketCode": "KRW-BTC",
                "quantityAvailable": 1.1,
                "quantity": 1.1,
                "totalPrice": 1.1E7
            },
            {
                "marketCode": "KRW-ETH",
                "quantityAvailable": 0.6,
                "quantity": 0.9,
                "totalPrice": 99000.0
            }
        ],
        "money": 893950.0
    }
}
```



## 지정가 거래 요청(T)

- url

  `{root}/api/v1/transaction/fixed`

- method

  `POST`

- request body

  | Key             | Type(?) | Description                       |
  | --------------- | ------- | --------------------------------- |
  | transactionType | String  | BID(매수) / ASK(매도)             |
  | marketCode      | String  | 거래할 마켓                       |
  | requestPrice    | Double  | 요청 가격 (**1코인당 가격 의미**) |
  | quantity        | Double  | 요청 거래 수량                    |

- response

  - 매수시
    - totalPrice = quantity * requestPrice만큼 user 돈을 뺌
    - 수수료는 아직 안냄
  - 매도시
    - quantity만큼 wallet에 있는지 확인
    - 수수료는 아직 안냄
  - 정상 케이스

  ```json
  {
      "result_code": 200,
      "result_msg": "ASK 주문 완료",
      "detail": 1	//transactionId
  }
  ```

  - 오류 케이스

  ```json
  {
      "result_code": 400,
      "result_msg": "거래 가능한 수량보다 요청 수량이 많아 거래가 불가능합니다.",
      "detail": null
  }
  
  {
      "result_code": 400,
      "result_msg": "마켓 코드가 올바르지 않습니다.",
      "detail": null
  }
  
  {
      "result_code": 402,
      "result_msg": "거래를 위한 잔액이 부족합니다",
      "detail": null
  }
  ```



## 시장가 거래 요청(T)

- url

  `{root}/api/v1/transaction/market`

- method

  `POST`

- request body

  | Key             | Type(?) | Description           |
  | --------------- | ------- | --------------------- |
  | transactionType | String  | BID(매수) / ASK(매도) |
  | marketCode      | String  | 거래할 마켓           |
  | currentPrice    | int     | 현재 snapshot의 가격  |
  | totalPrice      | int     | 매수/매도할 거래 금액 |

- response

  ```json
  {
      "result_code": 200,
      "result_msg": "BID 주문0.02631578947368421 개 거래 완료",
      "detail": 0.02631578947368421
  }
  ```
  



## 체결 거래 내역 조회(T)

- url

  `{root}/api/v1/transaction/completes/`

- method

  `GET`

- response

  ```json
  {
      "result_code": 200,
      "result_msg": "testid의 체결 내역",
      "detail": {
          "transactionList": [
              {
                  "transactionId": 1,
                  "marketCode": "KRW-BTC",
                  "quantity": 0.01,
                  "transactionType": "BID",
                  "completeTime": null,
                  "resultType": "COMPLETE",
                  "charge": 17989.649,
                  "requestPrice": 3.5979298E7,
                  "completePrice": 3.5979298E7
              }
          ],
          "userId": "testid"
      }
  }
  ```

  

## 거래 내역 조회(T)

- url

  `{root}/api/v1/transaction/requests/`

- method

  `GET`

- response

  - 정상 케이스

  ```json
  {
      "result_code": 200,
      "result_msg": "testid의 거래 내역",
      "detail": {
          "transactionList": [
              {
                  "transactionId": 1,
                  "marketCode": "KRW-BTC",
                  "quantity": 0.01,
                  "transactionType": "BID",
                  "completeTime": null,
                  "resultType": "WAIT",
                  "charge": 17989.649,
                  "requestPrice": 3.5979298E7,
                  "completePrice": 0.0
              },
              {
                  "transactionId": 2,
                  "marketCode": "KRW-BTC",
                  "quantity": 0.01,
                  "transactionType": "BID",
                  "completeTime": null,
                  "resultType": "WAIT",
                  "charge": 17989.649,
                  "requestPrice": 3.5979298E7,
                  "completePrice": 0.0
              },
            	{
                  "transactionId": 3,
                  "marketCode": "KRW-BTC",
                  "quantity": 0.01,
                  "transactionType": "BID",
                  "completeTime": null,
                  "resultType": "COMPLETE",
                  "charge": 17989.649,
                  "requestPrice": 3.5979298E7,
                  "completePrice": 3.5979298E7
              }
          ],
          "userId": "testid"
      }
  }
  ```
  
  

## 미체결 거래 내역 취소(T)

- url

  `{root}/api/v1/transaction/requests/{transaction_id}`

- method

  `PUT`

- response

  - 정상 케이스

  ```json
  {
      "result_code": 200,
      "result_msg": "거래 취소 성공",
      "detail": {
          "transaction": {
              "transactionId": 8,
              "marketCode": "KRW-BTC",
              "quantity": 0.01,
              "transactionType": "BID",
              "completeTime": "2023-05-30T01:53:09.176654",
              "resultType": "CANCEL",
              "charge": 17989.0,
              "requestPrice": 3.5979298E7,
              "completePrice": 0.0
          }
      }
  }
  ```

  - 오류 케이스

  ```json
  {
      "result_code": 404,
      "result_msg": "해당 거래는 이미 취소되었습니다",
      "detail": null
  }
  
  {
      "result_code": 404,
      "result_msg": "해당하는 거래 내용이 없습니다",
      "detail": null
  }
  ```

  

## 입출금 내역(T)

- url

  `{root}/api/v1/user/bank/`

- method

  `GET`

- response

  ```json
  {
      "result_code": 200,
      "result_msg": "testname 입출금 내역",
      "detail": {
          "bankList": [
              {
                  "bankType": "DEPOSIT",
                  "money": 10000000,
                  "tradeAt": "2023-05-30T19:57:34.131833"
              },
              {
                  "bankType": "WITHDRAW",
                  "money": 100000,
                  "tradeAt": "2023-05-30T23:27:23.655242"
              }
          ]
      }
  }
  ```
  



## 입출금 요청(T)

- url

  `{root}/api/v1/user/bank/`

- method

  `POST`

- request body

  | Key          | Type(?) | Description |
  | ------------ | ------- | ----------- |
  | request_type | String  | 입금 / 출금 |
  | money        | int     | 요청 금액   |

- response

  ```json
  {
      "result_code": 200,
      "result_msg": "testname 입출금 완료",
      "detail": {
          "bank": {
              "bankType": "DEPOSIT",
              "money": 5000000,
              "tradeAt": "2023-05-30T23:49:05.678111"
          }
      }
  }
  ```
  
  ```json
  {
      "result_code": 400,
      "result_msg": "missing parameters",
      "detail": {
          "requestType": "requestType은 DEPOSIT이나 WITHDRAW로만 구성되어야 합니다."
      }
  }
  ```
  
  ```json
  {
      "result_code": 402,
      "result_msg": "입금 횟수 제한 3회를 이미 사용하였습니다",
      "detail": null
  }
  ```
