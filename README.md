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

| Step                 | Key parameters                                  |
|----------------------|-------------------------------------------------|
| `sayHello`           | `name`, `greeting`                              |
| `buildApp`           | `appName` (req), `buildTool`, `environment`     |
| `runTests`           | `appName` (req), `suite`                         |
| `deployApp`          | `appName` (req), `environment` (req), `replicas`|
| `sendNotification`   | `appName` (req), `channel`, `status`            |

## The two pipelines

Both Jenkinsfiles share the **same structure** — 3 stages (`Build`, `Test`, `Deploy`),
2 steps per stage, and a **parallel** `Test` stage — but run with **different
default parameter sets** (a mix of `string` and `choice` parameters):

| Parameter     | Type   | `Jenkinsfile.serviceA` default | `Jenkinsfile.serviceB` default |
|---------------|--------|--------------------------------|--------------------------------|
| `APP_NAME`    | string | `orders-service`               | `web-frontend`                 |
| `BUILD_TOOL`  | choice | `maven`                        | `npm`                          |
| `ENVIRONMENT` | choice | `dev`                          | `staging`                      |
| `REPLICAS`    | string | `2`                            | `1`                            |

> The library steps `echo` the commands they *would* run instead of actually
> invoking `sh`, so the pipelines are safe to run on any agent without a real
> build toolchain. Swap the `echo` lines for `sh` calls to make them real.
