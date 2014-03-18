var gulp = require('gulp');

gulp.task('default', function () {
  console.log('hello');
});

var gulp = require('gulp');
var browserify = require('gulp-browserify');
// var hbsfy = require('hbsfy');
var hbsfy = require('hbsfy').configure({
  extensions: ['html']
});

// Basic usage
gulp.task('scripts', function() {
  // Single entry point to browserify
  gulp.src('src/app.js')
    .pipe(browserify({
      transform: [hbsfy],
      insertGlobals : true,
      debug : !gulp.env.production
    }))
    .pipe(gulp.dest('./target'));
    // .pipe(gulp.dest('./build/js'))
});