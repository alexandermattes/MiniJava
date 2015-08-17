package minijava.analysis;

import minijava.node.AClassDecl;
import minijava.node.AFieldAccessExp;
import minijava.node.AMethodCallExp;
import minijava.node.AMethodDecl;
import minijava.node.ATypedVar;
import minijava.node.Node;
import minijava.node.TId;

/**
 * resolves names 
 */
public interface NameAnalysis {
	
	
	/**
	 * 
	 * @param node a field access node 
	 * @return the declaration node of the field
	 * @throws TypeCheckingExpcetion when the field could not be found
	 */
	ATypedVar getField (AFieldAccessExp node);

	/**
	 * @param e a method call
	 * @return the method declaration corresponding to the method call
	 * @throws TypeCheckingExpcetion when the method could not be found
	 */
	AMethodDecl getMethod(AMethodCallExp e);

	/**
	 * 
	 * @param name a use of a variable name
	 * @return the declaration node of the variable
	 * @throws TypeCheckingExpcetion when the variable could not be found
	 */
	ATypedVar lookupVar(TId name);

	/**
	 * 
	 * @param name a use of a class name
	 * @return the class declaration corresponding to the given name
	 * @throws TypeCheckingExpcetion when the class could not be found
	 */
	AClassDecl lookupClass(TId name);

	/**
	 * @param c a class declaration node
	 * @return the class declaration node of the super class or 'null' if there is no super class
	 */
	AClassDecl getSuperClass(AClassDecl c);

	/**
	 * @param node some AST node
	 * @return the nearest class declaration for the given node, or null if the given node is not within a class
	 */
	AClassDecl getNearestClass(Node node);

}