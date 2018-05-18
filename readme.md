# Forex Guru
This is a project for experimenting with Forex, Spring Security (OAuth2), Technical Analysis (TA4J), and 
AWS Simple Email Service.

## Deployment

Create a MySQL schema called `ForexGuru` using the SQL dump.

## Usage
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
curl -X GET -H "Authorization: Bearer {ACCESS_TOKEN}" http://localhost:8080/indicators
```

