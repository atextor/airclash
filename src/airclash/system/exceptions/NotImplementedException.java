package airclash.system.exceptions;

/**
 * Exception that is thrown in methods that are not written yet.
 * 
 * @author Andreas Textor
 */
@SuppressWarnings("serial")
public class NotImplementedException extends RuntimeException {
	/**
	 * Constructor with a message.
	 * @param pMessage The message
	 */
	public NotImplementedException(final String pMessage) {
		super(pMessage);
	}
	
	/**
	 * Constructor with a message and a throwable cause.
	 * @param pMessage The message
	 * @param pThrowable The cause
	 */
	public NotImplementedException(final String pMessage, final Throwable pThrowable) {
		super(pMessage, pThrowable);
	}
	
	/**
	 * Constructor with a throwable cause.
	 * @param pThrowable The cause
	 */
	public NotImplementedException(final Throwable pThrowable) {
		super(pThrowable);
	}
}
