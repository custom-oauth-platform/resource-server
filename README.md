# OAuth Resource Server

이 프로젝트는 Authorization Server가 발급한 JWT access token의 서명과 기본 클레임을 검증한 뒤, 사용자 프로필 데이터를 반환하는 Spring Boot 기반 Resource Server입니다.

현재 구현은 `resource_member_profile` 테이블을 조회해 로그인한 사용자의 마이페이지 정보를 응답합니다. 응답 필드는 access token에 포함된 scope에 따라 선택적으로 노출됩니다.

## 개요

- JWT access token 서명 및 기본 클레임 검증
- OAuth2 Resource Server 구성
- 사용자 프로필 조회 API 제공
- scope 기반 응답 필드 제어
- MySQL + JPA 기반 데이터 조회

## 기술 스택

- Java 17
- Spring Boot 4.0.3
- Spring Security
- OAuth2 Resource Server
- Spring Data JPA
- MySQL
- Lombok

## 프로젝트 구조

```text
src/main/java/dev/oauth/resourceserver
├── controller
│   └── UserController.java
├── profile
│   ├── dto
│   │   └── MyPageResponse.java
│   ├── entity
│   │   ├── Gender.java
│   │   └── ResourceMemberProfile.java
│   ├── repository
│   │   └── ResourceMemberProfileRepository.java
│   └── service
│       └── ProfileService.java
├── security
│   └── SecurityConfig.java
└── ResourceServerApplication.java
```

## 실행 환경

기본 설정은 [application.yml](/mnt/c/woori-project-back/OAuth/resource-server/src/main/resources/application.yml)에 정의되어 있습니다.

- Server Port: `8081`
- Authorization Server Issuer: `http://localhost:9000`
- Database: `oauth_resource_db`

기본 DB 연결 정보:

- URL: `jdbc:mysql://localhost:3306/oauth_resource_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=Asia/Seoul&allowPublicKeyRetrieval=true`
- Username: `root`
- Password: `1234`

## 보안 구성

[SecurityConfig.java](/mnt/c/woori-project-back/OAuth/resource-server/src/main/java/dev/oauth/resourceserver/security/SecurityConfig.java) 기준 현재 보안 정책은 다음과 같습니다.

- 세션을 사용하지 않는 stateless API
- CSRF 비활성화
- `/api/mypage` 요청은 인증 필요
- 그 외 모든 요청은 차단

JWT 검증은 `issuer-uri` 설정을 사용합니다.

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:9000
```

Resource Server는 issuer 메타데이터를 통해 공개키 정보를 조회하고, 해당 키로 access token의 서명을 검증합니다. 이 과정에서 issuer, 만료 시간 같은 기본 JWT 클레임도 함께 검증됩니다.

## API

### GET `/api/mypage`

인증된 사용자의 프로필 정보를 조회합니다.

요청 예시:

```http
GET /api/mypage
Authorization: Bearer <access_token>
```

사용자 식별 순서:

1. `preferred_username`
2. `user_id`
3. `sub`

응답 필드 노출 규칙:

- `name` scope가 있으면 `name` 포함
- `email` scope가 있으면 `email` 포함
- `gender` scope가 있으면 `gender` 포함
- `birthdate` scope가 있으면 `birthdate` 포함

scope가 없는 필드는 `null`로 반환될 수 있습니다.

응답 예시:

```json
{
  "userId": "user123",
  "name": "홍길동",
  "email": "user@example.com",
  "gender": "MALE",
  "birthdate": "2000-01-01"
}
```

오류:

- 사용자 프로필이 없으면 `404 Not Found`

## 처리 흐름
![resource-flow](https://github.com/custom-oauth-platform/resource-server/blob/main/resource-flow.png?raw=true)

```text
Client
  -> Bearer access token 포함 요청
Resource Server
  -> JWT 검증
  -> 사용자 식별자 추출
  -> 프로필 테이블 조회
  -> scope에 맞는 필드만 응답 생성
  -> JSON 반환
```

## 데이터 모델

프로필 엔티티는 [ResourceMemberProfile.java](/mnt/c/woori-project-back/OAuth/resource-server/src/main/java/dev/oauth/resourceserver/profile/entity/ResourceMemberProfile.java) 기준으로 아래 컬럼을 사용합니다.

- `id`
- `user_id`
- `name`
- `email`
- `gender`
- `birthdate`

테이블명:

```text
resource_member_profile
```

## 실행 방법

```bash
./gradlew bootRun
```

Windows:

```bash
gradlew.bat bootRun
```
