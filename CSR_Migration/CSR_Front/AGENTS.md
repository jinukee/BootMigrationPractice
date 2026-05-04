# React 프론트엔드 작업 지침 (CSR Frontend)

## 1. 역할 및 목표
- 이 디렉터리는 **React 기반의 Client-Side Rendering(CSR) 프론트엔드 애플리케이션**입니다.
- 백엔드(Spring Boot API)와 통신하여 사용자에게 화면을 구성하고 렌더링합니다.

## 2. 아키텍처 및 코딩 원칙
- **책임 분리**: API 호출부(Network Layer)와 UI 컴포넌트(Presentation Layer)의 책임을 명확히 분리하십시오.
- **기존 구조 준수**: 이 디렉터리에 이미 설정된 폴더 구조(예: components, pages, api, store 등)와 상태 관리 방식을 우선적으로 따르십시오.
- **Mock 데이터 사용 주의**: API 연동 과정에서 백엔드 API Contract와 맞지 않는 임의의 Mock 데이터를 남용하지 마십시오. Mock 데이터가 사용된 경우 실제 API 연동 상태인지 명확히 구분해야 합니다.

## 3. 요약 및 보고
- UI를 변경하거나 추가할 때는 사용자 흐름(User Flow)을 기준으로 작업 내용을 설명하십시오.
- API 변경 사항이 필요하여 `CSR_Back`의 수정이 동반되는 경우, 프론트엔드 작업 보고 시 해당 백엔드 영향도를 함께 반드시 언급하십시오.
