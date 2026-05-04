---
name: react-csr-feature
description: CSR_Front (React) 환경에서 클라이언트 측 UI 기능 및 API 연동 로직을 구현하거나 수정할 때 사용하는 스킬
---

# React CSR Feature Implementation

## When to use
`CSR_Migration/CSR_Front` 디렉터리 내에서 사용자와 상호작용하는 UI 화면(React Component, Page 등)을 개발하고 백엔드 API와 데이터를 연동하는 작업을 할 때 사용합니다.

## Workflow
1. **분석**: 요구되는 UI 화면 흐름, 라우팅(Route), 필요한 상태 관리(State Management) 요소를 파악합니다.
2. **API Contract 확인**: 백엔드(`CSR_Back`)에서 제공하는 API 문서나 코드를 바탕으로 정확한 Request/Response 규격을 확인합니다.
3. **로직 작성**:
   - `pages` 또는 `components` 디렉터리에 UI 작성
   - `api` 혹은 데이터 패칭 계층에서 올바른 API Endpoint 호출
   - 로딩(Loading) 및 에러(Error) 상태에 대한 UI 피드백 처리
4. **Mock 데이터 검토**: UI 개발용 Mock 데이터가 사용 중이라면, 이를 실제 백엔드 API 호출 로직으로 치환하거나 명확히 분리하여 표시합니다.

## Rules
- 백엔드 데이터 구조와 불일치하는 임의의 포맷으로 클라이언트 상태를 억지로 변경하지 말고, 백엔드 API 수정을 먼저 요청하거나 협의하십시오.
- 기존 폴더 구조(네이밍 등)를 유지하며 책임 분리(UI 로직 vs 비즈니스/네트워크 로직)를 지켜야 합니다.

## Output
- 생성/수정된 React Component, API Client 파일 요약
- 사용자 화면 흐름(UI Flow)에 대한 설명
- Loading/Error 상태 처리 명시
- 백엔드(`CSR_Back`) 측 API 변경 요청사항 (필요 시)
