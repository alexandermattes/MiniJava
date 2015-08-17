package minijava.postchecking;

import minijava.analysis.DepthFirstAdapter;
import minijava.node.AArrayLookupExp;
import minijava.node.AAssignStmt;
import minijava.node.ACallStmt;
import minijava.node.AFieldAccessExp;
import minijava.node.AIdentifierExp;
import minijava.node.AMethodCallExp;
import minijava.node.AObjAllocExp;
import minijava.node.PExp;



/**
 * Checks for illegal statements and throws an exception if necessary.
 */
public class IllegalStatementChecker extends DepthFirstAdapter {
    
    /**
     * Check if an AAssignStmt node has a valid expression on the left-hand-side.
     * It has to be either an AArrayLookupExp, an AFieldAccessExp or an AIdentifierExp.
     * @param node the AAssignStmt node
     * @throws CustomParserException if the left-hand-side is not valid
     */
    @Override
    public void inAAssignStmt(AAssignStmt node) {
        PExp exp = node.getLhs();
        if(!(exp instanceof AArrayLookupExp
          || exp instanceof AFieldAccessExp
          || exp instanceof AIdentifierExp)) {
            
            throw new CustomParserException(node, "Illegal statement on left-hand-side of an assignment");
        }       
    }
    
    /**
     * Check if an ACallStmt node is really a method call.
     * @param node the ACallStmt node
     * @throws CustomParserException if the statement is not a method call
     */
    @Override
    public void inACallStmt(ACallStmt node) {
        PExp exp = node.getExp();
        if(!(exp instanceof AObjAllocExp
          || exp instanceof AMethodCallExp)) {
            throw new CustomParserException(node, "Not a valid statement. Has to be a method call.");
        }
    }
}
