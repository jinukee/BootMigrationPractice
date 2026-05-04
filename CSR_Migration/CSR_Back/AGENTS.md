# Spring Boot API 서버 작업 지침 (CSR Backend)

## 1. 역할 및 목표
- 이 디렉터리는 React 프론트엔드와 통신하기 위한 **RESTful API 중심의 Spring Boot 서버**입니다.

## 2. 아키텍처 및 코딩 원칙
- **REST API 패턴**: Controller는 HTML View가 아닌 JSON 형식의 Request/Response 처리에만 집중합니다.
- **계층 구조**: 비즈니스 로직은 `Service`에, 데이터 영속성은 `Repository`에 위임하십시오.
- **DTO 강제**: Entity 객체의 외부 노출을 금지합니다. API Request와 Response는 반드시 전용 DTO를 통해 스펙(Contract)을 명확히 유지하십시오.

## 3. 문서화 및 소통
- API(Endpoint)의 추가/변경 시 프론트엔드(`CSR_Front`)에 미치는 영향을 파악하고, 요약하여 보고하십시오.
- 가능하면 새로운 기능 작업 시 아래 사항들을 명확히 문서화하거나 주석으로 남기십시오:
  - HTTP Method 및 Endpoint URL
  - Request Body / Query Parameters
  - Response Body 구조
  - 예상되는 Error Response 포맷
