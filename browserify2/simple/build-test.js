// $ browserify -x ./src/main/js/models/user.js ./src/test/js/models/userSpec.js > ./target/test.js

var glob = require('glob');
var browserify = require('browserify');

var b = browserify();

// add all tests
glob.sync('./src/test/js/**/*.js').forEach(function (file) {
  b.add(file);
});

// externalize core
glob.sync('./src/main/js/**/*.js').forEach(function (file) {
  b.external(file);
});

b.bundle().pipe(process.stdout);
