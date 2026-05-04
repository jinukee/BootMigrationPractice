---
name: migration-plan
description: JSP/Servlet, Spring Boot SSR, Spring Boot + React CSR 간의 마이그레이션 계획을 수립할 때 사용하는 스킬
---

# Migration Plan

## When to use
프로젝트의 한 구현체에서 다음 단계의 구현체로(예: JSP → Spring Boot SSR, 또는 SSR → CSR) 코드를 포팅하거나 마이그레이션 작업을 시작하기 전에 계획을 세울 때 사용합니다.

## Workflow
1. **Source 파악**: 마이그레이션의 기준이 되는 이전 구현체의 코드(기능, 화면 흐름, 데이터 구조)를 확인합니다.
2. **Target 확인**: 마이그레이션 대상 디렉터리의 아키텍처와 기술 스택 요구사항을 확인합니다.
3. **기능 동등성 매핑**: Source의 기능이 Target에서 어떻게 똑같이 동작할 것인지 매핑합니다.
4. **아키텍처 변경점 식별**: 단순 복사가 아니라 Target의 기술 스택(예: JSP → Thymeleaf, Controller 구조 변화 등)에 맞춘 변경 사항을 식별합니다.
5. **계획 수립**: 단계별 마이그레이션 진행 계획(예: 엔티티 → 레포지토리 → 서비스 → 컨트롤러/API → UI)을 작성합니다.

## Rules
- 코드를 실제로 수정하기 전에 반드시 계획을 먼저 출력하여 사용자에게 제시해야 합니다.
- 기능 동등성(Feature Parity) 유지와 아키텍처적 변경(Architectural Change)을 명확히 구분하여 계획에 반영해야 합니다.

## Output
- Source와 Target 구현체 요약
- 아키텍처 변경점 목록
- 단계별 세부 마이그레이션 계획(Step 1, Step 2...)
