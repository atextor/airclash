package airclash.system.console;

import org.fenggui.render.Font;
import org.fenggui.util.Alphabet;
import org.fenggui.util.fonttoolkit.FontFactory;

import airclash.system.Core;
import airclash.system.Main;
import airclash.system.exceptions.InvalidResourceException;
import airclash.system.gfx.Texture;
import airclash.system.gfx.TextureLoader;

/**
 * Game Console.
 * @author Andreas Textor
 */
public class Console extends org.fenggui.console.Console {
	/** The background texture. */
	private Texture background;
	
	/** The appearance for this console. */
	private ConsoleAppearance appearance = null;
	
	/**
	 * Constructor. This creates the console and registers the available commands.
	 * @param pCore The core reference
	 * @throws InvalidResourceException Thrown when loading of a texture failed.
	 */
	public Console(final Core pCore) throws InvalidResourceException {
		super();
		
		final Font font = FontFactory.renderStandardFont(
				new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 14),
				true, Alphabet.getDefaultAlphabet());
		getAppearance().getTextRenderer().setText(
				"---------------------------------------------------\n"
				+ "Airclash Console (Version " + Main.VERSION + ")\n"
				+ "---------------------------------------------------\n");
		getAppearance().setFont(font);

		// Register commands
		removeAll();
		for (Object key : Core.PROPERTIES.keySet()) {
			final String s = (String)key;
			add(new PropertyReadCommand(s.toLowerCase()));
		}
		add(new PropertySetCommand());
		add(new HelpCommand());
		add(new LevelInfoCommand(pCore));
		
		this.background = TextureLoader.getInstance().getTexture("console");
	}
	
	/**
	 * This is executed when enter is pressed. This checks if the text entered
	 * is a valid command with parameters, and runs the command.
	 * @param pCommandLine The string that was entered
	 */
	@Override
	public void run(final String pCommandLine) {
		getOut().println(PROMPT + pCommandLine);
		final String[] split = pCommandLine.split(" ");

		final AbstractCommand command = (AbstractCommand)getCommand(split[0]);

		if (command == null) {
			getOut().println("Command \"" + split[0] + "\" not recognized!");
		} else {
			if (command.validateParams(split)) {
				command.execute(getOut(), this, split);
			} else {
				getOut().println("Invalid parameters.");
				command.getHelp(getOut());
			}
		}
	}
	
	/**
	 * Returns the custom console appearance.
	 * @return The appearance for this console
	 */
	@Override
	public ConsoleAppearance getAppearance() {
		if (this.appearance == null) {
			this.appearance = new ConsoleAppearance(this, super.getAppearance());
		}
		return this.appearance;
	}
	
	/**
	 * Returns the background texture.
	 * @return The background
	 */
	public Texture getBackground() {
		return this.background;
	}
}
