package airclash.system;

/**
 * Main class that is used to start the program.
 * @author Andreas Textor
 */
public class Main {
	/** The version of the game. */
	public static final String VERSION = "0.1";
	
	/**
	 * Starts the program.
	 * @param pArgs The commandline arguments
	 */
	public static void main(final String[] pArgs) {
		final Core core = new Core();
		core.execute();
		System.exit(0);
	}
}
