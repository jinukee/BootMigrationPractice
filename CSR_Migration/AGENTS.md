# CSR 마이그레이션 작업 지침 (상위 Workspace)

## 1. 역할 및 목표
- 이 디렉터리는 **Spring Boot REST API (Backend)**와 **React (Frontend)**로 완전히 분리된 Client-Side Rendering (CSR) 버전의 상위 워크스페이스입니다.

## 2. 아키텍처 원칙
- **책임 분리**: `CSR_Back` (API 서버)과 `CSR_Front` (React 클라이언트)의 책임과 역할을 명확히 분리하여 유지하십시오.
- **Contract 중심**: API 스펙(Contract) 변경 시 반드시 백엔드와 프론트엔드 양쪽의 영향도를 모두 확인하고 동기화해야 합니다.

## 3. 주의 사항
- 완전히 분리된 환경이므로 백엔드와 프론트엔드 경계에서 발생하는 문제들에 각별히 주의하십시오:
  - CORS (Cross-Origin Resource Sharing) 설정
  - 인증(Authentication) 및 인가(Authorization) 방식 (예: JWT 등)
  - 통일된 Error 응답 포맷 (Error Handling)
  - 일관된 DTO(Data Transfer Object) 구조 규약
