function getMessage() {
  return 'Hello, World!';
}

if (require.main === module) {
  console.log(getMessage());
}

module.exports = { getMessage };
