var user = require('../../../../src/main/js/models/user');
var authors = require('../../../../src/main/js/views/authors');

describe('Authors View', function () {
  describe('trying to test authors list view', function () {
    it('should display the list of authors', function () {
      authors.init(user);
      console.log('authors testing, calling authors.render() [' + authors.render() + ']');
    });
  });
});