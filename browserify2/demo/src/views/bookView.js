var Backbone = require('backbone');
var template = require('./book.html');
// var template = require('./book.hbs');

var BookView = Backbone.View.extend({

  el: 'body',

  events: {
    'click': 'toggle'
  },

  render: function () {
    this.$el.empty();
    this.$el.html(template(this.model.toJSON()));
    return this;
  },

  toggle: function () {
    alert('hola');
  }

});

module.exports = BookView;