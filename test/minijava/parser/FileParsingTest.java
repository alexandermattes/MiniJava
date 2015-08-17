package minijava.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import minijava.lexer.LexerException;
import minijava.main.Main;
import minijava.node.Start;
import minijava.postchecking.CustomParserException;
import minijava.printer.AstPrinter;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.unifr.proglang.testing.LabeledParameterized;
import de.unifr.proglang.testing.LabeledParameterized.LabeledParameters;

/**
 * <p>
 * Generic test case class to run the parser on one input file and then expect
 * either a parse success or a parse failure.
 * </p>
 * <p>
 * How to use this class: Create a directory "testdata-ok" and a directory
 * "testdata-error" in the root of the Project. Fill it with files you want to
 * parse: Every file in testdata-ok will induce a test case saying
 * "the parser (being tested) must accept that file", and every file in
 * testdata-error induces a test case saying "the parser must reject that file".
 * </p>
 * 
 * @author anton
 * 
 */
@RunWith(LabeledParameterized.class)
public class FileParsingTest {

	/**
	 * The text file to parse.
	 */
	private final File inputFile;
	/**
	 * True iff this is a test case in which you believe the input file to have
	 * correct syntax. False if you expect a parse error.
	 */
	private final boolean parseSuccessMeansTestPasses;

	/**
	 * Create a new test case (to be called by JUnit, not by you)
	 * 
	 * @param inputFile
	 *            the file to parse
	 * @param parseSuccessMeansTestPasses
	 *            true iff you expect the parser to accept it
	 */
	public FileParsingTest(File inputFile, boolean parseSuccessMeansTestPasses) {
		this.inputFile = inputFile;
		this.parseSuccessMeansTestPasses = parseSuccessMeansTestPasses;
	}

	/**
	 * The only test method of this test class: run a fresh parser instance on
	 * the input file, and judge the result according to the
	 * {@link #parseSuccessMeansTestPasses} parameter: parse error becomes test
	 * failure iff parseSuccessMeansTestPasses is true.
	 * 
	 * @throws IOException
	 *             if file IO goes wrong (shouldn't; but want to know when)
	 */
	@Test
	public void testParser() throws IOException {
		File file = inputFile;
		try {
			Start ast = Main.parseToAST(file);

			// if we reach this, the parser and the visitor has accepted.
			// how bad that is depends on parseSuccessMeansTestPasses
			Assert.assertTrue(file.getName() + ": parser accepted it, should reject it",
					parseSuccessMeansTestPasses);
			
			// parsing the printed AST again should yield the same result:
			String printedAst = AstPrinter.print(ast);
			Start ast2 = Main.parseToAST(printedAst);
			String printedAst2 = AstPrinter.print(ast2);
			Assert.assertEquals(printedAst, printedAst2);
		} catch (ParserException e) {
			// if we reach this, the parser has rejected.
			Assert.assertFalse(file.getName() + ": parser rejected it, should accept it. Error was: \n" + e,
					parseSuccessMeansTestPasses);
		} catch (CustomParserException e) {
			// if we reach this, the visitor has rejected it.
			Assert.assertFalse(file.getName() + ": parser rejected it, should accept it. Error was: \n" + e,
					parseSuccessMeansTestPasses);
		} catch (LexerException e) {
			Assert.assertFalse(file.getName() + ": lexer rejected it, should accept it. Error was: \n" + e,
					parseSuccessMeansTestPasses);
		}

	}

	/**
	 * For {@link Parameterized}, create a list of constructor argument arrays.
	 * In this case, we turn all the files in testdata-ok into positive and all
	 * the files in testdata-error into negative test cases.
	 * 
	 * @return a list of arrays, each of which contains valid constructor params
	 *         for this class
	 */
	@LabeledParameters
	public static Collection<Object[]> data() {
		ArrayList<Object[]> ctorParams = new ArrayList<Object[]>();

		appendTestCasesFromDir(new File("testdata/parser/ok"), true, ctorParams);
		appendTestCasesFromDir(new File("testdata/parser/error"), false, ctorParams);
		return ctorParams;
	}

	/**
	 * Helper function to fill a list of constructor args with data gained from
	 * file names in a directory
	 * 
	 * @param testDataDir
	 *            the directory whose files should be parsed
	 * @param isOkTest
	 *            true iff you expect parse OK
	 * @param ctorParams
	 *            the growing list to be one day returned by {@link #data()}
	 */
	private static void appendTestCasesFromDir(File testDataDir,
			boolean isOkTest, ArrayList<Object[]> ctorParams) {
		if (testDataDir.exists()) {
			if (testDataDir.isDirectory()) {
				File[] files = testDataDir.listFiles();
				for (File f : files) {
					if(f.isFile()){
						ctorParams.add(new Object[] { f.getName(), f, isOkTest });
					}
				}
			}
			else {
				// yes, RuntimeException is dirty technique, but this is just
				// a simple test case.
				throw new RuntimeException(String.format(
						"Cannot derive parser test from %s: not a directory",
						testDataDir.getAbsolutePath()));
			}
		}
	}
}
