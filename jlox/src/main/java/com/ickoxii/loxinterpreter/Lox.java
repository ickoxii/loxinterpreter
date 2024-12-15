package com.ickoxii.loxinterpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Lox is the base class for our loxinterpreter.
 * */
public class Lox {
  static boolean hadError = false;

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

    for(Token token : tokens) {
      System.out.println(token);
    }
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
}
