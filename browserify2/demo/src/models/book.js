var Backbone = require('backbone');

var Book = Backbone.Model.extend({
  defaults: {
    title: 'The catcher in the rye',
    author: 'J D Salinger'
  }
});

module.exports = Book;