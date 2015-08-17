package minijava.typechecker;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import minijava.analysis.DepthFirstAdapter;
import minijava.analysis.MjAnalysis;
import minijava.analysis.TypeChecker;
import minijava.analysis.types.MjType;
import minijava.lexer.LexerException;
import minijava.main.Main;
import minijava.node.AClassDecl;
import minijava.node.AClassType;
import minijava.node.AFieldAccessExp;
import minijava.node.AIdentifierExp;
import minijava.node.AMethodCallExp;
import minijava.node.AMethodDecl;
import minijava.node.ATypedVar;
import minijava.node.Node;
import minijava.node.PExp;
import minijava.node.Start;
import minijava.parser.ParserException;

import org.junit.Assert;
import org.junit.Test;

public class TypecheckerTest {
	
	
	@Test
	public void testLookupVar() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { int x;  x = 5; }}";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		AIdentifierExp left = (AIdentifierExp) selectNode(ast.getPProgram(), "getMain", "getStms", 0, "getLhs");
		ATypedVar localVar_x = (ATypedVar) selectNode(ast.getPProgram(), "getMain", "getLocalVars", 0);
		
		ATypedVar decl = analysis.getNameAnalysis().lookupVar(left.getName());
		Assert.assertEquals(localVar_x, decl);
	}
	
	
	@Test
	public void testShadowVar() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { }} \n"
				+ "class A { int x; public int foo() { int x; x = 5; return 1; }}";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		AIdentifierExp left = (AIdentifierExp) selectNode(ast.getPProgram(), "getDecls", 0, "getMethods", 0, "getStmts", 0, "getLhs");
		ATypedVar localVar_x = (ATypedVar) selectNode(ast.getPProgram(),  "getDecls", 0, "getMethods", 0, "getLocalVars", 0);
		
		ATypedVar decl = analysis.getNameAnalysis().lookupVar(left.getName());
		Assert.assertEquals(localVar_x, decl);
	}
	
	@Test
	public void testShadowVar2() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { }} \n"
				+ "class A { int x; public int foo() { int x; this.x = 5; return 1; }}";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		AFieldAccessExp left = (AFieldAccessExp) selectNode(ast.getPProgram(), "getDecls", 0, "getMethods", 0, "getStmts", 0, "getLhs");
		ATypedVar field_x = (ATypedVar) selectNode(ast.getPProgram(),  "getDecls", 0, "getFields", 0);
		
		ATypedVar decl = analysis.getNameAnalysis().lookupVar(left.getName());
		Assert.assertEquals(field_x, decl);
	}
	
	
	@Test
	public void testGetField() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { A a; int x; a = new A(); x = a.x;  }} \n"
				+ "class A { int x; int y; }";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		AFieldAccessExp left = (AFieldAccessExp) selectNode(ast.getPProgram(), "getMain", "getStms", 1, "getRhs");
		ATypedVar field_x = (ATypedVar) selectNode(ast.getPProgram(),  "getDecls", 0, "getFields", 0);
		
		ATypedVar decl = analysis.getNameAnalysis().getField(left);
		Assert.assertEquals(field_x, decl);
	}
	
	
	@Test
	public void testGetMethod() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { A a; int x; a = new A(); x = a.foo();  }} \n"
				+ "class A { int foo; public int foo() { return 42; } }";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		AMethodCallExp call = (AMethodCallExp) selectNode(ast.getPProgram(), "getMain", "getStms", 1, "getRhs");
		AMethodDecl method_foo = (AMethodDecl) selectNode(ast.getPProgram(),  "getDecls", 0, "getMethods", 0);
		
		AMethodDecl decl = analysis.getNameAnalysis().getMethod(call);
		Assert.assertEquals(method_foo, decl);
	}
	
	@Test
	public void testGetNearestClass() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) {  }} \n"
				+ "class A { public int foo() { return 42; } } \n"
				+ "class B { public int foo() { return 77; } }";
		Start ast = Main.parseToAST(input);
		final MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		final AClassDecl classA = (AClassDecl) selectNode(ast.getPProgram(), "getDecls", 0);
		final AClassDecl classB = (AClassDecl) selectNode(ast.getPProgram(), "getDecls", 1);
		
		classA.apply(new DepthFirstAdapter() {
			@Override
			public void defaultIn(Node node) {
				Assert.assertEquals(classA, analysis.getNameAnalysis().getNearestClass(node));
			}
		});
		

		classB.apply(new DepthFirstAdapter() {
			@Override
			public void defaultIn(Node node) {
				Assert.assertEquals(classB, analysis.getNameAnalysis().getNearestClass(node));
			}
		});
	}	
	
	@Test
	public void testGetSuperClass() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) {  }} \n"
				+ "class A { } \n"
				+ "class B extends A {  }";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		AClassDecl classA = (AClassDecl) selectNode(ast.getPProgram(), "getDecls", 0);
		AClassDecl classB = (AClassDecl) selectNode(ast.getPProgram(), "getDecls", 1);
		
		Assert.assertNull(analysis.getNameAnalysis().getSuperClass(classA));
		Assert.assertEquals(classA, analysis.getNameAnalysis().getSuperClass(classB));
	}
	
	@Test
	public void testLookupClass() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { A a; B b; }} \n"
				+ "class A { } \n"
				+ "class B extends A {  }";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		AClassDecl classA = (AClassDecl) selectNode(ast.getPProgram(), "getDecls", 0);
		AClassDecl classB = (AClassDecl) selectNode(ast.getPProgram(), "getDecls", 1);
		AClassType localVar_a = (AClassType) selectNode(ast.getPProgram(),  "getMain", "getLocalVars", 0, "getType");
		AClassType localVar_b = (AClassType) selectNode(ast.getPProgram(),  "getMain", "getLocalVars", 1, "getType");
		
		Assert.assertEquals(classA, analysis.getNameAnalysis().lookupClass(localVar_a.getName()));
		Assert.assertEquals(classB, analysis.getNameAnalysis().lookupClass(localVar_b.getName()));
	}
	
	
	@Test
	public void testType() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { boolean x; A a; x = 3 < 4 && 5 < 6; a = new A(); }} \n"
				+ "class A {}";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		PExp exp1 = (PExp) selectNode(ast.getPProgram(),  "getMain", "getStms", 0, "getRhs");
		PExp exp2 = (PExp) selectNode(ast.getPProgram(),  "getMain", "getStms", 1, "getRhs");
		
		Assert.assertNotNull(analysis.getTypeAnalysis().getType(exp1));
		Assert.assertNotNull(analysis.getTypeAnalysis().getType(exp2));
		// could do more tests here, but we did not specify how to distinguish different types
	}
	
	@Test
	public void testSubtype() throws ParserException, LexerException, IOException {
		String input = "class Main { public static void main(String[] args) { A a; B b; a = new A(); b = new B(); }} \n"
				+ "class A {}  class B extends A {}";
		Start ast = Main.parseToAST(input);
		MjAnalysis analysis = TypeChecker.typecheckOrFail(ast);
		
		PExp exp1 = (PExp) selectNode(ast.getPProgram(),  "getMain", "getStms", 0, "getRhs");
		PExp exp2 = (PExp) selectNode(ast.getPProgram(),  "getMain", "getStms", 1, "getRhs");
		
		MjType type1 = analysis.getTypeAnalysis().getType(exp1);
		MjType type2 = analysis.getTypeAnalysis().getType(exp2);
		
		Assert.assertFalse(type1.isSubtypeOf(type2, analysis));
		Assert.assertTrue(type2.isSubtypeOf(type1, analysis));
	}
	
	
	/**
	 * Selects an AST node based on some selector names
	 * @param start         the node to start from
	 * @param selectors     a list of strings and integers, where strings invoke a get-method and integers take the nth element of a list  
	 * @return              the selected node from the ast
	 */
	private Node selectNode(Node start, Object ... selectors) {
		Object current = start;
		try {
			for (Object selector : selectors) {
				if (selector instanceof String) {
					Method m = current.getClass().getMethod((String) selector);
					current = m.invoke(current);
				} else if (selector instanceof Integer) {
					Integer index = (Integer) selector;
					current = ((List<?>) current).get(index);
				} else {
					throw new RuntimeException("unhandled selector: " + selector);
				}
			}
			return (Node) current;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("no method " + e.getMessage() + "found.\nAvailable methods are: " + printMethods(current.getClass()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * prints all the methods available for the given class
	 */
	private String printMethods(Class<?> class1) {
		String result = "";
		Method[] methods = class1.getMethods();
		Arrays.sort(methods, new Comparator<Method>() {
			@Override
			public int compare(Method o1, Method o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		for (Method m : methods) {
			result +="\n" + m.getName();
		}
		return result;
	}
	

}
