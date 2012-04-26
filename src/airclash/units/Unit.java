package airclash.units;

import java.util.HashSet;
import java.util.Set;

import net.phys2d.raw.Body;
import airclash.units.buildings.Module;

/**
 * Abstract base class for all units.
 * @author Andreas Textor
 */
public abstract class Unit implements IUnit {
	/** Determines if this unit is the one controlled by the player. */
	protected boolean selected = false;
	
	/** Set of all units. */
	private static final Set<Unit> UNITS = new HashSet<Unit>();
	
	/** Set of buildings. */
	private static final Set<Module> MODULES = new HashSet<Module>();
	
	/**
	 * Constructor. Adds this unit to the set of all available units.
	 */
	public Unit() {
		if (this instanceof Module) {
			MODULES.add((Module)this);
		} else {
			UNITS.add(this);
		}
	}
	
	/**
	 * Selects this unit and automatically unselects all other units.
	 */
	public void select() {
		for (Unit u : UNITS) {
			u.unselect();
		}
		for (Module b : MODULES) {
			b.unselect();
		}
		this.selected = true;
	}
	
	/**
	 * Callback method when another unit than this is selected.
	 */
	public void unselect() {
		this.selected = false;
	}
	
	/**
	 * Callback method that is called when this unit is disposed.
	 */
	public void delete() {
		if (this instanceof Module) {
			MODULES.remove(this);
		} else {
			UNITS.remove(this);
		}
	}
	
	/**
	 * Returns the set of all units.
	 * @return All units
	 */
	public static Set<Unit> getUnits() {
		return UNITS;
	}

	/**
	 * Returns the set of all buildings.
	 * @return All buildings
	 */
	public static Set<Module> getModules() {
		return MODULES;
	}

	/**
	 * Setup method: All settings of the unit, that are to be performed after
	 * the unit was added to the world. This method make sure that units are
	 * added to the collision exclude list of buildings and vice versa.
	 * This method may be overridden, but don't forget the super call then.
	 */
	public void setup() {
		for (Unit u : this instanceof Module ? Unit.getUnits() : Unit.getModules()) {
			if (this != u) {
				for (Body b1 : u.getBodyParts()) {
					for (Body b2 : getBodyParts()) {
						b1.addExcludedBody(b2);
					}
				}
			}
		}
	}
}
