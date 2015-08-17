package minijava.analysis.types;

import minijava.analysis.MjAnalysis;

public class MjBool extends MjType {
	@Override
	public boolean isSubtypeOf(MjType other, MjAnalysis analysis) {
        return other instanceof MjBool;
    }
}