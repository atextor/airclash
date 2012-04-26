/* 
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package airclash.test;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

/**
 * Tests switching between windowed and fullscreen.
 *
 * @author Brian Matzon (brian@matzon.dk)
 * @version $Revision$
 * $Id$
 */
public class FullScreenWindowedTest {
	/** Intended deiplay mode. */
	private DisplayMode			mode;
	/** our quad moving around. */
	private Vector2f			quadPosition;
	/** our quadVelocity. */
	private Vector2f			quadVelocity;
	/** angle of quad. */
	private float				angle;
	/** degrees to rotate per frame. */
	private float				angleRotation	= 1.0f;
	/** Max speed of all changable attributes. */
	private static final float	MAX_SPEED		= 20.0f;
	
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
			// find displaymode
			this.mode = findDisplayMode(800, 600, Display.getDisplayMode().getBitsPerPixel());
			// start of in windowed mode
			Display.create();
			glInit();
			this.quadPosition = new Vector2f(100f, 100f);
			this.quadVelocity = new Vector2f(1.0f, 1.0f);
		} catch (Exception e) {
			// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
			e.printStackTrace();
		}
	}
	
	/**
	 * Runs the main loop of the "test".
	 */
	private void mainLoop() {
		while (!Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) && !Display.isCloseRequested()) {
			if (Display.isVisible()) {
				// check keyboard input
				processKeyboard();
				// do "game" logic, and render it
				logic();
				render();
			} else {
				// no need to render/paint if nothing has changed (ie. window
				// dragged over)
				if (Display.isDirty()) {
					render();
				}
				// don't waste cpu time, sleep more
				try {
					Thread.sleep(100);
				} catch (InterruptedException inte) {
					// Do nothing
				}
			}
			// Update window
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
		if (this.quadPosition.x + 50 >= this.mode.getWidth() || this.quadPosition.x - 50 <= 0) {
			this.quadVelocity.x *= -1;
		}
		// check collision with horizontal border
		if (this.quadPosition.y + 50 >= this.mode.getHeight() || this.quadPosition.y - 50 <= 0) {
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
	}
	
	/**
	 * Processes keyboard input.
	 */
	private void processKeyboard() {
		// check for fullscreen key
		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			try {
				Display.setFullscreen(true);
			} catch (Exception e) {
				// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
				e.printStackTrace();
			}
		}
		// check for window key
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			try {
				Display.setFullscreen(false);
			} catch (Exception e) {
				// @PMD:REVIEWED:AvoidPrintStackTrace: by tex on 4/19/08 11:09 PM
				e.printStackTrace();
			}
		}
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
	}
	
	/**
	 * Cleans up the test.
	 */
	private void cleanup() {
//		Display.destroy();
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
			if (modes[i].getWidth() == pWidth
					&& modes[i].getHeight() == pHeight
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
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
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
		final FullScreenWindowedTest fswTest = new FullScreenWindowedTest();
		fswTest.execute();
		System.exit(0);
	}
}

