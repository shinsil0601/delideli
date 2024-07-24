# DeliDeli
## * 꼭 읽어주세요.
1. 깃허브에 PR를 하게 되면 카톡에 꼭 알려주세요.


2. 팀장이 merge를 하고 다시 올리면 그걸 push 받아서 작업해주셔야 합니다.


3. 개인 PR를 하다가 conflict 문제가 발생하면 팀장껄 다시 받아서 해결해주시고 올려주세요.


4. 페이지 세분화를 해주시는게 좋습니다.


5. 새로운 수정사항이 생기면 바로 알려주세요.



## 폴더구조 설명

---------
```FrontEnd``` 
-
- static 폴더
  
static 폴더는 웹 애플리케이션의 정적 리소스를 저장하는 곳입니다. 여기에는 CSS, JavaScript, 이미지 파일 등이 포함됩니다. Spring Boot는 기본적으로 src/main/resources/static 디렉토리에 있는 파일들을 정적 리소스로 처리합니다.

1. common: 애플리케이션 전체에서 공통으로 사용하는 CSS 및 JavaScript 파일을 저장합니다.


2. admin: 관리자 페이지에서 사용하는 CSS 및 JavaScript 파일을 저장합니다.


3. user: 사용자 페이지에서 사용하는 CSS 및 JavaScript 파일을 저장합니다.


4. client: 클라이언트 페이지에서 사용하는 CSS 및 JavaScript 파일을 저장합니다.


- templates 폴더

templates 폴더는 Spring Boot에서 Thymeleaf 템플릿 파일을 저장하는 곳입니다. HTML 파일을 포함하며, 동적인 웹 페이지를 생성하기 위해 사용됩니다. Spring Boot는 기본적으로 src/main/resources/templates 디렉토리에 있는 파일들을 템플릿으로 처리합니다.

1. inc: 공통으로 포함되는 템플릿 파일을 저장합니다. 예를 들어, 헤더와 푸터 파일들이 있습니다.


2. user: 사용자 페이지용 헤더와 푸터 템플릿 파일을 저장합니다.


3. admin: 관리자 페이지 관련 템플릿 파일을 저장합니다.


4. client: 클라이언트 페이지 관련 템플릿 파일을 저장합니다.


5. user: 사용자 페이지 관련 템플릿 파일을 저장합니다.

---------
```BackEnd```
-
1. controller: 클라이언트 요청을 처리하고 적절한 뷰 또는 JSON 데이터를 반환하는 컨트롤러 클래스들을 포함합니다.
예: AdminController, UserController, ClientController


2. domain: 데이터베이스 테이블과 매핑되는 엔티티 클래스들을 포함합니다. 각 엔티티 클래스는 데이터베이스의 특정 테이블을 나타내며, JPA를 사용하여 매핑됩니다.
예: Admin, User, Client


3. dto: 데이터 전송 객체(Data Transfer Object)를 포함합니다. DTO는 주로 컨트롤러와 서비스 계층 간에 데이터를 주고받기 위해 사용됩니다.
예: AdminDTO, UserDTO, ClientDTO


4. exception: 특정 도메인과 관련된 커스텀 예외 클래스들을 포함합니다. 예외 처리를 통해 에러 상황을 명확하게 정의하고 관리할 수 있습니다.
예: AdminNotFoundException, UserNotFoundException, ClientNotFoundException


5. repository: 데이터베이스와 상호작용하는 리포지토리 인터페이스를 포함합니다. JPA 리포지토리를 사용하여 데이터베이스 쿼리를 처리합니다.
예: AdminRepository, UserRepository, ClientRepository


6. service: 비즈니스 로직을 처리하는 서비스 클래스들을 포함합니다. 서비스 계층은 트랜잭션을 관리하고, 컨트롤러와 리포지토리 사이의 중간 계층 역할을 합니다.
예: AdminService, UserService, ClientService


7. config 패키지
config: 애플리케이션의 설정 및 구성 파일을 포함합니다. 보안 설정, 데이터베이스 설정, 웹 설정 등의 구성 클래스들이 포함됩니다.
예: SecurityConfig, WebConfig


8. util 패키지
util: 공통적으로 사용되는 유틸리티 클래스들을 포함합니다. 이 패키지에는 다양한 도메인과 기능에서 재사용 가능한 유틸리티 메소드와 클래스들이 포함됩니다.
예: DateUtil, StringUtil