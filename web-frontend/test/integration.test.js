const assert = require('assert');
const { execFileSync } = require('child_process');
const path = require('path');

// Integration check: run the app as a real subprocess and verify stdout,
// exercising the same entry point Jenkins/production would invoke.
const output = execFileSync(process.execPath, [path.join(__dirname, '..', 'index.js')])
  .toString()
  .trim();

assert.strictEqual(output, 'Hello, World!', 'running index.js should print the greeting');

console.log('integration test passed');
