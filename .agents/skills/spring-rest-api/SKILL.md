---
name: spring-rest-api
description: CSR_Back (Spring Boot) 환경에서 React 프론트엔드를 위한 RESTful API를 구현하거나 수정할 때 사용하는 스킬
---

# Spring REST API Implementation

## When to use
`CSR_Migration/CSR_Back` 디렉터리 내에서 클라이언트(주로 React)와 통신하기 위한 JSON 기반 REST API 컨트롤러와 로직을 개발, 수정, 설계할 때 사용합니다.

## Workflow
1. **Contract 설계/확인**: 프론트엔드와 규약이 될 API Endpoint, HTTP Method, Request/Response DTO 구조를 설계하거나 기존 규약을 확인합니다.
2. **로직 구현**:
   - `Repository` 및 `Service`를 통한 비즈니스 로직 개발
   - `@RestController`를 이용한 JSON 응답 처리 로직 작성
3. **예외 처리**: Validation 및 Error Handling 로직을 구현하여 명확한 Error Response 포맷을 유지합니다.
4. **영향도 파악**: API 변경이 프론트엔드(`CSR_Front`)에 어떤 영향을 미치는지 분석합니다.
5. **테스트 제안**: `cURL`, `HTTPie` 예시 코드나 API 단위 테스트(MockMvc 등)를 제안합니다.

## Rules
- 서버 사이드 렌더링용 로직(Model, View Name 반환)을 절대 포함하지 마십시오.
- API Contract(DTO 구조 등)를 명확하고 일관되게 유지하십시오.
- 변경된 API 스펙은 프론트엔드 개발 관점에서 명확히 문서화/요약되어야 합니다.

## Output
- 생성/수정된 API Endpoint, Request/Response 구조 요약 문서
- 에러 응답 구조 명시
- 프론트엔드(`CSR_Front`) 영향도 알림
- API 호출 테스트 예시 (cURL 등)
