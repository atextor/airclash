package airclash.system.gfx;

import org.fenggui.Button;
import org.fenggui.Display;
import org.fenggui.FengGUI;
import org.fenggui.composites.Window;
import org.fenggui.event.ButtonPressedEvent;
import org.fenggui.event.IButtonPressedListener;
import org.fenggui.event.mouse.MouseButton;
import org.fenggui.render.lwjgl.LWJGLBinding;
import org.fenggui.theme.ITheme;
import org.fenggui.theme.XMLTheme;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;

import airclash.system.Core;
import airclash.system.console.Console;
import airclash.system.exceptions.InvalidResourceException;

/**
 * This class is responsible for building and maintaining the GUI, e.g.
 * Frames, Buttons etc.
 * 
 * @author Andreas Textor
 */
public class Gui {
	/** The height of the console. */
	private final int consoleHeight;
	
	/** The reference to the game core. */
	private Core core;
	
	/** The FengGUI display. */
	private Display display = null;
	
	/** The Gui frame. */
	private Window frame = null;
	
	/** The game console. */
	private Console console = null;
	
	/**
	 * Constructor.
	 * @param pCore The core object.
	 * @throws InvalidResourceException Thrown when loading of a texture failed.
	 */
	public Gui(final Core pCore) throws InvalidResourceException {
		this.core = pCore;
		this.consoleHeight = this.core.getWindowSize().height / 2;
		
		final LWJGLBinding binding = new LWJGLBinding();

		// init the root Widget, that spans the whole screen
		this.display = new Display(binding);

		try {
			final ITheme qtCurve = new XMLTheme("data/themes/QtCurve/QtCurve.xml");
			FengGUI.setTheme(qtCurve);
		} catch (Exception e) {
			Core.LOGGER.error("Could not load theme", e);
		}

		this.frame = new Window(true, false, false, false);
		this.display.addWidget(this.frame);
		this.frame.setX(650);
		this.frame.setY(550);
		this.frame.setSize(300, 100);
		this.frame.setTitle("Control");
		this.frame.setExpandable(false);
		this.frame.setResizable(false);
		
		this.console = new Console(pCore);
		this.display.addWidget(this.console);
		this.console.setXY(10, 0);
		this.console.setSize(this.core.getWindowSize().width - 10, this.consoleHeight);
		this.console.setVisible(false);
		
		final Button restart = new GuiButton("blank");
		restart.addButtonPressedListener(new IButtonPressedListener() {
			@SuppressWarnings("synthetic-access")
			public void buttonPressed(final ButtonPressedEvent pEvent) {
				Gui.this.core.gridSelect();
			}
		});
		this.frame.getContentContainer().addWidget(restart);
//		this.frame.pack();
	}
	
	/**
	 * Sets the main menu visible or invisible.
	 * @param pVisible Visibility
	 */
	public void setMainMenuVisible(final boolean pVisible) {
		this.frame.setVisible(pVisible);
	}
	
	/**
	 * Returns the GUI display.
	 * @return The display.
	 */
	public Display getDisplay() {
		return this.display;
	}
	
	/**
	 * Returns the game console.
	 * @return The game console
	 */
	public Console getConsole() {
		return this.console;
	}
	
	/**
	 * Hides or shows the console.
	 * @param pShow true to show, false to hide
	 */
	public void showConsole(final boolean pShow) {
		this.console.setY(pShow ? this.core.getWindowSize().height - this.consoleHeight : 0);
		this.console.setVisible(pShow);
		if (pShow) {
			this.display.fireMousePressedEvent(65,
					this.core.getWindowSize().height - this.consoleHeight + 10,
					MouseButton.LEFT, 1);
			this.display.fireMouseReleasedEvent(65,
					this.core.getWindowSize().height - this.consoleHeight + 10,
					MouseButton.LEFT, 1);
		}
	}
	
	/**
	 * Draws the gui components.
	 * @param pRenderer The renderer to draw on
	 */
	public void draw(final Renderer pRenderer) {
		if (this.console.isVisible()) {
			pRenderer.drawImage(this.console.getBackground(),
					0, this.core.getWindowSize().height - this.consoleHeight,
					this.core.getWindowSize().width, this.core.getWindowSize().height / 2);
		}
		GL11.glLoadIdentity();
		GLU.gluLookAt(10, 8, 8, 0, 0, 0, 0, 0, 1);
		this.display.display();
	}
}
