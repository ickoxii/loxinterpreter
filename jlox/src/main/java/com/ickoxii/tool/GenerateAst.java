package com.ickoxii.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

/**
 * A utility class to generate Abstract Syntax Tree (AST) classes for a
 * programming language.
 * This class uses the Visitor design pattern to create base and derived classes
 * for expressions and statements.
 * */
public class GenerateAst {
  /**
   * Entry point for the AST generator.
   *
   * @param args Command-line arguments. Expects a single argument specifying the
   *             output directory.
   * @throws IOException If an error occurs while writing to the output files.
   * */
  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: generate_ast <output directory>");
      System.exit(64);
    }

    String outputDir = args[0];

    // AST for expressions
    defineAst(outputDir, "Expr", Arrays.asList(
        "Assign   : Token name, Expr value",
        "Binary   : Expr left, Token operator, Expr right",
        "Call     : Expr callee, Token paren, List<Expr> arguments",
        "Get      : Expr object, Token name",
        "Grouping : Expr expression",
        "Literal  : Object value",
        "Logical  : Expr left, Token operator, Expr right",
        "Set      : Expr object, Token name, Expr value",
        "Super    : Token keyword, Token method",
        "This     : Token keyword",
        "Unary    : Token operator, Expr right",
        "Variable : Token name"));

    // AST for statements and state
    defineAst(outputDir, "Stmt", Arrays.asList(
        "Block      : List<Stmt> statements",
        "Class      : Token name, Expr.Variable superclass," +
                    " List<Stmt.Function> methods",
        "Expression : Expr expression",
        "Function   : Token name, List<Token> params, List<Stmt> body",
        "If         : Expr condition, Stmt thenBranch, Stmt elseBranch",
        "Print      : Expr expression",
        "Return     : Token keyword, Expr value",
        "Var        : Token name, Expr initializer",
        "While      : Expr condition, Stmt body"));
  }

  /**
   * Defines an abstract base class and its subclasses for the given types.
   *
   * @param outputDir The directory where the generated Java files will be
   *                  written.
   * @param baseName  The name of the abstract base class.
   * @param types     A list of type definitions in the format "ClassName :
   *                  fieldType fieldName, ...".
   * @throws IOException If an error occurs while writing the file.
   * */
  private static void defineAst(
      String outputDir, String baseName, List<String> types)
      throws IOException {
    String path = outputDir + "/" + baseName + ".java";
    PrintWriter writer = new PrintWriter(path, "UTF-8");

    writer.println(" /**");
    writer.println("  * Automatically generated code");
    writer.println("  * */");
    writer.println();
    writer.println("package com.ickoxii.jlox;");
    writer.println();
    writer.println("import java.util.List;");
    writer.println();
    writer.println("abstract class " + baseName + " {");

    // Define the visitor interface
    defineVisitor(writer, baseName, types);

    // Define the AST sublasses
    for (String type : types) {
      String className = type.split(":")[0].trim();
      String fields = type.split(":")[1].trim();
      defineType(writer, baseName, className, fields);
    }

    // Define the base accept() method for visitor.
    writer.println();
    writer.println("  /**");
    writer.println("   * Accept a visitor to process this " + baseName + " node.");
    writer.println("   *");
    writer.println("   * @param <R> The return type of the visitor.");
    writer.println("   * @param visitor The visitor instance.");
    writer.println("   * @return The result of the visitor's processing.");
    writer.println("   * */");
    writer.println("  abstract <R> R accept(Visitor<R> visitor);");

    writer.println("}");
    writer.close();
  }

  /**
   * Defines a Visitor interface for the AST classes.
   *
   * @param writer   The writer to output the generated code.
   * @param baseName The name of the abstract base class.
   * @param types    A list of type definitions.
   * */
  private static void defineVisitor(
      PrintWriter writer, String baseName, List<String> types) {
    writer.println("  interface Visitor<R> {");

    for (String type : types) {
      String typeName = type.split(":")[0].trim();
      writer.println("    /**");
      writer.println("     * Visit a " + typeName + " node.");
      writer.println("     *");
      writer.println("     * @param " + baseName.toLowerCase() + " The " + baseName + " node.");
      writer.println("     * @return The result of processing the node.");
      writer.println("     * */");
      writer.println("    R visit" + typeName + baseName + "(" +
          typeName + " " + baseName.toLowerCase() + ");");
    }

    writer.println("  }");
  }

  /**
   * Defines a concrete subclass of the abstract base class.
   *
   * @param writer    The writer to output the generated code.
   * @param baseName  The name of the abstract base class.
   * @param className The name of the concrete subclass.
   * @param fieldList A comma-separated list of fields in the format "fieldType fieldName".
   * */
  private static void defineType(
      PrintWriter writer, String baseName,
      String className, String fieldList) {
    writer.println("  static class " + className + " extends " + baseName + " {");

    // Constructor
    String[] fields = fieldList.split(", ");
    writer.println("    /**");
    writer.println("     * Constructs a new " + className + " instance.");
    writer.println("     *");
    for (String field : fields) {
      writer.println("     * @param " + field.split(" ")[1] + " The " + field.split(" ")[1] + " of the " + className + " node.");
    }
    writer.println("     * */");
    writer.println("    " + className + "(" + fieldList + ") {");

    // Store parameters in fields
    for (String field : fields) {
      String name = field.split(" ")[1];
      writer.println("      this." + name + " = " + name + ";");
    }

    writer.println("    }");

    // Visitor patter.
    writer.println();
    writer.println("    @Override");
    writer.println("    <R> R accept(Visitor<R> visitor) {");
    writer.println("      return visitor.visit" +
        className + baseName + "(this);");
    writer.println("    }");

    // Fields
    writer.println();
    for (String field : fields) {
      writer.println("    final " + field + ";");
    }

    writer.println("  }");
  }
}
