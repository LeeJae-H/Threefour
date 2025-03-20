## 기술 스택
#### Backend
<img src="https://github.com/user-attachments/assets/2df0a4c8-23a7-4264-86e7-84015a9568f4" width="680" height="100">
    
#### Frontend
<img src="https://github.com/user-attachments/assets/c490fc2d-3ca2-4eaf-8ed8-f8637a3996b8" width="430" height="100">

<br>
<br>

## 요구사항
<div align="center">
    <img src="https://github.com/user-attachments/assets/c96a4de2-996b-4c38-bd39-ef7c8c76c271" width="680" height="410">
</div>
<br>

## 도메인 모델
<div align="center">
    <img src="https://github.com/user-attachments/assets/432b4310-1bec-44e7-93ca-a14340d1994f" width="600" height="300">
</div>
<br>
<div align="center">
    <img src="https://github.com/user-attachments/assets/705fd997-778c-4a78-abf4-39129c12b9c5" width="650" height="300">
</div>
<br>
<br>

## 소프트웨어 아키텍처 - 계층 구조
<div align="center">
    <img src="https://github.com/user-attachments/assets/94f2049f-e07a-42b2-bc8a-102aaee871ad" width="470" height="470">
</div>
<br>

- **표현(Presentation) 계층**
  - 클라이언트의 요청을 해석하고, 해당 요청에 맞는 응용 서비스를 실행한 후 그 결과를 클라이언트에게 응답한다.
  
- **응용(Application) 계층**
  - 도메인 계층을 조합해서 기능을 실행한다.
  - 값 검증(필수 값과 값의 형식 검증​ + 논리적 오류 검증​)을 수행한다.
  
- **도메인(Domain) 계층**
  - 도메인 규칙을 구현한다.
  
- **인프라스트럭처(Infrastructure) 계층**
  - 프레임워크, 구현 기술, 외부 시스템과의 연동 등을 처리한다.
<br>

## 모듈 구성
> **각 모듈 계층은 소프트웨어 아키텍처의 각 계층에 대응한다.**
<div align="center">
    <img src="https://github.com/user-attachments/assets/e2bc3b03-2e3a-4c32-b393-15912fa258b7" width="420" height="320">
</div>

<br>

- **공통(common) 모듈 계층**

  - 공통(common) 모듈은 모든 모듈에서 사용될 수 있다.
  - 공통(common) 모듈은 프로젝트 내 어떠한 모듈도 의존하지 않는다. => 추가적으로, 이 프로젝트의 공통(common) 모듈은 어떠한 외부 의존도 갖지 않도록 했다. (= 순수 Java 클래스)

<br>

## 패키지 구성
```bash
├── application
│   ├── auth
│   ├── user
│   └── post
|
├── common
│   ├── auth
│   ├── user
│   └── post
|
├── domain
│   ├── auth
│   ├── user
│   └── post
|
├── dto
│   ├── auth
│   ├── user
│   └── post
|
├── infrastructure
│   ├── auth
│   ├── config
│   └── user
|
├── ui
│   ├── auth
│   ├── user
│   └── post
``` 
