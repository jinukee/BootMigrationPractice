# Claude Code 전용 진입점 (Entrypoint)

**⚠️ Read `AGENTS.md` first and treat it as the shared project instruction.**

이 문서는 Claude Code가 이 프로젝트에서 작업할 때 준수해야 할 전용 행동 지침입니다. 공통 프로젝트 목표와 규칙은 반드시 루트의 `AGENTS.md`를 우선 참고하십시오.

## 1. 작업 프로세스
- **계획 수립 (Planning First)**: 여러 파일에 걸친 변경이나 마이그레이션 작업 시, 코드를 작성하거나 명령어를 실행하기 전에 반드시 작업 계획(Plan)을 작성하여 사용자에게 제시하십시오.
- **Skill 우선 활용**: 프로젝트 내에 정의된 스킬을 우선적으로 활용하십시오.
  - 공용 스킬: `.agents/skills/`
  - Claude 전용 스킬: `.claude/skills/`

## 2. 결과 보고 (Reporting)
- Migration 작업이나 비교 분석 작업을 완료한 후에는, **각 구현체(JSP, Spring Boot SSR, CSR)별로 변경 사항을 나누어 요약**하여 보고하십시오.
- 단순히 코드만 보여주지 말고, 아키텍처 차이와 기능 동등성 측면에서 어떤 변화가 있었는지 명확히 설명하십시오.

*(※ 기타 금지 사항 및 코딩 컨벤션은 모두 `AGENTS.md`의 정책을 상속합니다.)*
