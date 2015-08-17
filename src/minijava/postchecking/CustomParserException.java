package minijava.postchecking;

import minijava.node.Node;

/**
 * Custom Exception to be thrown when encountering bad assignment or bad method call
 */
public class CustomParserException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4661322987354306393L;
	/**
	 * The node that caused the exception
	 */
	private Node blame;
	/**
	 * The message of the exception
	 */
	private String msg;
	
	/**
	 * Create a new CustomParserException
	 * 
	 * @param blame
	 *            the node that caused the exception 
	 * @param msg
	 *            The message of the exception
	 */
	public CustomParserException(Node blame, String msg){
		super("Parser error in line " + blame.getLine()+": " + msg);
		this.blame = blame;
		this.msg = msg;
	}
	
	/**
	 * Gets the blame of the exception
	 * 
	 * @return the Node blame
	 */
	public Node getBlame() {
		return blame;
	}

	/**
	 * Gets the  message of the exception 
	 * 
	 * @return the String message
	 */
	public String getMsg() {
		return msg;
	}
}
