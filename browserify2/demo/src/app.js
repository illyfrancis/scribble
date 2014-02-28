var Backbone = require('backbone');
var $ = require('jquery');
Backbone.$ = $;

var Book = require('./models/book'),
    BookView = require('./views/bookView');

var bv = new BookView({
  model: new Book({
    title: 'Heidi'
  })
});

bv.render();