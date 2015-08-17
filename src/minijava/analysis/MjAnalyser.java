package minijava.analysis;

import minijava.node.AProgram;


/**
 * Analyses a MiniJava Program
 */
public class MjAnalyser implements MjAnalysis {

    /**
     * The analysed program
     */
    private AProgram aProgram;

    /**
     * The NameAnalysis
     */
    private NameAnalysis nameAnalysis;

    /**
     * The TypeAnalysis
     */
    private TypeAnalysis typeAnalysis;


    /**
     * @param aProgram the program you want to analyse
     */
    public MjAnalyser(AProgram aProgram) {
        this.aProgram = aProgram;
        this.nameAnalysis =  new NameAnalyser(aProgram);
        this.typeAnalysis = new TypeAnalyser(this);
    }


	/**
	 * @return an object to do name analysis
	 */
	@Override
	public NameAnalysis getNameAnalysis() {
        return nameAnalysis;
	}

	/**
	 * @return an object to do type analysis
	 */
	@Override
	public TypeAnalysis getTypeAnalysis()  {
        return typeAnalysis;
    }

	/**
	 * checks the complete program for errors
	 */
	@Override
	public void checkProgram() {
        typeAnalysis.checkProgram(aProgram);
    }

	/**
	 * @return the program which is analyzed
	 */
	@Override
	public AProgram getProgram() {
        return aProgram;
    }
}