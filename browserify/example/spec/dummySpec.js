var Book = require('../app/models/book');

describe("A suite", function() {
  it("with dummy expectation", function() {
    var book = new Book();
    expect(book.get('title')).toBe('A Book');
  });
});