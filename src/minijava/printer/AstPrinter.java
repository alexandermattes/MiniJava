package minijava.printer;

import java.util.List;

import minijava.analysis.AnalysisAdapter;
import minijava.node.AAddExp;
import minijava.node.AAndExp;
import minijava.node.AArrayAllocExp;
import minijava.node.AArrayLookupExp;
import minijava.node.AAssignStmt;
import minijava.node.ABlockStmt;
import minijava.node.ABoolType;
import minijava.node.ACallStmt;
import minijava.node.AClassDecl;
import minijava.node.AClassType;
import minijava.node.ACmpExp;
import minijava.node.AFalseExp;
import minijava.node.AIdentifierExp;
import minijava.node.AIfStmt;
import minijava.node.AIntLiteralExp;
import minijava.node.AIntType;
import minijava.node.AIntarrayType;
import minijava.node.AMainClass;
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
import minijava.node.Start;

/**
 * This class can print an abstract syntax tree. This can be useful for testing
 * and debugging.
 */
public class AstPrinter {

	/**
	 * @param n
	 *            a node to print
	 * @return a string representation of the given node
	 */
	public static String print(Node n) {
		PrintVisitor printer = new PrintVisitor();
		n.apply(printer);
		return printer.result.toString();
	}

	/** the visitor for printing expressions */
	private static class PrintVisitor extends AnalysisAdapter {
		/** StringBuilder for building the result string */
		private StringBuilder result = new StringBuilder();
		/**
		 * the current indentation level of the code (how many tab characters to
		 * insert before a line
		 */
		private int indentation = 0;

		private void newlines(int count) {
			for (int i = 0; i < count; i++) {
				result.append("\n");
			}
			printIndent();
		}

		private void printIndent() {
			for (int i = 0; i < indentation; i++) {
				result.append("\t");
			}
		}

		private void printListWithNewlines(List<? extends Node> nodes, int nrNewlines) {
			boolean first = true;
			for (Node node : nodes) {
				if (!first) {
					newlines(nrNewlines);
				}
				node.apply(this);
				first = false;
			}
		}

		private void printListWithNewlinesAndSemicolon(List<? extends Node> nodes, int nrNewlines) {
			boolean first = true;
			for (Node node : nodes) {
				if (!first) {
					newlines(nrNewlines);
				}
				node.apply(this);
				result.append(";");
				first = false;
			}
		}

		private void printListWithSeperator(List<? extends Node> nodes, String sep) {
			boolean first = true;
			for (Node node : nodes) {
				if (!first) {
					result.append(sep);
				}
				node.apply(this);
				first = false;
			}
		}

		private void startBlock() {
			result.append(" {");
			indentation++;
			newlines(1);
		}

		private void endBlock() {
			indentation--;
			newlines(1);
			result.append("}");
			newlines(1);
		}

		@Override
		public void caseStart(Start node) {
			node.getPProgram().apply(this);
		}

		@Override
		public void caseAProgram(AProgram node) {
			node.getMain().apply(this);
			newlines(2);
		}

		@Override
		public void caseAMainClass(AMainClass node) {
			result.append("class Main");
			startBlock();
			result.append("public static void main(String[] ");
			result.append(node.getMainArgs().getText());
			result.append(")");
			startBlock();
			printListWithNewlinesAndSemicolon(node.getLocalVars(), 1);
			newlines(2);
			printListWithNewlines(node.getStms(), 1);
			endBlock();
			endBlock();
		}

		@Override
		public void caseAClassDecl(AClassDecl node) {
			result.append("class ");
			result.append(node.getId().getText());
			if (node.getSuper() != null) {
				result.append(" extends ");
				result.append(node.getSuper().getText());
			}
			startBlock();
			printListWithNewlinesAndSemicolon(node.getFields(), 1);
			printListWithNewlines(node.getMethods(), 2);
			endBlock();

		}

		@Override
		public void caseATypedVar(ATypedVar node) {
			node.getType().apply(this);
			result.append(" ");
			result.append(node.getName().getText());
		}

		@Override
		public void caseAMethodDecl(AMethodDecl node) {
			result.append("public ");
			node.getRetType().apply(this);
			result.append(" ");
			result.append(node.getName().getText());
			result.append("(");
			printListWithSeperator(node.getFormalParams(), ", ");
			result.append(")");
			startBlock();
			printListWithNewlinesAndSemicolon(node.getLocalVars(), 1);
			newlines(2);
			printListWithNewlines(node.getStmts(), 1);
			endBlock();
		}

		@Override
		public void caseAIntarrayType(AIntarrayType node) {
			result.append("int[]");
		}

		@Override
		public void caseABoolType(ABoolType node) {
			result.append("boolean");
		}

		@Override
		public void caseAIntType(AIntType node) {
			result.append("int");
		}

		@Override
		public void caseAClassType(AClassType node) {
			result.append(node.getName().getText());
		}

		@Override
		public void caseABlockStmt(ABlockStmt node) {
			startBlock();
			printListWithNewlines(node.getStmts(), 1);
			endBlock();
		}

		@Override
		public void caseAAssignStmt(AAssignStmt node) {
			node.getLhs().apply(this);
			result.append(" = ");
			node.getRhs().apply(this);
			result.append(";");
		}

		@Override
		public void caseACallStmt(ACallStmt node) {
			node.getExp().apply(this);
			result.append(";");
		}

		@Override
		public void caseAIfStmt(AIfStmt node) {
			result.append("if (");
			node.getCond().apply(this);
			result.append(") ");
			node.getTstmt().apply(this);
			result.append(" else ");
			node.getFstmt().apply(this);
		}

		@Override
		public void caseAWhileStmt(AWhileStmt node) {
			result.append("while (");
			node.getCond().apply(this);
			result.append(") ");
			node.getBody().apply(this);
		}

		@Override
		public void caseAAndExp(AAndExp node) {
			result.append("(");
			node.getLeft().apply(this);
			result.append(" && ");
			node.getRight().apply(this);
			result.append(")");
		}

		@Override
		public void caseACmpExp(ACmpExp node) {
			result.append("(");
			node.getLeft().apply(this);
			result.append(" < ");
			node.getRight().apply(this);
			result.append(")");
		}

		@Override
		public void caseAAddExp(AAddExp node) {
			result.append("(");
			node.getLeft().apply(this);
			result.append(" + ");
			node.getRight().apply(this);
			result.append(")");
		}

		@Override
		public void caseASubExp(ASubExp node) {
			result.append("(");
			node.getLeft().apply(this);
			result.append(" - ");
			node.getRight().apply(this);
			result.append(")");
		}

		@Override
		public void caseAMultExp(AMultExp node) {
			result.append("(");
			node.getLeft().apply(this);
			result.append(" * ");
			node.getRight().apply(this);
			result.append(")");
		}

		@Override
		public void caseANotExp(ANotExp node) {
			result.append("!");
			node.getExp().apply(this);
		}

		@Override
		public void caseANegExp(ANegExp node) {
			result.append("- ");
			node.getExp().apply(this);
		}

		@Override
		public void caseAIntLiteralExp(AIntLiteralExp node) {
			result.append(node.getInteger().getText());
		}

		@Override
		public void caseAIdentifierExp(AIdentifierExp node) {
			result.append(node.getName().getText());
		}

		@Override
		public void caseAArrayAllocExp(AArrayAllocExp node) {
			result.append("new int[");
			node.getSize().apply(this);
			result.append("]");
		}

		@Override
		public void caseAObjAllocExp(AObjAllocExp node) {
			result.append("new ");
			result.append(node.getName().getText());
			result.append("()");
		}

		@Override
		public void caseAArrayLookupExp(AArrayLookupExp node) {
			node.getExp().apply(this);
			result.append("[");
			node.getOffset().apply(this);
			result.append("]");
		}

		@Override
		public void caseAMethodCallExp(AMethodCallExp node) {
			node.getObj().apply(this);
			result.append(".");
			result.append(node.getName().getText());
			result.append("(");
			printListWithSeperator(node.getArgs(), ", ");
			result.append(")");
		}

		@Override
		public void caseAFieldAccessExp(minijava.node.AFieldAccessExp node) {
			node.getObj().apply(this);
			result.append(".");
			result.append(node.getName().getText());
		}

		@Override
		public void caseATrueExp(ATrueExp node) {
			result.append("true");
		}

		@Override
		public void caseAFalseExp(AFalseExp node) {
			result.append("false");
		}

		@Override
		public void caseAThisExp(AThisExp node) {
			result.append("this");
		}

	}
}
