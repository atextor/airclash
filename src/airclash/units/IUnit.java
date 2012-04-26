package airclash.units;

import java.util.List;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Joint;
import airclash.system.IDrawable;

/**
 * This interface represents a unit. This may be a vehicle or a plane,
 * or even a building.
 * 
 * @author Andreas Textor
 */
public interface IUnit extends IDrawable {
	/** The vector indicating movement to the left. */
	Vector2f LEFT = new Vector2f(-1, 0);
	
	/** The vector indicating movement to the right. */
	Vector2f RIGHT = new Vector2f(1, 0);
	
	/** The vector indicating movement upwards. */
	Vector2f UP = new Vector2f(0, 1);
	
	/**
	 * Sets the unit to a specific position. The unit has to make sure
	 * that all parts are set accordingly.
	 * @param pPosition The new position
	 */
	void setPosition(Vector2f pPosition);
	
	/**
	 * Returns the list of all parts of this unit that should be added to
	 * the world (this means all parts the unit has).
	 * @return The list of parts
	 */
	List<Body> getBodyParts();
	
	/**
	 * Returns the list of joints the unit has.
	 * @return The list of joints
	 */
	List<Joint> getJoints();

	/**
	 * Add a force to the whole unit. This used for example on jumping.
	 * @param pForce The force
	 */
	void addForce(Vector2f pForce);
	
	/**
	 * This unit is selected and receives a movement command (e.g. arrow keys).
	 * @param pForce The force vector that indicates the direction. This has to
	 * be a normalized vector.
	 */
	void move(Vector2f pForce);
	
	/**
	 * This unit is the currently controlled unit.
	 */
	void select();
	
	/**
	 * Another unit was selected, this is the notification.
	 */
	void unselect();
	
	/**
	 * Trigger any functionality when the unit is removed.
	 */
	void delete();
	
	/**
	 * Setup method: All settings of the unit, that are to be performed after
	 * the unit was added to the world.
	 */
	void setup();
}
