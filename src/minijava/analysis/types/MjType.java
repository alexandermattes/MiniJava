package minijava.analysis.types;

import minijava.analysis.MjAnalysis;

public abstract class MjType {
	public abstract boolean isSubtypeOf(MjType other, MjAnalysis analysis);
	

}
