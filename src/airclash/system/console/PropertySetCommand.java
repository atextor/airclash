package airclash.system.console;

import java.io.PrintStream;

import airclash.system.Core;
import airclash.system.Properties;
import airclash.system.Properties.Prop;

/**
 * Console command "set", used to change the value of system properties.
 * @author Andreas Textor
 */
public class PropertySetCommand extends AbstractCommand {
	/**
	 * Executes this command.
	 * @param pOut The output print stream.
	 * @param pSource The console that issued this command
	 * @param pArgs The array of parameters. pArgs[0] is the command name.
	 */
	public void execute(final PrintStream pOut,
			final org.fenggui.console.Console pSource, final String[] pArgs) {
		pOut.println("set: " + pArgs[1] + " to " + pArgs[2]);
		Core.PROPERTIES.setProperty(pArgs[1].toUpperCase(), pArgs[2]);
	}

	/**
	 * Returns the name of this command.
	 * @return "set"
	 */
	public String getCommand() {
		return "set";
	}
	
	/**
	 * Method that checks if the supplied parameters are valid for this command.
	 * @param pParams The array of parameters. pParams[0] is the command name.
	 * @return true, if the commands are valid, false if otherwise.
	 */
	@Override
	public boolean validateParams(final String[] pParams) {
		return pParams.length == 3
			&& pParams[0].equals(getCommand())
			&& Core.PROPERTIES.getProperty(pParams[1].toUpperCase()) != null;
	}
	
	/**
	 * Outputs help for this command.
	 * @param pOut The print stream
	 */
	@Override
	public void getHelp(final PrintStream pOut) {
		pOut.println("Syntax: set <key> <value>");
		pOut.println("   valid keys:");
		for (Object o : Core.PROPERTIES.keySet()) {
			final String s = (String)o;
			final Prop p = Properties.Prop.valueOf(s);
			pOut.println("   " + s.toLowerCase()
					+ " -- takes a " + p.getType() + " argument");
		}
		pOut.println("For boolean arguments, use 0 for false and 1 for true.");
	}
}
