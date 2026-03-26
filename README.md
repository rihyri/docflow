<h1>DocFlow 🤖</h1>
<p>AI 기반 문서 관리 & 팀 협업 플랫폼.</p> 
<p>Spring Boot + Thymeleaf 기반의 웹 애플리케이션으로, 팀 단위 문서 관리와 Claude AI를 활용한 자동 문서 요약 기능을 제공한다.</p>
<hr />

<h2>✨ 프로젝트 소개</h2>
<p>📁<b> 프로젝트명 : DocFlow</b></p>
<p>🗓️<b> 프로젝트 기간 : 2026.02.04 ~ 2026.03.17 (기능 추가중)</b></p>
<p>👥<b> 구성원 : 이혜리</b></p>
<hr />

<h2>🖥 기획 배경</h2>
<p>AI 기술이 일상과 업무 전반에 걸쳐 빠르게 도입되고 있는 요즘, 협업 과정에서 발생하는 문서들을 효율적으로 관리할 수 있는 방법이 필요하다고 생각했다.</p>
<p>특히 팀원이 업로드하는 다양한 문서를 AI가 자동으로 요약해준다면, 소요 시간을 단축하고 핵심 내용을 빠르게 파악할 수 있어 효과적인 소통이 가능할 것이라 확신했다.</p>
<p>이러한 필요성을 바탕으로, AI 기반 문서 요약 기능을 핵심으로 하는 팀 협업 플랫폼 'DocFlow'를 개발하게 되었다.</p>
<hr />

<h2>📌 화면 및 기능 소개</h2>

> <h3>✅ 메인/홈페이지 소개 화면</h3>

<br>

<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/43acc6ff-beb5-4f84-9fa4-3968d9a7f9d5" />
<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/44d1b086-b26a-4782-854f-9d7726977e0c" />

<br>
<br>

> <h3>✅ 회원가입/로그인 화면</h3>
· 유저 회원가입시 닉네임, 이메일 유효성 검사 실시

<br>
<br>

<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/ec61f5bc-f953-4409-a5df-39fcaa1a2e4c" />
<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/3f851a80-a5e0-46d5-84f3-5058eb040bcb" />

<br>
<br>

> <h3>✅ 팀 목록</h3>
· 현재 등록되어 있는 팀들, 내가 가입된 팀 확인 가능

<br>
<br>

<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/365cd3ce-20d7-4f8f-81a5-436f992fd89d" />

<br>
<br>

> <h3>✅ 팀 상세페이지 / 팀 정보 수정 / 팀원 초대하기</h3>
· 리스트에서 팀을 클릭하면 팀 상세페이지로 이동, 팀원들과 팀원의 역할 확인 가능<br>
· ADMIN의 경우 팀 정보 수정 가능<br>
· ADMIN의 경우 팀 멤버 초대 가능

<br>
<br>

<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/a0d5b245-dea7-4a8f-bab2-b7181ac6c81f" />
<img width="60%" alt="Image" src="https://github.com/user-attachments/assets/318df6a4-87ec-4ac7-a061-f64fb9275200" />
<img width="60%" alt="Image" src="https://github.com/user-attachments/assets/9ce5874f-b22a-443a-b29c-e78844d59fac" />

<br>
<br>

> <h3>✅ 문서 리스트</h3>
· 각 팀에서 올린 문서 리스트 목록

<br>
<br>

<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/2180ab5e-b303-4603-bacb-451ea8ac6388" />

<br>
<br>

> <h3>✅ 문서 업로드</h3>
· 팀 문서 정보와 파일 업로드

<br>
<br>

<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/54b9c6d8-d966-44a9-aa1d-2c89e5c5ef16" />

<br>
<br>

> <h3>✅ 문서 상세보기</h3>
· 문서 상세보기 페이지<br>
· 문서 수정, 삭제, 댓글 기능<br>
· AI가 요약해준 요약문, AI가 생성해준 태그 확인 가능

<br>
<br>

<img width="85%" alt="Image" src="https://github.com/user-attachments/assets/75a0c762-0a6e-4803-89c3-7c1bcc407587" />

<br>
<br>

> <h3>✅ 댓글 목록 / 댓글 수정</h3>
· 문서 상세 페이지에서 댓글 목록, 댓글 수정, 삭제 기능

<br>
<br>

<img width="70%" src="https://github.com/user-attachments/assets/346d03e4-9e7d-45e3-a35c-604451c6dfb5" />
<img width="70%" alt="Image" src="https://github.com/user-attachments/assets/ddbb7ee8-a5e1-4afd-8b9a-ebd142d5f7e5" />

<hr />

<h2>🛠 기술 스택</h2>

> ### Backend
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17 | 런타임 |
| Spring Boot | 4.0.2 | 웹 프레임워크 |
| Spring Security | 6.x | 인증/인가 |
| Spring Data JPA | - | ORM |
| Hibernate | - | JPA 구현체 |
| Gradle | 9.3.0 | 빌드 툴 |


> ### Database
| 기술 | 버전 | 용도 |
|------|------|------|
| MariaDB | 11.2 | 관계형 데이터베이스 |


> ### Frontend
| 기술 | 버전 | 용도 |
|------|------|------|
| Thymeleaf | 3.x | 서버사이드 템플릿 |
| Thymeleaf Layout Dialect | - | 레이아웃 구성 |
| HTML5 / CSS3 | - | 마크업 & 스타일 |
| JavaScript | - | 클라이언트 로직 |
| Font Awesome | 6.5.0 | 아이콘 |


> ### AI & 문서 처리
| 기술 | 버전 | 용도 |
|------|------|------|
| Anthropic Claude API | 0.1.0 | AI 문서 요약 |
| Apache PDFBox | 2.0.30 | PDF 텍스트 추출 |
| Apache POI | 5.2.5 | Word/Excel 문서 처리 |
| OkHttp3 | 4.12.0 | HTTP 클라이언트 |
| Anthropic Claude Code | 2.1.81 | 퍼블리싱 |


> ### 유틸리티
| 기술 | 버전 | 용도 |
|------|------|------|
| Lombok | - | 보일러플레이트 코드 제거 |
| Google Gson | 2.10.1 | JSON 직렬화 |
| Spring Boot DevTools | - | 개발 편의 도구 |


> ### 인프라
| 기술 | 버전 | 용도 |
|------|------|------|
| Docker | - | 컨테이너화 |
| Docker Compose | 3.8 | 멀티 컨테이너 오케스트레이션 |

<hr />

<h2>⚙ ERD</h2>
<img width="100%" height="884" alt="Image" src="https://github.com/user-attachments/assets/3ab38c2a-9a49-47a9-b2c2-bec5df2dbc34" />

<hr />

<h2>📄 API 명세</h2>
<img width="1000" height="320" alt="Image" src="https://github.com/user-attachments/assets/128a0898-3b0e-456e-8f5c-c69de2058714" />
<img width="1000" height="626" alt="Image" src="https://github.com/user-attachments/assets/469128ed-ccba-42da-8cfa-b42641f5071a" />
<img width="1000" height="1136" alt="Image" src="https://github.com/user-attachments/assets/dfa40923-7442-45bd-88c5-3c5f6f6ca53e" />
<img width="1000" height="1136" alt="Image" src="https://github.com/user-attachments/assets/01371e2a-ac02-495e-b5a9-57cf3f8250d4" />

<hr />

<h2>👥 팀원</h2>

| 이름 | 역할 |
|------|------|
| 이혜리 | Backend, Frontend |

