package airclash.units;

import net.phys2d.raw.Body;

/**
 * This interface determines units that are vehicles (= units that have wheels
 * and don't fly).
 * @author Andreas Textor
 */
public interface IVehicle extends IUnit {
	/**
	 * Returns an array of wheels. These are Bodies that apply force to the
	 * unit on movement only if they touch the floor.
	 * @return The array of wheels
	 */
	Body[] getWheels();
}
