# Muse
<p align="center">
<img width="135" height="81" src="https://github.com/user-attachments/assets/bf2eb203-0a57-4f0b-b585-fee2ca6f3231" />
</p>

[Muse](https://sadajobe.shop)는 사용자의 취향에 맞는 음악을 발견하고 다른 사람들과 음악적 경험을 공유할 수 있는 웹 애플리케이션입니다.  
- **총 9824**개의 노래를 담은 방대한 음악 데이터 기반으로 사용자들이 직접 평점과 리뷰를 남기고 소통할 수 있습니다.
- 모든 노래엔 직접 들을 수 있는 스포티파이 링크 및 아티스트 상세 페이지 링크를 제공합니다.
- 단순한 감상을 넘어 음악에 대한 깊이 있는 칼럼이 제공되고 댓글을 통해 다양한 의견을 나눌 수 있습니다.
- 사용자 개인화된 **노래 추천 기능**을 제공해 새로운 음악을 쉽게 발견할 수 있도록 돕습니다. 

## 주요 기능
-   **음악 검색 및 상세 정보**: 원하는 곡을 검색하고, 앨범 커버, 아티스트, 오디오 특성 등 상세 정보를 확인하고 노래를 들을 수 있습니다.
-   **평점 및 리뷰**: 좋아하는 곡에 별점을 매기고, 상세한 리뷰를 작성하여 다른 사용자들과 감상을 공유할 수 있습니다.
-   **개인화 추천**: 사용자의 청취 기록과 평가를 기반으로 한 개인화된 음악 추천을 제공합니다. 추천 기능은 [추천용 FastAPI 서버](https://github.com/silverttthin/muse-recommender-system)에서 서빙합니다.
-   **음악 칼럼**: 음악에 대한 깊이 있는 이야기를 읽고 댓글로 의견을 나눌 수 있는 칼럼 게시판 기능을 제공합니다.
-   **소셜 기능**: 사용자간 팔로잉/팔로우 및 목록을 확인할 수 있습니다.
-   **관리자 페이지**: 사용자 관리 기능 및 패턴 매칭 비속어 필터링 기능을 통해 서비스 운영을 위한 관리자 기능을 제공합니다.

## 기술 스택
-   **Backend**: Java, Spring Boot, Spring Security(used only for password encryption), Spring Data JPA
-   **Frontend**: Thymeleaf, HTML, CSS
-   **Build Tool**: Gradle
-   **Containerization**: Docker
-   **Deployment**: Private Home Server (self-hosted, running in my room)

## 아키텍쳐
<p align="center">
<img width="571" height="316" alt="muse drawio" src="https://github.com/user-attachments/assets/daf1f35b-1f3a-4372-bd3e-576c2f7a2e83" />
</p>


## 프로젝트 구조

```
src
└── main
    ├── java
    │   └── org
    │       └── siwoong
    │           └── muse
    │               ├── admin # 관리자 기능
    │               ├── column # 칼럼 기능
    │               ├── common # 기본 엔터티, 보안 등 유틸 모음
    │               ├── page # 정적 페이지 컨트롤러
    │               ├── profanity # 비속어 감지 기능 API 클라이언트
    │               ├── recommendation # 추천 로직 API 클라이언트
    │               ├── search # 검색 기능
    │               ├── song # 노래, 평점, 리뷰 기능
    │               └── user # 사용자, 소셜 및 인증 기능
    └── resources
        ├── static
        │   ├── css
        │   └── images
        └── templates
            ├── admin
            ├── column
            ├── error # 404 페이지
            ├── fragments # 공통 컴포넌트 UI(nav)
            ├── search
            ├── song
            └── user

```


## ERD

<img width="892" height="670" alt="image" src="https://github.com/user-attachments/assets/fdf3b077-867c-45cd-af23-61badf3a9d52" />

