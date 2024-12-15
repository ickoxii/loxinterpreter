package com.ickoxii.loxinterpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.lang.RuntimeException;

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
  /** A simple sentinel class used to unwind the parse */
  private static class ParseError extends RuntimeException {}

  private final List<Token> tokens;
  private int current = 0;

  Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  List<Stmt> parse() {
    List<Stmt> statements = new ArrayList<>();

    while (!isAtEnd()) {
      statements.add(declaration());
    }

    return statements;
  }

  /**
   * Expression rule simply expands to equality rule.
   * */
  private Expr expression() {
    return assignment();
  }

  private Stmt declaration() {
    try {
      if (match(VAR)) return varDeclaration();

      return statement();
    } catch (ParseError error) {
      synchronize();
      return null;
    }
  }

  /**
   * Statement rule to handle statements and state.
   * */
  private Stmt statement() {
    if (match(FOR)) return forStatement();
    if (match(IF)) return ifStatement();
    if (match(PRINT)) return printStatement();
    if (match(WHILE)) return whileStatement();
    if (match(LEFT_BRACE)) return new Stmt.Block(block());

    return expressionStatement();
  }

  private Stmt forStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'for'.");

    Stmt initializer;
    if (match(SEMICOLON)) {
      initializer = null;
    } else if (match(VAR)) {
      initializer = varDeclaration();
    } else {
      initializer = expressionStatement();
    }

    Expr condition = null;
    if (!check(SEMICOLON)) {
      condition = expression();
    }
    consume(SEMICOLON, "Expect ';' after loop condition.");

    Expr increment = null;
    if (!check(RIGHT_PAREN)) {
      increment = expression();
    }
    consume(RIGHT_PAREN, "Expect ')' after for clauses.");

    Stmt body = statement();

    // Increment executes after the body in each iteration.
    // We replace the boyd with a little block that contains
    // the original body followed by an expression statement
    // that evaluates the increment.
    if (increment != null) {
      body = new Stmt.Block(
          Arrays.asList(
              body,
              new Stmt.Expression(increment)));
    }

    // If condition is omitted, make an infinite loop
    if (condition == null) condition = new Expr.Literal(true);
    body = new Stmt.While(condition, body);

    // If there is an initializer, it runs once before the entire loop
    if (initializer != null) {
      body = new Stmt.Block(Arrays.asList(initializer, body));
    }

    return body;
  }

  private Stmt ifStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'if'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after if condition.");

    Stmt thenBranch = statement();
    Stmt elseBranch = null;
    if (match(ELSE)) {
      elseBranch = statement();
    }

    return new Stmt.If(condition, thenBranch, elseBranch);
  }

  private Stmt printStatement() {
    Expr value = expression();
    consume(SEMICOLON, "Expect ';' after value.");
    return new Stmt.Print(value);
  }

  private Stmt expressionStatement() {
    Expr expr = expression();
    consume(SEMICOLON, "Expect ';' after expression.");
    return new Stmt.Expression(expr);
  }

  private List<Stmt> block() {
    List<Stmt> statements = new ArrayList<>();

    while (!check(RIGHT_BRACE) && !isAtEnd()) {
      statements.add(declaration());
    }

    consume(RIGHT_BRACE, "Expect '}' after block.");
    return statements;
  }

  private Expr assignment() {
    Expr expr = or();

    if (match(EQUAL)) {
      Token equals = previous();
      Expr value = assignment();

      if (expr instanceof Expr.Variable) {
        Token name = ((Expr.Variable)expr).name;
        return new Expr.Assign(name, value);
      }

      error(equals, "Invalid assignment target.");
    }

    return expr;
  }

  private Expr or() {
    Expr expr = and();

    while (match(OR)) {
      Token operator = previous();
      Expr right = and();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Expr and() {
    Expr expr = equality();

    while (match(AND)) {
      Token operator = previous();
      Expr right = equality();
      expr = new Expr.Logical(expr, operator, right);
    }

    return expr;
  }

  private Stmt varDeclaration() {
    Token name = consume(IDENTIFIER, "Expect variable name.");

    Expr initializer = null;
    if (match(EQUAL)) {
      initializer = expression();
    }

    consume(SEMICOLON, "Expect ';' after variable declaration.");
    return new Stmt.Var(name, initializer);
  }

  private Stmt whileStatement() {
    consume(LEFT_PAREN, "Expect '(' after 'while'.");
    Expr condition = expression();
    consume(RIGHT_PAREN, "Expect ')' after condition.");
    Stmt body = statement();

    return new Stmt.While(condition, body);
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

    if (match(IDENTIFIER)) {
      return new Expr.Variable(previous());
    }

    // If we read a left paren, we must find a ) token
    if (match(LEFT_PAREN)) {
      Expr expr = expression();
      consume(RIGHT_PAREN, "Expect ')' after expression.");
      return new Expr.Grouping(expr);
    }

    throw error(peek(), "Expect expression.");
  }

  /**
   * Enter panic mode?
   * */
  private Token consume(TokenType type, String message) {
    if (check(type)) return advance();

    throw error(peek(), message);
  }

  /**
   * handle error
   *
   * Returns the error instead of throwing it because we want
   * the calling method inside parser to decide whether to
   * unwind or not.
   * */
  private ParseError error(Token token, String message) {
    Lox.error(token, message);
    return new ParseError();
  }

  /**
   * Discards tokens until we are at the beginning of the next
   * statement. After a semicolon, we're probably finished with
   * a statement.
   *
   * Most statements start with a keyword. When the next token
   * is any of those, we're probably about to start a statement.
   * */
  private void synchronize() {
    advance();

    while (!isAtEnd()) {
      if (previous().type == SEMICOLON) return;

      switch (peek().type) {
        case CLASS:
        case FUN:
        case VAR:
        case FOR:
        case IF:
        case WHILE:
        case PRINT:
        case RETURN:
          return;
      }

      advance();
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
