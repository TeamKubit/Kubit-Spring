# Kubit-Spring

`KUBIT` backend server with `Spring Boot` 

# Schema

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

로그인 성공 이후, 로딩창에서 사용자에게 필요한 정보 제공

- url

  `{root}/api/v1/user/wallet_overall`

- method

  `GET`

- response

```json
{
    "result_code": 200,
    "result_msg": "지갑 정보",
    "detail": [
        {
            "marketCode": "KRW_BTC",
            "quantity": 1.0,
            "totalPrice": 10000.0
        },
        {
            "marketCode": "KRW_ETH",
            "quantity": 2.0,
            "totalPrice": 2.0E7
        }
    ]
}
```



## 지정가 거래 요청(T)

- url

  `{root}/api/v1/transaction/fixed/{bid/ask}`

  - bid : 매수

  - ask : 매도

- method

  `POST`

- request body

  | Key           | Type(?) | Description                  |
  | ------------- | ------- | ---------------------------- |
  | market_code   | String  | 거래할 마켓                  |
  | request_price | Double  | 요청 가격(1코인당 가격 의미) |
  | quantity      | Double  | 요청 거래 수량               |

- response



## 시장가 거래 요청(T)

- url

  `{root}/api/v1/transaction/market/{bid/ask}`

  - bid : 매수
  - ask : 매도

- method

  `POST`

- request body

  | Key         | Type(?) | Description    |
  | ----------- | ------- | -------------- |
  | market_code | String  | 거래할 마켓    |
  | quantity    | Double  | 요청 거래 수량 |

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

