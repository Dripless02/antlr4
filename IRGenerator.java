
import java.util.List;
import java.io.*;
public class IRGenerator extends CCALBaseVisitor<String> {

    private int loopCount;
    private PrintStream o;



    public IRGenerator() throws FileNotFoundException {
        loopCount = 1;
        o = new PrintStream(new File("./output.txt"));
    }

    private void writeToOutput(String output) {
        o.println(output);
    }

    @Override
    public String visitMain_block(CCALParser.Main_blockContext ctx) {
        writeToOutput("main: ");
        return visitChildren(ctx);
    }

    @Override
    public String visitVar_decl(CCALParser.Var_declContext ctx) {
        return visitChildren(ctx);
    }

    @Override
    public String visitConst_decl(CCALParser.Const_declContext ctx) {
        writeToOutput("\t" + ctx.IDENTIFIER().getText() + " =  " + ctx.expression().getText());
        return visitChildren(ctx);
    }

    @Override
    public String visitFunction(CCALParser.FunctionContext ctx) {
        writeToOutput(ctx.IDENTIFIER().getText() + ":");

        List<CCALParser.Nonempty_parameter_listContext> params = ctx.parameter_list().nonempty_parameter_list();
        for (int i = 0; i < params.size(); i++) {
            writeToOutput("\t" + params.get(i).IDENTIFIER().getText() + " = getparam " + (i + 1));
        }
        return visitChildren(ctx);
    }

    // visit while statement
    @Override
    public String visitWhile_statement(CCALParser.While_statementContext ctx) {
        String result = visit(ctx.condition());
        String label1 = "L" + loopCount++;
        String label2 = "L" + loopCount++;
        String label3 = "L" + loopCount++;

        // while statement
        writeToOutput(label1 + ":");
        writeToOutput(String.format("\tif %s goto %s\n\tgoto %s", result, label2, label3));
        writeToOutput(String.format("%s:", label2));

        visit(ctx.statement_block());
        writeToOutput(String.format("\tgoto %s", label1));
        writeToOutput(String.format("%s:", label3));
        // while statement block


        return null;
    }

    @Override
    public String visitAssignment_statement(CCALParser.Assignment_statementContext ctx) {
        writeToOutput("\t" + ctx.IDENTIFIER().getText() + " = " + visit(ctx.expression()));
        return visitChildren(ctx);
    }

    @Override
    public String visitIf_statement(CCALParser.If_statementContext ctx) {
        // writeToOutput("if");

        String result = visit(ctx.condition());

        String label1 = "L" + loopCount++;
        String label2 = "L" + loopCount++;
        String label3 = "L" + loopCount++;

        if (!result.contains("&&") && !result.contains("||")) {
            // if statement block
            writeToOutput(String.format("\tif %s goto %s\n\tgoto %s", result, label1, label2));
            writeToOutput(String.format("%s:", label1));

            visit(ctx.statement_block(0));
            writeToOutput(String.format("\tgoto %s\n", label3));
            // else statement block
            if (ctx.statement_block(1) != null) {
                writeToOutput(String.format("%s:", label2));
                visit(ctx.statement_block(1));

            }
            writeToOutput(String.format("%s:", label3));
        } else if (result.contains("&&")) {
            String[] conditions = result.split(" && ");

            String left = conditions[0];
            String right = conditions[1];

            String label4 = "L" + loopCount++;

            writeToOutput(String.format("\tif %s goto %s\n\tgoto %s", left, label1, label3));

            writeToOutput(String.format("%s:", label1));
            writeToOutput(String.format("\tif %s goto %s\n\tgoto %s", right, label2, label3));

            writeToOutput(String.format("%s:", label2));
            visit(ctx.statement_block(0));
            writeToOutput(String.format("\tgoto %s\n", label4));

            if (ctx.statement_block(1) != null) {
                writeToOutput(String.format("%s:", label3));
                visit(ctx.statement_block(1));
            }

            writeToOutput(String.format("%s:", label4));
        } else if (result.contains("||")) {
            String[] conditions = result.split(" \\|\\| ");

            String left = conditions[0];
            String right = conditions[1];

            String label4 = "L" + loopCount++;
            //if left is true go label 2 else go label 1, if right is true go label 2 else go label 3, then at the end go to label 4

            writeToOutput(String.format("\tif %s goto %s\n\tgoto %s", left, label2, label1));

            writeToOutput(String.format("%s:", label1));
            writeToOutput(String.format("\tif %s goto %s\n\tgoto %s", right, label2, label3));

            writeToOutput(String.format("%s:", label2));
            visit(ctx.statement_block(0));
            writeToOutput(String.format("\tgoto %s\n", label4));

            if (ctx.statement_block(1) != null) {
                writeToOutput(String.format("%s:", label3));
                visit(ctx.statement_block(1));
            }

            writeToOutput(String.format("%s:", label4));
        }
        return null;
    }

    @Override
    public String visitCall_statement(CCALParser.Call_statementContext ctx) {
        List<CCALParser.Nonempty_arg_listContext> args = ctx.arg_list().nonempty_arg_list();

        return "call " + ctx.IDENTIFIER().getText() + ", " + args.size();
    }

    @Override
    public String visitParenscondition(CCALParser.ParensconditionContext ctx) {
        return visit(ctx.condition());
    }

    @Override
    public String visitAndcondition(CCALParser.AndconditionContext ctx) {
        String left = visit(ctx.condition(0));
        String right = visit(ctx.condition(1));
        String result = "";
        if (left != null && right != null) {
            result = String.format("%s && %s", left, right);
        }
        return result;
    }

    @Override
    public String visitOrcondition(CCALParser.OrconditionContext ctx) {
        String left = visit(ctx.condition(0));
        String right = visit(ctx.condition(1));
        String result = "";

        if (left != null && right != null) {
            result = String.format("%s || %s", left, right);
        }
        return result;
    }

    // visit compcondition
    @Override
    public String visitCompcondition(CCALParser.CompconditionContext ctx) {
        String left = visit(ctx.expression(0));
        String right = visit(ctx.expression(1));
        String result = "";

        if (left != null && right != null) {
            if (ctx.comp_op().getText().equalsIgnoreCase("&&"))
                writeToOutput("what the fuck is going on");
            if (ctx.comp_op().getText().equals(">")) {
                result = String.format("%s > %s", left, right);
            } else if (ctx.comp_op().getText().equals("<")) {
                result = String.format("%s < %s", left, right);
            } else if (ctx.comp_op().getText().equals("==")) {
                result = String.format("%s == %s", left, right);
            } else if (ctx.comp_op().getText().equals("!=")) {
                result = String.format("%s != %s", left, right);
            } else if (ctx.comp_op().getText().equals(">=")) {
                result = String.format("%s >= %s", left, right);
            } else if (ctx.comp_op().getText().equals("<=")) {
                result = String.format("%s <= %s", left, right);
            }
        }
        return result;
    }

    // visit intfrag
    @Override
    public String visitIntfrag(CCALParser.IntfragContext ctx) {
        return ctx.INTEGER().getText();
    }

    // visit idfrag
    @Override
    public String visitIdfrag(CCALParser.IdfragContext ctx) {
        return ctx.IDENTIFIER().getText();
    }

    // visit truefrag
    @Override
    public String visitTruefrag(CCALParser.TruefragContext ctx) {
        return ctx.TRUE().getText();
    }

    @Override
    public String visitFalsefrag(CCALParser.FalsefragContext ctx) {
        return ctx.FALSE().getText();
    }

    @Override
    public String visitNegidfrag(CCALParser.NegidfragContext ctx) {
        return "-" + ctx.IDENTIFIER().getText();
    }

    @Override
    public String visitParens_expr(CCALParser.Parens_exprContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    // visit binary_arith_opexpr
    public String visitBinary_arith_opexpr(CCALParser.Binary_arith_opexprContext ctx) {
        String left = this.visit(ctx.fragM(0));
        String right = this.visit(ctx.fragM(1));
        String result = null;

        if(ctx.binary_arith_op(0).getText().equals("+")){
            result = left + " + " + right;
        }
        else if(ctx.binary_arith_op(0).getText().equals("-")){
            result = left + " - " + right;
        }
        return result;
    }

    
}
