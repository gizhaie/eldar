# eldar

A monitoring/alerting tool for Storm topologies.

## Documentation

A [swagger](http://swagger.io/) documentation is available at:

* http://${DOMAIN}/eldar/api-docs

## Deploying

Assuming that `$ELDAR_HOME` is the root of this project, and that [leiningen](http://leiningen.org/) is installed. 

```
cd $ELDAR_HOME

lein uberjar

nohup java -Deldar.config=$ELDAR_HOME/config/config_pro.edn \
-jar target/uberjar/eldar-x.y.z-standalone.jar >& /dev/null &
```

## Developing

### Environment

To begin developing, start with a REPL.

```
lein repl
```

Run `go` to initiate and start the system.

```
user=> (go)
:started
```

By default this creates a web server at <http://localhost:3000>.

When you make changes to your source files, use `reset` to reload any
modified files and reset the server.

```
user=> (reset)
:reloading (...)
:resumed
```

### Testing

Testing is fastest through the REPL, as you avoid environment startup
time.

```
user=> (test)
...
```

But you can also run tests through Leiningen.

```
lein test
```

### Generators

This project has several [generators][] to help you create files.

* `lein gen endpoint <name>` to create a new endpoint
* `lein gen component <name>` to create a new component

[generators]: https://github.com/weavejester/lein-generate

