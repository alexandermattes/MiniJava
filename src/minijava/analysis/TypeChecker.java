package minijava.analysis;

import minijava.node.Start;
import minijava.node.AProgram;

public class TypeChecker {

	/**
	 * Checks if a given program is type correct
	 * 
	 * @param ast
	 *            the AST of the program to analyze
	 * @return An analysis object which provides methods for getting the results
	 *         of name and type analysis for all relevant nodes in the AST
	 * @throws TypeCheckingExpcetion
	 *             when there is a type error in the program
	 */
	public static MjAnalysis typecheckOrFail(Start ast)
			throws TypeCheckingExpcetion {
        AProgram aProgram = (AProgram) ast.getPProgram();
        MjAnalysis mjAnalysis = new MjAnalyser(aProgram);
        mjAnalysis.checkProgram();
        return mjAnalysis;
	}
}
