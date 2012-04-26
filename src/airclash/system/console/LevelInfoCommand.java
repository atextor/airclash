package airclash.system.console;

import java.io.PrintStream;

import org.fenggui.console.Console;

import airclash.system.Core;

/**
 * LevelInfo console command.
 * @author Andreas Textor
 */
public class LevelInfoCommand extends AbstractCommand {
	/** The core reference. */
	private Core core;
	
	/**
	 * Constructor.
	 * @param pCore The core reference
	 */
	public LevelInfoCommand(final Core pCore) {
		this.core = pCore;
	}
	
	/**
	 * Validates the parameters. The help command can be used with 0 or 1 parameters.
	 * @param pParams The array of parameters. pParams[0] is the command name.
	 * @return true, if the commands are valid, false if otherwise.
	 */
	@Override
	public boolean validateParams(final String[] pParams) {
		return validateNoParams(pParams);
	}

	/**
	 * Executes this command.
	 * @param pOut The output print stream.
	 * @param pSource The console that issued this command
	 * @param pArgs The array of parameters. pArgs[0] is the command name.
	 */
	public void execute(final PrintStream pOut, final Console pSource,
			final String[] pArgs) {
		pOut.println(this.core.getLevel());
	}

	/**
	 * Returns the name of this command.
	 * @return "help"
	 */
	public String getCommand() {
		return "levelinfo";
	}
	
	/**
	 * Outputs help for this command.
	 * @param pOut The print stream
	 */
	@Override
	public void getHelp(final PrintStream pOut) {
		pOut.println("Syntax: levelinfo");
		pOut.println("Outputs information about the currently loaded level");
	}

}
