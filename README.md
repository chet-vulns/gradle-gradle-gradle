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
| [project-c](project-c) | 8.10.2 | Groovy | Hard-coded credentials (CWE-798), insecure deserialization (CWE-502), insecure randomness (CWE-330), LDAP injection (CWE-90) |

All third-party dependencies are pinned to current, **non-vulnerable**
versions — every finding in this repo should originate from the project's
own source files.

## Building

```bash
cd project-a && ./gradlew build
cd project-b && ./gradlew build
cd project-c && ./gradlew build
```

## CodeQL: fixing the `Required Gradle version not specified` warning

If GitHub code scanning reports:

> :warning: **Required Gradle version not specified** — Analyzed a Gradle
> project without the Gradle wrapper. This may use an incompatible
> version of Gradle.

…CodeQL has detected a Gradle build at the repository root (a
`settings.gradle`/`settings.gradle.kts` or `build.gradle`/`build.gradle.kts`)
but found no Gradle wrapper next to it. Without a wrapper, CodeQL
(and any clean clone of the repo) has no way to know which Gradle
version the build expects, and falls back to whatever Gradle is on the
runner's `PATH` — which may be incompatible with your build scripts.

### Fix

Commit a Gradle wrapper at the same directory as the root build script.
Run this once on a developer machine (or in a one-off CI job) with any
locally installed `gradle`:

```bash
# From the repo root
gradle wrapper --gradle-version 8.10.2 --distribution-type bin
```

This generates four files that **must be committed**:

```
gradlew                              # POSIX launcher script
gradlew.bat                          # Windows launcher script
gradle/wrapper/gradle-wrapper.jar    # tiny bootstrapper (~50 KB)
gradle/wrapper/gradle-wrapper.properties   # pins the Gradle version + SHA-256
```

Pick a `--gradle-version` that:

1. Is supported on the JDK the CodeQL runner uses (Gradle 8.5+ supports
   JDK 21, which is the default on `ubuntu-latest`).
2. Is compatible with the plugins your `build.gradle` uses.

Then commit and push:

```bash
git add gradlew gradlew.bat gradle/wrapper/
git commit -m "Add Gradle wrapper for reproducible builds and CodeQL"
git push
```

### Verifying the fix

On the next CodeQL run:

- The `Required Gradle version not specified` warning should be gone.
- If you also see `Failed to extract dependency information from build
  tool Gradle` or `Java analysis failed to extract a dependency graph
  from Gradle`, the wrapper is now being used but Gradle itself is
  failing to resolve the project. Common causes:
  - Dependencies in a **private Maven registry** (Artifactory, Nexus,
    GitHub Packages, AWS CodeArtifact, …) — the workflow needs the same
    credentials your local machine has. Provide them via repository
    secrets and a `~/.gradle/gradle.properties` file written in the
    workflow.
  - The pinned wrapper version is **incompatible with the runner's JDK**
    (e.g. Gradle 6.x on JDK 21). Bump the wrapper, or set up a
    matching JDK in the workflow with `actions/setup-java`.
  - A **plugin in `settings.gradle`** requires authentication to
    resolve. Same fix as private-registry credentials above.
  - A **composite/included build** is failing. Try
    `./gradlew :help --info` locally to surface the underlying error.

### Note about this repo

This monorepo is itself a demonstration of the warning and the fix:
each subproject has always shipped its own wrapper, but the **root**
`settings.gradle` (a Gradle composite build) was deliberately left
without a root wrapper to reproduce the CodeQL message. The
`gradlew` / `gradle/` files now committed at the root are the fix.

