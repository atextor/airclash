package airclash.units.vehicles;

import java.util.Arrays;
import java.util.List;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.Joint;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.Circle;
import airclash.system.exceptions.InvalidResourceException;
import airclash.system.gfx.Renderer;
import airclash.system.gfx.Texture;
import airclash.system.gfx.TextureLoader;
import airclash.units.IVehicle;

/**
 * Simple two-wheeled vehicle without weaponry.
 * @author Andreas Textor
 */
public class Scout extends AbstractVehicle implements IVehicle {
	/** The body of the vehicle. */
	private Body box;
	
	/** The first wheel. */
	private Body wheel1;
	
	/** The second wheel. */
	private Body wheel2;
	
	/** The first wheel joint. */
	private Joint wheelJoint1;
	
	/** The second wheel joint. */
	private Joint wheelJoint2;
	
	/** The array of wheels. */
	private Body[] wheels;
	
	/** The texture of the vehicle. */
	private Texture texture;
	
	/**
	 * Constructor.
	 * @throws InvalidResourceException Thrown, when the texture can't be loaded
	 */
	public Scout() throws InvalidResourceException {
		this.box = new Body("Body", new Box(50, 15), 3);
		this.box.setMaxVelocity(30, 80);
		this.wheel1 = new Body("Wheel", new Circle(8), 1);
		this.wheel1.setMaxVelocity(30, 80);
		this.wheel2 = new Body("Wheel", new Circle(8), 1);
		this.wheel2.setMaxVelocity(30, 80);
		setPosition(new Vector2f());
		
		this.wheels = new Body[] {this.wheel1, this.wheel2};
		this.wheelJoint1 = new FixedJoint(this.box, this.wheel1);
		this.wheelJoint2 = new FixedJoint(this.box, this.wheel2);
		setEnginePower(300);

		this.texture = TextureLoader.getInstance().getTexture("scout");
	}
	
	/**
	 * {@inheritDoc}
	 */
	public List<Body> getBodyParts() {
		return Arrays.asList(this.box, this.wheel1, this.wheel2);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Joint> getJoints() {
		return Arrays.asList(this.wheelJoint1, this.wheelJoint2);
	}

	/**
	 * {@inheritDoc}
	 */	
	public void setPosition(final Vector2f pPosition) {
		this.box.setPosition(pPosition.x, pPosition.y);
		this.wheel1.setPosition(pPosition.x - 20, pPosition.y - 10);
		this.wheel2.setPosition(pPosition.x + 20, pPosition.y - 10);
	}

	/**
	 * {@inheritDoc}
	 */
	public Body[] getWheels() {
		return this.wheels;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addForce(final Vector2f pForce) {
		this.box.addForce(pForce);
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void move(final Vector2f pForce) {
		addWheelForce(pForce);
	}
	
	/**
	 * {@inheritDoc}
	 */	
	public void draw(final Renderer pRenderer) {
		pRenderer.drawBoxBody(this.box, (Box)this.box.getShape(), this.texture);
		pRenderer.drawCircleBody(this.wheel1, (Circle)this.wheel1.getShape());
		pRenderer.drawCircleBody(this.wheel2, (Circle)this.wheel1.getShape());
	}

	/**
	 * Setup the unit's collision mask.
	 */
	@Override
	public void setup() {
		super.setup();
		this.box.addExcludedBody(this.wheel1);
		this.box.addExcludedBody(this.wheel2);
		this.wheel1.addExcludedBody(this.box);
		this.wheel2.addExcludedBody(this.box);
	}
}
