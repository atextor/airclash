package airclash.system;

import java.io.PrintStream;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Simple Log4J appender that writes messages to a print stream.
 * @author Andreas Textor
 */
public class PrintStreamAppender extends AppenderSkeleton {
	/** The print stream to write to. */
	private PrintStream out;

	/**
	 * Constructor.
	 * @param pOut The output print stream
	 * @param pLayout The layout to use
	 */
	public PrintStreamAppender(final PrintStream pOut, final Layout pLayout) {
		this.out = pOut;
		this.layout = pLayout;
	}

	/**
	 * A log message was received. Output it to the printstream.
	 * @param pEvent The logging event
	 */
	@Override
	protected void append(final LoggingEvent pEvent) {
		final String output = this.layout.format(pEvent);
		this.out.println("--- " + output);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void close() {
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean requiresLayout() {
		return true;
	}

}
