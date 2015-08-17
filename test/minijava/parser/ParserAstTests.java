package minijava.parser;

import minijava.main.Main;
import minijava.node.Start;
import minijava.printer.AstPrinter;

import org.junit.Assert;
import org.junit.Test;

public class ParserAstTests {
	
	@Test
	public void testMinimalProg() throws Exception {
		String input = "class Main { public static void main(String[] args) { }}";
		Main.parseToAST(input);
	}
	
	@Test
	public void testPrint() throws Exception {
		String input = "class Main { public static void main(String[] args) { System.out.println(42); }}";
		Start ast = Main.parseToAST(input);
		String printed = AstPrinter.print(ast);
		Assert.assertTrue(printed.contains("System.out.println(42);"));
	}
	
	@Test
	public void testLocalVar() throws Exception {
		String input = "class Main { public static void main(String[] args) { boolean x; }}";
		Start ast = Main.parseToAST(input);
		String printed = AstPrinter.print(ast);
		Assert.assertTrue(printed.contains("boolean x;"));
	}
	
	@Test
	public void testAssignment() throws Exception {
		String input = "class Main { public static void main(String[] args) { int x; x = 5; }}";
		Start ast = Main.parseToAST(input);
		String printed = AstPrinter.print(ast);
		Assert.assertTrue(printed.contains("x = 5;"));
	}
	
	@Test
	public void testIfStmt() throws Exception {
		String input = "class Main { public static void main(String[] args) { int x; if (true) x=5; else x = 7;  }}";
		Start ast = Main.parseToAST(input);
		String printed = AstPrinter.print(ast);
		Assert.assertTrue(printed.contains("if (true) x = 5; else x = 7;"));
	}
	
	@Test
	public void testWhileStmt() throws Exception {
		String input = "class Main { public static void main(String[] args) { int x; while (x < 10) x=x+1;  }}";
		Start ast = Main.parseToAST(input);
		String printed = AstPrinter.print(ast);
		Assert.assertTrue(printed.contains("while ((x < 10)) x = (x + 1);"));
	}
	
	@Test
	public void operators() throws Exception {
		String input = "class Main { public static void main(String[] args) { boolean x; x = ((((3 * 4) + 5) < 2) && (1 < 3)); }}";
		Start ast = Main.parseToAST(input);
		String printed = AstPrinter.print(ast);
		Assert.assertTrue(printed.contains("x = ((((3 * 4) + 5) < 2) && (1 < 3))"));
	}
	
	@Test
	public void operatorPrecedence() throws Exception {
		String input = "class Main { public static void main(String[] args) { boolean x; x = 3*4+5 < 2 && 1 < 3; }}";
		Start ast = Main.parseToAST(input);
		String printed = AstPrinter.print(ast);
		Assert.assertTrue(printed.contains("x = ((((3 * 4) + 5) < 2) && (1 < 3))"));
	}

}
