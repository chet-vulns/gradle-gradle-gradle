# gradle-gradle-gradle

A dummy monorepo containing **three independent Gradle projects**, each with
its **own Gradle wrapper pinned to a different Gradle version**, and each
containing **first-party security vulnerabilities** in its own source code
(not in third-party dependencies) for SAST / code-scanning tool
demonstrations.

> [!WARNING]
> The Java sources here intentionally contain SQL injection, command
> injection, XSS, SSRF, XXE, insecure deserialization, hard-coded
> credentials, and similar bugs. **Do not deploy any of this code.**

There is **no root Gradle wrapper** — each subproject is built independently
from its own directory using its own `./gradlew`.

## Layout

| Project                | Gradle | DSL    | First-party vulns in `App.java`                                                                                              |
| ---------------------- | ------ | ------ | ---------------------------------------------------------------------------------------------------------------------------- |
| [project-a](project-a) | 7.6.4  | Groovy | SQL injection (CWE-89), OS command injection (CWE-78), path traversal (CWE-22), broken hash / MD5 password (CWE-327)         |
| [project-b](project-b) | 8.5    | Groovy | Reflected XSS (CWE-79), SSRF (CWE-918), open redirect (CWE-601), XXE (CWE-611)                                               |
| [project-c](project-c) | 8.10.2 | Kotlin | Hard-coded credentials (CWE-798), insecure deserialization (CWE-502), insecure randomness (CWE-330), LDAP injection (CWE-90) |

All third-party dependencies are pinned to current, **non-vulnerable**
versions — every finding in this repo should originate from the project's
own source files.

## Building

```bash
cd project-a && ./gradlew build
cd project-b && ./gradlew build
cd project-c && ./gradlew build
```
