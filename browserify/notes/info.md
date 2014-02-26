## Intro

### Installation

The `browserify` command line tool requires node.js and npm installed.

`$ npm install browserify -g`

### Example

In a `main.js` require some other modules.

```
var greetings = require('./greetings.js');
var bar = require('../lib/bar.js');
```

The `greetings.js` should export the module.

```
module.exports = function(name) {
	return 'Hello ' + name + '!';
}
```

### Command

To bundle...

`$ browserify main.js > bundle.js` 

or

`$ browserify main.js -o bundle.js`

### Dev configuration

Before starting out, create package.json file. Either an empty or via `npm init`.

Then any modules that the development requires should be installed via.

`$ npm install underscored --save-dev`

Which updates the package.json file with the dependency. And now `underscore` module can be used with `require(underscore)` function.

```
var _ = require('underscore');
_.each([1, 2, 3], function(n) {
	console.log(n);
});
```

### Templates

With `underscore` templates.

???

`$ browserify ????`