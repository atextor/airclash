package airclash.system.gfx;

import org.lwjgl.opengl.GL11;

/**
 * A texture to be bound within JOGL/GL11. This object is responsible for
 * keeping track of a given OpenGL texture and for calculating the
 * texturing mapping coordinates of the full image.
 * 
 * Since textures need to be powers of 2 the actual texture may be
 * considerably bigged that the source image and hence the texture
 * mapping coordinates need to be adjusted to matchup drawing the
 * sprite against the texture.
 *
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class Texture {
    /** The GL target type. */
    private int target;
    
    /** The GL texture ID. */
    private int textureID;
    
    /** The height of the image. */
    private int height;
    
    /** The width of the image. */
    private int width;
    
    /** The width of the texture. */
    private int texWidth;
    
    /** The height of the texture. */
    private int texHeight;
    
    /** The ratio of the width of the image to the texture. */
    private float widthRatio;
    
    /** The ratio of the height of the image to the texture. */
    private float heightRatio;
    
    /**
     * Create a new texture.
     *
     * @param pTarget The GL target
     * @param pTextureID The GL texture ID
     */
    public Texture(final int pTarget, final int pTextureID) {
        this.target = pTarget;
        this.textureID = pTextureID;
    }
    
    /**
     * Bind the specified GL context to a texture.
     */
    public void bind() {
      GL11.glBindTexture(this.target, this.textureID);
    }
    
    /**
     * Set the height of the image.
     *
     * @param pHeight The height of the image
     */
    public void setHeight(final int pHeight) {
        this.height = pHeight;
        setHeight();
    }
    
    /**
     * Set the width of the image.
     *
     * @param pWidth The width of the image
     */
    public void setWidth(final int pWidth) {
        this.width = pWidth;
        setWidth();
    }
    
    /**
     * Get the height of the original image.
     *
     * @return The height of the original image
     */
    public int getImageHeight() {
        return this.height;
    }
    
    /** 
     * Get the width of the original image.
     *
     * @return The width of the original image
     */
    public int getImageWidth() {
        return this.width;
    }
    
    /**
     * Get the height of the physical texture.
     *
     * @return The height of physical texture
     */
    public float getHeight() {
        return this.heightRatio;
    }
    
    /**
     * Get the width of the physical texture.
     *
     * @return The width of physical texture
     */
    public float getWidth() {
        return this.widthRatio;
    }
    
    /**
     * Set the height of this texture.
     *
     * @param pTexHeight The height of the texture
     */
    public void setTextureHeight(final int pTexHeight) {
        this.texHeight = pTexHeight;
        setHeight();
    }
    
    /**
     * Set the width of this texture. 
     *
     * @param pTexWidth The width of the texture
     */
    public void setTextureWidth(final int pTexWidth) {
        this.texWidth = pTexWidth;
        setWidth();
    }
    
    /**
     * Set the height of the texture. This will update the
     * ratio also.
     */
    private void setHeight() {
        if (this.texHeight != 0) {
            this.heightRatio = ((float)this.height) / this.texHeight;
        }
    }
    
    /**
     * Set the width of the texture. This will update the
     * ratio also.
     */
    private void setWidth() {
        if (this.texWidth != 0) {
            this.widthRatio = ((float)this.width) / this.texWidth;
        }
    }
}

