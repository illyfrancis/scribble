// $ browserify -r ./src/main/js/models/user:foo ./src/main/js/app.js > ./target/main.js

var glob = require('glob');
var browserify = require('browserify');

var b = browserify('./src/main/js/app.js');

// set up require
glob.sync('./src/main/js/**/*.js').forEach(function (file) {
  b.require(file, { expose: '.' });
});

b.bundle().pipe(process.stdout);
