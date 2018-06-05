# Forex Guru
This is a project for experimenting with Forex data , Spring Security (OAuth2), Technical Analysis (TA4J), and 
AWS Simple Email Service.

## Endpoints

```
/signals
```
Gives a rate for each of the major currency pairs in the Forex market based on all implemented signals. I positive rate 
indicates buy while a negative rate indicates sell. The greater the value is in either direction, the stronger the signal.

```
/dailyindicator?type={INDICATOR}&symbol={CURRENCY_PAIR}&trailing={DAYS}
```
Gives a decimal value for the given indicator, currency pair and trailing days.

The indicators implemented (using TA4J) include:
* SMA - Simple Moving Average
* EMA - Exponential Moving Average

The currency pairs tracked include:
* EURUSD
* USDJPY 
* GBPUSD
* USDCAD
* USDCHF
* AUDUSD
* NZDUSD

## Deployment

Clone this repo using ```git clone https://github.com/kevgraham/forex_guru.git```

Create a MySQL Database using the SQL dump at ```/resources/dump.sql```.

## Security
This API is secured with OAuth2. To access secure endpoints you need to exchange your client
credentials for an access token.

You can register client credentials by making a POST request to the Authorization Server with the
client_id and client_secret of your choosing.
```
curl -X POST -H 'Content-Type: application/json' -d '{"client_id": "guru", "client_secret": "secret", "scope": "read,write", "authorized_grant_types":"client_credentials"}' http://localhost:8080/oauth/client
```

In order to obtain an access token you should make a POST request to the Authorization Server with your client credentials.

```
curl -X POST --user 'guru:secret' -d 'grant_type=client_credentials' http://localhost:8080/oauth/token
```

This will return an access token that will expire after the allotted time has passed.
You can make a request to any endpoint using the access token in the authorization header.

```
curl -X GET -H "Authorization: Bearer {ACCESS_TOKEN}" http://localhost:8080/signals
```

