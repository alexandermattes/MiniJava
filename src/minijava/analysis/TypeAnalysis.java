package minijava.analysis;

import minijava.analysis.types.MjType;
import minijava.node.AProgram;
import minijava.node.PExp;

/**
 * calculates types of expressions and checks if a program is type correct
 */
public interface TypeAnalysis {

	/**
	 * @param e some expression
	 * @return the type of the given expression
	 */
	public abstract MjType getType(PExp e);

	/**
	 * Checks a whole program for type errors
	 * @param prog the program to check
	 * @throws TypeCheckingExpcetion when there is a type error
	 */
	public abstract void checkProgram(AProgram prog);

}