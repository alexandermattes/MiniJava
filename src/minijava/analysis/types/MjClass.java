package minijava.analysis.types;

import minijava.analysis.MjAnalysis;
import minijava.node.AClassDecl;

public class MjClass extends MjType {
    private AClassDecl classDecl;
    private String className;
    

    public MjClass(AClassDecl classDecl) {
        this.classDecl = classDecl;
        this.className = classDecl.getId().getText();
    }


    @Override
	public boolean isSubtypeOf(MjType other, MjAnalysis analysis) {

        if(!(other instanceof MjClass))  {
            return false;
        }

        if(this.className == ((MjClass) other).className)  {
            return true;
        }

        AClassDecl superClassDecl = analysis.getNameAnalysis().getSuperClass(classDecl);
        return (superClassDecl != null) && new MjClass(superClassDecl).isSubtypeOf(other, analysis);
    }


    public AClassDecl getClassDecl() {
        return classDecl;
    }


    public String getClassName() {
        return className;
    }
}
