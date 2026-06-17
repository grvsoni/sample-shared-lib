import org.example.Logger

/**
 * Sends a build/deploy notification.
 *
 * Required params:
 *   appName  (String) - application name
 * Optional params:
 *   channel  (String) - notification channel (default: '#builds')
 *   status   (String) - 'SUCCESS' | 'FAILURE' (default: currentBuild.currentResult)
 */
def call(Map config = [:]) {
    String appName = config.appName ?: error('sendNotification: "appName" parameter is required')
    String channel = config.get('channel', '#builds')
    String status  = config.get('status', currentBuild?.currentResult ?: 'SUCCESS')

    Logger log = new Logger(this, 'notify')
    log.info("Notifying ${channel}: ${appName} finished with status ${status}")
    echo "Slack -> ${channel}: [${status}] ${appName} pipeline complete."
}
