package airclash.units.vehicles;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import airclash.units.Unit;
import airclash.units.IVehicle;

/**
 * Abstract base class for all vehicles (= units that have wheels and don't fly).
 * @author Andreas Textor
 */
public abstract class AbstractVehicle extends Unit implements IVehicle {
	/**
	 * The power of the engine. This determines the x component of the force
	 * vector that is applied to the wheel when moving.
	 */
	private int enginePower = 1;
	
	/**
	 * Set the power of the engine. This determines the x component of the force
	 * vector that is applied to the wheel when moving.
	 * @param pPower The new power
	 */
	public void setEnginePower(final int pPower) {
		this.enginePower = pPower;
	}
	
	/**
	 * Takes a force and applies the force to each wheel
	 * that touches another Body.
	 * @param pForce The force
	 */
	protected void addWheelForce(final Vector2f pForce) {
		if (pForce == UP) {
			return;
		}
		final Vector2f force = new Vector2f(pForce.x * this.enginePower, pForce.y);
		for (Body w : getWheels()) {
			if (w.getTouching().size() > 0) {
				w.addForce(force);
			}
		}
	}
}
