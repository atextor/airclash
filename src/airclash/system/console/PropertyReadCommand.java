package airclash.system.console;

import java.io.PrintStream;

import airclash.system.Core;

/**
 * Console command that outputs the value of a system property.
 * @author Andreas Textor
 */
public class PropertyReadCommand extends AbstractCommand {
	/** The name of the command/property. */
	private String commandName;
	
	/**
	 * Constructor.
	 * @param pCommandName The name of the property
	 */
	public PropertyReadCommand(final String pCommandName) {
		this.commandName = pCommandName;
	}

	/**
	 * Executes this command.
	 * @param pOut The output print stream.
	 * @param pSource The console that issued this command
	 * @param pArgs The array of parameters. pArgs[0] is the command name.
	 */
	public void execute(final PrintStream pOut,
			final org.fenggui.console.Console pSource, final String[] pArgs) {
		pOut.println(this.commandName + ": " + Core.PROPERTIES.get(this.commandName.toUpperCase()));
	}

	/**
	 * Returns the name of this command.
	 * @return The name of the property
	 */
	public String getCommand() {
		return this.commandName;
	}
	
	/**
	 * Method that checks if the supplied parameters are valid for this command.
	 * @param pParams The array of parameters. pParams[0] is the command name.
	 * @return true, if the commands are valid, false if otherwise.
	 */
	@Override
	public boolean validateParams(final String[] pParams) {
		return this.validateNoParams(pParams);
	}
	
	/**
	 * Outputs help for this command.
	 * @param pOut The print stream
	 */
	@Override
	public void getHelp(final PrintStream pOut) {
		pOut.println("Syntax: " + getCommand());
		pOut.println("Use this command to read the corresponding property.");
	}
}
