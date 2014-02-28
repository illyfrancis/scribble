var Backbone = require('backbone');
var $ = require('jquery');
Backbone.$ = $;

var Book = require('./models/Book'),
    BookView = require('./views/BookView');

var bv = new BookView({
  model: new Book({
    title: 'Heidi'
  })
});

bv.render();