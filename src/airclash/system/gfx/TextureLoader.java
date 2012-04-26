package airclash.system.gfx;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.imageio.ImageIO;

import org.lwjgl.opengl.GL11;

import airclash.system.exceptions.InvalidResourceException;

/**
 * A utility class to load textures for JOGL. This source is based
 * on a texture that can be found in the Java Gaming (www.javagaming.org)
 * Wiki. It has been simplified slightly for explicit 2D graphics use.
 * 
 * OpenGL uses a particular image format. Since the images that are
 * loaded from disk may not match this format this loader introduces
 * a intermediate image which the source image is copied into. In turn,
 * this image is used as source for the OpenGL texture.
 * 
 * This class is a singleton.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 * @author Andreas Textor
 */
public final class TextureLoader {
	/** The table of textures that have been loaded in this loader. */
	private Map<String, Texture> table = new HashMap<String, Texture>();

	/** The colour model including alpha for the GL image. */
	private ColorModel glAlphaColorModel;

	/** The colour model for the GL image. */
	private ColorModel glColorModel;

	/** The singleton instance. */
	private static TextureLoader instance = null;
	
	/**
	 * Returns the singleton instance.
	 * @return The singleton instance
	 */
	public static TextureLoader getInstance() {
		if (instance == null) {
			instance = new TextureLoader();
		}
		return instance;
	}
	
	/** 
	 * Create a new texture loader.
	 */
	private TextureLoader() {
		this.glAlphaColorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 8 },
				true, false, Transparency.TRANSLUCENT,
				DataBuffer.TYPE_BYTE);

		this.glColorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[] { 8, 8, 8, 0 },
				false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
	}

	/**
	 * Create a new texture ID.
	 *
	 * @return A new texture ID
	 */
	private int createTextureID() {
		final IntBuffer tmp = createIntBuffer(1);
		GL11.glGenTextures(tmp);
		return tmp.get(0);
	}

	/**
	 * Load a texture.
	 *
	 * @param pResourceName The location of the resource to load
	 * @return The loaded texture
	 * @throws InvalidResourceException Indicates a failure to access the resource
	 */
	public Texture getTexture(final String pResourceName) throws InvalidResourceException {
		try {
			Texture tex = this.table.get(pResourceName);
	
			if (tex != null) {
				return tex;
			}
	
			tex = getTexture(pResourceName,
					// target
					GL11.GL_TEXTURE_2D,
					// dst pixel format
					GL11.GL_RGBA,
					// min filter (unused)
					GL11.GL_LINEAR,
					GL11.GL_LINEAR);
	
			this.table.put(pResourceName, tex);
	
			return tex;
		} catch (IOException e) {
			throw new InvalidResourceException("Error while loading texture", e);
		}
	}

	/**
	 * Load a texture into OpenGL from a image reference on
	 * disk.
	 *
	 * @param pResourceName The location of the resource to load
	 * @param pTarget The GL target to load the texture against
	 * @param pDstPixelFormat The pixel format of the screen
	 * @param pMinFilter The minimising filter
	 * @param pMagFilter The magnification filter
	 * @return The loaded texture
	 * @throws IOException Indicates a failure to access the resource
	 */
	public Texture getTexture(final String pResourceName, final int pTarget,
			final int pDstPixelFormat, final int pMinFilter, final int pMagFilter)
			throws IOException {
		int srcPixelFormat = 0;

		// create the texture ID for this texture
		final int textureID = createTextureID();
		final Texture texture = new Texture(pTarget, textureID);

		// bind this texture
		GL11.glBindTexture(pTarget, textureID);

		final BufferedImage bufferedImage = loadImage("data/textures/" + pResourceName + ".png");
		texture.setWidth(bufferedImage.getWidth());
		texture.setHeight(bufferedImage.getHeight());

		if (bufferedImage.getColorModel().hasAlpha()) {
			srcPixelFormat = GL11.GL_RGBA;
		} else {
			srcPixelFormat = GL11.GL_RGB;
		}

		// convert that image into a byte buffer of texture data
		final ByteBuffer textureBuffer = convertImageData(bufferedImage, texture);

		if (pTarget == GL11.GL_TEXTURE_2D) {
			GL11.glTexParameteri(pTarget, GL11.GL_TEXTURE_MIN_FILTER, pMinFilter);
			GL11.glTexParameteri(pTarget, GL11.GL_TEXTURE_MAG_FILTER, pMagFilter);
		}

		// produce a texture from the byte buffer
		GL11.glTexImage2D(pTarget, 0, pDstPixelFormat, get2Fold(bufferedImage
				.getWidth()), get2Fold(bufferedImage.getHeight()), 0,
				srcPixelFormat, GL11.GL_UNSIGNED_BYTE, textureBuffer);

		return texture;
	}

	/**
	 * Get the closest greater power of 2 to the fold number.
	 * 
	 * @param pFold The target number
	 * @return The power of 2
	 */
	private int get2Fold(final int pFold) {
		int ret = 2;
		while (ret < pFold) {
			ret *= 2;
		}
		return ret;
	}

	/**
	 * Convert the buffered image to a texture.
	 *
	 * @param pBufferedImage The image to convert to a texture
	 * @param pTexture The texture to store the data into
	 * @return A buffer containing the data
	 */
	private ByteBuffer convertImageData(final BufferedImage pBufferedImage,
			final Texture pTexture) {
		ByteBuffer imageBuffer = null;
		WritableRaster raster;
		BufferedImage texImage;

		int texWidth = 2;
		int texHeight = 2;

		// find the closest power of 2 for the width and height
		// of the produced texture
		while (texWidth < pBufferedImage.getWidth()) {
			texWidth *= 2;
		}
		while (texHeight < pBufferedImage.getHeight()) {
			texHeight *= 2;
		}

		pTexture.setTextureHeight(texHeight);
		pTexture.setTextureWidth(texWidth);

		// create a raster that can be used by OpenGL as a source
		// for a texture
		if (pBufferedImage.getColorModel().hasAlpha()) {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 4, null);
			texImage = new BufferedImage(this.glAlphaColorModel, raster, false,
					new Hashtable<String, Texture>());
		} else {
			raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
					texWidth, texHeight, 3, null);
			texImage = new BufferedImage(this.glColorModel, raster, false,
					new Hashtable<String, Texture>());
		}

		// copy the source image into the produced image
		final Graphics g = texImage.getGraphics();
		g.setColor(new Color(0f, 0f, 0f, 0f));
		g.fillRect(0, 0, texWidth, texHeight);
		g.drawImage(pBufferedImage, 0, 0, null);

		// build a byte buffer from the temporary image
		// that be used by OpenGL to produce a texture.
		final byte[] data = ((DataBufferByte)texImage.getRaster().getDataBuffer())
				.getData();

		imageBuffer = ByteBuffer.allocateDirect(data.length);
		imageBuffer.order(ByteOrder.nativeOrder());
		imageBuffer.put(data, 0, data.length);
		imageBuffer.flip();

		return imageBuffer;
	}

	/** 
	 * Load a given resource as a buffered image.
	 * 
	 * @param pRef The location of the resource to load
	 * @return The loaded buffered image
	 * @throws IOException Indicates a failure to find a resource
	 */
	private BufferedImage loadImage(final String pRef) throws IOException {
		final InputStream stream = new FileInputStream(pRef);
		final BufferedImage bufferedImage = ImageIO.read(
				new BufferedInputStream(stream));
		return bufferedImage;
	}

	/**
	 * Creates an integer buffer to hold specified ints.
	 * Strictly a utility method.
	 *
	 * @param pSize how many int to contain
	 * @return created IntBuffer
	 */
	protected IntBuffer createIntBuffer(final int pSize) {
		final ByteBuffer temp = ByteBuffer.allocateDirect(4 * pSize);
		temp.order(ByteOrder.nativeOrder());

		return temp.asIntBuffer();
	}
}
