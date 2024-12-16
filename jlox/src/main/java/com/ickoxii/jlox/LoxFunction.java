package com.ickoxii.jlox;

import java.util.List;

class LoxFunction implements LoxCallable {
  private final Stmt.Function declaration;
  private final Environment closure;

  LoxFunction(Stmt.Function declaration, Environment closure) {
    this.closure = closure;
    this.declaration = declaration;
  }

  /**
   * Managing name environments is a core part of language implementation.
   *
   * Parameters are core to fucntions. Functions envapsulates its
   * parameters -- no other code outside the function can see them.
   * i.e. Each function gets its own environment where it stores
   * those variables.
   *
   * This environment must be created dynamically. Each function call
   * gets its own environment. Otherwise recursion would break.
   * */
  @Override
  public Object call(Interpreter interpreter,
                     List<Object> arguments) {
    Environment environment = new Environment(closure);
    for (int i = 0; i < declaration.params.size(); i++) {
      environment.define(declaration.params.get(i).lexeme, arguments.get(i));
    }

    try {
      interpreter.executeBlock(declaration.body, environment);
    } catch (Return returnValue) {
      return returnValue.value;
    }
    return null;
  }

  @Override
  public int arity() {
    return declaration.params.size();
  }

  @Override
  public String toString() {
    return "<fn " + declaration.name.lexeme + ">";
  }
}
