var express = require("express");
var app = express();
var port = 3700;


app.set("views", __dirname + "/tpl");
app.set("view engine", "jade");
app.engine("jade", require("jade").__express);
app.get("/", function(req, res) {
  res.render("page");
});

app.use(express.static(__dirname + "/public"));

var io = require("socket.io").listen(app.listen(port));

console.log("Listening on port " + port);

io.enable('browser client minification');
io.enable('log level 1');

io.sockets.on('connection', function (socket) {
  socket.emit('message', { message: 'tell me something' });
  socket.on('send', function (data) {
    io.sockets.emit('message', data);
  });
  socket.on('disconnect', function() {
    console.log('disconnecting...');
  });
});

