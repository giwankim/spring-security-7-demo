# Spring Security 7 OAuth2 Demo

A multi-module Spring Boot 4 / Spring Security 7 learning project exploring OAuth2, multi-factor authentication, passkeys, and more. Based on Josh Long's ["Bootiful Spring Security 7"](https://www.youtube.com/watch?v=68P21jtmoy0) video.

## Tech Stack

- Spring Boot 4.0.3 / Spring Security 7
- Kotlin 2.2.21
- Java 24
- Gradle 9.3.1
- PostgreSQL (via Docker Compose)

## Project Structure

| Module | Port | Role |
|---|---|---|
| `auth-service` | 8080 | OAuth2 Authorization Server + user authentication |
| `service` | 8081 | OAuth2 Resource Server (JWT-protected API) |
| `client` | 8082 | OAuth2 Client (authorization code flow) |

## Features Demonstrated

**OAuth2 Authorization Server** (`auth-service`)
- Authorization code + refresh token grants
- OpenID Connect (scopes: `openid`, `profile`)
- JDBC-backed user store

**OAuth2 Resource Server** (`service`)
- JWT validation via OIDC discovery (`issuer-uri: http://localhost:8080`)

**OAuth2 Client** (`client`)
- Authorization code flow with `@RegisteredOAuth2AuthorizedClient`
- Token-bearing `RestClient` calling the resource server

**Multi-Factor Authentication**
- `@EnableMultiFactorAuthentication` with `AuthorizationManagerFactories.multiFactor()`
- `/admin` requires MFA; `/user` requires single-factor

**One-Time Token (Magic Link) Login**
- OTT generated with 10-minute validity
- Login via `/login/ott?token={token}`

**WebAuthn / Passkeys**
- FIDO2 passkey registration and authentication
- Relying party: `localhost`

**Password Encoding**
- Delegating password encoder: Argon2 default (Password4j), bcrypt/noop/sha256 legacy support

## Prerequisites

- Java 24
- Docker (for PostgreSQL)

## How to Run

1. Start PostgreSQL:

   ```sh
   docker compose up -d
   ```

2. Start all three modules (each in its own terminal):

   ```sh
   ./gradlew :auth-service:bootRun
   ./gradlew :service:bootRun
   ./gradlew :client:bootRun
   ```

3. Open `http://localhost:8082/client` in a browser. You'll be redirected through the OAuth2 authorization code flow:
   - Login at the auth-service (`localhost:8080`)
   - Consent to scopes
   - Redirected back to the client with an access token
   - The client calls the resource server and displays the response

## Default Users

| Username | Password | Roles |
|---|---|---|
| `user` | `user123` | `ROLE_USER` |
| `admin` | `admin123` | `ROLE_USER`, `ROLE_ADMIN` |
