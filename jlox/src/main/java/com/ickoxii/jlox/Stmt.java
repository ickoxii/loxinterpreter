 /**
  * Automatically generated code
  * */

package com.ickoxii.jlox;

import java.util.List;

abstract class Stmt {
  interface Visitor<R> {
    /**
     * Visit a Block node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitBlockStmt(Block stmt);
    /**
     * Visit a Class node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitClassStmt(Class stmt);
    /**
     * Visit a Expression node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitExpressionStmt(Expression stmt);
    /**
     * Visit a Function node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitFunctionStmt(Function stmt);
    /**
     * Visit a If node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitIfStmt(If stmt);
    /**
     * Visit a Print node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitPrintStmt(Print stmt);
    /**
     * Visit a Return node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitReturnStmt(Return stmt);
    /**
     * Visit a Var node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitVarStmt(Var stmt);
    /**
     * Visit a While node.
     *
     * @param stmt The Stmt node.
     * @return The result of processing the node.
     * */
    R visitWhileStmt(While stmt);
  }
  static class Block extends Stmt {
    /**
     * Constructs a new Block instance.
     *
     * @param statements The statements of the Block node.
     * */
    Block(List<Stmt> statements) {
      this.statements = statements;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBlockStmt(this);
    }

    final List<Stmt> statements;
  }
  static class Class extends Stmt {
    /**
     * Constructs a new Class instance.
     *
     * @param name The name of the Class node.
     * @param superclass The superclass of the Class node.
     * @param methods The methods of the Class node.
     * */
    Class(Token name, Expr.Variable superclass, List<Stmt.Function> methods) {
      this.name = name;
      this.superclass = superclass;
      this.methods = methods;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitClassStmt(this);
    }

    final Token name;
    final Expr.Variable superclass;
    final List<Stmt.Function> methods;
  }
  static class Expression extends Stmt {
    /**
     * Constructs a new Expression instance.
     *
     * @param expression The expression of the Expression node.
     * */
    Expression(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitExpressionStmt(this);
    }

    final Expr expression;
  }
  static class Function extends Stmt {
    /**
     * Constructs a new Function instance.
     *
     * @param name The name of the Function node.
     * @param params The params of the Function node.
     * @param body The body of the Function node.
     * */
    Function(Token name, List<Token> params, List<Stmt> body) {
      this.name = name;
      this.params = params;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitFunctionStmt(this);
    }

    final Token name;
    final List<Token> params;
    final List<Stmt> body;
  }
  static class If extends Stmt {
    /**
     * Constructs a new If instance.
     *
     * @param condition The condition of the If node.
     * @param thenBranch The thenBranch of the If node.
     * @param elseBranch The elseBranch of the If node.
     * */
    If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
      this.condition = condition;
      this.thenBranch = thenBranch;
      this.elseBranch = elseBranch;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitIfStmt(this);
    }

    final Expr condition;
    final Stmt thenBranch;
    final Stmt elseBranch;
  }
  static class Print extends Stmt {
    /**
     * Constructs a new Print instance.
     *
     * @param expression The expression of the Print node.
     * */
    Print(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitPrintStmt(this);
    }

    final Expr expression;
  }
  static class Return extends Stmt {
    /**
     * Constructs a new Return instance.
     *
     * @param keyword The keyword of the Return node.
     * @param value The value of the Return node.
     * */
    Return(Token keyword, Expr value) {
      this.keyword = keyword;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitReturnStmt(this);
    }

    final Token keyword;
    final Expr value;
  }
  static class Var extends Stmt {
    /**
     * Constructs a new Var instance.
     *
     * @param name The name of the Var node.
     * @param initializer The initializer of the Var node.
     * */
    Var(Token name, Expr initializer) {
      this.name = name;
      this.initializer = initializer;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVarStmt(this);
    }

    final Token name;
    final Expr initializer;
  }
  static class While extends Stmt {
    /**
     * Constructs a new While instance.
     *
     * @param condition The condition of the While node.
     * @param body The body of the While node.
     * */
    While(Expr condition, Stmt body) {
      this.condition = condition;
      this.body = body;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitWhileStmt(this);
    }

    final Expr condition;
    final Stmt body;
  }

  /**
   * Accept a visitor to process this Stmt node.
   *
   * @param <R> The return type of the visitor.
   * @param visitor The visitor instance.
   * @return The result of the visitor's processing.
   * */
  abstract <R> R accept(Visitor<R> visitor);
}
