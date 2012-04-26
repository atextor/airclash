package airclash.system.console;

import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;

import org.fenggui.console.ICommand;

/**
 * Abstract base class for console commands. This keeps track of all commands that
 * exist.
 * @author Andreas Textor
 */
public abstract class AbstractCommand implements ICommand {
	/** The set of all commands. */
	private static Set<AbstractCommand> commands = new HashSet<AbstractCommand>();
	
	/**
	 * Constructor. Adds this command to the static set of commands.
	 */
	public AbstractCommand() {
		commands.add(this);
	}
	
	/**
	 * Returns the set of all available commands.
	 * @return The set of commands
	 */
	public static Set<AbstractCommand> getRegisteredCommands() {
		return commands;
	}
	
	/**
	 * Method that checks if the supplied parameters are valid for this command.
	 * @param pParams The array of parameters. pParams[0] is the command name.
	 * @return true, if the commands are valid, false if otherwise.
	 */
	public abstract boolean validateParams(final String[] pParams);

	/**
	 * Default check method for boolean parameters.
	 * @param pParams The array of parameters. pParams[0] is the command name.
	 * @return true, if the commands are valid, false if otherwise. 
	 */
	protected boolean validateBoolean(final String[] pParams) {
		return pParams.length == 2
			&& pParams[0].equals(getCommand())
			&& (pParams[1].equals("0") || pParams[1].equals("1"));
	}
	
	/**
	 * Default check method for commands without parameters.
	 * @param pParams The array of parameters. pParams[0] is the command name.
	 * @return true, if the commands are valid, false if otherwise.
	 */
	protected boolean validateNoParams(final String[] pParams) {
		return pParams.length == 1
			&& pParams[0].equals(getCommand());
	}
	
	/**
	 * This method prints out the help for the command. Commands should override
	 * this method.
	 * @param pOut The print stream
	 */
	public void getHelp(final PrintStream pOut) {
		pOut.println("No help available for " + getCommand());
	}
}
