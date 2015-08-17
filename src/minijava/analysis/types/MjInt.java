package minijava.analysis.types;

import minijava.analysis.MjAnalysis;

public class MjInt extends MjType {
	@Override
	public boolean isSubtypeOf(MjType other, MjAnalysis analysis) {
        return other instanceof MjInt;
    }
}