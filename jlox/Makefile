JAR_NAME=loxinterpreter-0.0.1-SNAPSHOT.jar
ARTIFACT_ID=loxinterpreter

# Package project
build: generate-ast compile
	mvn package
.PHONY: build

compile:
	mvn clean compile
.PHONY: compile

generate-ast:
	# mvn exec:java -Dexec.mainClass="com.ickoxii.tool.GenerateAst" -Dexec.args="src/main/java/com/ickoxii/loxinterpreter"
	javac -d target/classes -cp src/main/java src/main/java/com/ickoxii/tool/GenerateAst.java
	java -cp target/classes com.ickoxii.tool.GenerateAst src/main/java/com/ickoxii/jlox
.PHONY: generate-ast

# Run project jar
run:
	java -jar target/$(JAR_NAME)
.PHONY: run

# Copy dependencies from pom without compiling project
update:
	mvn dependency:copy-dependencies
.PHONY: update
