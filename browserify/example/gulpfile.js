var gulp = require('gulp'),
  jshint = require('gulp-jshint'),
  concat = require('gulp-concat'),
  uglify = require('gulp-uglify');

// Lint JS
gulp.task('lint', function() {
  gulp.src('./app/**/*.js')
    .pipe(jshint())
    .pipe(jshint.reporter('default'));
});

// Concat & Minify JS
gulp.task('minify', function() {
  gulp.src('./app/*.js')
    .pipe(concat('all.js'))
    .pipe(uglify())
    .pipe(gulp.dest('./dist/'));
});

// --- deprecated warning, use below instead ---
// gulp.task('default', function () {
//   gulp.run('lint', 'minify');

//   // watch JS files
//   gulp.watch('./app/*.js', function(event) {
//     gulp.run('lint', 'minify');
//   });
// });

gulp.task('styles', function () {
  // just a placeholder...
});

gulp.task('watch', function () {
  gulp.watch('./app/css/*.css', ['styles']);
  gulp.watch('./app/**/*.js', ['lint', 'minify']);
});

gulp.task('clean', function() {
  // empty  
});


// Jasmine test
var jasmine = require('gulp-jasmine');

gulp.task('test', function () {
  gulp.src('spec/*Spec.js')
    .pipe(jasmine());
});


// Jasmine test (with coverage)
// var jasmine = require('gulp-jasmine'),
//   cover = require('gulp-coverage');

// gulp.task('test', function () {
//   gulp.src('spec/*Spec.js')
//     .pipe(cover.instrument({
//       pattern: ['./app/**/*.js'],
//       debugDirectory: 'debug'
//     }))
//     .pipe(jasmine())
//     .pipe(cover.report({
//       outFile: 'jasmine.html'
//     }));
// });

gulp.task('default', ['clean', 'watch']);
// gulp.task('default', ['clean'], function() {
//   gulp.start('watch');
// });