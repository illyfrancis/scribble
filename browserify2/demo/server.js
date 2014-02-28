var connect = require('connect');

connect.createServer(
  connect.static('target')
).listen(9091);