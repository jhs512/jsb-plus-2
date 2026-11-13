# jsb-plus-2

『점프 투 스프링부트』(SBB)를 처음부터 구현하면서 장희성 강사의 14163(점프 투 스프링부트 개선)·14168(JPA·타임리프 개선) 수업 내용을 적용한 학습용 저장소입니다.

**각 단계마다 핵심 코드의 변경 전후와 실행 화면을 함께 남겼습니다.** 아래 작업내역을 위에서부터 따라 읽으면 됩니다.

- 진행 계획: [docs/PLAN.md](docs/PLAN.md)
- 1차 구현: [jhs512/jsb-plus](https://github.com/jhs512/jsb-plus)
- 단계 링크: **이 시점의 코드**(`/tree/태그`, 그 시점의 README와 이미지) · **변경 내용**(`/commit/태그`, diff)
- 태그: 기능 태그(`004-03`)와 커밋 순번 태그(`001`, `002`, …)

## 작업내역

### 000-01 저장소 준비, README와 진행 계획
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/000-01) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/000-01)

**무엇을** 빈 저장소에 README(작업내역 뼈대), 진행 계획([docs/PLAN.md](docs/PLAN.md)), `.gitignore`를 만들었습니다.

**왜** 이 저장소는 단계마다 "변경 전후 코드 + 실행 화면"을 README에 쌓아 가는 학습 기록입니다. 그 틀을 먼저 세웁니다.

`.gitignore`
```gitignore
.DS_Store
.idea/
build/
.gradle/
```

> **확인해 볼 것** 위의 "이 시점의 코드" 링크를 열면 GitHub이 이 커밋 시점의 README를 보여 줍니다. 앞으로 모든 단계를 이렇게 그 시점 기준으로 열어 볼 수 있습니다.

### 001-01 프로젝트 생성, Spring Initializr로 생성
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/001-01) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/001-01)

**무엇을** [Spring Initializr](https://start.spring.io)로 프로젝트를 만들었습니다. 의존성은 Spring Web MVC, Lombok, DevTools 세 가지입니다.

**왜** 교재는 Spring Boot 3.2 + Gradle Groovy를 쓰지만, 14163 강의처럼 최신 환경(Boot 4.1.2, Java 25, Kotlin DSL)으로 시작합니다.

`build.gradle.kts`
```kotlin
plugins {
    java
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"
}

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    // ...
}
```

**결과** 앱은 뜨지만, 아직 요청을 처리할 컨트롤러가 없어서 `/`는 **404**입니다.
![GET / 404](docs/steps/001-01-1.jpg)

> **교재와 다른 점** Boot 3.2.1 → 4.1.2, `build.gradle`(Groovy) → `build.gradle.kts`(Kotlin). Boot 4부터 웹 스타터 이름이 `starter-web`에서 `starter-webmvc`로 바뀌었습니다.
> **확인해 볼 것** 메인 클래스 `SbbApplication`이 있는 패키지(`com.mysite.sbb`) 아래에 있어야 스프링이 컴포넌트를 찾습니다.

### 001-02 프로젝트 생성, application.yaml로 전환
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/001-02) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/001-02)

**무엇을** 설정 파일을 `application.properties`에서 `application.yaml`로 바꿨습니다.

**왜** 설정이 늘어나면 `spring.jpa.hibernate.ddl-auto`처럼 같은 앞부분이 반복됩니다. yaml은 들여쓰기로 계층을 표현해서 읽기 쉽습니다(14163·14168 방식).

`application.properties` → `application.yaml`
```diff
- spring.application.name=sbb
+ spring:
+   application:
+     name: sbb
+   output:
+     ansi:
+       enabled: always   # 콘솔 로그에 색을 입힘
```

> **확인해 볼 것** 같은 설정을 properties로 쓰면 `spring.output.ansi.enabled=always`입니다. 두 형식이 1:1로 대응합니다.

### 002-01 간단한 웹 프로그램, [실험] void 액션은 500
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/002-01) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/002-01)

**무엇을** `MainController`에 `GET /`, `GET /hello`를 처리하는 액션 메서드를 만들었습니다. 반환형은 일부러 `void`로 했습니다. **(실험 커밋)**

**왜** `@Controller`와 `@GetMapping`으로 URL과 메서드가 연결되는 것을 확인하고, "메서드가 실행되는 것"과 "브라우저가 응답을 받는 것"이 다르다는 것을 직접 봅니다(14163 3강).

`MainController.java`
```java
@Controller
public class MainController {
    @GetMapping("/hello")
    public void hello() {
        System.out.println("hello"); // 서버 콘솔에만 찍힌다
    }
}
```

**결과** 서버 콘솔에는 `hello`가 찍히지만(메서드는 실행됨), 브라우저는 **500**을 받습니다.
![GET /hello 500](docs/steps/002-01-1.jpg)

> **왜 500일까?** `void`를 반환하면 스프링은 요청 URL(`hello`)을 화면(뷰) 이름으로 씁니다. 그 화면을 찾으려고 다시 `/hello`로 보내고, 또 같은 메서드가 실행되는 무한 반복이 되어 `Circular view path` 에러가 납니다.
> **확인해 볼 것** 없는 주소(`/not-exist`)는 500이 아니라 404입니다. 매핑된 메서드가 아예 없기 때문입니다.

### 002-02 간단한 웹 프로그램, @ResponseBody로 문자열 응답
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/002-02) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/002-02)

**무엇을** 액션 메서드가 `String`을 반환하고 `@ResponseBody`를 붙였습니다. 브라우저 대신 반복해서 확인할 수 있도록 MockMvc 테스트도 만들었습니다.

**왜** `@ResponseBody`가 있으면 반환값이 **화면 이름이 아니라 응답 본문 그 자체**가 됩니다(14163 4강).

`MainController.java`
```diff
  @GetMapping("/hello")
- public void hello() {
-     System.out.println("hello");
- }
+ @ResponseBody // 반환값이 그대로 HTTP 응답 본문이 된다.
+ public String hello() {
+     return "안녕하세요.";
+ }
```

`MainControllerTest.java`
```java
mvc.perform(get("/hello"))
        .andExpect(status().isOk())
        .andExpect(content().string("안녕하세요."));
```

**결과** 이제 **200**과 함께 문자열이 그대로 보입니다.
![GET /hello 200](docs/steps/002-02-1.jpg)

> **확인해 볼 것** 문자열 대신 객체를 반환하면 어떻게 될까요? 스프링이 자동으로 JSON으로 바꿉니다(14168 29강).

### 003-01 DB 설정, H2·JPA 추가와 dev/test 프로파일 분리
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/003-01) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/003-01)

**무엇을** H2 데이터베이스와 Spring Data JPA를 추가하고, 설정을 **dev(파일 DB)**와 **test(메모리 DB)** 프로파일로 나눴습니다.

**왜** 교재는 DB 하나로 개발과 테스트를 함께 합니다. 그러면 테스트가 개발 데이터를 계속 쌓거나 지웁니다. 14163 5강처럼 둘을 분리합니다.

`build.gradle.kts`
```diff
  implementation("org.springframework.boot:spring-boot-starter-webmvc")
+ implementation("org.springframework.boot:spring-boot-starter-data-jpa")
+ implementation("org.springframework.boot:spring-boot-h2console")
+ runtimeOnly("com.h2database:h2")
```

`application-dev.yaml` / `application-test.yaml`
```yaml
# dev: 재시작해도 데이터가 남는 파일 DB
url: jdbc:h2:./db_dev
# test: 테스트가 끝나면 사라지는 메모리 DB
url: jdbc:h2:mem:db_test
```

`application.yaml`
```yaml
spring:
  profiles:
    active: dev   # 기본은 dev, 테스트 클래스는 @ActiveProfiles("test")
  jpa:
    hibernate:
      ddl-auto: update   # 엔티티를 보고 테이블을 자동 생성/수정
```

**결과** `/h2-console`에 접속됩니다. 아직 엔티티가 없어서 테이블은 없습니다.
![H2 콘솔](docs/steps/003-01-1.jpg)

> **교재와 다른 점** DB 하나(`~/local`) → dev/test 분리. 개발 DB 파일(`db_dev.mv.db`)은 `.gitignore`에 추가했습니다.
> **확인해 볼 것** 앱을 한 번 실행하면 프로젝트 폴더에 `db_dev.mv.db` 파일이 생깁니다.

### 003-02 DB 설정, 테스트는 항상 test 프로파일
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/003-02) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/003-02)

**무엇을** Gradle 테스트 태스크에 `spring.profiles.active=test`를 지정했습니다. 그리고 일부러 `@ActiveProfiles`를 붙이지 않은 확인용 테스트를 만들었습니다.

**왜** 테스트 클래스마다 `@ActiveProfiles("test")`를 붙이는 방식은 하나만 빠뜨려도 기본 프로파일(dev)이 적용됩니다. 그러면 그 테스트가 개발용 파일 DB를 건드립니다. 실수가 생기지 않도록 테스트의 기본값을 아예 test로 고정합니다.

`build.gradle.kts`
```diff
  tasks.withType<Test> {
      useJUnitPlatform()
+     systemProperty("spring.profiles.active", "test")
  }
```

`DefaultTestProfileTest.java`
```java
@SpringBootTest // @ActiveProfiles 일부러 없음
class DefaultTestProfileTest {
    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Test
    void t1() {
        assertThat(datasourceUrl).startsWith("jdbc:h2:mem:");
    }
}
```

**결과** 설정 전에는 이 테스트가 **개발용 파일 DB**(`jdbc:h2:./db_dev`)를 쓰고 있어서 실패했습니다. 설정 후에는 모든 테스트가 메모리 DB로 실행됩니다.
![변경 전](docs/steps/003-02-1.jpg)
![변경 후](docs/steps/003-02-2.jpg)

> **교재와 다른 점** 교재에는 없는 안전장치입니다. 1차 구현에서는 마지막(040)에야 넣었는데, DB 설정 단계로 옮겼습니다.

### 003-03 DB 설정, SQL 로그 출력
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/003-03) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/003-03)

**무엇을** JPA가 실행하는 SQL을 콘솔에 보기 좋게 출력하도록 설정했습니다. `?` 자리에 들어간 실제 값과 트랜잭션 시작·종료도 함께 출력합니다.

**왜** JPA는 SQL을 대신 만들어 줍니다. 그래서 "지금 무슨 SQL이 나갔는지"를 눈으로 확인하는 습관이 중요합니다(14163 6강). 다음 단계부터 엔티티를 만들면 이 로그로 테이블 생성 SQL을 확인합니다.

`application.yaml`
```diff
  jpa:
    hibernate:
      ddl-auto: update
+   show-sql: true                 # 실행되는 SQL 출력
+   properties:
+     hibernate:
+       format_sql: true           # 줄바꿈 정렬
+       highlight_sql: true        # 색상 강조
+       use_sql_comments: true     # 어떤 JPA 동작에서 나온 SQL인지 주석
+ logging:
+   level:
+     org.hibernate.orm.jdbc.bind: TRACE      # ? 자리에 바인딩된 실제 값
+     org.hibernate.orm.jdbc.extract: TRACE   # 조회 결과 값
+     org.springframework.transaction.interceptor: TRACE  # 트랜잭션 시작/종료
```

> **확인해 볼 것** 아직 엔티티가 없어서 출력될 SQL이 없습니다. 바로 다음 단계(004-01)에서 `create table question …`이 이 형식으로 찍힙니다.

### 004-01 엔티티, Question 엔티티
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/004-01) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/004-01)

**무엇을** 질문을 저장할 `Question` 엔티티를 만들었습니다.

**왜** `@Entity`가 붙은 클래스를 보고 JPA가 테이블을 만듭니다(`ddl-auto: update`). 테이블을 SQL로 직접 만들지 않고 자바 클래스로 설계합니다(교재 2-04).

`Question.java` (새 파일)
```java
@Entity            // 이 클래스를 기준으로 question 테이블이 만들어진다.
@Getter @Setter
public class Question {
    @Id                                    // 기본키
    @GeneratedValue(strategy = IDENTITY)   // 1, 2, 3 … 자동 증가
    private int id;

    @Column(length = 200)                  // varchar(200)
    private String subject;

    @Column(columnDefinition = "TEXT")     // 길이 제한 없는 텍스트
    private String content;

    private LocalDateTime createDate;      // 컬럼 이름은 create_date
}
```

**결과** H2 콘솔에서 `QUESTION` 테이블과 컬럼을 확인했습니다. 앞 단계에서 켠 SQL 로그에도 테이블 생성문이 찍힙니다.
![QUESTION 테이블](docs/steps/004-01-1.jpg)
![create table 로그](docs/steps/004-01-2.jpg)

> **확인해 볼 것** `@Setter`는 지금은 편하지만, 009-02에서 생성자와 의미 있는 메서드로 바꿉니다(14168 방식).

### 004-02 엔티티, Answer와 @ManyToOne(LAZY)
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/004-02) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/004-02)

**무엇을** 답변을 저장할 `Answer` 엔티티를 만들고, 답변이 어느 질문에 속하는지 `@ManyToOne`으로 연결했습니다.

**왜** 답변 여러 개(N)가 질문 하나(1)에 속합니다. `@ManyToOne`을 붙이면 answer 테이블에 `question_id` 외래키 컬럼이 생깁니다. `@ManyToOne`의 기본값은 즉시 로딩(EAGER)이라 답변을 조회할 때마다 질문도 함께 조회합니다. 그래서 필요할 때만 조회하는 지연 로딩(LAZY)을 명시합니다(14163 25강).

`Answer.java` (새 파일, 핵심)
```java
@Entity
public class Answer {
    // ... id, content, createDate

    @ManyToOne(fetch = LAZY)   // 기본값 EAGER 대신 LAZY
    private Question question; // → question_id 컬럼 + 외래키
}
```

**결과** `ANSWER` 테이블에 `QUESTION_ID` 컬럼과 `QUESTION`을 가리키는 외래키 제약이 생겼습니다.
![ANSWER 테이블과 외래키](docs/steps/004-02-1.jpg)

> **교재와 다른 점** 교재는 `@ManyToOne`만 씁니다(EAGER). 여기서는 `fetch = LAZY`를 붙였습니다.
> **확인해 볼 것** 외래키가 있으니 존재하지 않는 질문 번호(예: 999)로 답변을 INSERT하면 DB가 거부합니다.

### 004-03 엔티티, [실험] mappedBy 없는 OneToMany는 중간 테이블을 만든다
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/004-03) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/004-03)

**무엇을** `Question`에 답변 목록 필드를 `@OneToMany`로 추가했습니다. 이때 일부러 `mappedBy`를 빼 봤습니다. **(실험 커밋)**

**왜** 질문 쪽에서도 답변 목록을 바로 꺼내 쓰려면 `@OneToMany` 필드가 필요합니다. 그런데 이 필드와 `Answer.question`이 **같은 관계**라는 걸 JPA에게 알려 주지 않으면 어떻게 되는지 직접 봅니다(14163 11강).

`Question.java`
```diff
  private LocalDateTime createDate;
+
+ @OneToMany   // mappedBy 없음!
+ private List<Answer> answers = new ArrayList<>();
```

**결과** 만든 적 없는 `QUESTION_ANSWERS` 테이블이 생겼습니다. 이미 `answer.question_id`로 충분히 표현되는 관계를 중간 테이블에 한 번 더 저장하려는 것입니다.
![QUESTION_ANSWERS 중간 테이블](docs/steps/004-03-1.jpg)

> **확인해 볼 것** 다음 커밋에서 `mappedBy = "question"` 한 단어를 넣으면 이 테이블이 사라집니다.

### 004-04 엔티티, mappedBy와 cascade로 OneToMany 완성
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/004-04) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/004-04)

**무엇을** `@OneToMany`에 `mappedBy = "question"`과 `cascade`를 넣었습니다.

**왜** `mappedBy = "question"`은 "이 관계의 주인은 `Answer.question` 필드(= `question_id` 외래키)"라는 뜻입니다. 질문 쪽 `answers`는 그 관계를 읽기만 하므로 중간 테이블이 필요 없습니다(14163 12강). `cascade`는 질문을 저장하거나 삭제할 때 답변도 함께 처리하도록 합니다(14163 21강).

`Question.java`
```diff
- @OneToMany
+ @OneToMany(mappedBy = "question", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
  private List<Answer> answers = new ArrayList<>();
```

| 옵션 | 뜻 |
|---|---|
| `mappedBy = "question"` | 외래키는 `Answer.question`이 관리 → 중간 테이블 없음 |
| `PERSIST` | 이 목록에 새로 추가한 답변은 자동 INSERT |
| `REMOVE` | 질문을 삭제하면 답변도 자동 DELETE |
| `= new ArrayList<>()` | null이 아니라서 바로 `add` 가능 (14163 23강) |

**결과** 이전 단계와 비교하면 `QUESTION_ANSWERS` 테이블이 사라졌습니다.

| 이전 (004-03) | 이후 (004-04) |
|---|---|
| ![이전](docs/steps/004-03-1.jpg) | ![이후](docs/steps/004-04-1.jpg) |

### 004-05 엔티티, BaseEntity와 JPA Auditing
[이 시점의 코드](https://github.com/jhs512/jsb-plus-2/tree/004-05) · [변경 내용](https://github.com/jhs512/jsb-plus-2/commit/004-05)

**무엇을** 모든 엔티티가 공통으로 갖는 id, 생성일, 수정일을 `BaseEntity` 하나로 모았습니다. 날짜는 JPA Auditing이 자동으로 채웁니다.

**왜** 교재는 엔티티마다 id와 날짜를 따로 선언하고, `createDate`를 서비스에서 `LocalDateTime.now()`로 직접 넣습니다. 공통 필드는 한 곳에서 관리하고 날짜는 자동으로 기록합니다(14168 2강).

`BaseEntity.java` (새 파일)
```java
@MappedSuperclass   // 테이블은 안 되고, 필드만 자식 엔티티 테이블에 들어간다
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    private int id;

    @CreatedDate        // INSERT 될 때 자동 기록
    private LocalDateTime createDate;

    @LastModifiedDate   // INSERT, UPDATE 될 때 자동 기록
    private LocalDateTime modifyDate;
}
```

`Question.java`, `Answer.java`
```diff
- public class Question {
-     @Id @GeneratedValue(strategy = IDENTITY)
-     private int id;
-     private LocalDateTime createDate;
+ public class Question extends BaseEntity {
```

`SbbApplication.java`
```diff
  @SpringBootApplication
+ @EnableJpaAuditing   // @CreatedDate, @LastModifiedDate 동작에 필요
```

**결과** 두 테이블 모두 `CREATE_DATE`와 새 `MODIFY_DATE` 컬럼을 갖게 되었습니다.
![BaseEntity 컬럼](docs/steps/004-05-1.jpg)
