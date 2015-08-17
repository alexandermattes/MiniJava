package minijava.analysis;

import minijava.node.AProgram;

/**
 * provides access to the different parts of the analysis
 */
public interface MjAnalysis {

	/**
	 * @return an object to do name analysis
	 */
	NameAnalysis getNameAnalysis();

	/**
	 * @return an object to do type analysis
	 */
	TypeAnalysis getTypeAnalysis();

	/**
	 * checks the complete program for errors
	 */
	void checkProgram();

	/**
	 * @return the program which is analyzed
	 */
	AProgram getProgram();

}