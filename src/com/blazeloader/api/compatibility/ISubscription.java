package com.blazeloader.api.compatibility;

/**
 * Subscription interface for listening to values on the wall.
 *
 * @param <I>	The type of the value you are subscribed to
 */
public interface ISubscription<I> {
	
	/**
	 * Event triggered when the values subscribed to on the wall is about to be changed.
	 * 
	 * @param originalValue		The value currently present
	 * @param newValue			The new value that will be assinged
	 * 
	 * @return true to accept the value, false to reject it.
	 */
	public boolean valueChanged(String key, I originalValue, I newValue);
}
