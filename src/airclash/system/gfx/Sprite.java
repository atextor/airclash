package airclash.system.gfx;

import org.lwjgl.opengl.GL11;

import airclash.system.Core;
import airclash.system.exceptions.InvalidResourceException;

/**
 * Implementation of sprite that uses an OpenGL quad and a texture
 * to render a given image to the screen.
 * 
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class Sprite {
	/** The texture that stores the image for this sprite. */
	private Texture texture;

	/** The width in pixels of this sprite. */
	private int width;

	/** The height in pixels of this sprite. */
	private int height;

	/**
	 * Create a new sprite from a specified image.
	 * 
	 * @param pRef A reference to the image on which this sprite should be based
	 */
	public Sprite(final String pRef) {
		try {
			this.texture = TextureLoader.getInstance().getTexture(pRef);

			this.width = this.texture.getImageWidth();
			this.height = this.texture.getImageHeight();
		} catch (InvalidResourceException e) {
			// a tad abrupt, but our purposes if you can't find a
			// sprite's image you might as well give up.
			Core.LOGGER.error("Unable to load texture: " + pRef, e);
			System.exit(0);
		}
	}

	/**
	 * Get the width of this sprite in pixels.
	 * 
	 * @return The width of this sprite in pixels
	 */
	public int getWidth() {
		return this.texture.getImageWidth();
	}

	/**
	 * Get the height of this sprite in pixels.
	 * 
	 * @return The height of this sprite in pixels
	 */
	public int getHeight() {
		return this.texture.getImageHeight();
	}

	/**
	 * Draw the sprite at the specified location.
	 * 
	 * @param pX The x location at which to draw this sprite
	 * @param pY The y location at which to draw this sprite
	 */
	public void draw(final int pX, final int pY) {
		// store the current model matrix
		GL11.glPushMatrix();

		// bind to the appropriate texture for this sprite
		this.texture.bind();

		// translate to the right location and prepare to draw
		GL11.glTranslatef(pX, pY, 0);
		GL11.glColor3f(1, 1, 1);

		// draw a quad textured to match the sprite
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(0, this.texture.getHeight());
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0, this.height);
		GL11.glTexCoord2f(this.texture.getWidth(), 0);
		GL11.glVertex2f(this.width, this.height);
		GL11.glTexCoord2f(this.texture.getWidth(), this.texture.getHeight());
		GL11.glVertex2f(this.width, 0);
		GL11.glEnd();

		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}

}
