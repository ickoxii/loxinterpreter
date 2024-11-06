# 001 Error Handling

Add some `ErrorReporter` interface that gets passed to the scanner
and parse so that we can swap out different reporting strategies
(i.e. stderr, IDE's error window, logged to file, etc).
