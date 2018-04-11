package upsimulator.exceptions;

/**
 * If a membrane class does not exist, this exception will be thrown.
 * 
 * @author quan
 *
 */
public class UnknownMembraneClassException extends Exception {
	private static final long serialVersionUID = -417203572128137441L;

	private String memClassName;

	public UnknownMembraneClassException(String memClassName) {
		super("Couldn't find the defination of membrane class: " + memClassName);
		this.memClassName = memClassName;
	}
}
