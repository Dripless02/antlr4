import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;


/**
 * The CCAL class contains the main method that parses a CCAL program.
 */
public class CCAL {
    /**
     * The main method of the CCAL class.
     * @param args The command line arguments.
     * @throws Exception If there is an error while parsing the program.
     */
    public static void main(String[] args) throws Exception {
        try {
            String inputFile = null;
            if (args.length > 0)
                inputFile = args[0];

            InputStream is = System.in;
            if (inputFile != null)
                is = new FileInputStream(inputFile);

            CCALLexer lexer = new CCALLexer(CharStreams.fromStream(is));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            CCALParser parser = new CCALParser(tokens);
            parser.removeErrorListeners();
            ParseTree tree = parser.program();

            if (parser.getNumberOfSyntaxErrors() == 0) {
                System.out.println(inputFile + " parsed successfully");
                ExtendedVisitor Visitor = new ExtendedVisitor();
                IRGenerator irVisitor = new IRGenerator();
                Visitor.visit(tree);
                irVisitor.visit(tree);
            } else {
                System.out.println(inputFile + " has not parsed");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

