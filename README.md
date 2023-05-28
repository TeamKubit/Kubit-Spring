# Kubit-Spring

`KUBIT` backend server with `Spring Boot` 

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
  | quantity        | Double  | 요청 거래 수량        |

- response

  | Key  | Type(?) | Description |
  | ---- | ------- | ----------- |
  |      |         |             |



## 체결 거래 내역 조회(T)

- url

  `{root}/api/v1/transaction/completes/`

- method

  `GET`

- response

  | Key  | Type(?) | Description |
  | ---- | ------- | ----------- |
  |      |         |             |



## 미체결 거래 내역 조회(T)

- url

  `{root}/api/v1/transaction/requests/`

- method

  `GET`

- response

  | Key  | Type(?) | Description |
  | ---- | ------- | ----------- |
  |      |         |             |



## 미체결 거래 내역 취소(T)

- url

  `{root}/api/v1/transaction/requests/{transaction_id}`

- method

  `PUT`

- response

  | Key  | Type(?) | Description |
  | ---- | ------- | ----------- |
  |      |         |             |



## 입출금 내역(T)

- url

  `{root}/api/v1/user/bank/`

- method

  `GET`

- response

  | Key  | Type(?) | Description |
  | ---- | ------- | ----------- |
  |      |         |             |



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

  | Key  | Type(?) | Description |
  | ---- | ------- | ----------- |
  |      |         |             |

입출금 제한도 처리



a가 이메일 요청을 해 -> 12345 생성 -> 이메일 전송

a가 검증 요청을 해 -> a string을 보내주겠지? -> 

