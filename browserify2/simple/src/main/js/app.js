var user = require('./models/user');
var view = require('./views/authors');

view.init(user);
view.render();

user.changeName('Joe');
view.render();