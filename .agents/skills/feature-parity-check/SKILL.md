---
name: feature-parity-check
description: JSP/Servlet, Spring Boot SSR, CSR 세 가지 구현체 간의 기능 동등성 및 동작 차이를 비교 검증할 때 사용하는 스킬
---

# Feature Parity Check

## When to use
동일한 기능이 여러 구현체(JSP, SSR, CSR)에 걸쳐 올바르게, 그리고 동일한 비즈니스 규칙으로 구현되었는지 비교하고 검증할 때 사용합니다.

## Workflow
1. **기능 정의**: 비교할 핵심 기능을 식별합니다. (예: 게시글 작성, 회원가입 등)
2. **항목별 비교**: 
   - URL/Route 매핑
   - Request / Response 형식
   - 화면 전환(Redirect/Forward 등) 흐름
   - Validation 로직
   - 데이터 영속성(Persistence) 처리
   - Error Handling
3. **분석**: 세 구현체 간의 차이점을 파악합니다.
4. **분류**: 파악된 차이점이 기술 스택 변경에 따른 '의도적인 아키텍처 차이'인지, 아니면 마이그레이션 과정에서 발생한 '기능 누락/버그'인지 분류합니다.

## Rules
- 각 구현체의 `AGENTS.md` 원칙을 참고하여 비교해야 합니다.
- 누락된 기능이 있다면 어떻게 복구해야 할지 제안해야 합니다.

## Output
- 표 형태(Markdown Table) 또는 섹션별 비교 요약 리포트
- 의도적 차이점과 누락된 기능 명시
