/**
 * Simple greeting step. Accepts a name parameter.
 *
 *   sayHello 'World'
 *   sayHello name: 'Team', greeting: 'Hi'
 */
def call(def arg = [:]) {
    String name
    String greeting = 'Hello'
    if (arg instanceof Map) {
        name = arg.get('name', 'World')
        greeting = arg.get('greeting', 'Hello')
    } else {
        name = arg?.toString() ?: 'World'
    }
    echo "${greeting}, ${name}!"
}
