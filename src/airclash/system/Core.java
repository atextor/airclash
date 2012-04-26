package airclash.system;

import java.awt.Dimension;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.fenggui.render.lwjgl.EventHelper;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;

import airclash.system.exceptions.InvalidResourceException;
import airclash.system.gfx.Gui;
import airclash.system.gfx.Renderer;
import airclash.units.IUnit;
import airclash.units.Unit;
import airclash.units.buildings.HQ;
import airclash.units.buildings.Module;
import airclash.units.vehicles.Scout;
import airclash.world.Level;


/**
 * Main class.
 * @author Andreas Textor
 */
public class Core {
	/** The logger. */
	public static final Logger LOGGER = Logger.getLogger(Core.class);
	
	/** The properties object. This is used to configure all subsystems. */
	public static final Properties PROPERTIES = new Properties();
	
	/**
	 * The logger layout. This will look like:
	 * <code>15:10:30 INFO : some message</code>, followed by a linebreak.
	 * @see org.apache.log4j.PatternLayout
	 */
	private final PatternLayout loggerLayout =
		new PatternLayout("%d{HH:mm:ss} %-5p: %m%n");

	/** Intended display mode. */
	private DisplayMode mode;
	
	/** Desired frame time. */
	private static final int FRAMERATE = 50;

	/** The mouse button that was pressed. */
	private int lastButtonDown = -1;

	/** The Gui object. This is responsible for frames, buttons etc. */
	private Gui gui;
	
	/** The renderer that is used to display everything. */
	private Renderer renderer;

	/** The level where the action takes place. */
	private Level level;
	
	/** The size of the window. */
	private final Dimension windowSize = new Dimension(1024, 768);
	
	/** This is used to restrict the number of jumps of a vehicle per time. */
	private int jumpTimeout = 0;
	
	/** The player. */
	private Player player;
	
	/** View offset (level scrolling). */
	private int offsetX = 0;
	
	/** View offset (level scrolling). */
	private int offsetY = 0;
	
	/** Mouse mode: selection means placing a unit. */
	private boolean selectionMode = false;
	
	/**
	 * Static constructor. Fill the default properties.
	 */
	static {
		PROPERTIES.set(Properties.Prop.FULLSCREEN, "0");
		PROPERTIES.set(Properties.Prop.PLAYERNAME, "UnnamedPlayer");
		PROPERTIES.set(Properties.Prop.DRAWCONTACTS, "0");
	}
	
	/**
	 * Constructor.
	 * Initializes the logger.
	 */
	public Core() {
		BasicConfigurator.configure(new ConsoleAppender(this.loggerLayout));
	}

	/**
	 * Runs the program.
	 */
	public void execute() {
		initialize();
		mainLoop();
		cleanup();
	}
	
	/**
	 * Initializes the OpenGL output, constructs the GUI, creates a
	 * TextureLoader instance etc.
	 */
	private void initialize() {
		try {
			LOGGER.info("Setting video mode");
			this.mode = findDisplayMode(this.windowSize.width,
					this.windowSize.height,
					Display.getDisplayMode().getBitsPerPixel());
			Display.setDisplayMode(this.mode);
			Display.setTitle("Airclash");
			Display.create();
			
			LOGGER.info("Initializing OpenGL");
			initOpenGL();

			LOGGER.info("Setting up environment");
			// Loading info
			this.renderer = new Renderer(this);
			this.renderer.drawLoadingScreen();
			
			// Create other stuff
			Keyboard.create();
			this.gui = new Gui(this);
			// Output log messages also to the game console
			LOGGER.addAppender(new PrintStreamAppender(
					this.gui.getConsole().getOut(), this.loggerLayout));

			this.level = new Level();
			this.level.load("level1");
			
			this.player = new Player(PROPERTIES.getString(Properties.Prop.PLAYERNAME));
			this.player.addUnit(new Scout());
//			this.player.addUnit(new Drone());
			this.player.addUnit(new HQ(this.gui));
			
			// Register property listeners
			registerPropertyListeners();
			
			// Use the reset to initially place the units
			reset();
		} catch (Exception e) {
			LOGGER.error("Error while initializing system", e);
		}
	}
	
	/**
	 * Register listeners for system property changes.
	 */
	private void registerPropertyListeners() {
		PROPERTIES.registerListener(Properties.Prop.FULLSCREEN, new IPropertyListener() {
			public void changedEvent(final String pNewValue) {
				try {
					Display.setFullscreen("1".equals(pNewValue));
				} catch (Exception e) {
					LOGGER.error("Error while switching to fullscreen/window", e);
				}
			}
		});
		
		PROPERTIES.registerListener(Properties.Prop.PLAYERNAME, this.player);
	}

	/**
	 * Initializes OGL.
	 */
	private void initOpenGL() {
		// Go into orthographic projection mode.
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0, this.mode.getWidth(), 0, this.mode.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, this.mode.getWidth(), this.mode.getHeight());
		// set clear color to black
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// sync frame
		Display.setVSyncEnabled(true);
	}
	
	/**
	 * Resets the units and the level.
	 */
	public void reset() {
		this.level.init();
		final IUnit u1 = this.player.getUnits().get(0);
//		final IUnit u2 = this.player.getUnits().get(1);
		u1.setPosition(new Vector2f(330, 200));
//		u2.setPosition(new Vector2f(330, 300));
		this.level.addUnit(u1);
//		this.level.addUnit(u2);
		
		final IUnit hq = this.player.getUnits().get(1);
		hq.setPosition(new Vector2f(150, 200));
		this.level.addUnit(hq);
		
		u1.select();
	}
	
	/**
	 * Runs the main loop of the "test".
	 */
	private void mainLoop() {
		boolean finished = false;
		
		// Now is the right time to clean stuff up that piled up during
		// the loading process
		System.gc();
		LOGGER.info("Game start");
		while (!finished) {
//			try {
				readBufferedKeyboard();
				readBufferedMouse();
				Display.update();
				
				if (Display.isCloseRequested()) {
					finished = true;
				} else if (Display.isActive()) {
					logic();
					render();
					Display.sync(FRAMERATE);
				} else {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					logic();
	
					// Only bother rendering if the window is visible or dirty
					if (Display.isVisible() || Display.isDirty()) {
						render();
					}
				}
//			} catch (Exception e) {
//				LOGGER.warn("Exception occured: " + e.getClass().getSimpleName()
//						+ e.getMessage(), e);
//			}
		}
	}
	
	/**
	 * Game logic. This method is called every frame.
	 */
	private void logic() {
		this.level.step();
	}
	
	/**
	 * Renders the scene.
	 */
	private void render() {
		// clear background
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glLoadIdentity();
		this.renderer.drawBackground();
		
		// Scroll the view
		final ROVector2f pos = this.player.getSelectedUnit().getBodyParts().get(0).getPosition();
		this.offsetX = (int)-pos.getX() + this.getWindowSize().width / 2;
		this.offsetY = (int)(this.windowSize.getHeight() - pos.getY() - this.windowSize.height / 2);
		if (this.offsetY > 0) {
			this.offsetY = 0;
		}
		if (this.offsetX > 0) {
			this.offsetX = 0;
		}
		if (this.offsetX < -this.level.getDescription().getWidth() + this.windowSize.width) {
			this.offsetX = -this.level.getDescription().getWidth() + this.windowSize.width;
		}
		GL11.glTranslatef(this.offsetX, this.offsetY, 0);
		
		// Draw the world and units etc.
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.level.drawWorld(this.renderer);
		for (IUnit u : Unit.getModules()) {
			u.draw(this.renderer);
		}
		for (IUnit u : Unit.getUnits()) {
			u.draw(this.renderer);
		}
		this.level.drawOverlay(this.renderer);
		
		if (this.selectionMode) {
			this.renderer.drawSelection(Mouse.getX() - this.offsetX, Mouse.getY() - this.offsetY);
		}
		
		// Render GUI
		GL11.glLoadIdentity();
		this.gui.draw(this.renderer);
	}

	/**
	 * Toogles the console.
	 */
	private void toggleConsole() {
		final boolean visible = this.gui.getConsole().isVisible();
		this.gui.showConsole(!visible);
	}
	
	/**
	 * Read the keyboard.
	 */
	private void readBufferedKeyboard() {
		// Check for keys that are used for console and normal game
		if (this.gui.getConsole().isVisible()) {
			while (Keyboard.next()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_GRAVE) {
					if (!Keyboard.getEventKeyState()) {
						toggleConsole();
					}
				} else {
					if (Keyboard.getEventKeyState()) {
						this.gui.getDisplay().fireKeyPressedEvent(EventHelper.mapKeyChar(),
								EventHelper.mapEventKey());
						this.gui.getDisplay().fireKeyTypedEvent(EventHelper.mapKeyChar());
					} else {
						this.gui.getDisplay().fireKeyReleasedEvent(EventHelper.mapKeyChar(),
								EventHelper.mapEventKey());
					}
				}
			}
		} else {
			if (this.jumpTimeout > 0) {
				this.jumpTimeout--;
			}
			
			// Check for keys that are not supposed to be triggered repetitively
			while (Keyboard.next()) {
				switch (Keyboard.getEventKey()) {
				case Keyboard.KEY_ESCAPE:
					if (!Keyboard.getEventKeyState()) {
						if (this.selectionMode) {
							this.selectionMode = false;
						} else {
							cleanup();
							System.exit(0);
						}
					}
					break;
				case Keyboard.KEY_GRAVE:
					if (!Keyboard.getEventKeyState()) {
						toggleConsole();
						return;
					}
					break;
				case Keyboard.KEY_SPACE:
					if (this.jumpTimeout == 0) {
						this.player.getSelectedUnit().addForce(new Vector2f(0, 50000));
						this.jumpTimeout = 60;
					}
					break;
				case Keyboard.KEY_TAB:
					if (!Keyboard.getEventKeyState()) {
						this.player.selectNextUnit();
					}
					break;
				default:
					break;
				}
			}
			
			// Check for keys that can be hold down
			
			// Left and right make the wheels move
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				this.player.getSelectedUnit().move(IUnit.LEFT);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				this.player.getSelectedUnit().move(IUnit.RIGHT);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				this.player.getSelectedUnit().move(IUnit.UP);
			}
		}

		// check keys, buffered
		Keyboard.poll();
	}

	/**
	 * Creates a new hq module.
	 * @param pX The world x coordinate
	 * @param pY The world y coordinate
	 */
	private void createModule(final int pX, final int pY) {
		try {
			final HQ hq = (HQ)this.player.getSelectedUnit();
			final Module mod = new HQ(this.gui);
			final int x = pX / Module.GRIDSIZE
				- (int)hq.getBody().getPosition().getX() / Module.GRIDSIZE;
			final int y = pY / Module.GRIDSIZE
				- (int)hq.getBody().getPosition().getY() / Module.GRIDSIZE;
			if (hq.addModule(mod, x, y)) {
				this.level.addUnit(hq);
			} else {
				mod.delete();
			}
		} catch (InvalidResourceException e) {
			LOGGER.error("Placing module failed");
		}
	}

	/**
	 * reads a mouse in buffered mode.
	 */
	private void readBufferedMouse() {
		final int x = Mouse.getX();
		final int y = Mouse.getY();

		@SuppressWarnings("unused")
    boolean hitGUI = false;

		// @todo the click count is not considered in LWJGL! #
		
		if (this.selectionMode && Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
			this.selectionMode = false;
			createModule(x - this.offsetX, y - this.offsetY);
		}
		
		if (this.lastButtonDown != -1 && Mouse.isButtonDown(this.lastButtonDown)) {
			hitGUI |= this.gui.getDisplay().fireMouseDraggedEvent(x, y, EventHelper
					.getMouseButton(this.lastButtonDown));
		} else {
			if (Mouse.getDX() != 0 || Mouse.getDY() != 0) {
				hitGUI |= this.gui.getDisplay().fireMouseMovedEvent(x, y);
			}

			if (this.lastButtonDown != -1) {
				hitGUI |= this.gui.getDisplay().fireMouseReleasedEvent(x, y, EventHelper
						.getMouseButton(this.lastButtonDown), 1);
				this.lastButtonDown = -1;
			}
			while (Mouse.next()) {
				if (Mouse.getEventButton() != -1 && Mouse.getEventButtonState()) {
					this.lastButtonDown = Mouse.getEventButton();
					hitGUI |= this.gui.getDisplay().fireMousePressedEvent(x, y, EventHelper
							.getMouseButton(this.lastButtonDown), 1);
				}
				final int wheel = Mouse.getEventDWheel();
				if (wheel != 0) {
					hitGUI |= this.gui.getDisplay().fireMouseWheel(x, y, wheel > 0, 1);
				}
			}
		}
	}
	
	/**
	 * Cleans up the test.
	 */
	private void cleanup() {
		Display.destroy();
	}
	
	/**
	 * Retrieves a displaymode, if one such is available.
	 * 
	 * @param pWidth Required width
	 * @param pHeight Required height
	 * @param pBpp Minimum required bits per pixel
	 * @return The displaymode
	 * @throws LWJGLException The display mode could not be set
	 */
	private DisplayMode findDisplayMode(final int pWidth, final int pHeight,
			final int pBpp) throws LWJGLException {
		final DisplayMode[] modes = Display.getAvailableDisplayModes();
		for (int i = 0; i < modes.length; i++) {
			if (modes[i].getWidth() == pWidth && modes[i].getHeight() == pHeight
					&& modes[i].getBitsPerPixel() >= pBpp
					&& modes[i].getFrequency() <= 60) {
				try {
					Display.setDisplayMode(modes[i]);
				} catch (LWJGLException e) {
					LOGGER.error("Error while fetching display modes", e);
				}
				return modes[i];
			}
		}
		return Display.getDisplayMode();
	}
	
	/**
	 * Turns on placing of a unit.
	 */
	public void gridSelect() {
		this.selectionMode = true;
	}
	
	/**
	 * Returns the renderer object.
	 * @return The renderer.
	 */
	public Renderer getRenderer() {
		return this.renderer;
	}

	/**
	 * Returns the window dimension.
	 * @return The size of the window
	 */
	public Dimension getWindowSize() {
		return this.windowSize;
	}

	/**
	 * Returns the level.
	 * @return The level
	 */
	public Level getLevel() {
		return this.level;
	}

}
