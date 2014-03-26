var gulp = require('gulp');
var browserify = require('browserify');
var glob = require('glob');
var source = require('vinyl-source-stream')
// var streamify = require('gulp-streamify');
// var buffer = require('gulp-buffer');

gulp.task('default', function () {
  console.log('hello');
});

gulp.task('test-main', function () {
  var b = browserify();

  glob.sync('./src/main/js/**/*.js').forEach(function (file) {
    b.require(file, { expose: '.' });
  });

  b.add('./src/main/js/app.js');
  b.bundle()
    .pipe(source('g_core.js'))
    .pipe(gulp.dest('./target'));
});

gulp.task('test-build', function () {
  var b = browserify();

  // externalize core
  glob.sync('./src/main/js/**/*.js').forEach(function (file) {
    b.external(file);
  });

  // add all tests
  glob.sync('./src/test/js/**/*.js').forEach(function (file) {
    b.add(file);
  });

  b.bundle()
    .pipe(source('g_test.js'))
    .pipe(gulp.dest('./target'));
});
