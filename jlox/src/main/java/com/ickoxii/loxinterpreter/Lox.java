package com.ickoxii.loxinterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import com.ickoxii.loxinterpreter.enums.TokenType;

/**
 * Lox is the base class for our loxinterpreter.
 * */
public class Lox {
  private static final Interpreter interpreter = new Interpreter();

  static boolean hadError = false;
  static boolean hadRuntimeError = false;

  public static void main(String[] args) throws IOException {
    if(args.length > 1) {
      System.out.println("Usage: jlox [script]");
      System.exit(64);
    } else if(args.length == 1) {
      runFile(args[0]);
    } else {
      runPrompt();
    }
  }

  /**
   * Read the file and execute.
   *
   * @throws IOException If error occurs when reading file.
   * */
  private static void runFile(String path) throws IOException {
    byte[] bytes = Files.readAllBytes(Paths.get(path));
    run(new String(bytes, Charset.defaultCharset()));

    if(hadError) System.exit(65);
    if(hadRuntimeError) System.exit(70);
  }

  /**
   * Called when {@code jlox} is called without any command
   * line arguments. This launches {@code jlox} in REPL mode
   * (Read, Evaluate, Print Loop) where users can enter and execute
   * code line by line.
   *
   * @throws IOException If error occurs when reading code.
   * */
  private static void runPrompt() throws IOException {
    InputStreamReader input = new InputStreamReader(System.in);
    BufferedReader reader = new BufferedReader(input);

    for(;;) {
      System.out.print("> ");
      String line = reader.readLine();
      if(line == null) break;
      run(line);
      hadError = false; // Reset error flag so it doesn't terminate entire session
    }
  }

  /**
   *
   * */
  private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    List<Stmt> statements = parser.parse();

    // Stop if there was any syntax error.
    if (hadError) return;

    interpreter.interpret(statements);
  }

  /**
   * Wrapper for the report error function. It is good practice to decouple
   * code that generates errors from the code that reports them.
   *
   * @param line Line number containing the error.
   * @param message Error message.
   * */
  static void error(int line, String message) {
    report(line, "", message);
  }

  static void runtimeError(RuntimeError error) {
    System.err.println(error.getMessage() +
        "\n[line " + error.token.line + "]");
    hadRuntimeError = true;
  }

  /**
   * Prints a formatted error message.
   *
   * @param line Line number containing error.
   * @param where Location where error occurs.
   * @param message Error message.
   * */
  private static void report(int line, String where, String message) {
    System.err.println(
      "[line " + line + "] Error" + where + ": " + message
    );
    hadError = true;
  }

  static void error(Token token, String message) {
    if (token.type == TokenType.EOF) {
      report(token.line, " at end", message);
    } else {
      report(token.line, " at '" + token.lexeme + "'", message);
    }
  }
}
