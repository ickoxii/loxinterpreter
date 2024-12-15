JAR_NAME=loxinterpreter-0.0.1-SNAPSHOT.jar
ARTIFACT_ID=loxinterpreter

# Package project
build: compile
	mvn package
.PHONY: build

compile: generate-ast
	mvn clean compile
.PHONY: compile

generate-ast:
	mvn exec:java -Dexec.mainClass="com.ickoxii.tool.GenerateAst" -Dexec.args="src/main/java/com/ickoxii/loxinterpreter"
.PHONY: generate-ast

# Run project jar
run:
	java -jar target/$(JAR_NAME)
.PHONY: build

# Copy dependencies from pom without compiling project
update:
	mvn dependency:copy-dependencies
.PHONY: update
