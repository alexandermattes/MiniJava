package minijava.analysis;

import minijava.node.AClassDecl;
import minijava.node.AFieldAccessExp;
import minijava.node.AMethodCallExp;
import minijava.node.AMethodDecl;
import minijava.node.ATypedVar;
import minijava.node.Node;
import minijava.node.Start;
import minijava.node.AProgram;
import minijava.node.TId;

/**
 * resolves names 
 */
public class NameAnalyser implements NameAnalysis {

    /**
     * The analysed program
     */
	private AProgram aProgram;

	/**
     * The nameVisitor
     */
	private NameVisitor nameVisitor;


    /**
     * @param aProgram the program that gets analysed
     */
    public NameAnalyser(AProgram aProgram) {
        this.aProgram = aProgram;
        this.nameVisitor = new NameVisitor();
        this.aProgram.apply(this.nameVisitor);
    }


	/**
	 * 
	 * @param node a field access node 
	 * @return the declaration node of the field
	 * @throws TypeCheckingExpcetion when the field could not be found
	 */
	@Override
	public ATypedVar getField(AFieldAccessExp node) {
        ATypedVar field = nameVisitor.getFieldDecls().get(node);
        if(field == null) {
            throw new TypeCheckingExpcetion(node, "No suitable field declaration for '" + node.getName() + "' found.");
        }
        return field;
	}

	/**
	 * @param e a method call
	 * @return the method declaration corresponding to the method call
	 * @throws TypeCheckingExpcetion when the method could not be found
	 */
	@Override
	public AMethodDecl getMethod(AMethodCallExp e) {
        AMethodDecl method = nameVisitor.getMethodDecls().get(e);
        if(method == null) {
            throw new TypeCheckingExpcetion(e, "No suitable method declaration for '" + e.getName() + "' found.");
        }
        return method;
    }

	/**
	 * 
	 * @param name a use of a variable name
	 * @return the declaration node of the variable
	 * @throws TypeCheckingExpcetion when the variable could not be found
	 */
	@Override
	public ATypedVar lookupVar(TId name) {
        ATypedVar var = nameVisitor.getVariableDecls().get(name);
        if(var == null) {
            throw new TypeCheckingExpcetion(name, "No suitable variable declaration for '" + name + "' found.");
        }
        return var;
    }

	/**
	 * 
	 * @param name a use of a class name
	 * @return the class declaration corresponding to the given name
	 * @throws TypeCheckingExpcetion when the class could not be found
	 */
	@Override
	public AClassDecl lookupClass(TId name) {
        AClassDecl classDecl = nameVisitor.getClassDecls().get(name);
        if(classDecl == null) {
            throw new TypeCheckingExpcetion(name, "No suitable class declaration for '" + name + "' found.");
        }
        return classDecl;
    }

	/**
	 * @param c a class declaration node
	 * @return the class declaration node of the super class or 'null' if there is no super class
	 */
	@Override
	public AClassDecl getSuperClass(AClassDecl c) {
        TId name = c.getSuper();
        if(name == null) {
            return null;
        }
        return lookupClass(name);
    }

	/**
	 * @param node some AST node
	 * @return the nearest class declaration for the given node, or null if the given node is not within a class
	 */
	@Override
	public AClassDecl getNearestClass(Node node) {
        Node iter = node;
        while(! (iter instanceof Start)) {
            if(iter instanceof AClassDecl) {
                return (AClassDecl) iter;
            }
            iter = iter.parent();
        }
        return null;
    }
}