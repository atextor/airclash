package airclash.system.console;

import java.io.PrintStream;

import org.fenggui.console.Console;

/**
 * Help console command.
 * @author Andreas Textor
 */
public class HelpCommand extends AbstractCommand {
	/**
	 * Validates the parameters. The help command can be used with 0 or 1 parameters.
	 * @param pParams The array of parameters. pParams[0] is the command name.
	 * @return true, if the commands are valid, false if otherwise.
	 */
	@Override
	public boolean validateParams(final String[] pParams) {
		return (pParams.length == 1
			|| pParams.length == 2)
			&& pParams[0].equals(getCommand());
	}

	/**
	 * Executes this command.
	 * @param pOut The output print stream.
	 * @param pSource The console that issued this command
	 * @param pArgs The array of parameters. pArgs[0] is the command name.
	 */
	public void execute(final PrintStream pOut, final Console pSource,
			final String[] pArgs) {
		if (pArgs.length == 1) {
			pOut.println("Use help <command> for help on a command.");
			pOut.println("Available commands:");
			for (AbstractCommand command : AbstractCommand.getRegisteredCommands()) {
				pOut.println(command.getCommand());
			}
		} else {
			for (AbstractCommand command : AbstractCommand.getRegisteredCommands()) {
				if (command.getCommand().equals(pArgs[1].trim())) {
					command.getHelp(pOut);
				}
			}
		}
	}

	/**
	 * Returns the name of this command.
	 * @return "help"
	 */
	public String getCommand() {
		return "help";
	}
	
	/**
	 * Outputs help for this command.
	 * @param pOut The print stream
	 */
	@Override
	public void getHelp(final PrintStream pOut) {
		pOut.println("Syntax: help");
		pOut.println("You need help for the help command? :-)");
	}

}
