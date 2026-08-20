import org.example.Logger

/**
 * Compiles / packages the application.
 *
 * Required params:
 *   appName       (String) - name of the application being built
 * Optional params:
 *   buildTool     (String) - 'maven' | 'gradle' | 'npm'  (default: 'maven')
 *   environment   (String) - target environment label     (default: 'dev')
 *   path          (String) - directory of the app to build, relative to the
 *                             repo root. When provided, the real build
 *                             command is executed via `sh` in that directory.
 *                             When omitted, the step falls back to its
 *                             mock/echo behavior (backward compatible).
 */
def call(Map config = [:]) {
    String appName     = config.appName ?: error('buildApp: "appName" parameter is required')
    String buildTool   = config.get('buildTool', 'maven')
    String environment = config.get('environment', 'dev')
    String path        = config.get('path', null)

    Logger log = new Logger(this, 'build')
    log.banner("Building ${appName} (${environment}) with ${buildTool}")

    Map commands = [
        maven : 'mvn -B clean package',
        gradle: './gradlew clean build',
        npm   : 'npm ci && npm run build'
    ]

    String cmd = commands.get(buildTool)
    if (!cmd) {
        error("buildApp: unsupported buildTool '${buildTool}'. Use one of ${commands.keySet()}")
    }

    if (path) {
        log.info("Running in ${path}: ${cmd}")
        dir(path) {
            sh cmd
        }
    } else {
        log.info("Would run: ${cmd}")
        // In a real pipeline this would be: sh cmd  (or bat on Windows)
    }
    echo "Artifact ${appName}-${environment}.jar produced."
    return "${appName}-${environment}.jar"
}
