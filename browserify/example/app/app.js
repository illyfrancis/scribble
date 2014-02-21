var Backbone = require('backbone');
// var $ = require('jquery/dist/jquery'); --> no longer needed
var $ = require('jquery');

Backbone.$ = $;

var Book = require('./models/book');

console.log(new Book().get('title'));