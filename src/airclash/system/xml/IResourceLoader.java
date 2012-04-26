package airclash.system.xml;

/**
 * Any Loader should implement this interface. The Loader should perform its
 * loading task in the constructor, so that the loaded object description is
 * directly available when the getter is called. 
 * @author Andreas Textor
 */
public interface IResourceLoader {
	/**
	 * Returns the loaded object description.
	 * @return The description
	 */
	IResourceDescription getDescription();
}
