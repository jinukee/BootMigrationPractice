# AI Agent 공통 작업 지침 (Source of Truth)

이 문서는 Codex, Claude Code, Antigravity 등 여러 AI coding agent가 이 프로젝트에서 협업할 때 공통으로 참조해야 하는 최상위 지시사항입니다.

## 0. 에이전트 필수 초기화 절차 (Bootstrap Procedure)
모든 AI Agent는 사용자의 요청을 처리하기 전에 **반드시 다음 절차를 스스로 수행**하여 컨텍스트를 파악해야 합니다.
1. **Target Directory 탐색**: 사용자가 작업을 요청한 디렉터리(`JSPAndServlet`, `bootMigration`, `CSR_Migration` 등) 내부에 존재하는 `AGENTS.md` 파일을 먼저 읽어 해당 기술 스택의 특화된 규칙을 파악하십시오.
2. **Skills 탐색 및 활용**: 프로젝트 루트의 `.agents/skills/` (및 해당되는 경우 `.claude/skills/`) 디렉터리를 조회하여 현재 작업 목적과 일치하는 `SKILL.md` 파일이 있다면 반드시 먼저 읽고, 그 안에 정의된 Workflow와 Rules에 따라 작업을 수행하십시오.

## 1. 프로젝트 개요 및 목표
- 이 저장소는 **"간단한 블로그 프로젝트를 단계적으로 migration하며 학습/비교하는 용도"**입니다.
- **Migration 단계**:
  1. `JSPAndServlet`: JSP & Servlet 기반 기존 레거시 블로그
  2. `bootMigration`: Spring Boot SSR 기반 블로그
  3. `CSR_Migration`: Spring Boot API (`CSR_Back`) + React CSR (`CSR_Front`) 기반 블로그
- 각 구현체는 **같은 핵심 도메인과 기능을 유지**하는 것을 최우선 목표로 합니다.

## 2. 공통 작업 원칙
- **차이점 고려**: 한 구현체를 수정할 때는 다른 구현체(이전/이후 단계)와의 아키텍처 및 기능적 차이를 항상 고려해야 합니다.
- **기능 동등성 보장**: Migration 작업 시 기존 동작 흐름과 데이터 처리 결과가 동일하게 유지되어야 합니다.
- **범위 제한**: 요청받은 작업과 무관한 디렉터리나 파일을 임의로 수정하지 마십시오.
- **코드 스타일**: 각 디렉터리에 이미 적용된 기존 코드 스타일, 네이밍 컨벤션, 아키텍처 패턴을 우선적으로 따르십시오.

## 3. 작업 프로세스 가이드
- **계획 우선 제시**: 여러 파일을 수정하거나 대규모 변경이 예상되는 경우, 코드 작성 전에 반드시 변경 계획(Plan)을 먼저 제시하십시오.
- **테스트 및 검증 제안**: 코드 변경 시 관련된 범위의 단위/통합 테스트 코드 작성을 우선 고려하고, 테스트 코드가 없다면 명확한 수동 검증 절차(예: cURL, UI 흐름 등)를 제안하십시오.
- **명확한 구분**: Migration 작업 내용 설명 시 "기능 동등성(Feature Parity)을 위해 유지한 부분"과 "아키텍처 차이(Architectural Difference)로 인해 변경된 부분"을 명확히 구분하여 설명하십시오.
- **의존성 추가 규칙**: 새로운 Dependency(라이브러리/패키지) 추가 시 그 이유와 필요성을 사용자에게 명확히 설명하십시오.

## 4. 🚫 금지 사항 (Strictly Forbidden)
- 애플리케이션 구조를 파괴하거나 되돌리기 어려운 **Destructive Command 금지**:
  - `git reset --hard`, `git clean -fd`
  - `git push --force`
  - 임의의 DB 초기화 또는 대량 파일 삭제 명령어 실행 금지
- 허가되지 않은 다른 migration 디렉터리 임의 수정 금지.
