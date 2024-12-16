# Potential TODOs

* [ ] [ErrorHandling](001-error-handling)
* [ ] Static Types
* [ ] Potentially refactor to use GoF Interpreter design pattern instead of visitor pattern
* [ ] Disallow redifining an existing variable, choose to throw an error instead
* [ ] Desugaring to implement ternary operator
* [ ] Add built-in functions to read user input, work w/ files, etc.
* [ ] Add built-in data types like lists, maps, etc

## 001 Error Handling

Add some `ErrorReporter` interface that gets passed to the scanner
and parse so that we can swap out different reporting strategies
(i.e. stderr, IDE's error window, logged to file, etc).
