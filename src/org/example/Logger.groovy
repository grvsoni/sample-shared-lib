package org.example

/**
 * Small logging helper used by the shared library steps.
 * Demonstrates the use of a class under src/ in a Jenkins shared library.
 */
class Logger implements Serializable {

    private final def steps
    private final String context

    Logger(def steps, String context = 'pipeline') {
        this.steps = steps
        this.context = context
    }

    void info(String message) {
        steps.echo "[INFO][${context}] ${message}"
    }

    void warn(String message) {
        steps.echo "[WARN][${context}] ${message}"
    }

    void banner(String message) {
        String line = '=' * (message.length() + 8)
        steps.echo "\n${line}\n==  ${message}  ==\n${line}"
    }
}
