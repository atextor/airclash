package airclash.system.xml;

import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.phys2d.math.Vector2f;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import airclash.system.exceptions.InvalidResourceException;
import airclash.world.LevelDescription;

/**
 * This class loads a level from a  file.
 * 
 * @author Andreas Textor
 */
public class LevelLoader implements IResourceLoader {
	/** The container for the loaded description. */
	private LevelDescription description = new LevelDescription();
	
	/**
	 * Constructor. This already loads the level. The level data may then
	 * be retrieved using the getter methods.
	 * 
	 * @param pLevel The level file to load.
	 * @throws InvalidResourceException Is thrown, when the file is not readable
	 * or invalid.
	 */
	public LevelLoader(final String pLevel) throws InvalidResourceException {
		try {
			final String filename = "data/levels/" + pLevel + ".svg";
	
			// Unfortunately, for some reason, Inkscape includes two xmlns
			// attributes, one with a  namespace, and one without. This
			// confuses dom4j, so we remove one of them.
			final RandomAccessFile file = new RandomAccessFile(filename, "r");
			final byte[] fileContent = new byte[(int)file.length()];
			file.read(fileContent);
			final Reader stringReader = new StringReader(new String(fileContent)
					.replace("xmlns=\"http://www.w3.org/2000/svg\"", ""));
			final SAXReader reader = new SAXReader();
	        final Document document = reader.read(stringReader);

	        final Node n = document.selectSingleNode("//svg/g/path[@id='floor']");
	        final String path = n.valueOf("@d");
	        
	        final String height = document.selectSingleNode("//svg").valueOf("@height")
	        	.replaceAll("[a-zA-Z]", "");
	        
	        parsePath(path, Integer.parseInt(height));
		} catch (Exception e) {
			throw new InvalidResourceException("Error while loading level", e);
		}
	}
	
	/**
	 * Parses a SVG path. This path is a string of the form:
	 * <code>M 0.5,747 L 106.5,698 L 184.5,704 L 252.5,704 L 336.5,693</code> etc.
	 * The method creates the geometry vector from it and saves it into the level
	 * description object. As the origin in a SVG is at the top left, but we need
	 * coordinates where the origin is in the bottom left, the height of the
	 * svg image is used to revert the y values of the points.
	 * @param pPath The SVG path
	 * @param pHeight The height of the source SVG
	 */
	protected void parsePath(final String pPath, final int pHeight) {
		final List<Vector2f> geom = new ArrayList<Vector2f>();
		
		// Remember the last point as width.
		int width = 0;
		
		// Remember the old coordinates, as the last appears two times in the path
		int oldX = -1;
		int oldY = -1;
		for (final Iterator<String> it = Arrays.asList(pPath.split(" ")).iterator();
				it.hasNext();) {
			// Ignore M or L
			String pair = it.next();
			// Read the pair of coordinates
			pair = it.next();
			
			final String[] coords = pair.split(",");
			final int x = (int)Float.parseFloat(coords[0]);
			final int y = (int)Float.parseFloat(coords[1]);
			if (x != oldX || y != oldY) {
				final Vector2f v = new Vector2f(x, pHeight - y);
	        	geom.add(v);
	        	width = (int)v.x;
			}
			oldX = x;
			oldY = y;
		}
        this.description.setGeometry(geom.toArray(new Vector2f[geom.size()]));
        this.description.setWidth(width);
	}
	
	/**
	 * Returns the level blueprint.
	 * @return The level blueprint
	 */
	public LevelDescription getDescription() {
		return this.description;
	}
}
