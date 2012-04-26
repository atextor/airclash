package airclash.system;

import java.util.ArrayList;
import java.util.List;

import airclash.units.IUnit;

/**
 * A player has a name, a list of units and a currently selected unit.
 * 
 * @author Andreas Textor
 */
public class Player implements IPropertyListener {
	/** The list of units this player owns. */
	private List<IUnit> units;
	
	/** The player's name. */
	private String name;
	
	/** The index of the currently selected unit. */
	private int currentUnit = 0;
	
	/**
	 * Constructor.
	 * @param pName The name of the player
	 */
	public Player(final String pName) {
		this.name = pName;
		this.units = new ArrayList<IUnit>();
	}
	
	/**
	 * Returns the player's name.
	 * @return The name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Adds a unit to the player's fleet.
	 * @param pUnit The new unit.
	 */
	public void addUnit(final IUnit pUnit) {
		this.units.add(pUnit);
	}
	
	/**
	 * Returns the currently selected unit.
	 * @return The currently selected unit.
	 */
	public IUnit getSelectedUnit() {
		return this.units.get(this.currentUnit);
	}
	
	/**
	 * Selects the next unit of the fleet.
	 */
	public void selectNextUnit() {
		if (this.units.isEmpty()) {
			return;
		}
		
		this.currentUnit++;
		if (this.currentUnit >= this.units.size()) {
			this.currentUnit = 0;
		}
		final IUnit curUnit = getSelectedUnit();
		for (IUnit u : this.units) {
			if (u == curUnit) {
				u.select();
			} else {
				u.unselect();
			}
		}
	}
	
	/**
	 * Returns the list of units of this player.
	 * @return The list of units
	 */
	public List<IUnit> getUnits() {
		return this.units;
	}

	/**
	 * Property changed event: The player name was changed.
	 * @param pNewValue The new name
	 */
	public void changedEvent(final String pNewValue) {
		this.name = pNewValue;
	}
}
