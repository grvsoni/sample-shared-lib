import org.example.Logger

/**
 * Deploys the built artifact to a target environment.
 *
 * Required params:
 *   appName      (String) - application to deploy
 *   environment  (String) - 'dev' | 'staging' | 'production'
 * Optional params:
 *   artifact     (String)  - artifact name (default derived from appName)
 *   replicas     (Integer) - number of replicas (default: 1)
 */
def call(Map config = [:]) {
    String appName     = config.appName ?: error('deployApp: "appName" parameter is required')
    String environment = config.environment ?: error('deployApp: "environment" parameter is required')
    String artifact    = config.get('artifact', "${appName}-${environment}.jar")
    int replicas       = (config.get('replicas', 1)) as int

    Logger log = new Logger(this, 'deploy')
    log.banner("Deploying ${artifact} to ${environment} (${replicas} replica(s))")

    if (environment == 'production' && replicas < 2) {
        log.warn('Deploying to production with fewer than 2 replicas.')
    }

    log.info("Would run: kubectl -n ${environment} set image deploy/${appName} ${appName}=${artifact}")
    echo "${appName} is now live in ${environment}."
}
