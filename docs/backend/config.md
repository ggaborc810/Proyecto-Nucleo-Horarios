# Configuración Backend

## Dependencias `pom.xml`

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>

<properties>
    <java.version>17</java.version>
</properties>

<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-websocket</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- PostgreSQL -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.5</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Tests -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
            <configuration>
                <excludes>
                    <exclude>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclude>
                </excludes>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## `application.yml`

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/horarios_db}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD:postgres}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate          # validate en producción; create-drop en dev local
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

server:
  port: 8080
  servlet:
    context-path: /

app:
  jwt:
    secret: ${JWT_SECRET:cambiar-en-produccion-clave-segura-de-32-caracteres-minimo}
    expiration: 86400000          # 24h en ms
  scheduler:
    max-iteraciones: 10000
    timeout-segundos: 60

logging:
  level:
    co.edu.unbosque: INFO
    org.springframework.security: INFO
    org.hibernate.SQL: WARN
```

## `application-dev.yml`

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  datasource:
    url: jdbc:postgresql://localhost:5432/horarios_dev

logging:
  level:
    co.edu.unbosque: DEBUG
    org.hibernate.SQL: DEBUG
```

## `application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect

app:
  jwt:
    secret: test-secret-key-de-al-menos-32-caracteres-para-pruebas
    expiration: 3600000
  scheduler:
    max-iteraciones: 1000
    timeout-segundos: 30
```

## Estructura de Paquetes

```
co.edu.unbosque.horarios/
├── HorariosApplication.java          (clase main)
├── config/
│   └── CorsConfig.java
├── model/                            (entidades JPA)
├── repository/                       (interfaces JpaRepository)
├── service/
│   ├── HorarioService.java
│   ├── DocenteService.java
│   ├── AulaService.java
│   ├── CursoService.java
│   ├── GrupoService.java
│   ├── ParametroSemestreService.java
│   ├── AsignacionService.java
│   ├── ValidacionDatosMaestrosService.java
│   ├── HorarioPublicoService.java
│   └── algorithm/                    (componentes de dominio)
│       ├── HCEvaluator.java
│       ├── SoftEvaluator.java
│       ├── AsignacionCandidato.java
│       ├── HorarioContexto.java
│       ├── ResultadoGeneracion.java
│       ├── ConflictoAsignacion.java
│       ├── SchedulerEngine.java
│       ├── evaluators/               (10 implementaciones de HCEvaluator)
│       └── soft/                     (2 implementaciones de SoftEvaluator)
├── controller/                       (REST controllers)
├── websocket/
│   ├── WebSocketConfig.java
│   └── AsignacionWebSocketController.java
├── security/
│   ├── SecurityConfig.java
│   ├── JwtUtil.java
│   ├── JwtAuthFilter.java
│   ├── UserDetailsServiceImpl.java
│   └── AuthService.java
├── dto/                              (records de transferencia)
└── exception/                        (excepciones de dominio)
```

## Variables de Entorno

| Variable | Descripción | Default |
|----------|------------|---------|
| `DB_URL` | URL JDBC PostgreSQL | `jdbc:postgresql://localhost:5432/horarios_db` |
| `DB_USER` | Usuario BD | `postgres` |
| `DB_PASSWORD` | Contraseña BD | `postgres` |
| `JWT_SECRET` | Clave HMAC para JWT (≥ 32 chars) | inseguro por defecto |
| `JWT_EXPIRATION` | Duración del JWT en ms | `86400000` (24h) |

## Inicialización (clase main)

```java
package co.edu.unbosque.horarios;

@SpringBootApplication
public class HorariosApplication {
    public static void main(String[] args) {
        SpringApplication.run(HorariosApplication.class, args);
    }
}
```

## Comandos

```bash
# Compilar
mvn clean compile

# Ejecutar tests
mvn test

# Ejecutar solo tests del motor (deben pasar en < 10s)
mvn test -Dtest='*Evaluator*Test,*EngineTest,*MotorTest'

# Iniciar servidor (perfil dev)
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Build JAR
mvn clean package

# Ejecutar JAR
java -jar target/horarios-1.0.0.jar --spring.profiles.active=prod
```
