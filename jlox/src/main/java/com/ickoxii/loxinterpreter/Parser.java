package com.ickoxii.loxinterpreter;

import java.util.List;

import com.ickoxii.loxinterpreter.enums.TokenType;
import static com.ickoxii.loxinterpreter.enums.TokenType.*;

/**
 * Grammer Rules:
 *
 * expression -> equality;
 * equality   -> comparison ( ( "!=" | "==" ) comparison )*;
 * comparison -> term ( ( ">" | ">=" | "<" | "<=" ) term )*;
 * term       -> factor ( ( "-" | "+") factor )*;
 * factor     -> unary ( ( "/" | "*" ) unary )*;
 * unary      -> ( "!" | "-") unary
 *               | primary;
 * primary    -> NUMBER | STRING | "true" | "false" | "nil"
 *               | "(" expression ")";
 * */
class Parser {
  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * Expression rule simply expands to equality rule.
   * */
  private Expr expression() {
    return equality();
  }

  /**
   * Equality rule:
   *  * First comparison nonterminal translates to first call to comparison
   *    * We store this in a local variable
   *  * ( ... )* loop in the rule maps to a while loop
   *    * If we don't see a != or == token, we must be done with equality
   *    * We use a match function to check
   * */
  private Expr equality() {
    Expr expr = comparison();
    while (match(BANG_EQUAL, EQUAL_EQUAL)) {
      Token operator = previous();
      Expr right = comparison();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Virtually identical to equality, we just change the token types
   * for the operators we match, and the method we call for the operands
   * */
  private Expr comparison() {
    Expr expr = term();

    while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
      Token operator = previous();
      Expr right = term();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Checks to see if the current token has any of the given types. If
   * so, it consumes the token and returns true. Otherwise it returns
   * false and leaves the current token alone
   * */
  private boolean match(TokenType... types) {
    for (TokenType type : types) {
      if (check(type)) {
        advance();
        return true;
      }
    }

    return false;
  }

  /**
   * Binary operator for addition and subtraction
   * */
  private Expr term() {
    Expr expr = factor();

    while (match(MINUS, PLUS)) {
      Token operator = previous();
      Expr right = factor();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Binary operator for multiplication and division
   * */
  private Expr factor() {
    Expr expr = unary();

    while (match(SLASH, STAR)) {
      Token operator = previous();
      Expr right = unary();
      expr = new Expr.Binary(expr, operator, right);
    }

    return expr;
  }

  /**
   * Unary operator
   * */
  private Expr unary() {
    if (match(BANG, MINUS)) {
      Token operator = previous();
      Expr right = unary();
      return new Expr.Unary(operator, right);
    }

    return primary();
  }

  /**
   * primary expression - highest level of precedence
   * */
  private Expr primary() {
    if (match(FALSE)) return new Expr.Literal(false);
    if (match(TRUE)) return new Expr.Literal(true);
    if (match(NIL)) return new Expr.Literal(null);

    if (match(NUMBER, STRING)) {
      return new Expr.Literal(previous().literal);
    }

    // If we read a left paren, we must find a ) token
    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }
  }

  /**
   * Returns true if the current token is of the given type. This does
   * not consume any tokens and merely looks at it
   * */
  private boolean check(TokenType type) {
    if (isAtEnd()) return false;
    return peek().type == type;
  }

  /**
   * Consumes the current token and returns it.
   * */
  private Token advance() {
    if (!isAtEnd()) current++;
    return previous();
  }

  /**
   * Checks if we've run out of tokens to parse
   * */
  private boolean isAtEnd() {
    return peek().type == EOF;
  }

  /**
   * Returns the current token we have yet to consume
   * */
  private Token peek() {
    return tokens.get(current);
  }

  /**
   * Returns the most recently consumed token
   * */
  private Token previous() {
    return tokens.get(current - 1);
  }
}
