package airclash.system.exceptions;

/**
 * Exception that is thrown when a resource can't be read or is invalid.
 * 
 * @author Andreas Textor
 */
@SuppressWarnings("serial")
public class InvalidResourceException extends Exception {
	/**
	 * Constructor with a message.
	 * @param pMessage The message
	 */
	public InvalidResourceException(final String pMessage) {
		super(pMessage);
	}
	
	/**
	 * Constructor with a message and a throwable cause.
	 * @param pMessage The message
	 * @param pThrowable The cause
	 */
	public InvalidResourceException(final String pMessage, final Throwable pThrowable) {
		super(pMessage, pThrowable);
	}
	
	/**
	 * Constructor with a throwable cause.
	 * @param pThrowable The cause
	 */
	public InvalidResourceException(final Throwable pThrowable) {
		super(pThrowable);
	}
}
