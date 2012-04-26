package airclash.system.gfx;

import net.phys2d.math.ROVector2f;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import net.phys2d.raw.shapes.Line;
import net.phys2d.raw.shapes.Polygon;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import airclash.system.Core;
import airclash.units.buildings.Module;

/**
 * This class is reponsible for displaying graphics onto the window.
 * @author Andreas Textor
 */
public class Renderer {
	/** The Core object reference. */
	private Core core;
	
	/**
	 * Constructor. This is intended to be called from the core object.
	 * @param pCore The core object reference.
	 */
	public Renderer(final Core pCore) {
		this.core = pCore;
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glColorMask(true, true, true, true);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
//	/**
//	 * Draw a body.
//	 * 
//	 * @param pBody The body to be drawn
//	 */
//	public void drawBody(final Body pBody) {
//		if (pBody.getShape() instanceof Box) {
//			drawBoxBody(pBody, (Box)pBody.getShape());
//		}
//		if (pBody.getShape() instanceof Circle) {
//			drawCircleBody(pBody, (Circle)pBody.getShape());
//		}
//		if (pBody.getShape() instanceof Line) {
//			drawLineBody(pBody, (Line)pBody.getShape());
//		}
//		if (pBody.getShape() instanceof Polygon) {
//			drawPolygonBody(pBody, (Polygon)pBody.getShape());
//		}
//	}
	
	/**
	 * Draws a box body.
	 * @param pBody The body
	 * @param pBox The box
	 * @param pTex The texture for the block
	 */
	public void drawBoxBody(final Body pBody, final Box pBox, final Texture pTex) {
		final Vector2f[] pts = pBox.getPoints(pBody.getPosition(), pBody.getRotation());
		
		final Vector2f v1 = pts[0];
		final Vector2f v2 = pts[1];
		final Vector2f v3 = pts[2];
		final Vector2f v4 = pts[3];
		
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		pTex.bind();
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(0, pTex.getHeight());
		GL11.glVertex2f((int)v1.x, (int)v1.y);
		GL11.glTexCoord2f(pTex.getWidth(), pTex.getHeight());
		GL11.glVertex2f((int)v2.x, (int)v2.y);
		GL11.glTexCoord2f(pTex.getWidth(), 0);
		GL11.glVertex2f((int)v3.x, (int)v3.y);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f((int)v4.x, (int)v4.y);
		GL11.glEnd();
	}
	
	/**
	 * Draw a line.
	 * 
	 * @param pBody The body describing the line's position
	 * @param pLine The line to be drawn
	 */
	public void drawLineBody(final Body pBody, final Line pLine) {
		final Vector2f[] verts = pLine.getVertices(pBody.getPosition(), pBody.getRotation());
		
		GL11.glLineWidth(1.0f);
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL11.GL_LINE_LOOP);
		GL11.glVertex2i((int)verts[0].getX(), (int)verts[0].getY());
		GL11.glVertex2i((int)verts[1].getX(), (int)verts[1].getY());
		GL11.glEnd();
	}
		
	/**
	 * Draw a polygon.
	 * 
	 * @param pBody The body describing the poly's position
	 * @param pPoly The poly to be drawn
	 * @param pTex The texture to use
	 */
	public void drawPolygonBody(final Body pBody, final Polygon pPoly,
			final Texture pTex) {
		final ROVector2f[] verts = pPoly.getVertices(pBody.getPosition(), pBody.getRotation());
		GL11.glColor3f(1.0f, 1.0f, 1.0f);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		pTex.bind();
		
		GL11.glBegin(GL11.GL_POLYGON);
		for (int i = 0; i < verts.length; i++) {
			final int x = (int)(0.5f + verts[i].getX());
			final int y = (int)(0.5f + verts[i].getY());
			GL11.glTexCoord2f(x / (float)pTex.getImageWidth(), y / (float)pTex.getImageHeight());
			GL11.glVertex2i(x, y);
		}
		GL11.glEnd();
	}
	
	/**
	 * Draw a circle in the world.
	 * 
	 * @param pBody The body to be drawn
	 * @param pCircle The shape to be drawn
	 */
	public void drawCircleBody(final Body pBody, final Circle pCircle) {
		final int numSegments = (int)pCircle.getRadius() / 10 + 10;
		GL11.glColor3f(0, 0, 0);
		GL11.glDisable(GL11.GL_BLEND);
    	GL11.glBegin(GL11.GL_POLYGON);
    	for (float angle = 0; angle <= 2 * Math.PI; angle += Math.PI / numSegments) {
    		final float x = (float)(pCircle.getRadius() * 2 * Math.cos(angle) / 2);
    		final float y = (float)(pCircle.getRadius() * 2 * Math.sin(angle) / 2);
    		GL11.glVertex2f(x + pBody.getPosition().getX(), y + pBody.getPosition().getY());
    	}
    	GL11.glEnd();
	}
	
	
	/**
	 * Draw a specific contact point determined from the simulation.
	 * 
	 * @param pContact The contact to draw
	 */
	public void drawContact(final Contact pContact) {
		final int x = (int)pContact.getPosition().getX();
		final int y = (int)pContact.getPosition().getY();
		drawSimpleBox(x, y, 4, 4, 1.0f, 1.0f, 0.0f, 1.0f);
	}
	
	/**
	 * Draws a selection box.
	 * @param pX The x coordinate
	 * @param pY The y coordinate
	 */
	public void drawSelection(final int pX, final int pY) {
		drawSimpleBox(pX, pY, Module.GRIDSIZE, Module.GRIDSIZE, 1.0f, 1.0f, 1.0f, 0.5f);
	}

	/**
	 * Draws a simple filled box in a given color and size at a given position.
	 * @param pX The x coordinate
	 * @param pY The y coordinate
	 * @param pWidth The width
	 * @param pHeight The height
	 * @param pR red (0.0f - 1.0f)
	 * @param pG green (0.0f - 1.0f)
	 * @param pB blue (0.0f - 1.0f)
	 * @param pA alpha (1.0f = opaque, 0.0f = translucent)
	 */
	public void drawSimpleBox(final int pX, final int pY, final int pWidth,
			final int pHeight, final float pR, final float pG, final float pB,
			final float pA) {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(pR, pG, pB, pA);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex2f(pX - pWidth / 2, pY + pHeight / 2);
		GL11.glVertex2f(pX + pWidth / 2, pY + pHeight / 2);
		GL11.glVertex2f(pX + pWidth / 2, pY - pHeight / 2);
		GL11.glVertex2f(pX - pWidth / 2, pY - pHeight / 2);
		GL11.glEnd();
	}

	/**
	 * Displays the loading screen while the other systems are initialized.
	 */
	public void drawLoadingScreen() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glLoadIdentity();
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		final Sprite s = new Sprite("loading");
		s.draw(this.core.getWindowSize().width / 2 - s.getWidth() / 2,
				this.core.getWindowSize().height / 2 - s.getHeight() / 2);
		Display.update();
	}
	
	/**
	 * Draws an image at a position, stretched to a certain size.
	 * @param pTex The texture
	 * @param pX The x coordinate
	 * @param pY The y coordinate
	 * @param pWidth The width
	 * @param pHeight The height
	 */
	public void drawImage(final Texture pTex, final int pX, final int pY,
			final int pWidth, final int pHeight) {
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		pTex.bind();
		GL11.glColor3f(1, 1, 1);
		GL11.glPushMatrix();
		GL11.glTranslatef(pX, pY, 0);

		// draw a quad textured to match the sprite
		GL11.glBegin(GL11.GL_QUADS);
		
		GL11.glTexCoord2f(0, pTex.getHeight());
		GL11.glVertex2f(0, 0);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex2f(0, pHeight);
		GL11.glTexCoord2f(pTex.getWidth(), 0);
		GL11.glVertex2f(pWidth, pHeight);
		GL11.glTexCoord2f(pTex.getWidth(), pTex.getHeight());
		GL11.glVertex2f(pWidth, 0);
		GL11.glEnd();
		
		GL11.glPopMatrix();
	}
	
	/**
	 * Draws an image at a given position in its natural size.
	 * @param pTex The texture
	 * @param pX The x coordinate
	 * @param pY The y coordinate
	 */
	public void drawImage(final Texture pTex, final int pX, final int pY) {
		drawImage(pTex, pX, pY, pTex.getImageWidth(), pTex.getImageHeight());
	}

	/**
	 * Draws the level background.
	 */
	public void drawBackground() {
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor3f(1, 1, 1);
	
		// draw a quad textured to match the sprite
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor3f(0.17f, 0.184f, 0.54f);
		GL11.glVertex2f(0, 0);
		GL11.glColor3f(1, 1, 1);
		GL11.glVertex2f(0, this.core.getWindowSize().width);
		GL11.glVertex2f(this.core.getWindowSize().width,
				this.core.getWindowSize().height);
		GL11.glColor3f(0.17f, 0.184f, 0.54f);
		GL11.glVertex2f(this.core.getWindowSize().width, 0);
		GL11.glEnd();
	}
}
