package airclash.world;

import net.phys2d.math.Vector2f;
import airclash.system.xml.IResourceDescription;

/**
 * This class is a blueprint of a level. This is the data that is loaded and is
 * used to construct a level instance from.
 * 
 * @author Andreas Textor
 */
public class LevelDescription implements IResourceDescription {
	/**
	 * The geometry of the level. This consists of a list of points (2D vectors)
	 * that define the surface of the level floor.
	 */
	private Vector2f[] geometry;
	
	/** The width of the level in pixel. */
	private int width;
	
	/** The name of the level. */
	private String name;

	/**
	 * Returns the geometry of the level.
	 * @return The geometry
	 */
	public Vector2f[] getGeometry() {
		return this.geometry;
	}

	/**
	 * Sets the new geometry.
	 * @param pGeometry The new geometry
	 */
	public void setGeometry(final Vector2f[] pGeometry) {
		this.geometry = pGeometry;
	}

	/**
	 * Returns the name of the level.
	 * @return The name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the new name.
	 * @param pName The name.
	 */
	public void setName(final String pName) {
		this.name = pName;
	}

	/**
	 * Returns the width of the level in pixel.
	 * @return The width
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Sets the width of the level.
	 * @param pWidth The width
	 */
	public void setWidth(final int pWidth) {
		this.width = pWidth;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result = "Name: " + this.name + "\nGeometry: ";
		int count = 0;
		for (Vector2f v : this.geometry) {
			result += v + " ";
			count++;
			if (count == 4) {
				result += "\n";
				count = 0;
			}
		}
		return result;
	}
	
}
