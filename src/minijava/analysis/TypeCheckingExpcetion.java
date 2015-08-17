package minijava.analysis;

import minijava.node.Node;

public class TypeCheckingExpcetion extends RuntimeException {
	private static final long serialVersionUID = -6901336765012408325L;
	private Node blame;
	private String msg;

	public TypeCheckingExpcetion(Node blame, String msg) {
		super("Error in line " + blame.getLine()+": " + msg);
		this.blame = blame;
		this.msg = msg;
	}

	public Node getBlame() {
		return blame;
	}

	public String getMsg() {
		return msg;
	}

	
}
