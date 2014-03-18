var glob = require('glob');
var _ = require('underscore');
var path = require('path');

// glob('./src/**/*.js', function (er, files) {
//   _.each(files, function (v, k, list) {
//     console.log(v + ':' + k);
//   });
// });

var requires = glob.sync('./src/**/*.js').map(function (file) {
  return [file, { expose: path.basename(file, '.js') }];
});

_.each(requires, function (v, k, list) {
  console.log(v + ':' + k);
  console.log('is array? :' + _.isArray(v));
  // console.log('first [' + _.first(v) + '] tail [' + _.tail(v) + ']');
  // JSON.stringify(v);
});

