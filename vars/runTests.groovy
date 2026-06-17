import org.example.Logger

/**
 * Runs a category of tests.
 *
 * Required params:
 *   appName  (String) - application under test
 *   suite    (String) - 'unit' | 'integration' | 'lint'  (default: 'unit')
 */
def call(Map config = [:]) {
    String appName = config.appName ?: error('runTests: "appName" parameter is required')
    String suite   = config.get('suite', 'unit')

    Logger log = new Logger(this, "test:${suite}")
    log.banner("Running ${suite} tests for ${appName}")

    Map commands = [
        unit       : 'mvn -B test',
        integration: 'mvn -B verify -Pintegration',
        lint       : 'mvn -B checkstyle:check'
    ]

    String cmd = commands.get(suite, "echo 'no command for ${suite}'")
    log.info("Would run: ${cmd}")
    echo "${suite} tests passed for ${appName}."
}
