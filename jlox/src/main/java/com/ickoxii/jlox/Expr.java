 /**
  * Automatically generated code
  * */

package com.ickoxii.jlox;

import java.util.List;

abstract class Expr {
  interface Visitor<R> {
    /**
     * Visit a Assign node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitAssignExpr(Assign expr);
    /**
     * Visit a Binary node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitBinaryExpr(Binary expr);
    /**
     * Visit a Call node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitCallExpr(Call expr);
    /**
     * Visit a Get node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitGetExpr(Get expr);
    /**
     * Visit a Grouping node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitGroupingExpr(Grouping expr);
    /**
     * Visit a Literal node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitLiteralExpr(Literal expr);
    /**
     * Visit a Logical node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitLogicalExpr(Logical expr);
    /**
     * Visit a Set node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitSetExpr(Set expr);
    /**
     * Visit a Super node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitSuperExpr(Super expr);
    /**
     * Visit a This node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitThisExpr(This expr);
    /**
     * Visit a Unary node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitUnaryExpr(Unary expr);
    /**
     * Visit a Variable node.
     *
     * @param expr The Expr node.
     * @return The result of processing the node.
     * */
    R visitVariableExpr(Variable expr);
  }
  static class Assign extends Expr {
    /**
     * Constructs a new Assign instance.
     *
     * @param name The name of the Assign node.
     * @param value The value of the Assign node.
     * */
    Assign(Token name, Expr value) {
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitAssignExpr(this);
    }

    final Token name;
    final Expr value;
  }
  static class Binary extends Expr {
    /**
     * Constructs a new Binary instance.
     *
     * @param left The left of the Binary node.
     * @param operator The operator of the Binary node.
     * @param right The right of the Binary node.
     * */
    Binary(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitBinaryExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }
  static class Call extends Expr {
    /**
     * Constructs a new Call instance.
     *
     * @param callee The callee of the Call node.
     * @param paren The paren of the Call node.
     * @param arguments The arguments of the Call node.
     * */
    Call(Expr callee, Token paren, List<Expr> arguments) {
      this.callee = callee;
      this.paren = paren;
      this.arguments = arguments;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitCallExpr(this);
    }

    final Expr callee;
    final Token paren;
    final List<Expr> arguments;
  }
  static class Get extends Expr {
    /**
     * Constructs a new Get instance.
     *
     * @param object The object of the Get node.
     * @param name The name of the Get node.
     * */
    Get(Expr object, Token name) {
      this.object = object;
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGetExpr(this);
    }

    final Expr object;
    final Token name;
  }
  static class Grouping extends Expr {
    /**
     * Constructs a new Grouping instance.
     *
     * @param expression The expression of the Grouping node.
     * */
    Grouping(Expr expression) {
      this.expression = expression;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitGroupingExpr(this);
    }

    final Expr expression;
  }
  static class Literal extends Expr {
    /**
     * Constructs a new Literal instance.
     *
     * @param value The value of the Literal node.
     * */
    Literal(Object value) {
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLiteralExpr(this);
    }

    final Object value;
  }
  static class Logical extends Expr {
    /**
     * Constructs a new Logical instance.
     *
     * @param left The left of the Logical node.
     * @param operator The operator of the Logical node.
     * @param right The right of the Logical node.
     * */
    Logical(Expr left, Token operator, Expr right) {
      this.left = left;
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitLogicalExpr(this);
    }

    final Expr left;
    final Token operator;
    final Expr right;
  }
  static class Set extends Expr {
    /**
     * Constructs a new Set instance.
     *
     * @param object The object of the Set node.
     * @param name The name of the Set node.
     * @param value The value of the Set node.
     * */
    Set(Expr object, Token name, Expr value) {
      this.object = object;
      this.name = name;
      this.value = value;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSetExpr(this);
    }

    final Expr object;
    final Token name;
    final Expr value;
  }
  static class Super extends Expr {
    /**
     * Constructs a new Super instance.
     *
     * @param keyword The keyword of the Super node.
     * @param method The method of the Super node.
     * */
    Super(Token keyword, Token method) {
      this.keyword = keyword;
      this.method = method;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitSuperExpr(this);
    }

    final Token keyword;
    final Token method;
  }
  static class This extends Expr {
    /**
     * Constructs a new This instance.
     *
     * @param keyword The keyword of the This node.
     * */
    This(Token keyword) {
      this.keyword = keyword;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitThisExpr(this);
    }

    final Token keyword;
  }
  static class Unary extends Expr {
    /**
     * Constructs a new Unary instance.
     *
     * @param operator The operator of the Unary node.
     * @param right The right of the Unary node.
     * */
    Unary(Token operator, Expr right) {
      this.operator = operator;
      this.right = right;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitUnaryExpr(this);
    }

    final Token operator;
    final Expr right;
  }
  static class Variable extends Expr {
    /**
     * Constructs a new Variable instance.
     *
     * @param name The name of the Variable node.
     * */
    Variable(Token name) {
      this.name = name;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
      return visitor.visitVariableExpr(this);
    }

    final Token name;
  }

  /**
   * Accept a visitor to process this Expr node.
   *
   * @param <R> The return type of the visitor.
   * @param visitor The visitor instance.
   * @return The result of the visitor's processing.
   * */
  abstract <R> R accept(Visitor<R> visitor);
}
