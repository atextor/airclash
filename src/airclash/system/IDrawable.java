package airclash.system;

import airclash.system.gfx.Renderer;

/**
 * Determines a drawable object.
 * @author Andreas Textor
 */
public interface IDrawable {
	/**
	 * Draws the object using the renderer.
	 * @param pRenderer The renderer
	 */
	void draw(Renderer pRenderer);
}
