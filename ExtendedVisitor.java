import java.util.HashMap;
import java.util.Stack;
class SymbolTableEntry {
    String value;
    String type;
    Boolean isVar;

    public SymbolTableEntry(String value, String type, Boolean isVar) {
        this.value = value;
        this.type = type;
        this.isVar = isVar;
    }

    @Override
    public String toString() {
        return "SymbolTableEntry [isVar=" + isVar + ", type=" + type + ", value=" + value + "]";
    }
}

/**
 * This class represents the Abstract Syntax Tree (AST) for the CCAL language.
 * It extends the CCALBaseVisitor class and overrides its visit methods to perform
 * specific actions when visiting different nodes of the AST.
 */
public class ExtendedVisitor extends CCALBaseVisitor<SymbolTableEntry> {
    Stack<HashMap<String, SymbolTableEntry>> symbolTable = new Stack<>();

    HashMap<String, CCALParser.FunctionContext> functions = new HashMap<>();

    Boolean isFunction = false;

    @Override
    public SymbolTableEntry visitProgram(CCALParser.ProgramContext ctx) {
         symbolTable.push(new HashMap<>());
          return visitChildren(ctx);
      }

    @Override
      public SymbolTableEntry visitMain_block(CCALParser.Main_blockContext ctx) {
         symbolTable.push(new HashMap<>(symbolTable.peek()));
        return visitChildren(ctx);
      }

    @Override
      public SymbolTableEntry visitDecl_list(CCALParser.Decl_listContext ctx) {
         return visitChildren(ctx);
      }

    @Override
      public SymbolTableEntry visitDecl(CCALParser.DeclContext ctx) {
         return visitChildren(ctx);

    }

    @Override
      public SymbolTableEntry visitVar_decl(CCALParser.Var_declContext ctx) {
         String name = ctx.IDENTIFIER().getText();
        String type = ctx.type().getText();
        if (symbolTable.peek().containsKey(name) && !isFunction) {
         } else {
            symbolTable.peek().put(name, new SymbolTableEntry(null, type, true));
        }

        return null;
    }

    @Override
      public SymbolTableEntry visitConst_decl(CCALParser.Const_declContext ctx) {
         String name = ctx.IDENTIFIER().getText();
        String type = ctx.type().getText();
        String value = ctx.expression().getText();

        if (symbolTable.peek().containsKey(name) && !isFunction) {
         } else {
            symbolTable.peek().put(name, new SymbolTableEntry(value, type, false));
        }
        return null;
    }

    @Override
        public SymbolTableEntry visitAssignment_statement(CCALParser.Assignment_statementContext ctx) {
         String name = ctx.IDENTIFIER().getText();
        if (symbolTable.peek().containsKey(name)) {
            if (symbolTable.peek().get(name).isVar) {
                  if (symbolTable.peek().get(name).type.equalsIgnoreCase("integer")) {
                    symbolTable.peek().put(name, new SymbolTableEntry(visit(ctx.expression()).value, "integer", true));
                }
                  else if (symbolTable.peek().get(name).type.equalsIgnoreCase("boolean")){
                    if (visit(ctx.expression()).value.equalsIgnoreCase("true") || visit(ctx.expression()).value.equalsIgnoreCase("false")) {
                        symbolTable.peek().put(name, new SymbolTableEntry(visit(ctx.expression()).value, "boolean", true));
                    }
                    else {
                     }
                    symbolTable.peek().put(name, new SymbolTableEntry(visit(ctx.expression()).value, "boolean", true));
                }
                else {
                    symbolTable.peek().get(name).value = ctx.expression().getText();
                }
            } else {
             }
        } else {
         }
        return symbolTable.peek().get(name);
    }


    @Override
    public SymbolTableEntry visitIf_statement(CCALParser.If_statementContext ctx) {
        SymbolTableEntry data = visit(ctx.condition());
        if (Boolean.parseBoolean(data.value)) {
            return visit(ctx.statement_block(0));
        } else {
            return visit(ctx.statement_block(1));
        }
    }
      @Override
    public SymbolTableEntry visitSkip_statement(CCALParser.Skip_statementContext ctx) {
         return null;
    }

    @Override
    public SymbolTableEntry visitIdfrag(CCALParser.IdfragContext ctx) {
        String name = ctx.IDENTIFIER().getText();
        try {
            if (symbolTable.peek().containsKey(name)) {
                return symbolTable.peek().get(name);
            } else {
                return null;
            }
        } catch (Exception e) {
            // Handle other potential exceptions
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }

      @Override
    public SymbolTableEntry visitWhile_statement(CCALParser.While_statementContext ctx) {
         while (Boolean.parseBoolean(visit(ctx.condition()).value)) {
            visit(ctx.statement_block());
        }
        return null;
    }

      @Override
    public SymbolTableEntry visitNegidfrag(CCALParser.NegidfragContext ctx) {
        String value = ctx.IDENTIFIER().getText();
         if (symbolTable.peek().containsKey(value)) {
            SymbolTableEntry entry = symbolTable.peek().get(value);
            if (entry.type.equalsIgnoreCase("integer")) {
                return new SymbolTableEntry(Integer.toString(-Integer.parseInt(entry.value)), "integer", false);
            } else if (entry.type.equalsIgnoreCase("boolean")) {
                return new SymbolTableEntry("false", "boolean", false);
            } else {
                return new SymbolTableEntry("true", "boolean", false);
            }
        }
        return null;
    }
      @Override
    public SymbolTableEntry visitCompcondition(CCALParser.CompconditionContext ctx) {
        SymbolTableEntry left = visit(ctx.expression(0));
        SymbolTableEntry right = visit(ctx.expression(1));
        SymbolTableEntry result = null;
        if (ctx.comp_op().getText().equals("<")) {
            if (left.type.equalsIgnoreCase("integer") && right.type.equalsIgnoreCase("integer")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Integer.parseInt(left.value) < Integer.parseInt(right.value)), "boolean", false);
               } else if (left.type.equalsIgnoreCase("boolean") && right.type.equalsIgnoreCase("boolean")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Boolean.parseBoolean(left.value) && !Boolean.parseBoolean(right.value)), "boolean", false);
               } else {
             }

           } else if (ctx.comp_op().getText().equals(">")) {
            if (left.type.equalsIgnoreCase("integer") && right.type.equalsIgnoreCase("integer")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Integer.parseInt(left.value) > Integer.parseInt(right.value)), "boolean", false);
               } else if (left.type.equalsIgnoreCase("boolean") && right.type.equalsIgnoreCase("boolean")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Boolean.parseBoolean(left.value) && !Boolean.parseBoolean(right.value)), "boolean", false);
               } else {
             }

           } else if (ctx.comp_op().getText().equals("<=")) {
            if (left.type.equalsIgnoreCase("integer") && right.type.equalsIgnoreCase("integer")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Integer.parseInt(left.value) <= Integer.parseInt(right.value)), "boolean", false);
               } else if (left.type.equalsIgnoreCase("boolean") && right.type.equalsIgnoreCase("boolean")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Boolean.parseBoolean(left.value) && !Boolean.parseBoolean(right.value)), "boolean", false);
               } else {
             }
           } else if (ctx.comp_op().getText().equals(">=")) {
            if (left.type.equalsIgnoreCase("integer") && right.type.equalsIgnoreCase("integer")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Integer.parseInt(left.value) >= Integer.parseInt(right.value)), "boolean", false);
               } else if (left.type.equalsIgnoreCase("boolean") && right.type.equalsIgnoreCase("boolean")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Boolean.parseBoolean(left.value) && !Boolean.parseBoolean(right.value)), "boolean", false);
               } else {
             }
           } else if (ctx.comp_op().getText().equals("==")) {
            if (left.type.equalsIgnoreCase("integer") && right.type.equalsIgnoreCase("integer")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Integer.parseInt(left.value) == Integer.parseInt(right.value)), "boolean", false);
               } else if (left.type.equalsIgnoreCase("boolean") && right.type.equalsIgnoreCase("boolean")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Boolean.parseBoolean(left.value) && Boolean.parseBoolean(right.value)), "boolean", false);

            } else {
             }
         } else if (ctx.comp_op().getText().equals("!=")) {
            if (left.type.equalsIgnoreCase("integer") && right.type.equalsIgnoreCase("integer")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Integer.parseInt(left.value) != Integer.parseInt(right.value)), "boolean", false);
               } else if (left.type.equalsIgnoreCase("boolean") && right.type.equalsIgnoreCase("boolean")) {
                result = new SymbolTableEntry(
                        Boolean.toString(Boolean.parseBoolean(left.value) && !Boolean.parseBoolean(right.value)), "boolean", false);
               } else {
             }
           }
        return result;
    }


      @Override
    public SymbolTableEntry visitNegcondition(CCALParser.NegconditionContext ctx) {
        SymbolTableEntry value = visit(ctx.condition());

        if (value.value.equals("true")) {
            return new SymbolTableEntry("false", "boolean", false);
        } else {
            return new SymbolTableEntry("true", "boolean", false);
        }
    }

    @Override
    public SymbolTableEntry visitBinary_arith_opexpr(CCALParser.Binary_arith_opexprContext ctx) {
        try {
            SymbolTableEntry left = this.visit(ctx.fragM(0));
            SymbolTableEntry right = this.visit(ctx.fragM(1));
            SymbolTableEntry result = null;

            if (ctx.binary_arith_op(0).getText().equals("+")) {
                result = new SymbolTableEntry(Integer.toString(Integer.parseInt(left.value) + Integer.parseInt(right.value)), "integer", false);
            } else if (ctx.binary_arith_op(0).getText().equals("-")) {
                result = new SymbolTableEntry(Integer.toString(Integer.parseInt(left.value) - Integer.parseInt(right.value)), "integer", false);
            }

            return result;
        } catch (NumberFormatException e) {
            // Handle the case where parsing integers fails
            System.err.println("Error: Unable to parse integers in arithmetic operation.");
            return null;
        }
    }
      @Override
    public SymbolTableEntry visitParens_expr(CCALParser.Parens_exprContext ctx) {
        return visit(ctx.expression());
    }
      @Override
    public SymbolTableEntry visitParenscondition(CCALParser.ParensconditionContext ctx) {
        return visit(ctx.condition());
    }

      @Override
    public SymbolTableEntry visitOrcondition(CCALParser.OrconditionContext ctx) {
        SymbolTableEntry left = this.visit(ctx.condition(0));
        SymbolTableEntry right = this.visit(ctx.condition(1));
        SymbolTableEntry result = null;
        if (ctx.OR().getText().equals("||")) {
            result = new SymbolTableEntry(Boolean.toString(Boolean.parseBoolean(left.value) || Boolean.parseBoolean(right.value)), "boolean", false);
           }
        return result;
    }

      @Override
    public SymbolTableEntry visitAndcondition(CCALParser.AndconditionContext ctx) {
        SymbolTableEntry left = this.visit(ctx.condition(0));
        SymbolTableEntry right = this.visit(ctx.condition(1));
        SymbolTableEntry result = null;
        if (ctx.AND().getText().equals("&&")) {
            result = new SymbolTableEntry(Boolean.toString(Boolean.parseBoolean(left.value) && Boolean.parseBoolean(right.value)), "boolean", false);
           }
        return result;
    }

    @Override
    public SymbolTableEntry visitIntfrag(CCALParser.IntfragContext ctx) {
        try {
            return new SymbolTableEntry(ctx.INTEGER().getText(), "integer", false);
        } catch (NumberFormatException e) {
            // Handle the case where parsing integers fails
            System.err.println("Error: Unable to parse integer value.");
            return null;
        }
    }

      @Override
    public SymbolTableEntry visitTruefrag(CCALParser.TruefragContext ctx) {
        return new SymbolTableEntry("true", "boolean", false);
    }

      @Override
    public SymbolTableEntry visitFalsefrag(CCALParser.FalsefragContext ctx) {
        return new SymbolTableEntry("false", "boolean", false);
    }


    @Override
    public SymbolTableEntry visitParensfrag(CCALParser.ParensfragContext ctx) {
        return visit(ctx.expression());
    }


      @Override
    public SymbolTableEntry visitStatement_block(CCALParser.Statement_blockContext ctx) {
          visitChildren(ctx);
         return null;
    }

      @Override
    public SymbolTableEntry visitFunction(CCALParser.FunctionContext ctx) {
         String name = ctx.IDENTIFIER().getText();
        if (functions.containsKey(name)) {
         } else {
            functions.put(name, ctx);
        }
        return null;
    }

      @Override
    public SymbolTableEntry visitCall_statement(CCALParser.Call_statementContext ctx) {
        symbolTable.push(new HashMap<>(symbolTable.peek()));
        isFunction = true;
        SymbolTableEntry result = null;
         String name = ctx.IDENTIFIER().getText();
        if (functions.containsKey(name)) {
              CCALParser.FunctionContext function = functions.get(name);
             if (function.parameter_list().nonempty_parameter_list().size() != 0) {
                for (int i = 0; i < function.parameter_list().nonempty_parameter_list().size(); i++) {
                    String parameterName = function.parameter_list().nonempty_parameter_list(i).IDENTIFIER().getText();
                    String parameterType = function.parameter_list().nonempty_parameter_list(i).type().getText();

                    SymbolTableEntry argument = symbolTable.peek().get(ctx.arg_list().nonempty_arg_list(i).getText());
                      if (parameterType.equalsIgnoreCase(argument.type)) {
                        symbolTable.peek().put(parameterName, new SymbolTableEntry(argument.value, parameterType, true));

                    } else {
                     }
                }
            } else {
             }
            if (function.decl_list() != null) {
                visit(function.decl_list());
            } else {
             }

            if (function.statement_block() != null) {
                visit(function.statement_block());

            } else {
             }

            if (function.expression() != null) {
                  result = visit(function.expression());

            } else {
             }
        }
          else {
           }
        symbolTable.pop();
         isFunction = false;
        return result;
    }

}
