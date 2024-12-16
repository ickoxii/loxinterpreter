package com.ickoxii.loxinterpreter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Visits every node in the syntax tree, implementing visitor abstraction.
 * */
class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {
  private final Interpreter interpreter;
  /**
   * Keeps track of the stack of scopes currently in scope.
   *
   * The scope stack is only used for local block scopes.
   *
   * Variables declared at top level in global scope are not
   * tracked by the resolver since they are more dynamic in Lox.
   *
   * When resolving a variable, if we can't find it in the stack
   * of local scopes, we assume it must be global.
   * */
  private final Stack<Map<String, Boolean>> scopes = new Stack<>();

  /**
   * Tracks if the current code we are visiting is inside a function declaration.
   *
   * This can catch nasty errors such as a return in global scope
   *
   * i.e. if the program is purely:
   * return "already in global";
   * */
  private FunctionType currentFunction = FunctionType.NONE;

  private enum FunctionType {
    NONE,
    FUNCTION
  }

  Resolver(Interpreter interpreter) {
    this.interpreter = interpreter;
  }

  /**
   * Create a new block scope.
   * */
  private void beginScope() {
    scopes.push(new HashMap<String, Boolean>());
  }

  /**
   * Exit a scope by popping it off the stack
   * */
  private void endScope() {
    scopes.pop();
  }

  /**
   * Declares a variable. Initialized it to false to indicate
   * that the variable has been declared but not yet defined.
   * */
  private void declare(Token name) {
    if (scopes.isEmpty()) return;

    Map<String, Boolean> scope = scopes.peek();
    /**
     * Don't allow declaring multiple variables with the same name
     * in local scope.
     *
     * i.e.
     * fun bad() {
     *  var a = "first";
     *  var a = "second";
     * }
     * */
    if (scope.containsKey(name.lexeme)) {
      Lox.error(name,
          "Already a variable with this name in this scope.");
    }

    scope.put(name.lexeme, false);
  }

  /**
   * Defines a variable by setting its value to true.
   * */
  private void define(Token name) {
    if (scopes.isEmpty()) return;
    scopes.peek().put(name.lexeme, true);
  }

  /**
   * Walks a list of statements and resolves each one
   * */
  void resolve(List<Stmt> statements) {
    for (Stmt statement : statements) {
      resolve(statement);
    }
  }

  /**
   * Resolves a statement.
   * */
  private void resolve(Stmt stmt) {
    stmt.accept(this);
  }

  /**
   * Resolves an expression
   * */
  private void resolve(Expr expr) {
    expr.accept(this);
  }

  /**
   * Actually resolves the variable itself.
   *
   * Starts at the innermost scope and works outwards,
   * looking in each map for a matching name. Once we
   * find the variable, we resolve it and pass the number
   * of scopes between the current innermost scope and
   * the scope where the variable was found.
   *
   * i.e. If found in the current scope, we pass 0.
   * If in the immediately enclosing scope, we pass 1.
   *
   * If we walk through all block scopes and never find
   * the variable, we leave it unresolved and assume its global.
   * */
  private void resolveLocal(Expr expr, Token name) {
    for (int i = scopes.size() - 1; i >= 0; --i) {
      if (scopes.get(i).containsKey(name.lexeme)) {
        interpreter.resolve(expr, scopes.size() - 1 - i);
        return;
      }
    }
  }

  /**
   * Creates a new scope for the function body and binds
   * variables for each of the function's parameters.
   * */
  private void resolveFunction(Stmt.Function function, FunctionType type) {
    /**
     * Allowing for arbitrarily deep function nests
     * */
    FunctionType enclosingFunction = currentFunction;
    currentFunction = type;

    beginScope();
    for (Token param : function.params) {
      declare(param);
      define(param);
    }
    resolve(function.body);
    endScope();

    currentFunction = enclosingFunction;
  }

  /**
   * Check to see if variable is being accessed inside its own
   * initializer. This is where values in scope map come into
   * play. If variable exists in current scope but its value
   * is false, then we have declared it bu tnot yet defined it.
   * Report that error.
   *
   * Then resolve variable itself using resolveLocal
   * */
  @Override
  public Void visitVariableExpr(Expr.Variable expr) {
    if (!scopes.isEmpty() &&
        scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
      Lox.error(expr.name,
          "Can't read local variable in its own initializer.");
    }

    resolveLocal(expr, expr.name);
    return null;
  }

  /**
   * First resolve expression for assigned value in case it also contains
   * references to other variables. Then use resolveLocal method to resolve
   * the variable that's being assigned to.
   * */
  @Override
  public Void visitAssignExpr(Expr.Assign expr) {
    resolve(expr.value);
    resolveLocal(expr, expr.name);
    return null;
  }

  @Override
  public Void visitBinaryExpr(Expr.Binary expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitCallExpr(Expr.Call expr) {
    resolve(expr.callee);

    for (Expr argument : expr.arguments) {
      resolve(argument);
    }

    return null;
  }

  @Override
  public Void visitGroupingExpr(Expr.Grouping expr) {
    resolve(expr.expression);
    return null;
  }

  /**
   * Literals don't mention any variables and thus do not need to be resolved
   * */
  @Override
  public Void visitLiteralExpr(Expr.Literal expr) {
    return null;
  }

  /**
   * Static analysis has no control flow or short-circuiting.
   *
   * Thus we resolve both sides of the expression.
   * */
  @Override
  public Void visitLogicalExpr(Expr.Logical expr) {
    resolve(expr.left);
    resolve(expr.right);
    return null;
  }

  @Override
  public Void visitUnaryExpr(Expr.Unary expr) {
    resolve(expr.right);
    return null;
  }

  /**
   * Functions bind names and introduce a scope. The name of the
   * function is bound in the surrounding scope where the function
   * is declared.
   *
   * When we step into the function's body, we also bind its
   * parameters to the innter function scope.
   * */
  @Override
  public Void visitFunctionStmt(Stmt.Function stmt) {
    declare(stmt.name);
    define(stmt.name);

    resolveFunction(stmt, FunctionType.FUNCTION);
    return null;
  }

  /**
   * Expressions contains a single expression to traverse.
   * */
  @Override
  public Void visitExpressionStmt(Stmt.Expression stmt) {
    resolve(stmt.expression);
    return null;
  }

  /**
   * Resolve condition, thenBranch, and elseBranch (if it exists).
   * */
  @Override
  public Void visitIfStmt(Stmt.If stmt) {
    resolve(stmt.condition);
    resolve(stmt.thenBranch);
    if (stmt.elseBranch != null) resolve(stmt.elseBranch);
    return null;
  }

  @Override
  public Void visitPrintStmt(Stmt.Print stmt) {
    resolve(stmt.expression);
    return null;
  }

  @Override
  public Void visitReturnStmt(Stmt.Return stmt) {
    if (currentFunction == FunctionType.NONE) {
      Lox.error(stmt.keyword, "Can't return from top-level code.");
    }

    if (stmt.value != null) {
      resolve(stmt.value);
    }

    return null;
  }

  /**
   * We only resolve the body of a while statement once.
   * */
  @Override
  public Void visitWhileStmt(Stmt.While stmt) {
    resolve(stmt.condition);
    resolve(stmt.body);
    return null;
  }

  /**
   * Begins a new scope, traverses into statements inside the block,
   * then discards the scope.
   * */
  @Override
  public Void visitBlockStmt(Stmt.Block stmt) {
    beginScope();
    resolve(stmt.statements);
    endScope();
    return null;
  }

  /**
   * When visiting expressions, we need to know if we are inside
   * the initializer for some variable. We do this by splitting
   * binding into two steps.
   *
   * The first step is declaring the variable.
   *
   * The second step is defining the variable.
   * */
  @Override
  public Void visitVarStmt(Stmt.Var stmt) {
    declare(stmt.name);
    if (stmt.initializer != null) {
      resolve(stmt.initializer);
    }

    define(stmt.name);
    return null;
  }
}
