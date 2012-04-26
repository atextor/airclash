package airclash.system;

/**
 * Listeners for system property changed events must implement this.
 * @author Andreas Textor
 */
public interface IPropertyListener {
	/**
	 * The property this listener was registered for was changed.
	 * @param pNewValue The value it was set to
	 */
	void changedEvent(String pNewValue);
}
