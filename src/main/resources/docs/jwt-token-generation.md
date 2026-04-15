# JWT Token Generation

For Token Generation remember that you must first :

1. Request API access by emailing to [patrnership@r2capital.co](mailto:patrnership@r2capital.co)

Once you have received you KeyID and Secrets, you can generate JWT Tokens as follows

# Golang

```go
package main

import (
	"errors"
	"sync"
	"time"

	"log"

	"github.com/golang-jwt/jwt"
)

var (
	jwtMutex sync.Mutex
)

func main() {
	password := "SecretPasswordGivenByR2"
	kid := "keyIdGivenByR2"
  // MerchantId is optional, if you don't want to filter by merchant, send an empty string as ""
  // But is required for embedabble components
  mid := "MerchantId"
  // Optional if you only operate on one country
  // Otherwise include the ISO_2 country code
  country := "MX"

	jwtToken, err := GenerateJWT(kid, password, mid, country)
	if err != nil {
		log.Println("Error generating jwt ", err.Error())
	}
	log.Println("Jwt generated ", jwtToken)
}

func GenerateJWT(kid, mySecretKey, merchantId, country string) (jwtToken string, err error) {
	// if you are not into a http handler or goroutine, you should add a unique access to this function.
	jwtMutex.Lock()
	defer jwtMutex.Unlock()
	// End of unique access
	
	if kid == "" || mySecretKey == "" {
		return "", errors.New("error, missing kid and secret")
	}
	token := jwt.New(jwt.SigningMethodHS256)
	token.Header["kid"] = kid

	claims := token.Claims.(jwt.MapClaims)

  // This is an example, you should set your own expiration in seconds
	tokenExpiration := 60

	if merchantId != "" {
		claims["mid"] = merchantId // Optional
	}
  if country != "" {
		claims["country"] = country // Optional
	}
	claims["exp"] = time.Now().Add(time.Second * time.Duration(tokenExpiration)).Unix()

	tokenString, err := token.SignedString([]byte(mySecretKey))

	if err != nil {
		log.Println("Something Went Wrong: ", err.Error())
		return "", err
	}

	return tokenString, nil
}
```

# Python

```python
from datetime import datetime, timezone, timedelta
import os
import jwt # pip install pyjwt


def generate_jwt_token(merchant_id: int | None, country: str | None, expiration_seconds: int = 600) -> str:
    """
    Generates a JSON Web Token (JWT) with the provided merchant ID, country, and expiration time.

    Args:
        merchant_id (int | None): The ID of the merchant. If None, the "mid" claim will not be included in the token.
        country (str | None): The country associated with the merchant. If None, the "country" claim will not be included in the token.
        expiration_seconds (int, optional): The number of seconds until the token expires. Defaults to 600.

    Returns:
        str: The generated JWT token.

    """
    secret = os.environ.get('JWT_SECRET')  # Provided by R2
    kid = os.environ.get('JWT_KID')  # Provided by R2

    now = datetime.now(timezone.utc)
    expiration = now + timedelta(seconds=expiration_seconds)

    payload = {"exp": expiration}

    if merchant_id is not None:
        payload["mid"] = merchant_id

    if country is not None:
        payload["country"] = country

    token = jwt.encode(payload, secret, algorithm='HS256', headers={"kid": kid})
    return token



if __name__ == '__main__':
    merchant_id = "123456"
    country = "CO"
    token = generate_jwt_token(merchant_id=merchant_id, country=country)
    print(token)

```