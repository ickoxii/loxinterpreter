package com.ickoxii.loxinterpreter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ickoxii.loxinterpreter.enums.TokenType.*;
import com.ickoxii.loxinterpreter.enums.TokenType;

class Scanner {
  private final String source;
  private final List<Token> tokens = new ArrayList<>();
  private int start = 0;
  private int current = 0;
  private int line = 1;

  private static final Map<String, TokenType> keywords;

  static {
    keywords = new HashMap<>();
    keywords.put("and",     AND);
    keywords.put("class",   CLASS);
    keywords.put("else",    ELSE);
    keywords.put("false",   FALSE);
    keywords.put("for",     FOR);
    keywords.put("fun",     FUN);
    keywords.put("if",      IF);
    keywords.put("nil",     NIL);
    keywords.put("or",      OR);
    keywords.put("print",   PRINT);
    keywords.put("return",  RETURN);
    keywords.put("super",   SUPER);
    keywords.put("this",    THIS);
    keywords.put("true",    TRUE);
    keywords.put("var",     VAR);
    keywords.put("while",   WHILE);
  }

  /**
   * Takes in raw source code as a simple string.
   * */
  Scanner(String source) {
    this.source = source;
  }

  /**
   * Runs through the raw source code and returns a list of all tokens.
   *
   * @return List of tokens appended with an {@code EOF}.
   * */
  List<Token> scanTokens() {
    while(!isAtEnd()) {
      start = current;
      scanToken();
    }

    tokens.add(new Token(EOF, "", null, line));
    return tokens;
  }

  /**
   * */
  private void scanToken() {
    char c = advance();
    switch(c) {
      case '(': addToken(LEFT_PAREN); break;
      case ')': addToken(RIGHT_PAREN); break;
      case '{': addToken(LEFT_BRACE); break;
      case '}': addToken(RIGHT_BRACE); break;
      case ',': addToken(COMMA); break;
      case '.': addToken(DOT); break;
      case '-': addToken(MINUS); break;
      case '+': addToken(PLUS); break;
      case ';': addToken(SEMICOLON); break;
      case '*': addToken(STAR); break;

      case '!':
        addToken(match('=') ? BANG_EQUAL: BANG);
        break;
      case '=':
        addToken(match('=') ? EQUAL_EQUAL: EQUAL);
        break;
      case '<':
        addToken(match('=') ? LESS_EQUAL: LESS);
        break;
      case '>':
        addToken(match('=') ? GREATER_EQUAL: GREATER);
        break;

      case '/':
        if(match('/')) {
          // A comment goes until the end of line.
          while(peek() != '\n' && !isAtEnd()) advance();
        } else {
          addToken(SLASH);
        }
        break;

      case ' ':
      case '\r':
      case '\t':
        // Ignore whitespace.
        break;

      case '\n':
        line++;
        break;

      case '"': string(); break;

      default:
        if(isDigit(c)) {
          number();
        } else if(isAlpha(c)) {
          identifier();
        }else {
          Lox.error(line, "Unexpected character.");
        }
        break;
    }
  }

  /**
   * Checks if we have consumed all of the characters.
   *
   * @return {@code true} if consumes, {@code false} if not.
   * */
  private boolean isAtEnd() {
    return current >= source.length();
  }

  /**
   * Checks if the current character is a digit.
   *
   * @param c Character to test.
   * @return {@code true} if {@code c} is a digit, {@code false} otherwise.
   * */
  private boolean isDigit(char c) {
    return c >= '0' && c <= '9';
  }

  /**
   * Checks if the current character is a letter or an underscore.
   *
   * @param c Character to test.
   * @return {@code true} if {@code c} is a letter or underscore,
   *  {@code false} otherwise.
   * */
  private boolean isAlpha(char c) {
    return  (c >= 'a' && c <= 'z') ||
            (c >= 'A' && c <= 'Z') ||
            c == '_';
  }

  /**
   * Checks if the current character is a letter, digit, or underscore.
   *
   * @param c Character to test.
   * @return {@code true} if {@code c} is alphanumeric, {@code false} otherwise.
   * */
  private boolean isAlphaNumeric(char c) {
    return isAlpha(c) || isDigit(c);
  }

  /**
   * Consumes the next character in the source file and returns it.
   *
   * @return Next character in source.
   * */
  private char advance() {
    return source.charAt(current++);
  }

  /**
   * Checks the next character without consuming it.
   *
   * @return Next character in source.
   * */
  private char peek() {
    if(isAtEnd()) return '\0';
    return source.charAt(current);
  }

  /**
   * Peeks two characters ahead and returns that character without
   * consuming it.
   *
   * @return The charcter two characters ahead.
   * */
  private char peekNext() {
    if(current + 1 >= source.length()) return '\0';
    return source.charAt(current + 1);
  }

  /**
   * Grabs the text of the current lexeme and creates a new token for it.
   *
   * @param type {@code TokenType}.
   * */
  private void addToken(TokenType type) {
    addToken(type, null);
  }

  /**
   * Grabs the text of the current lexeme and creates a new token for it.
   *
   * @param type {@code TokenType}.
   * @param literal Literal value of the current token.
   * */
  private void addToken(TokenType type, Object literal) {
    String text = source.substring(start, current);
    tokens.add(new Token(type, text, literal, line));
  }

  /**
   * Consumes the next character in the source file if it is the character
   * we are looking for.
   *
   * @param expected Character we are looking for.
   * @return {@code false} If we are at the end of file or if the next
   *   character is not the character we are looking for.
   *   <p>
   *   {@code true} If the next character matches our expected value.
   * */
  private boolean match(char expected) {
    if(isAtEnd()) return false;
    if(source.charAt(current) != expected) return false;

    current++;
    return true;
  }
  
  /**
   * Helper function for parsing string literals. This function
   * reads characters until a closing quotation mark is read.
   * If no closing quotation is found, then an error is printed.
   * */
  private void string() {
    while(peek() != '"' && !isAtEnd()) {
      if(peek() == '\n') line++;
      advance();
    }

    if(isAtEnd()) {
      Lox.error(line, "Unterminated String.");
      return;
    }

    // Skip closing '"'.
    advance();

    // Trim surrounding quotes
    String value = source.substring(start + 1, current - 1);
    addToken(STRING, value);
  }

  /**
   * Helper function for parsing number literals. This function allows
   * numbers to be in the form.
   * <p>
   * 1234
   * <p>
   * 12.34
   * <p>
   * We do not allow leading decimals or trailing decimals.
   * */
  private void number() {
    while(isDigit(peek())) advance();

    // Look for fractional part.
    if(peek() == '.' && isDigit(peekNext())) {
      // Consume the "."
      advance();

      while(isDigit(peek())) advance();
    }
    while(isDigit(peek())) advance();

    addToken(NUMBER,
      Double.parseDouble(source.substring(start, current)));
  }

  /**
   * Helper function for parsing identifier names.
   * */
  private void identifier() {
    while(isAlphaNumeric(peek())) advance();

    String text = source.substring(start, current);
    TokenType type = keywords.get(text);
    if(type == null) type = IDENTIFIER;
    addToken(type);
  }
}
