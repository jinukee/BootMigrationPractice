# Spring Boot SSR 구현체 작업 지침

## 1. 역할 및 목표
- 이 디렉터리는 **Spring Boot 기반 Server-Side Rendering(SSR) 블로그 구현체**입니다.
- 레거시(JSP & Servlet) 버전의 기능을 Spring Boot의 생태계 방식(Thymeleaf, Spring MVC, Spring Data JPA 등)으로 마이그레이션한 결과를 담고 있습니다.

## 2. 아키텍처 및 코딩 원칙
- **계층 구조 준수**: `Controller` → `Service` → `Repository` 계층 분리를 엄격히 권장합니다.
- **Controller의 역할**: SSR Template 엔진(Thymeleaf 등)을 사용하므로, Controller는 복잡한 로직을 처리하지 않고 View Name 반환 및 Model 객체 전달에 집중하십시오.
- **Entity 보호**: JPA Entity 객체를 View Template이나 외부 응답에 무분별하게 직접 노출하지 마십시오. 필요한 경우 DTO를 사용하십시오.

## 3. 검증 및 테스트
- 마이그레이션 관점을 항상 유지하며 기능 수정 시 JSP/Servlet 버전과의 기능 동등성을 확인하십시오.
- 기능 구현 후에는 관련된 단위/통합 테스트 작성을 제안하거나, 명확한 수동 검증 절차를 제시하십시오.
