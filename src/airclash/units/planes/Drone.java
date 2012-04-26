package airclash.units.planes;

import java.util.Arrays;
import java.util.List;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.Joint;
import net.phys2d.raw.shapes.Box;
import airclash.system.exceptions.InvalidResourceException;
import airclash.system.gfx.Renderer;
import airclash.system.gfx.Texture;
import airclash.system.gfx.TextureLoader;

/**
 * Simple flying unit whitout any weaponry.
 * @author Andreas Textor
 */
public class Drone extends AbstractPlane {
	/** The box that represents the unit. */
	private Body box;
	
	/** The texture of the unit. */
	private Texture texture;
	
	/**
	 * Constructor.
	 * @throws InvalidResourceException Thrown, when the texture can't be loaded
	 */
	public Drone() throws InvalidResourceException {
		this.box = new Body("Body", new Box(30, 30), 1);
		this.box.setMaxVelocity(30, 40);
		this.box.setRotatable(false);
		this.texture = TextureLoader.getInstance().getTexture("drone");
		setPosition(new Vector2f());
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
	public List<Body> getBodyParts() {
		return Arrays.asList(this.box);
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Joint> getJoints() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public void move(final Vector2f pForce) {
		final Vector2f force = new Vector2f(pForce.x * 100, pForce.y * 100);
		this.box.setIsResting(false);
		this.box.addForce(force);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPosition(final Vector2f pPosition) {
		this.box.setPosition(pPosition.x, pPosition.y);
	}
	
	/**
	 * {@inheritDoc}
	 */	
	public void draw(final Renderer pRenderer) {
		pRenderer.drawBoxBody(this.box, (Box)this.box.getShape(), this.texture);
	}

}
