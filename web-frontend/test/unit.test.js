const assert = require('assert');
const { getMessage } = require('../index');

assert.strictEqual(getMessage(), 'Hello, World!', 'getMessage() should return the greeting');

console.log('unit test passed');
