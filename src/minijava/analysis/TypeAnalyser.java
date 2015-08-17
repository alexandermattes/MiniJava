package minijava.analysis;

import java.util.List;

import minijava.analysis.types.MjBool;
import minijava.analysis.types.MjClass;
import minijava.analysis.types.MjInt;
import minijava.analysis.types.MjIntArray;
import minijava.analysis.types.MjType;
import minijava.node.AAddExp;
import minijava.node.AAndExp;
import minijava.node.AArrayAllocExp;
import minijava.node.AArrayLookupExp;
import minijava.node.AAssignStmt;
import minijava.node.ABoolType;
import minijava.node.ACallStmt;
import minijava.node.AClassDecl;
import minijava.node.AClassType;
import minijava.node.ACmpExp;
import minijava.node.AFalseExp;
import minijava.node.AFieldAccessExp;
import minijava.node.AIdentifierExp;
import minijava.node.AIfStmt;
import minijava.node.AIntLiteralExp;
import minijava.node.AIntType;
import minijava.node.AIntarrayType;
import minijava.node.AMethodCallExp;
import minijava.node.AMethodDecl;
import minijava.node.AMultExp;
import minijava.node.ANegExp;
import minijava.node.ANotExp;
import minijava.node.AObjAllocExp;
import minijava.node.AProgram;
import minijava.node.ASubExp;
import minijava.node.AThisExp;
import minijava.node.ATrueExp;
import minijava.node.ATypedVar;
import minijava.node.AWhileStmt;
import minijava.node.Node;
import minijava.node.PExp;
import minijava.node.PType;
import minijava.node.PTypedVar;
import minijava.node.TId;

/**
 * calculates types of expressions and checks if a program is type correct
 */
public class TypeAnalyser implements TypeAnalysis {

    /**
     * Result of the name analysis
     */
    private NameAnalysis nameAnalysis;

    /**
     * The complete analysis
     */
    private MjAnalysis mjAnalysis;


    /**
     * @param mjAnalysis reverence to the complete analysis
     */
    protected TypeAnalyser(MjAnalysis mjAnalysis) {
        this.mjAnalysis = mjAnalysis;
        this.nameAnalysis = mjAnalysis.getNameAnalysis();
    }


	/**
	 * @param e some expression
	 * @return the type of the given expression
	 */
	@Override
	public MjType getType(PExp e) {
        if(e instanceof AAndExp) {
            return new MjBool();
        }
        if(e instanceof ACmpExp) {
            return new MjBool();
        }
        if(e instanceof AAddExp) {
            return new MjInt();
        }
        if(e instanceof ASubExp) {
            return new MjInt();
        }
        if(e instanceof AMultExp) {
            return new MjInt();
        }
        if(e instanceof ANotExp) {
            return new MjBool();
        }
        if(e instanceof ANegExp) {
            return new MjInt();
        }
        if(e instanceof AIntLiteralExp) {
            return new MjInt();
        }
        if(e instanceof AIdentifierExp) {
            TId id = ((AIdentifierExp) e).getName();
            ATypedVar varDecl = nameAnalysis.lookupVar(id);
            PType t = varDecl.getType();
            return pTypeToMjType(t);
        }
        if(e instanceof AArrayAllocExp) {
            return new MjIntArray();
        }
        if(e instanceof AObjAllocExp) {
            TId className = ((AObjAllocExp) e).getName();
            AClassDecl classDecl = nameAnalysis.lookupClass(className);
            return new MjClass(classDecl);
        }
        if(e instanceof AArrayLookupExp) {
            return new MjInt();
        }
        if(e instanceof AFieldAccessExp) {
            PExp obj = ((AFieldAccessExp) e).getObj();
            String idName = ((AFieldAccessExp) e).getName().getText();

            if(idName.equals("length")) {
                if(getType(obj).isSubtypeOf(new MjIntArray(), mjAnalysis)) {
                    return new MjInt();
                }
            }
            ATypedVar varDecl = nameAnalysis.getField((AFieldAccessExp) e);
            PType t = varDecl.getType();
            return pTypeToMjType(t);
        }
        if(e instanceof AMethodCallExp) {
            AMethodDecl methodDecl = nameAnalysis.getMethod((AMethodCallExp) e);
            PType t = methodDecl.getRetType();
            return pTypeToMjType(t);
        }
        if(e instanceof ATrueExp) {
            return new MjBool();
        }
        if(e instanceof AFalseExp) {
            return new MjBool();
        }
        if(e instanceof AThisExp) {
            AClassDecl classDecl = nameAnalysis.getNearestClass(e);
            return new MjClass(classDecl);
        }
        throw new RuntimeException("Unknown subclass of PExp: " + e);
	}

    /**
     * @param t a PType
     * @return a MjType
     */
    private MjType pTypeToMjType(PType t) {
        if(t instanceof ABoolType) {
            return new MjBool();
        }
        if(t instanceof AClassType) {
            TId className = ((AClassType) t).getName();
            AClassDecl classDecl = nameAnalysis.lookupClass(className);
            return new MjClass(classDecl);
        }
        if(t instanceof AIntType) {
            return new MjInt();
        }
        if(t instanceof AIntarrayType) {
            return new MjIntArray();
        }
        throw new RuntimeException("Unknown subclass of PType: " + t);
    }


	/**
	 * Checks a whole program for type errors
	 * @param prog the program to check
	 * @throws TypeCheckingExpcetion when there is a type error
	 */
	@Override
	public void checkProgram(AProgram prog) {
        prog.apply(new TypeCheckVisitor());
	}

    /**
     * Visitor that checks if a AST is typecorrect. Uses there
     * nameAnalysis field of the TypeAnalyser as context for the analysis.
     */
    private class TypeCheckVisitor extends DepthFirstAdapter
    {
        /**
         * Check if an argument has the expected type
         * @param context a context for the possible exception
         * @param argument the expression that gets checked
         * @param argumentName a name for the argument
         * @param expected the expected type of the argument
         * @throws TypeCheckingExpcetion when there is a type error
         */
        private void check(Node context, PExp argument, String argumentName, MjType expected) {
            MjType argType = getType(argument);
            if(! argType.isSubtypeOf(expected, mjAnalysis)) {
                throw new TypeCheckingExpcetion(context, argumentName + " should have type " + expected + " but has type " + argType + ".");
            }
        }

        @Override
        public void outAMethodDecl(AMethodDecl node) {
            check(node, node.getRetExp(), "The return expression", pTypeToMjType(node.getRetType()));
        }

        @Override
        public void outAAssignStmt(AAssignStmt node) {
            check(node, node.getRhs(), "RHS in the assignment", getType(node.getLhs()));
        }

        @Override
        public void outAIfStmt(AIfStmt node) {
            check(node, node.getCond(), "Condition", new MjBool());
        }

        @Override
        public void outAWhileStmt(AWhileStmt node) {
            check(node, node.getCond(), "Condition", new MjBool());
        }

        @Override
        public void caseACallStmt(ACallStmt node) {

            //Special case for System.out.println
            if(node.getExp() != null) {
                PExp exp = node.getExp();
                if(((AMethodCallExp) exp).getName().getText().equals("println")) {
                    PExp obj = ((AMethodCallExp) exp).getObj();
                    if(obj instanceof AFieldAccessExp) {
                        if(((AFieldAccessExp) obj).getName().getText().equals("out")) {
                            PExp obj2 = ((AFieldAccessExp) obj).getObj();
                            if(obj2 instanceof AIdentifierExp) {
                                if(((AIdentifierExp) obj2).getName().getText().equals("System")) {
                                    List<PExp> args = ((AMethodCallExp) exp).getArgs();
                                    for(PExp e : args) {
                                        e.apply(this);
                                    }
                                    if(args.size() == 1) {
                                        if(getType(args.get(0)).isSubtypeOf(new MjInt(), mjAnalysis)) {
                                            return;
                                        }
                                    }
                                    throw new TypeCheckingExpcetion(exp, "Invalid parameter for System.out.println");
                                }
                            }
                        }
                    }
                }
            }
            super.caseACallStmt(node);
        }

        @Override
        public void outAAndExp(AAndExp node) {
            check(node, node.getLeft(), "Left argument", new MjBool());
            check(node, node.getRight(), "Right argument", new MjBool());
        }

        @Override
        public void outACmpExp(ACmpExp node) {
            check(node, node.getLeft(), "Left argument", new MjInt());
            check(node, node.getRight(), "Right argument", new MjInt());
        }

        @Override
        public void outAAddExp(AAddExp node) {
            check(node, node.getLeft(), "Left argument", new MjInt());
            check(node, node.getRight(), "Right argument", new MjInt());
        }

        @Override
        public void outASubExp(ASubExp node) {
            check(node, node.getLeft(), "Left argument", new MjInt());
            check(node, node.getRight(), "Right argument", new MjInt());
        }

        @Override
        public void outAMultExp(AMultExp node) {
            check(node, node.getLeft(), "Left argument", new MjInt());
            check(node, node.getRight(), "Right argument", new MjInt());
        }

        @Override
        public void outANotExp(ANotExp node) {
            check(node, node.getExp(), "Argument", new MjBool());
        }

        @Override
        public void outANegExp(ANegExp node) {
            check(node, node.getExp(), "Argument", new MjInt());
        }

        @Override
        public void outAArrayAllocExp(AArrayAllocExp node) {
            check(node, node.getSize(), "Size", new MjInt());
        }

        @Override
        public void outAArrayLookupExp(AArrayLookupExp node) {
            check(node, node.getExp(), "Array", new MjIntArray());
            check(node, node.getOffset(), "Index", new MjInt());
        }


        @Override
        public void caseAFieldAccessExp(AFieldAccessExp node) {
            PExp obj = node.getObj();
            String idName = node.getName().getText();

            //Special case for length
            if(idName.equals("length")) {
                obj.apply(this);
                if(getType(obj).isSubtypeOf(new MjIntArray(), mjAnalysis)) {
                    return;
                }
            }
            super.caseAFieldAccessExp(node);
        }

        @Override
        public void outAFieldAccessExp(AFieldAccessExp node) {
            //Nothing to do?
        }

        @Override
        public void outAMethodCallExp(AMethodCallExp node) {
            PExp obj = node.getObj();
            TId name = node.getName();
            List<PExp> args = node.getArgs();

            AMethodDecl methodDecl = nameAnalysis.getMethod(node);
            List<PTypedVar> formalParams = methodDecl.getFormalParams();

            if(args.size() < formalParams.size()) {
                throw new TypeCheckingExpcetion(node, "To few parameters applied: Only " + args.size() + " out of " + formalParams.size() + ".");
            }
            if(args.size() > formalParams.size()) {
                throw new TypeCheckingExpcetion(node, "To many parameters applied: " + args.size() + " instead of " + formalParams.size() + ".");
            }

            for(int i = 0; i < args.size(); i++) {
                MjType formalType = pTypeToMjType(((ATypedVar) formalParams.get(i)).getType());
                check(node, args.get(i), "Parameter number " + i + " ", formalType);
            }
        }
    }
}