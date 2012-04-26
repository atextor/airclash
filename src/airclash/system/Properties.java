package airclash.system;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import airclash.system.exceptions.NotImplementedException;

/**
 * Custom Properties object that keeps track of property changes and notifies
 * Listeners about property changes.
 * @see IPropertyListener
 * @author Andreas Textor
 */
@SuppressWarnings("serial")
public class Properties extends java.util.Properties {
	/**
	 * Enumeration of valid types for properties.
	 * @author Andreas Textor
	 */
	public static enum PropType {
		/** Strings. */
		STRING,
		
		/** Numbers. */
		NUMBER,
		
		/** Boolean values. */
		BOOLEAN
	}
	
	/**
	 * Enumeration of properties.
	 * @author Andreas Textor
	 */
	public static enum Prop {
		/** Does the game run in fullscreen? */
		FULLSCREEN(PropType.BOOLEAN),
		
		/** Name of the current player. */
		PLAYERNAME(PropType.STRING),
		
		/** Draw contact points of objects. */
		DRAWCONTACTS(PropType.BOOLEAN);
		
		/** The type of this property. */
		private PropType type;
		
		/**
		 * Returns the type of this property.
		 * @return The type
		 */
		public PropType getType() {
			return this.type;
		}
		
		/**
		 * Constructor: Each property has a type.
		 * @param pType The type
		 */
		private Prop(final PropType pType) {
			this.type = pType;
		}
	}
	
	/**
	 * Collection of property changed listeners, indexed by property name.
	 */
	private Map<Prop, Set<IPropertyListener>> listeners =
		new HashMap<Prop, Set<IPropertyListener>>();
	
	/**
	 * Registers a property changed listener.
	 * @param pProperty The property to register for
	 * @param pListener The listener
	 */
	public void registerListener(final Prop pProperty, final IPropertyListener pListener) {
		if (this.listeners.get(pProperty) == null) {
			this.listeners.put(pProperty, new HashSet<IPropertyListener>());
		}
		this.listeners.get(pProperty).add(pListener);
	}
	
	/**
	 * Removes a property changed listener.
	 * @param pListener The listener
	 */
	public void removeListener(final IPropertyListener pListener) {
		throw new NotImplementedException("Properties.removeListener()");
	}
	
	/**
	 * Sets a property and notifies all listeners for this property.
	 * @param pKey The name of the property.
	 * @param pValue The value of the property.
	 * @return The previous value.
	 */
	@Override
	public Object setProperty(final String pKey, final String pValue) {
		final Set<IPropertyListener> listenerSet = this.listeners.get(pKey);
		if (listenerSet != null) {
			for (IPropertyListener pl : listenerSet) {
				pl.changedEvent(pValue);
			}
		}
		return super.put(pKey, pValue);
	}
	
	/**
	 * Sets a property and notifies all listeners for this property.
	 * @param pKey The name of the property. This must be string.
	 * @param pValue The value of the property. This must be a string.
	 * @return The previous value
	 */
	@Override
	public Object put(final Object pKey, final Object pValue) {
		return setProperty((String)pKey, (String)pValue);
	}
	
	/**
	 * Sets a property.
	 * @param pKey The property
	 * @param pValue The new value
	 */
	public void set(final Prop pKey, final String pValue) {
		setProperty(pKey.toString(), pValue);
	}
	
	/**
	 * Gets a property value.
	 * @param pKey The property
	 * @return The value
	 */
	public String getString(final Prop pKey) {
		return super.getProperty(pKey.toString());
	}
	
	/**
	 * Gets a property value of a boolean property.
	 * @param pKey The property
 	 * @return The value
	 */
	public boolean getBoolean(final Prop pKey) {
		boolean result = false;
		if (pKey.getType() == PropType.BOOLEAN) {
			result = super.getProperty(pKey.toString()).equals("1");
		}
		return result;
	}
}
