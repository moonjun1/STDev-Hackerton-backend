
[ATOMIX_10팀 PDF 문서](ATOMIX_10팀.pdf)
![image](https://github.com/user-attachments/assets/e143e654-6bce-49af-bb74-17ab3cdc51f2)



화합물 검색 및 교육 서비스 API
이 프로젝트는 화학 물질을 검색하고 학습할 수 있는 교육용 서비스를 위한 백엔드 API입니다. 사용자는 화학 원소 조합을 입력하여 화학 물질을 검색하고, 발견한 화학 물질에 따라 칭호를 획득하며, 개인 및 조직 랭킹에 참여할 수 있습니다.
주요 기능
화학 물질 검색

화학 원소 조합 입력 (예: "h + h + o")으로 화학 물질 검색
표준 화학식 변환 및 다양한 표기법 지원 (예: H2O, OH2)
익명 또는 사용자 인증 기반 검색 지원

사용자 관리

사용자 등록 및 프로필 관리
사용자별 포인트 시스템
조직(학교) 소속 관리

칭호 시스템

화학 물질 발견에 따른 자동 칭호 부여
AI 기반 맞춤형 칭호 생성
칭호 관리 및 활성화

랭킹 시스템

개인별 화학 물질 발견 랭킹
조직별 화학 물질 발견 랭킹

보고서 및 분석

사용자 화학 활동 분석 보고서
AI 기반 보고서 생성

API 엔드포인트
화학 물질 API

POST /chemical/formula/search: 사용자 ID 제공하여 화학식 검색 (랭킹에 포함)
POST /chemical/formula/search/anonymous: 익명으로 화학식 검색
GET /chemical/user/formulas/{userId}: 사용자가 발견한 모든 화학식 조회

사용자 API

POST /user/register: 사용자 등록
GET /user/getUsernameById/{userId}: 사용자 ID로 닉네임 조회
GET /user/getIdByUsername/{userName}: 사용자 닉네임으로 ID 조회

포인트 API

POST /point: 포인트 추가/차감
GET /point/search/{userName}: 사용자 포인트 조회

칭호 API

GET /title/{userId}: 사용자의 모든 칭호 목록 조회
POST /title/{userId}/activate/{titleId}: 특정 칭호 활성화
GET /title/ai/{userId}: AI 생성 맞춤형 칭호 목록
POST /title/ai/{userId}/save: AI 생성 칭호 저장

랭킹 API

GET /ranking: 상위 10명의 개인 랭킹과 상위 10개 조직 랭킹 조회

보고서 API

GET /report/{userId}: 사용자의 화학 활동 분석 보고서 생성

도감 API

GET /collection/find/all: 모든 물질 정보 조회
GET /collection/find/{molecularFormula}: 화학식으로 특정 물질 정보 조회

기술 스택

언어: Java
프레임워크: Spring Boot
데이터베이스: JPA, Hibernate
외부 API: OpenAI API (ChatGPT)
문서화: Swagger UI

모델 구조
화학 물질

ChemicalEntity: 화학 물질 정보 (이름, 화학식, 설명, 교육 수준, 공격력)

사용자

UserEntity: 사용자 정보 (이름, 조직, 포인트)

검색 이력

FormulaSearchHistory: 화학식 검색 이력 (성공 여부, 발견한 화학식)

칭호

TitleEntity: 칭호 정보 (이름, 설명, 획득 조건)
UserTitleEntity: 사용자-칭호 관계 (획득 시간, 활성화 여부)

그룹

GroupEntity: 조직 정보 (이름)

화학식 검색 알고리즘

사용자 입력 파싱 (예: "h + h + o" → {"H": 2, "O": 1})
가능한 화학식 조합 생성 (예: H2O, OH2)
데이터베이스에서 일치하는 화학 물질 검색
결과 반환 및 검색 이력 저장
성공 시 칭호 체크 및 부여




기여 방법

이 저장소를 포크합니다.
새 기능 브랜치를 만듭니다 (git checkout -b feature/amazing-feature).
변경 사항을 커밋합니다 (git commit -m 'Add some amazing feature').
브랜치를 푸시합니다 (git push origin feature/amazing-feature).
Pull Request를 열어주세요.


개발사진

![image](https://github.com/user-attachments/assets/92ceb93a-4a7f-4e45-a264-feaa3c1dce1f)

![image](https://github.com/user-attachments/assets/d573ee09-5d64-436b-b001-38b41996d541)

![image](https://github.com/user-attachments/assets/79263664-4577-4f13-b882-3dff1ae41332)

![image](https://github.com/user-attachments/assets/77bc9f9c-a397-4938-bc18-594120fc6ed9)

![image](https://github.com/user-attachments/assets/0b5bac0a-ca8e-45d9-a9cb-f79ec3efa13b)

![image](https://github.com/user-attachments/assets/e4bc0fe6-5211-451f-acfb-625904ba5f71)

![image](https://github.com/user-attachments/assets/646275e0-6580-4884-a270-b5f0d0dbe7f2)




