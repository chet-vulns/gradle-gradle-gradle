# gradle-gradle-gradle

A dummy monorepo containing **three independent Gradle projects**, each with
its **own Gradle wrapper pinned to a different Gradle version** and each
intentionally depending on libraries with **known CVEs** for security tooling
demonstrations.

> [!WARNING]
> These projects pull in libraries with known critical vulnerabilities
> (Log4Shell, Spring4Shell, Text4Shell, etc.) on purpose. **Do not deploy
> any of this code.** It is intended only for testing SCA / vulnerability
> scanners.

There is **no root Gradle wrapper** — each subproject is built independently
from its own directory using its own `./gradlew`.

## Layout

| Project | Gradle | Build script | Sample vulnerable deps |
|---|---|---|---|
| [project-a](project-a) | 7.6.4 | Groovy DSL | log4j-core 2.14.1, commons-collections 3.2.1, jackson-databind 2.9.9, gson 2.8.6 |
| [project-b](project-b) | 8.5 | Groovy DSL | spring-webmvc 5.3.17, snakeyaml 1.30, commons-text 1.9, httpclient 4.5.12 |
| [project-c](project-c) | 8.10.2 | Kotlin DSL | guava 24.1-jre, xstream 1.4.17, h2 1.4.199, log4j 1.2.17 |

## Building

```bash
cd project-a && ./gradlew build
cd project-b && ./gradlew build
cd project-c && ./gradlew build
```
