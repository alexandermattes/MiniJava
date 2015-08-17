package minijava.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;

import minijava.lexer.Lexer;
import minijava.lexer.LexerException;
import minijava.node.Start;
import minijava.parser.Parser;
import minijava.parser.ParserException;
/**
 * Main entry point for the expression parser.
 */
public class Main {

	/**
	 * Main method. Read a file.
	 * 
	 * @param args
	 *            must contain just one filename.
	 */
	public static void main(String[] args) {
		if (args.length == 1) {
			String filename = args[0];
			try {
				parseToAST(new File(filename));
				System.out.print("Parse OK.");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(2);
			}
		} else {
			System.err.println("Expected: one file name");
			System.exit(1);
		}
	}

	/**
	 * Parse the file and create the AST
	 * @param file the file to parse
	 * @return an AST
	 * @throws FileNotFoundException if the file is not found
	 * @throws ParserException in case of syntax errors
	 * @throws LexerException in case of syntax errors
	 * @throws IOException in case of file IO errors
	 */
	public static Start parseToAST(File file)
			throws FileNotFoundException, ParserException, LexerException, IOException {
		PushbackReader reader = new PushbackReader(new FileReader(file));
		return parseToAST(reader);
	}

	/**
	 * Takes a reader and parses it to an AST
	 * @param reader the input reader
	 * @return the AST
	 * @throws ParserException when there is a syntax error
	 * @throws LexerException when there is a lexical syntax error
	 * @throws IOException when the input cannot be read
	 */
	public static Start parseToAST(PushbackReader reader) 
			throws ParserException, LexerException, IOException {
		Lexer lexer = new Lexer(reader);
		Parser parser = new Parser(lexer);
		Start ast = parser.parse();
		return ast;
	}

	/**
	 * Takes a reader and parses it to an AST
	 * @param input the input as a string
	 * @return the AST
	 * @throws ParserException when there is a syntax error
	 * @throws LexerException when there is a lexical syntax error
	 * @throws IOException when the input cannot be read
	 */
	public static Start parseToAST(String input) throws ParserException, LexerException, IOException {
		return parseToAST(new PushbackReader(new StringReader(input)));
	}
}
