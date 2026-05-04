---
name: spring-ssr-crud
description: bootMigration (Spring Boot SSR) 환경에서 CRUD 기능을 구현하거나 수정할 때 사용하는 스킬
---

# Spring Boot SSR CRUD Implementation

## When to use
`bootMigration` 디렉터리 내에서 Thymeleaf를 활용한 Server-Side Rendering 방식의 웹페이지 및 CRUD(생성, 조회, 수정, 삭제) 컨트롤러 로직을 작성하거나 수정할 때 사용합니다.

## Workflow
1. **Source 확인**: `JSPAndServlet` 디렉터리에 있는 원본 기능 코드를 확인하여 의도된 동작을 파악합니다.
2. **계층 구현**: 
   - `DTO/Form` 객체를 통한 데이터 바인딩 설정
   - `Repository` 데이터 접근 확인
   - `Service` 비즈니스 로직 작성
   - `Controller`에서 View Name 반환 및 Model Attribute 설정
3. **View 구현**: Thymeleaf 템플릿 파일에서 Model 데이터를 올바르게 매핑하여 뷰를 완성합니다.
4. **검증 제안**: 작성한 코드에 대한 단위/통합 테스트 코드(MockMvc 등) 또는 수동 검증 절차(Browser URL 테스트 등)를 제안합니다.

## Rules
- Controller는 JSON이 아닌 View(HTML)를 렌더링하기 위한 데이터를 다루어야 합니다.
- 기존 JSP/Servlet 레거시 동작과 기능 동등성(Feature Parity)이 유지되는지 지속적으로 확인해야 합니다.
- Entity 객체를 직접 View로 전달하지 말고 DTO 변환 과정을 거치십시오.

## Output
- 변경되거나 추가된 Controller, Service, DTO, View Template 파일 목록 및 요약
- 원본 JSP와의 차이점 설명
- 검증 절차 제안
