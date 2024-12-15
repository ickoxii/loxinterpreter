package com.ickoxii.loxinterpreter.enums;

/**
 * {@code TokenType} stores reserved {@code Lox} keywords.
 * <p>
 * A lexeme is a sequence of characters in the source code that
 * matches a predefined pattern by the language's grammar. It is
 * the string of characters that the lexer identifies as a meaningful
 * unit.
 * <p>
 * A token is an abstract representation of a lexeme. It consists of a
 * token name (or type) and, optionally, a token value. The token name
 * categorizes the lexeme (i.e. identifier, keyword, operator, etc).
 * The token value can hold additional information, such as the specific
 * characters of the lexeme or its position in the source code.
 * */
public enum TokenType {
  // Single-character tokens.
  LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
  COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

  // One or two character tokens.
  BANG, BANG_EQUAL,
  EQUAL, EQUAL_EQUAL,
  GREATER, GREATER_EQUAL,
  LESS, LESS_EQUAL,

  // Literals.
  IDENTIFIER, STRING, NUMBER,

  // Keywords.
  AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
  PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

  EOF
}
