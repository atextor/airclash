package airclash.test;

import org.fenggui.FengGUI;
import org.fenggui.composites.Window;
import org.fenggui.render.lwjgl.EventHelper;
import org.fenggui.render.lwjgl.LWJGLBinding;
import org.fenggui.theme.ITheme;
import org.fenggui.theme.XMLTheme;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

/**
 * LWJGL Sample with FengGUI integrated. 
 * 
 * @author Brian Matzon (brian@matzon.dk)
 * @author Andreas Textor
 */
public class GuiTest {
	/** Intended display mode. */
	private DisplayMode mode;
	
	/** our quad moving around. */
	private Vector2f quadPosition;
	
	/** our quadVelocity. */
	private Vector2f quadVelocity;
	
	/** angle of quad. */
	private float angle;
	
	/** degrees to rotate per frame. */
	private float angleRotation = 1.0f;
	
	/** Max speed of all changable attributes. */
	private static final float MAX_SPEED = 20.0f;

	/** The FengGUI display. */
	private org.fenggui.Display desk = null;
	
	/** The mouse button that was pressed. */
	private int lastButtonDown = -1;

	/** The Gui frame. */
	private Window frame = null;

	/**
	 * Executes the test.
	 */
	public void execute() {
		initialize();
		mainLoop();
		cleanup();
	}

	/**
	 * Initializes the test.
	 */
	private void initialize() {
		try {
			 this.mode = findDisplayMode(1024, 768, Display.getDisplayMode().getBitsPerPixel());
			Display.setDisplayMode(this.mode);
			Display.setTitle("Test");
			Display.create();
			
			glInit();
			this.quadPosition = new Vector2f(100f, 100f);
			this.quadVelocity = new Vector2f(1.0f, 1.0f);

			// initialize keyboard
			Keyboard.create();

			// build the gui
			buildGUI();
		} catch (Exception e) {
			// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
			e.printStackTrace();
		}
	}

	/**
	 * Read the keyboard.
	 */
	private void readBufferedKeyboard() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			cleanup();
			System.exit(0);
		}
		
//		// check for fullscreen key
//		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
//			try {
//				Display.setFullscreen(true);
//			} catch (Exception e) {
//				// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
//				e.printStackTrace();
//			}
//		}
//		// check for window key
//		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
//			try {
//				Display.setFullscreen(false);
//			} catch (Exception e) {
//				// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
//				e.printStackTrace();
//			}
//		}
		// check for speed changes
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			this.quadVelocity.y += 0.1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			this.quadVelocity.y -= 0.1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			this.quadVelocity.x += 0.1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			this.quadVelocity.x -= 0.1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_ADD)) {
			this.angleRotation += 0.1f;
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SUBTRACT)) {
			this.angleRotation -= 0.1f;
		}
		// throttle
		if (this.quadVelocity.x < -MAX_SPEED) {
			this.quadVelocity.x = -MAX_SPEED;
		}
		if (this.quadVelocity.x > MAX_SPEED) {
			this.quadVelocity.x = MAX_SPEED;
		}
		if (this.quadVelocity.y < -MAX_SPEED) {
			this.quadVelocity.y = -MAX_SPEED;
		}
		if (this.quadVelocity.y > MAX_SPEED) {
			this.quadVelocity.y = MAX_SPEED;
		}
		if (this.angleRotation < 0.0f) {
			this.angleRotation = 0.0f;
		}
		if (this.angleRotation > MAX_SPEED) {
			this.angleRotation = MAX_SPEED;
		}

		// check keys, buffered
		Keyboard.poll();

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				this.desk.fireKeyPressedEvent(EventHelper.mapKeyChar(), EventHelper
						.mapEventKey());
				this.desk.fireKeyTypedEvent(EventHelper.mapKeyChar());
			} else {
				this.desk.fireKeyReleasedEvent(EventHelper.mapKeyChar(), EventHelper
						.mapEventKey());
			}
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

		if (this.lastButtonDown != -1 && Mouse.isButtonDown(this.lastButtonDown)) {
			hitGUI |= this.desk.fireMouseDraggedEvent(x, y, EventHelper
					.getMouseButton(this.lastButtonDown));
		} else {
			if (Mouse.getDX() != 0 || Mouse.getDY() != 0) {
				hitGUI |= this.desk.fireMouseMovedEvent(x, y);
			}

			if (this.lastButtonDown != -1) {
				hitGUI |= this.desk.fireMouseReleasedEvent(x, y, EventHelper
						.getMouseButton(this.lastButtonDown), 1);
				this.lastButtonDown = -1;
			}
			while (Mouse.next()) {
				if (Mouse.getEventButton() != -1 && Mouse.getEventButtonState()) {
					this.lastButtonDown = Mouse.getEventButton();
					hitGUI |= this.desk.fireMousePressedEvent(x, y, EventHelper
							.getMouseButton(this.lastButtonDown), 1);
				}
				final int wheel = Mouse.getEventDWheel();
				if (wheel != 0) {
					hitGUI |= this.desk.fireMouseWheel(x, y, wheel > 0, 1);
				}
			}
		}
	}

	/**
	 * Sets up the Gui.
	 */
	public void buildGUI() {
		// init. the LWJGL Binding
		final LWJGLBinding binding = new LWJGLBinding();

		try {
			final ITheme qtCurve = new XMLTheme("data/themes/QtCurve/QtCurve.xml");
			FengGUI.setTheme(qtCurve);
		} catch (Exception e) {
			// @PMD:REVIEWED:SystemPrintln: by tex on 4/19/08 10:35 PM
			System.out.println("Could not load theme");
			// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
			e.printStackTrace();
		}

		// init the root Widget, that spans the whole screen
		this.desk = new org.fenggui.Display(binding);

		this.frame = new Window(true, false, false, true);
		this.desk.addWidget(this.frame);
		this.frame.setX(50);
		this.frame.setY(50);
		this.frame.setSize(300, 100);
		this.frame.setTitle("asdf");
		this.frame.setExpandable(false);
		this.frame.setResizable(false);
	}

	/**
	 * Runs the main loop of the "test".
	 */
	private void mainLoop() {
		while (!Display.isCloseRequested()) {

			readBufferedKeyboard();
			readBufferedMouse();
			logic();

			render();
			Display.update();
		}
	}

	/**
	 * Performs the logic.
	 */
	private void logic() {
		this.angle += this.angleRotation;
		if (this.angle > 90.0f) {
			this.angle = 0.0f;
		}
		this.quadPosition.x += this.quadVelocity.x;
		this.quadPosition.y += this.quadVelocity.y;
		// check colision with vertical border border
		if (this.quadPosition.x + 50 >= this.mode.getWidth()
				|| this.quadPosition.x - 50 <= 0) {
			this.quadVelocity.x *= -1;
		}
		// check collision with horizontal border
		if (this.quadPosition.y + 50 >= this.mode.getHeight()
				|| this.quadPosition.y - 50 <= 0) {
			this.quadVelocity.y *= -1;
		}
	}

	/**
	 * Renders the scene.
	 */
	private void render() {
		// clear background
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		// draw white quad
		GL11.glLoadIdentity();
		GL11.glPushMatrix();

		GL11.glTranslatef(this.quadPosition.x, this.quadPosition.y, 0);
		GL11.glRotatef(this.angle, 0.0f, 0.0f, 1.0f);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);

		GL11.glVertex2i(-50, -50);
		GL11.glVertex2i(50, -50);
		GL11.glVertex2i(50, 50);
		GL11.glVertex2i(-50, 50);

		GL11.glEnd();

		GL11.glPopMatrix();

		// Render GUI
		GL11.glLoadIdentity();
		GLU.gluLookAt(10, 8, 8, 0, 0, 0, 0, 0, 1);
		this.desk.display();
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
	 * @throws LWJGLException  The display mode could not be set
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
					// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
					e.printStackTrace();
				}
				return modes[i];
			}
		}
		return Display.getDisplayMode();
	}

	/**
	 * Initializes OGL.
	 */
	private void glInit() {
		// Go into orthographic projection mode.
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluOrtho2D(0, this.mode.getWidth(), 0, this.mode.getHeight());
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glViewport(0, 0, this.mode.getWidth(), this.mode.getHeight());
		// set clear color to black
		GL11.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		// sync frame (only works on windows)
		Display.setVSyncEnabled(true);
	}

	/**
	 * Test entry point.
	 * @param pArgs The commandline arguments
	 */
	public static void main(final String[] pArgs) {
		// @PMD:REVIEWED:SystemPrintln: by tex on 4/19/08 10:35 PM
		System.out.println("Change between fullscreen and windowed mode, by pressing "
						+ "F and W respectively");
		// @PMD:REVIEWED:SystemPrintln: by tex on 4/19/08 10:35 PM
		System.out.println("Move quad using arrowkeys, and change rotation using +/-");
		final GuiTest fswTest = new GuiTest();
		fswTest.execute();
		System.exit(0);
	}
}
