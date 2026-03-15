# Resource Server

OAuth 2.0 / OpenID Connect 기반 인증 구조에서 **보호된 API와 사용자 데이터를 제공하는 Resource Server**입니다.

Authorization Server에서 발급한 `access_token`을 검증한 후, 데이터베이스에서 사용자 정보를 조회하여 클라이언트 애플리케이션에 응답합니다.

<br />

## 1. 개요

- Authorization Server에서 발급한 **JWT access token 검증**
- 보호된 API 제공
- 사용자 프로필 정보 조회
- scope 기반 데이터 접근 제어
- 데이터베이스에서 사용자 정보 조회

<br />

## 2. 기술 스택

- **Java 17**
- **Spring Boot**
- **Spring Security**
- **OAuth2 Resource Server**
- **JWT**
- **JPA / Hibernate**
- **MySQL**

<br />

## 3. 프로젝트 구조

```
src/main/java/dev/oauth/resourceserver
├── controller
│   └── UserController.java
├── profile
│   ├── dto
│   │   └── MyPageResponse.java
│   ├── entity
│   │   └── ResourceMemberProfile.java
│   ├── repository
│   │   └── ResourceMemberProfileRepository.java
│   └── service
│       └── ProfileService.java
└── security
    └── SecurityConfig.java
```

<br />

## 4. 주요 API

### 4.1 공개 API

```
GET /public
```

인증 없이 접근 가능한 테스트 API입니다.


### 4.2 사용자 인증 정보 조회

```
GET /api/me
```

JWT 토큰 정보를 확인하는 API입니다.

응답 예시:

```json
{
  "message": "authenticated user",
  "sub": "user123",
  "iss": "http://localhost:9000",
  "scopes": ["openid", "name", "email"]
}
```

<br />

### 4.3 사용자 마이페이지 조회

```
GET /api/mypage
```

Authorization Server에서 발급한 access token을 검증한 후 사용자 프로필 정보를 반환합니다.

요청 예시

```
GET /api/mypage
Authorization: Bearer access_token
```

응답 예시

```json
{
  "userId": "user123",
  "name": "홍길동",
  "email": "user@email.com",
  "gender": "MALE",
  "birthdate": "2000-01-01"
}
```

<br />

## 5. 인증 처리 방식

Spring Security의 OAuth2 Resource Server 기능을 사용하여 JWT 토큰을 검증합니다.

```java
http
    .oauth2ResourceServer(oauth2 -> oauth2.jwt());
```

요청이 들어오면 다음 순서로 처리됩니다.

1. Authorization Header에서 Bearer Token 추출
2. JWT 서명 검증
3. 토큰 만료 시간 확인
4. scope를 Spring Security 권한으로 변환
5. 컨트롤러 실행

<br />

## 6. JWT 검증 방식

Resource Server는 Authorization Server의 **JWK 공개키**를 사용하여 토큰을 검증합니다.

```
http://localhost:9000/oauth2/jwks
```

이 공개키를 통해 JWT 서명을 검증하고 토큰의 무결성을 확인합니다.

<br />

## 7. 요청 처리 흐름

전체 요청 흐름은 다음과 같습니다.

```
Client Application
        │
        │ Authorization: Bearer access_token
        ▼
Resource Server (Spring Security)
        │
        │ JWT 검증
        ▼
Controller
        │
        ▼
Service
        │
        ▼
Repository
        │
        ▼
Database
        │
        ▼
DTO 변환
        │
        ▼
JSON Response
```

<br />

## 8. 전제 조건

Resource Server가 정상적으로 동작하려면 다음 서버가 실행되어 있어야 합니다.

### Authorization Server

```
http://localhost:9000
```

Authorization Server는 다음 역할을 수행합니다.

- 사용자 인증
- JWT access token 발급
- JWK 공개키 제공

<br />

## 9. 데이터베이스

사용자 프로필 정보는 다음 테이블에서 관리됩니다.

```
resource_member_profile
```

주요 컬럼

- user_id
- name
- email
- gender
- birthdate

<br />

## 10. 정리

Resource Server는 Authorization Server에서 발급한 JWT access token을 검증한 후 보호된 API를 제공하는 서버입니다.

클라이언트 애플리케이션은 로그인 후 발급받은 access token을 Authorization 헤더에 포함하여 요청을 보내며, Resource Server는 이를 검증한 뒤 데이터베이스에서 사용자 정보를 조회하여 응답합니다.
