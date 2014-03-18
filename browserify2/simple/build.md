### App build

From project root (i.e. `simple`)

```
$ browserify ./src/main/js/app.js -o ./target/app.js
```

### Mocha run

Run from `simple/src/` because `mocha` expects `test` directory as default.

```
mocha --recursive -R spec
```

### Test build (core and test combined)

From project root.

`browserify ./src/test/js/views/authorsSpec.js > ./target/test.js`

It produces `test.js` that includes **everything**. We want to externalize **core** from the test.
And use the **core** via `require`.

### Separate test build


#### Prep core

```
$ browserify -r ./src/main/js/models/user:foo ./src/main/js/app.js > ./target/main.js
```

#### Prep single test

```
$ browserify -x ./src/main/js/models/user.js ./src/test/js/models/userSpec.js > ./target/test.js
```

