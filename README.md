# Hanmo 한세 과팅/친구 사귀기 어플

## 💁 조원
* Frontend: 유상진, 이경환
* Backend: 김예람, 김태남, 박다혜, 박지훈

## 👉 프로젝트 개요
* ## 프로젝트 선택배경  <br/>
> : 학기 초 활발하게 이루어지는 과팅이 시간이 지나면서 관심이 줄어들면서, <br/>
**학생간의 교류가 적어지게 되는 문제점**을 해결하기 위함.  <br/>
또한, **축제기간 동안 함께 즐길 수 있는 친구**를 만들어 축제 활성화를 도움  <br/>
* ## 프로젝트 목표  <br/>
> : 1:1 동성 간 매칭, 2:2 이성 간 매칭을 통해  <br/>
교내에서 **새로운 친구를 사귈 수 있도록 돕는 서비스**를 <br/>
구현하여 **교내 학생들간의 교류를 활발**하기 위함  <br/>


## Server 구조
<img src="https://github.com/user-attachments/assets/baee50db-7995-4a7f-ac4c-e6fb01a97572" alt="Server 구성도 초안" width="700">

 > CI/CD </br>
> 1. Git Push를 하고 master브랜치(배포용 브랜치)에 올리면, Jenkins서버로 바로 Hook을 날립니다.
> 2. Jenkins에서 Test를 진행 한 후에 Docker 이미지로 제작을 합니다.
> 3. SpringBoot서버로 바로 컨테이너를 올려 배포를 시작합니다.

## 형상관리
* 그라파나, 프로메테우스로 관리

![image](https://github.com/user-attachments/assets/5796b961-e709-47d8-b7ac-5c241b63179a)

## ☑️ figma
주소 : https://www.figma.com/design/coLvBC7Iqj5uJgUjZAOUcY/%ED%95%9C%EB%AA%A8?node-id=1104-512&p=f&t=gmvhpHESq7JjDcyM-0

## ☑️ 프론트엔드 배포 주소
주소 : https://hanmo-front.vercel.app/landing

## ☑️ Swagger
주소 : https://hanmo.store/api/swagger-ui/index.html#/

## ☑️ 기능명세서
주소 : https://docs.google.com/spreadsheets/d/1JE8XKR276VxA5LdkBcax9DjOBxP-GwB1Ri0WR1a8sHE/edit?gid=0#gid=0

## ☑️ WBS
주소 : https://docs.google.com/spreadsheets/d/1MJ3ztSCG_p-YSx11Sm2T_OwdkF1ltrcpEdc58xBeTxA/edit?gid=0#gid=0

## ☑️ BE 오픈소스 고지서

| 라이브러리 | 버전 | 사용 목적 | 라이선스 | 확인 링크 |
|------------|------|-----------|-----------|------------|
| Spring Boot Starter Web | 3.4.3 | REST API 개발 | Apache 2.0 | [LICENSE](https://github.com/spring-projects/spring-boot/blob/main/LICENSE.txt) |
| Spring Boot Starter Security | 3.4.3 | 인증/인가 처리 | Apache 2.0 | [LICENSE](https://github.com/spring-projects/spring-boot/blob/main/LICENSE.txt) |
| Spring Boot Starter Data JPA | 3.4.3 | ORM 및 DB 연동 | Apache 2.0 | [LICENSE](https://github.com/spring-projects/spring-boot/blob/main/LICENSE.txt) |
| Spring Boot Starter Validation | 3.4.3 | 유효성 검사 | Apache 2.0 | [LICENSE](https://github.com/spring-projects/spring-boot/blob/main/LICENSE.txt) |
| Spring Boot Starter Data Redis | 3.4.3 | Redis 연동 | Apache 2.0 | [LICENSE](https://github.com/spring-projects/spring-boot/blob/main/LICENSE.txt) |
| Lettuce Core (Redis Client) | 자동 포함 | Redis 클라이언트 | Apache 2.0 | [LICENSE](https://github.com/lettuce-io/lettuce-core/blob/main/LICENSE) |
| MySQL Connector/J | 8.0.33 | MySQL DB 연동 | GPL v2 with FOSS Exception | [LICENSE](https://github.com/mysql/mysql-connector-j/blob/release/8.0/LICENSE) |
| Lombok | 1.18.30 | 코드 간결화 | MIT | [LICENSE](https://github.com/projectlombok/lombok/blob/master/LICENSE) |
| Springdoc OpenAPI UI | 2.7.0 | Swagger API 문서 자동화 | Apache 2.0 | [LICENSE](https://github.com/springdoc/springdoc-openapi/blob/master/LICENSE) |
| QueryDSL JPA | 5.0.0 | 동적 쿼리 작성 | Apache 2.0 | [LICENSE](https://github.com/querydsl/querydsl/blob/master/LICENSE.txt) |
| Nurigo SDK | 4.3.0 | SMS 문자 전송 | Apache 2.0 | [LICENSE](https://github.com/nurigo/javaSDK/blob/master/LICENSE) |
| Jasypt Spring Boot Starter | 3.0.4 | 민감 정보 암호화 | Apache 2.0 | [LICENSE](https://github.com/ulisesbocchio/jasypt-spring-boot/blob/main/LICENSE) |
| Mockito (inline, junit) | 4.8.1 | 단위 테스트 | MIT | [LICENSE](https://github.com/mockito/mockito/blob/main/LICENSE) |

## ☑️ FE 오픈소스 고지서

| 라이브러리               | 버전 | 사용 목적                      | 라이선스   | 확인 링크 |
|--------------------------|----|-------------------------------|------------|-----------|
| react                    | 18 | UI 구성                        | MIT        | [LICENSE](https://github.com/facebook/react/blob/main/LICENSE) |
| react-dom                | 18 | 가상 DOM 렌더링                | MIT        | [LICENSE](https://github.com/facebook/react/blob/main/LICENSE) |
| next                     | 14.2.26 | React 기반 SSR 프레임워크      | MIT        | [LICENSE](https://github.com/vercel/next.js/blob/canary/license.md) |
| axios                    | 1.8.4 | HTTP 클라이언트                | MIT        | [LICENSE](https://github.com/axios/axios/blob/master/LICENSE) |
| react-redux              | 9.2.0 | Redux 상태 관리 연동           | MIT        | [LICENSE](https://github.com/reduxjs/react-redux/blob/master/LICENSE.md) |
| @reduxjs/toolkit         | 2.6.1 | Redux 툴킷 (상태 관리 간소화)  | MIT        | [LICENSE](https://github.com/reduxjs/redux-toolkit/blob/master/LICENSE.md) |
| react-hook-form          | 7.55.0 | 폼 상태 관리                   | MIT        | [LICENSE](https://github.com/react-hook-form/react-hook-form/blob/master/LICENSE) |
| @hookform/resolvers      | 4.1.3 | react-hook-form의 유효성 검증  | MIT        | [LICENSE](https://github.com/react-hook-form/resolvers/blob/master/LICENSE) |
| zod                      | 3.24.2 | 스키마 기반 데이터 검증        | MIT        | [LICENSE](https://github.com/colinhacks/zod/blob/master/LICENSE) |
| react-hot-toast          | 2.5.2 | 사용자 알림 토스트 메시지      | MIT        | [LICENSE](https://github.com/timolins/react-hot-toast/blob/main/LICENSE) |
| framer-motion            | 12.6.3 | 애니메이션 라이브러리          | MIT        | [LICENSE](https://github.com/framer/motion/blob/main/LICENSE) |
| yarn                     | 1.22.22 | 패키지 매니저                  | BSD-2-Clause | [LICENSE](https://github.com/yarnpkg/yarn/blob/master/LICENSE) |
| typescript               | 5  | 정적 타입 시스템               | Apache 2.0 | [LICENSE](https://github.com/microsoft/TypeScript/blob/main/LICENSE.txt) |
| tailwindcss              | 3.4.1 | 유틸리티 기반 CSS 프레임워크   | MIT        | [LICENSE](https://github.com/tailwindlabs/tailwindcss/blob/master/LICENSE) |
| postcss                  | 8  | CSS 후처리 도구                | MIT        | [LICENSE](https://github.com/postcss/postcss/blob/main/LICENSE) |
| eslint                   | 8  | 코드 스타일 검사               | MIT        | [LICENSE](https://github.com/eslint/eslint/blob/main/LICENSE) |
| eslint-config-next       | 14.2.26 | Next.js용 ESLint 설정          | MIT        | [LICENSE](https://github.com/vercel/next.js/blob/canary/license.md) |
| @types/react             | 18 | React용 타입 정의              | MIT        | [LICENSE](https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/LICENSE) |
| @types/react-dom         | 18 | React DOM용 타입 정의          | MIT        | [LICENSE](https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/LICENSE) |
| @types/node              | 20 | Node.js용 타입 정의            | MIT        | [LICENSE](https://github.com/DefinitelyTyped/DefinitelyTyped/blob/master/LICENSE) |


---


## ✅ 개발 단계 ✅

### 🟢 1차 개발 (완료)
- 회원가입 & SMS 인증
- 로그인 (tempToken 헤더 인증)
- 예외 처리 & 유효성 검사
- 1:1·2:2 랜덤 매칭
- 개발자에게 한마디등의 게시판
- Docker Compose 기반 인프라 구축

### 🔵 2차 개발 (진행 중)
- 성별·MBTI·나이 등 **선호 조건 매칭**
- WebSocket/Redis Pub‑Sub 기반 **실시간 채팅** 
- 관리자 페이지 (신고·삭제 관리)
- 각 번개모임 게시판, (커피 지금 이공관에서 마실 사람~ 게시물 등의 페이지)
- React 프론트와 CORS·쿠키 연동 최적화

💡 **추가 아이디어**: 맛집 지도 · 대화 가이드

---

## 프로젝트 주요 관심사

### 공통 사항
- 지속적인 성능 개선
- 나쁜 코드 제거를 위한 리팩토링

### 코드 컨벤션
- Google Java Style Guide 준수
- IntelliJ STS CheckStyle 플러그인 적용
- 가이드 링크: https://google.github.io/styleguide/javaguide.html

### 성능 최적화
- Redis 등 캐싱 서버 적극 활용으로 서버 부하 감소
- N+1 쿼리 지양, DB 통신 최소화
- 적절한 인덱스 & 쿼리 튜닝
- 외부 API 호출은 비동기 처리

---

## 기술 스택
### Frontend
* 라이브러리: REACT<br/>
* 프레임워크: NEXT.JS<br/>
* 언어: TypeScript<br/>
* 스타일링: Styled-components (CSS-in-JS 방식)<br/>
* 상태 관리: Recoil<br/>
* API 통신: Axios
### Backend

- **프레임워크**: Spring Boot
- **언어**: Java 21
- **데이터베이스**: MySQL, Redis (NoSQL)
- **메시지 큐**: Kafka (여유 시 도입)
- **Infra & CI/CD**: Docker, Jenkins, AWS RDS, S3


# 주요 흐름

<img width="700" alt="image" src="https://github.com/user-attachments/assets/e363919a-e762-4672-93e7-d5133c911a1f" />


1. **첫 화면**  
   - 앱 실행 시 **“한모 : 한세에서 모여봐요!”** 타이틀과 한모 캐릭터가 표시된다.  
   - 하단에 **[매칭 확인] / [회원가입]** 버튼이 있고,  
     - `회원가입` → 온보딩 절차 시작  
     - `매칭 확인` → 로그인 후 매칭 현황으로 이동  

2. **전화번호 인증(본인 인증)**  
   - 휴대폰 번호 입력 → 서버가 6자리 SMS 인증번호 발송  
   - 인증번호가 일치하면 **임시토큰**을 Redis에 키값으로 매핑해 저장  
   - 이 토큰은 회원가입 완료 시 영구 세션 토큰으로 교체  

3. **기본 정보 입력**  
   - 입력 항목: **MBTI, 성별, 학과(학부), 학번(입학 연도), 나이, 인스타그램 ID**  
   - 서버가 값 검증·중복 체크 후 임시토큰 세션에 임시 보관 → 다음 단계 이동  

4. **랜덤 별명 생성**  
   - 규칙: **학과명 + 랜덤 형용사 + 랜덤 동물** (예: “컴퓨터공학과 수줍은 하마”)  
   - **“다시 뽑기”** 1회만 허용  
   - 별명 확정 시 프로필에 저장되고, 임시토큰이 “회원가입 완료” 토큰으로 교체  

5. **메인 페이지**  
   - 상단: 내 별명·매칭 상태  
   - 중앙 탭: **[매칭하기] [게시판] [채팅]**  
   - 채팅 탭은 매칭된 그룹이 있을 때만 활성화  

6. **매칭하기 – 대기열 등록**  
   - **[매칭 시작]** 클릭 시 설정  
     1. 모드 선택: **1 : 1 동성** / **2 : 2 이성**  
     2. 희망 **학번 범위**  
     3. 선호 **MBTI**  
   - 같은 학과는 무조건 제외  
   - 매칭 완료 후 **24 시간 쿨다운**(재매칭 불가)  

7. **매칭 알고리즘 – 그룹 완성**  
   - Redis 큐에 성별·학과·학번·MBTI 조건이 맞는 사용자를 순차 배치  
   - 1 : 1 → 동성, 2 : 2 → 남 2 + 여 2 구성  
   - 4명 충원 시 `match_group` 생성, 전원 `status = MATCHED`  

8. **매칭 완료 화면**  
   - 프런트가 주기적 **폴링**으로 매칭 결과 확인  
   - 화면: **4명 별명 + 인스타 ID** 표시,  
     - 안내: “매칭이 완료되었습니다. 30 분 내에 인스타 DM방을 만들어 인사해 주세요.”  
   - **[채팅 시작]** 버튼 활성화 → 그룹 전용 채팅방 진입  

9. **채팅 기능**  
   - 매칭 확정 시 `chat_room:{groupId}` 생성, **TTL 24 시간**  
   - 텍스트·이모지·이미지 실시간 WebSocket 전송  
   - TTL 만료 후 채팅 기록·방 자동 삭제  

10. **게시판**  
    - 카테고리: “개발자에게 한 마디”, “과팅 후기” 등  
    - 글 작성 최대 **500자**, Cursor 기반 무한 스크롤  
    - 글·댓글 **신고 3회 이상** → 자동 숨김


## 초기 ERD 설계 이미지
<img src="https://github.com/user-attachments/assets/478bb98d-5a3a-4ae1-a21d-6ca9b13f7fd0" alt="ERD 설계" width="700">
<hr>

## Figma 이미지

<img width="700" alt="image" src="https://github.com/user-attachments/assets/5d2ed60f-7bf8-4f3e-bb47-e9df97a2faa9" />
<img width="700" alt="image" src="https://github.com/user-attachments/assets/1c38d120-0385-4bb3-8141-db5e8918ea8d" />
<img width="700" alt="image" src="https://github.com/user-attachments/assets/b00d4e72-2b46-43c6-bafe-59205b90324e" />



## 초기 UI 이미지
<img src="https://github.com/user-attachments/assets/6e022090-84db-44c2-bfa2-5e1699de90df" width="700">
<img src="https://github.com/user-attachments/assets/69fc90ec-abf0-46fd-9bd7-f3950a25ce83" width="700">
<img src="https://github.com/user-attachments/assets/37b5c6b7-d6df-4d54-bbe2-ead14c85d858" width="700">


## 2차 변경 UI 이미지
<img src="https://github.com/user-attachments/assets/74bde040-bb39-4aa2-a663-901a86b136cb" width="700">


## 한모 축제 배포

<img width="330" alt="image" src="https://github.com/user-attachments/assets/aee1bcb6-9ff4-47b3-a6d2-2c90ba2749b5" />
<br>
<img width="330" alt="image" src="https://github.com/user-attachments/assets/aca37a62-884e-4de4-ad5e-2dce20212315" />

## 최종 시연 영상
https://github.com/user-attachments/assets/f8cd72cf-a8c7-486e-8584-16cbc30a5de2

## 매칭진행방식 일부 수정된 것 반영 (mbti 상관없음 추가, 매칭방식 선택후 출발! 버튼 눌러야 매칭시작)
https://github.com/user-attachments/assets/41a7e201-5ecc-4b11-a5ce-f46e58112290

## 유저 매칭 흐름 영상
https://github.com/user-attachments/assets/f7257c7d-a780-478d-a8c9-a0a2b8eb42cb

## 관리자 로그인 일반 로그인
https://github.com/user-attachments/assets/6c2a8669-6617-4bb2-8303-05deb74d2a0f





## 후기, 느낀점 

<img src="https://github.com/user-attachments/assets/2fd2543c-aaa0-4eb2-84fc-f3c6a78cc221" width="700">

