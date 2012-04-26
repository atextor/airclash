package airclash.units.buildings;

import net.phys2d.raw.Body;
import airclash.system.gfx.Texture;
import airclash.units.IModule;
import airclash.units.Unit;

/**
 * Abstract base class for buildings.
 * @author Andreas Textor
 */
public abstract class Module extends Unit implements IModule {
	/** The size of a grid unit in pixels. */
	public static final int GRIDSIZE = 64;
	
	/** The body of this module. */
	protected Body body;
	
	/** The texture of the building module. */
	protected Texture texture;
	
	/**
	 * {@inheritDoc}
	 */
	public Body getBody() {
		return this.body;
	}
}

