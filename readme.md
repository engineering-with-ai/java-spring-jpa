# [spring jpa] 🍃

![](https://img.shields.io/gitlab/pipeline-status/engineering-with-ai/java-spring-jpa?branch=main&logo=gitlab)
![](https://gitlab.com/engineering-with-ai/java-spring-jpa/badges/main/coverage.svg)
![](https://img.shields.io/badge/21-gray?logo=openjdk)
![](https://img.shields.io/badge/4.1.1-gray?logo=springboot)
![](https://img.shields.io/badge/build-maven-C71A36?logo=apachemaven)
![](https://img.shields.io/badge/ORM-hibernate%2Fjpa-59666C)


## Pre-Requisites

```shell
pipx install semgrep
```

PMD and OWASP dependency-check are plain Maven deps — no extra install. Semgrep is a standalone
CLI (no JVM-native equivalent covers the same ground), so it has to be on `PATH` separately —
same tier as this fleet's `cargo install cargo-audit ...` in `rust-base/readme.md`.

## Layout

```
src/main/java/com/ewa/springjpa/
  Application.java          entry point + OpenAPI bean
  AppController.java        GET / health check
  Config.java               cfg.yml loader + validated Config record (nested LogLevel, Loader)
  example/                  CRUD resource — controller, service, JpaRepository, dto/ records
    entities/                 JPA entity
  callapi/                  outbound RestClient call — controller, service, dto/ record
src/test/java/com/ewa/springjpa/
  <feature>/<Feature>ServiceTest.java   unit — Mockito, AAA (Surefire)
tests/java/com/ewa/springjpa/
  <Feature>ResourceIT.java              integration — @SpringBootTest + Testcontainers / WireMock (Failsafe)
  TestcontainersConfiguration.java      shared real-Postgres container for every *IT
```

## `make` verbs

Every verb dispatches the same way: `make <verb>` (the poe-task / npm-script / `cargo cmd`
analog).

| verb | runs |
|---|---|
| `dev` | `spring-boot:run` (devtools hot-reload) |
| `depcheck` | `dependency:analyze-only` — unused declared deps, advisory |
| `format` | `spotless:apply` |
| `lint` | `pmd:check` — correctness + style |
| `typecheck` | `-DskipTests compile` |
| `audit-src` | `spotbugs:check` + `semgrep scan --config p/java` |
| `audit-packages` | `dependency-check:check` (OWASP) — advisory, see below |
| `security` | `audit-src` + `audit-packages` |
| `checks` | `depcheck` + `format` + `lint` + `typecheck` + `security` |
| `unit` | `test` (`*Test`, Surefire) |
| `integration` | `failsafe:integration-test` (`*IT`, Failsafe — needs Docker) |
| `test` | `unit` + `integration` |
| `cover` | unit tests w/ JaCoCo agent + report + 70% line gate |
| `review` | `claude` code-reviewer agent against the diff |
| `commit` | `checks` + `test` + `review` + `git add -A && git cz && git push` |

`./mvnw verify` still works directly too (spotless → compile → Surefire → SpotBugs → PMD →
Failsafe → JaCoCo gate) — the `make` verbs are a dispatch layer on top, not a replacement.

`audit-packages` (OWASP dependency-check) is deliberately **not** wired into `mvn verify`'s
automatic gate — without a free NVD API key the first scan is slow against NVD's public rate
limit, and false positives are common. It only runs when invoked directly, same as the siblings'
`pip-audit` / `npm audit` verbs. Reads `NVD_API_KEY` straight from the environment (no `-D` flag
needed) — see `template-secrets.env`.

## Config & secrets

- Non-secrets: `cfg.yml`, `local` / `beta` blocks, selected by `$ENV` (default `local`).
- Secrets: environment only. Names tracked in `template-secrets.env` (`POSTGRES_PASSWORD`, optional `NVD_API_KEY`).
- OpenAPI: `/swagger-ui`, `/v3/api-docs`.

## Toolchain

Build runs on JDK 21 via `maven-toolchains-plugin`; the system default JDK is left alone.
Point it at a JDK 21 with `~/.m2/toolchains.xml`:

```xml
<toolchains><toolchain><type>jdk</type><provides><version>21</version></provides>
  <configuration><jdkHome>/path/to/jdk-21</jdkHome></configuration></toolchain></toolchains>
```

CI generates the same file from `$JAVA_HOME` into a repo-local `.ci-toolchains.xml` and passes it
with `-t`. The Dockerfile writes one the same way, against the temurin base image.
