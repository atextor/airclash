package airclash.units.buildings;

import java.util.LinkedList;
import java.util.List;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.FixedJoint;
import net.phys2d.raw.Joint;
import net.phys2d.raw.shapes.Box;
import airclash.system.exceptions.InvalidResourceException;
import airclash.system.gfx.Gui;
import airclash.system.gfx.Renderer;
import airclash.system.gfx.TextureLoader;
import airclash.units.IModule;

/**
 * The Headquarters (HQ) is the main building, e.g. the one where you can build
 * stuff.
 * @author Andreas Textor
 */
public class HQ extends Module {
	/**
	 * The size of a module grid: MAXDIMENSION * MAXDIMENSION bodies are allowed
	 * as part of the flying machine. This must be an odd number.
	 */
	private static final int MAXDIMENSION = 9;
	
	/** The gui object, so that we can hide and show dialogs. */
	private Gui gui;
	
	/** The list of body parts of the flying machine. */
	private List<Body> parts = new LinkedList<Body>();
	
	/** The list of modules of the flying machine. */
	private List<IModule> modules = new LinkedList<IModule>();
	
	/** The list of joints. */
	private List<Joint> joints = new LinkedList<Joint>();
	
	/**
	 * The grid of modules of this machine: The center element of the array
	 * marks this HQ element, the elements around are used, among other things,
	 * to determine where it is possible to build a module.
	 */
	private IModule[][] grid = new IModule[MAXDIMENSION + 1][MAXDIMENSION + 1];
	
	/**
	 * Constructor.
	 * @param pGui The gui object
	 * @throws InvalidResourceException Thrown, when a texture can't be loaded.
	 */
	public HQ(final Gui pGui) throws InvalidResourceException {
		this.texture = TextureLoader.getInstance().getTexture("hq");
		this.gui = pGui;
		this.body = new Body("Body", new Box(GRIDSIZE, GRIDSIZE), 1);
		this.body.setMaxVelocity(30, 80);
		this.body.setRotatable(false);
		this.parts.add(this.body);
		this.modules.add(this);
	}
	
	/**
	 * Adds a module to this machine. The coordinates for this method are grid
	 * coordinates relative to the HQ element, e.g. 0, 1 would mean the place
	 * just above the HQ element.
	 * @param pModule The module to add.
	 * @param pX The x coordinate.
	 * @param pY The y coordinate.
	 * @return true if the module was added, false if not
	 */
	public boolean addModule(final IModule pModule, final int pX, final int pY) {
		final int md2 = MAXDIMENSION / 2;
		if (pX > md2 || pY > md2 || pX < -md2 || pY < -md2) {
			return false;
		}
		if (this.grid[pX + md2][pY + md2] == null) {
			this.modules.add(pModule);
			this.body.addExcludedBody(pModule.getBody());
			pModule.getBody().addExcludedBody(this.body);
			pModule.setPosition(new Vector2f(this.body.getPosition().getX() + (GRIDSIZE * pX),
					this.body.getPosition().getY() + (GRIDSIZE * pY)));

			this.parts.add(pModule.getBody());
			final Joint j = new FixedJoint(this.body, pModule.getBody());
			this.joints.add(j);
			this.grid[pX + md2][pY + md2] = pModule;
			
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getGridWidth() {
		return 1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public int getGridHeight() {
		return 1;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void addForce(final Vector2f pForce) {
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Body> getBodyParts() {
		return this.parts;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Joint> getJoints() {
		return this.joints;
	}

	/**
	 * {@inheritDoc}
	 */
	public void move(final Vector2f pForce) {
		final Vector2f force = new Vector2f(pForce.x * 100, pForce.y * 100);
		for (Body b : this.parts) {
			b.setIsResting(false);
			b.addForce(force);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setPosition(final Vector2f pPosition) {
		this.body.setPosition(pPosition.x, pPosition.y);
	}

	/**
	 * {@inheritDoc}
	 */
	public void draw(final Renderer pRenderer) {
		pRenderer.drawBoxBody(this.body, (Box)this.body.getShape(), this.texture);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void select() {
		super.select();
		this.gui.setMainMenuVisible(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unselect() {
		super.unselect();
		this.gui.setMainMenuVisible(false);
	}
}
