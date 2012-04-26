package airclash.units;

import net.phys2d.raw.Body;

/**
 * Interface that the building module classes must implement.
 * @author Andreas Textor
 */
public interface IModule extends IUnit {
	/**
	 * Returns the width of this module in grid units.
	 * @return The width
	 */
	int getGridWidth();
	
	/**
	 * Returns the height of this module in grid units.
	 * @return The height
	 */
	int getGridHeight();
	
	/**
	 * Returns the body element of this module.
	 * @return The body
	 */
	Body getBody();
}
