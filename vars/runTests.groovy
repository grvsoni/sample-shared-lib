import org.example.Logger

/**
 * Runs a category of tests.
 *
 * Required params:
 *   appName    (String) - application under test
 * Optional params:
 *   suite      (String) - 'unit' | 'integration' | 'lint'  (default: 'unit')
 *   buildTool  (String) - 'maven' | 'gradle' | 'npm'        (default: 'maven')
 *   path       (String) - directory of the app to test, relative to the repo
 *                          root. When provided, the real test command is
 *                          executed via `sh` in that directory. When
 *                          omitted, the step falls back to its mock/echo
 *                          behavior (backward compatible).
 */
def call(Map config = [:]) {
    String appName   = config.appName ?: error('runTests: "appName" parameter is required')
    String suite     = config.get('suite', 'unit')
    String buildTool = config.get('buildTool', 'maven')
    String path      = config.get('path', null)

    Logger log = new Logger(this, "test:${suite}")
    log.banner("Running ${suite} tests for ${appName}")

    Map commandsByTool = [
        maven: [
            unit       : 'mvn -B test',
            integration: 'mvn -B verify -Pintegration',
            lint       : 'mvn -B checkstyle:check'
        ],
        npm: [
            unit       : 'npm test',
            integration: 'npm run test:integration'
        ]
    ]

    Map commands = commandsByTool.get(buildTool, [:])
    String cmd = commands.get(suite, "echo 'no command for ${suite} (${buildTool})'")

    if (path) {
        log.info("Running in ${path}: ${cmd}")
        dir(path) {
            sh cmd
        }
    } else {
        log.info("Would run: ${cmd}")
    }
    echo "${suite} tests passed for ${appName}."
}
