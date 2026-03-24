<h1>DocFlow</h1>
<p>AI 기반 문서 관리 & 팀 협업 플랫폼 Spring Boot + Thymeleaf 기반의 웹 애플리케이션으로, 팀 단위 문서 관리와 Claude AI를 활용한 자동 문서 요약 기능을 제공한다.</p>
<hr />
<h2>프로젝트 소개</h2>
<p>프로젝트명 : DocFlow</p>
<p>프로젝트 기간 : 2026.02.04 ~ 2026.03.17 (기능 추가중)</p>
<p>구성원 : 이혜리</p>
<hr />
<h2>기획 배경</h2>
<p>AI가 다방면으로 사용되고 있는 요즈음, 팀원이 업로드하는 문서를 AI가 요약해준다면 얼마나 간편할까?</p>
<p>다양한 문서를 AI가 요약해주면 보다 편하게 소통을 할 수 있을거라 확신하여 개발하게 되었다.</p>
<hr />
<h2>화면 및 기능 소개</h2>
<hr />
<h2>기술 스택</h2>

### · Backend
| 기술 | 버전 | 용도 |
|------|------|------|
| Java | 17 | 런타임 |
| Spring Boot | 4.0.2 | 웹 프레임워크 |
| Spring Security | 6.x | 인증/인가 |
| Spring Data JPA | - | ORM |
| Hibernate | - | JPA 구현체 |
| Gradle | 9.3.0 | 빌드 툴 |

### · Database
| 기술 | 버전 | 용도 |
|------|------|------|
| MariaDB | 11.2 | 관계형 데이터베이스 |

### · Frontend
| 기술 | 버전 | 용도 |
|------|------|------|
| Thymeleaf | 3.x | 서버사이드 템플릿 |
| Thymeleaf Layout Dialect | - | 레이아웃 구성 |
| HTML5 / CSS3 | - | 마크업 & 스타일 |
| JavaScript | - | 클라이언트 로직 |
| Font Awesome | 6.5.0 | 아이콘 |

### · AI & 문서 처리
| 기술 | 버전 | 용도 |
|------|------|------|
| Anthropic Claude API | 0.1.0 | AI 문서 요약 |
| Apache PDFBox | 2.0.30 | PDF 텍스트 추출 |
| Apache POI | 5.2.5 | Word/Excel 문서 처리 |
| OkHttp3 | 4.12.0 | HTTP 클라이언트 |
| Anthropic Claude Code | 2.1.81 | 퍼블리싱 |

### · 유틸리티
| 기술 | 버전 | 용도 |
|------|------|------|
| Lombok | - | 보일러플레이트 코드 제거 |
| Google Gson | 2.10.1 | JSON 직렬화 |
| Spring Boot DevTools | - | 개발 편의 도구 |

### · 인프라
| 기술 | 버전 | 용도 |
|------|------|------|
| Docker | - | 컨테이너화 |
| Docker Compose | 3.8 | 멀티 컨테이너 오케스트레이션 |

<br />
<hr />
<h2>ERD</h2>
<img width="100%" height="884" alt="Image" src="https://github.com/user-attachments/assets/3ab38c2a-9a49-47a9-b2c2-bec5df2dbc34" />
<hr />
<h2>API 명세</h2>
<img width="1000" height="320" alt="Image" src="https://github.com/user-attachments/assets/128a0898-3b0e-456e-8f5c-c69de2058714" />
<img width="1000" height="626" alt="Image" src="https://github.com/user-attachments/assets/469128ed-ccba-42da-8cfa-b42641f5071a" />
<img width="1000" height="1136" alt="Image" src="https://github.com/user-attachments/assets/dfa40923-7442-45bd-88c5-3c5f6f6ca53e" />
<img width="1000" height="1136" alt="Image" src="https://github.com/user-attachments/assets/01371e2a-ac02-495e-b5a9-57cf3f8250d4" />

<br />
<hr />
<h2>팀원</h2>

| 이름 | 역할 |
|------|------|
| 이혜리 | Backend, Frontend |

