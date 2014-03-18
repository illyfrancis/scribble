var user = require('../models/user');

var Authors = {
  init: function (u) {
    this.user = u;
  },
  render: function () {
    console.log('rendering Authors [' + this.user.name + ']');
    return this.user.name;
  }
}

module.exports = Authors;