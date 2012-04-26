package airclash.world;

import java.util.LinkedList;
import java.util.List;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Arbiter;
import net.phys2d.raw.ArbiterList;
import net.phys2d.raw.Body;
import net.phys2d.raw.Contact;
import net.phys2d.raw.Joint;
import net.phys2d.raw.StaticBody;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import net.phys2d.raw.shapes.ConvexPolygon;
import net.phys2d.raw.shapes.Polygon;
import net.phys2d.raw.strategies.QuadSpaceStrategy;
import airclash.system.Core;
import airclash.system.Properties;
import airclash.system.exceptions.InvalidResourceException;
import airclash.system.gfx.Renderer;
import airclash.system.gfx.Texture;
import airclash.system.gfx.TextureLoader;
import airclash.system.xml.LevelLoader;
import airclash.units.IUnit;

/**
 * Level is reponsible for maintaining and drawing the world.
 * 
 * @author Andreas Textor
 */
public class Level {
	/** 
	 * The level description. This contains all data to construct a
	 * level instance from.
	 */
	private LevelDescription description;
	
	/** The physical world. */
	private World world;
	
	/** The parts that make up the level. */
	private List<Body> levelParts;
	
	/** The texture of the polygons. */
	private Texture texture;
	
	/**
	 * Constructor.
	 * @throws InvalidResourceException Thrown when a texture can't be loaded
	 */
	public Level() throws InvalidResourceException {
		this.levelParts = new LinkedList<Body>();
		this.world = new World(new Vector2f(0.0f, 10.0f), 10, new QuadSpaceStrategy(20, 5));
		this.world.enableRestingBodyDetection(1, 1, 1);
		this.texture = TextureLoader.getInstance().getTexture("brick");
	}
	
	/**
	 * Returns the level description.
	 * @return The level description
	 */
	public LevelDescription getDescription() {
		return this.description;
	}
	
	/**
	 * Loads a level into this instance.
	 * @param pLevel The level file name
	 * @throws InvalidResourceException Thrown when the level can't be loaded
	 */
	public void load(final String pLevel) throws InvalidResourceException {
		Core.LOGGER.info("Loading level " + pLevel);
		final LevelLoader levelLoader = new LevelLoader(pLevel);
		this.description = levelLoader.getDescription();
	}
	
	/**
	 * Initialize/reset the level.
	 */
	public void init() {
		this.world.clear();
		this.world.getJoints().clear();
		this.world.getArbiters().clear();
		this.world.setGravity(0, -10);
		buildLevel(this.description.getGeometry());
	}
	
	/**
	 * Adds a unit to the world, e.g. makes it a part of physical and
	 * rendered world. After that, the unit's setup() method is called.
	 * @param pUnit The unit
	 */
	public void addUnit(final IUnit pUnit) {
		if (pUnit == null) {
			return;
		}
		if (pUnit.getBodyParts() != null) {
			for (Body b : pUnit.getBodyParts()) {
				if (!this.world.getBodies().contains(b)) {
					this.world.add(b);
				}
			}
		}
		if (pUnit.getJoints() != null) {
			for (Joint j : pUnit.getJoints()) {
				if (!this.world.getJoints().contains(j)) {
					this.world.add(j);
				}
			}
		}
		pUnit.setup();
	}

	/**
	 * Creates a floor from n coordinates. The coordinates represent the
	 * nodes of the floor top line. The floor is built out of n-1 convex
	 * rhomboids.
	 * 
	 * @param pData The coordinates
	 */
	private void buildLevel(final Vector2f... pData) {
		this.levelParts.clear();
		for (int i = 1; i < pData.length; i++) {
			final Vector2f[] verts = {
					new Vector2f(pData[i].x, 0),
					new Vector2f(pData[i].x, pData[i].y),
					new Vector2f(pData[i - 1].x, pData[i - 1].y),
					new Vector2f(pData[i - 1].x, 0)
			};
			final ConvexPolygon rhomboid = new ConvexPolygon(verts);
			final Body rBody = new StaticBody("ground" + i, rhomboid);
			this.levelParts.add(rBody);
			this.world.add(rBody);
		}

		// Bounding boxes
		final Box left = new Box(10, 5000);
		final Box right = new Box(10, 5000);
		final Box top = new Box(pData[pData.length - 1].x, 10f);
		final Body leftBody = new StaticBody("left", left);
		final Body rightBody = new StaticBody("right", right);
		final Body topBody = new StaticBody("top", top);
		leftBody.setPosition(-10, 2500);
		rightBody.setPosition(pData[pData.length - 1].x, 2500);
		topBody.setPosition(0, 5000);
		this.world.add(leftBody);
		this.world.add(rightBody);
		this.world.add(topBody);
	}
	
	/**
	 * Draw the world.
	 * @param pRenderer The renderer to draw on.
	 */
	public void drawWorld(final Renderer pRenderer) {
		for (Body b : this.levelParts) {
			pRenderer.drawPolygonBody(b, (Polygon)b.getShape(), this.texture);
		}
	}
	
	/**
	 * Draw everything that needs to be drawn after the units.
	 * @param pRenderer The renderer to draw on.
	 */
	public void drawOverlay(final Renderer pRenderer) {
		
		if (Core.PROPERTIES.getBoolean(Properties.Prop.DRAWCONTACTS)) {
			final ArbiterList arbs = this.world.getArbiters();
			
			for (int i = 0; i < arbs.size(); i++) {
				final Arbiter arb = arbs.get(i);
				
				final Contact[] cts = arb.getContacts();
				final int numContacts = arb.getNumContacts();
				
				for (int j = 0; j < numContacts; j++) {
					pRenderer.drawContact(cts[j]);
				}
			}
		}		
	}
	
	/**
	 * Move all objects in the physical world one step.
	 */
	public void step() {
		for (int i = 0; i < 5; i++) {
			this.world.step();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String result = this.getDescription().toString();
		result += "\nWorld Info: Total Energy: " + this.world.getTotalEnergy()
			+ "  Bodies: " + this.world.getBodies().size()
			+ "  Joints: " + this.world.getJoints().size()
			+ "  Arbiters: " + this.world.getArbiters().size();
		return result;
	}
}
