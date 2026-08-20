# sample-shared-lib

A minimal **Jenkins Shared Library** plus two example pipelines that consume it.

## Layout

```
sample-shared-lib/
├── src/org/example/Logger.groovy   # helper class (compiled, src/ classpath)
├── vars/                           # global pipeline steps (callable by name)
│   ├── sayHello.groovy             # simple greeting step
│   ├── buildApp.groovy             # build/package the app
│   ├── runTests.groovy             # run a test suite (unit | integration | lint)
│   ├── deployApp.groovy            # deploy artifact to an environment
│   └── sendNotification.groovy     # post a build notification
├── orders-service/                 # sample Maven "Hello World" app (built/tested by serviceA)
├── web-frontend/                   # sample npm "Hello World" app (built/tested by serviceB)
├── Jenkinsfile.serviceA            # pipeline with parameter set #1 (orders-service)
└── Jenkinsfile.serviceB            # pipeline with parameter set #2 (web-frontend)
```

## Registering the library in Jenkins

**Manage Jenkins → System → Global Pipeline Libraries → Add**

- **Name:** `sample-shared-lib`
- **Default version:** `main` (or your branch)
- **Retrieval method:** Modern SCM → Git → this repo URL

Then reference it from a Jenkinsfile:

```groovy
@Library('sample-shared-lib') _
```

## Library steps (all accept parameters)

| Step                 | Key parameters                                                  |
|----------------------|------------------------------------------------------------------|
| `sayHello`           | `name`, `greeting`                                                |
| `buildApp`           | `appName` (req), `buildTool`, `environment`, `path`               |
| `runTests`           | `appName` (req), `suite`, `buildTool`, `path`                     |
| `deployApp`          | `appName` (req), `environment` (req), `replicas`                  |
| `sendNotification`   | `appName` (req), `channel`, `status`                              |

`buildApp` and `runTests` accept an optional `path` — the directory (relative
to the repo root) containing the application. When `path` is supplied, the
step runs the real `mvn`/`npm` command for that `buildTool` inside that
directory via `sh`. When `path` is omitted, the steps fall back to their
original mock/echo behavior.

## The two pipelines

Both Jenkinsfiles share the **same structure** — 3 stages (`Build`, `Test`, `Deploy`),
2 steps per stage, and a **parallel** `Test` stage — but run with **different
default parameter sets** (a mix of `string` and `choice` parameters):

| Parameter     | Type   | `Jenkinsfile.serviceA` default | `Jenkinsfile.serviceB` default |
|---------------|--------|--------------------------------|--------------------------------|
| `APP_NAME`    | string | `orders-service`               | `web-frontend`                 |
| `APP_DIR`     | string | `orders-service`               | `web-frontend`                 |
| `BUILD_TOOL`  | choice | `maven`                        | `npm`                          |
| `ENVIRONMENT` | choice | `dev`                          | `staging`                      |
| `REPLICAS`    | string | `2`                            | `1`                            |

`Build` and `Test` now run **real** commands (`mvn`/`npm`) against the sample
apps in `orders-service/` and `web-frontend/`, via the `APP_DIR` parameter
passed through as `path` to `buildApp`/`runTests`. `Deploy` remains a
**mocked** step — `deployApp` only `echo`s the commands it *would* run, so
the pipelines are still safe to run on any agent without real deploy
credentials/infra. Swap the `echo` lines in `deployApp.groovy` for real
`sh`/`kubectl` calls to make deploys real too.

## Running the sample apps locally (macOS / Linux)

Both sample apps can be built and tested outside of Jenkins, using the same
commands the pipelines run.

### `orders-service` (Maven / Java)

Prerequisites: JDK 11+ and Maven 3.6+ (`java -version`, `mvn -version`).

```bash
cd orders-service

# Build (compiles + packages the jar into target/orders-service.jar)
mvn -B clean package

# Run the app
java -jar target/orders-service.jar
# -> Hello, World!

# Run unit tests only
mvn -B test

# Run the lint check (report-only; violations are printed but won't fail the build)
mvn -B checkstyle:check
```

### `web-frontend` (Node.js / npm)

Prerequisites: Node.js 14+ and npm (`node -v`, `npm -v`).

```bash
cd web-frontend

# Install dependencies (none required, but keeps parity with `npm ci` in CI)
npm install

# Build (copies index.js into dist/)
npm run build

# Run the app
node index.js
# -> Hello, World!

# Run unit tests
npm test

# Run integration tests
npm run test:integration
```
