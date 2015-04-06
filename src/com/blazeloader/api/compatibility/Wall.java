package com.blazeloader.api.compatibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.TypeConstraintException;

/**
 * A wall for mods to communicate between each other by setting and getting values by a string identifier.
 */
public class Wall implements Iterable<Wall.Entry> {
    private static Map<String, Wall.Entry> wallMap = new HashMap<String, Wall.Entry>();
    
    private static Wall instance = new Wall();
    
    /**
     * Gets a wall object for iteration and select functions from List.
     */
    public static Wall getWall() {
    	return instance;
    }
    
    /**
     * Retrieves the value of an item from the wall.
     * <b><p>Warning:</b> Type unsafe. Use watchValue to get a typed reference to an entry.
     *
     * @param name		The name of the item to get.
     * @return The item with the given name, or null if it is not set.
     */
    public static Object getItem(String name) {
    	if (isDefined(name)) {
    		return wallMap.get(name).getValue();
    	}
    	return null;
    }

    /**
     * Places an item on the wall.
     * If the item is already there it's value will be updated.
     * <br>Does not accept null.
     * 
     * <b><p>Warning:</b> Type unsafe. Updates may fail if the item's type and the type of the given value do not match.
     * 
     * @param name		The name of the item to get.
     * @param value The value to set the item to.
     */
    public static <I> void setItem(String name, I value) {
    	if (value == null) throw new IllegalArgumentException("value cannot be null");
    	if (isDefined(name)) {
    		wallMap.get(name).setValue(value);
    	} else {
			wallMap.put(name, new Wall.Entry(name, value));
    	}
    }

    /**
     * Gets the type of an item on the wall.
     * <br>The type is the name of the item's class.
     *
     * @param name		The name of the item to get.
     * @return The type of the item, or "null" if the item is not set.
     */
    public static String getItemType(String name) {
    	if (isDefined(name)) {
    		return wallMap.get(name).getType();
    	}
        return "null";
    }

    /**
     * Checks if the given item exists.
     *
     * @param name		The name of the item to check for.
     * @return true if the item exists, false if not.
     */
    public static boolean isDefined(String name) {
        return wallMap.containsKey(name);
    }
    
    /**
     * Check if the given subscriptionObject is subscribed to the item with the given name.
     * 
     * @param name					Name of the item
     * @param subscriptionObject	Handler that may be subscribed
     * 
     * @return true if the handler is subscribed.
     */
    public static boolean isSubscribedTo(String name, ISubscription subscriptionObject) {
    	return isDefined(name) && wallMap.get(name).isSubscribed(subscriptionObject);
    }
    
    /**
     * Subscribe to an item on the wall to be notified when it changes.
     * 
     * @param name					Name of the item
     * @param defaultValue			Default value for the item if not found
     * @param subscriptionObject	Handler to receive the event
     * 
     * @return WallItem for the subscribed item
     */
    public static <I> WallItem<I> subscripteTo(String name, I defaultValue, ISubscription<I> subscriptionObject) {
    	WallItem result = watchItem(name, defaultValue);
    	wallMap.get(name).subscribe(subscriptionObject);
    	return result;
    }
    
    /**
     * Removes a subscription from an item on the wall.
     * 
     * @param name					Name of the item
     * @param subscriptionObject	Handler previously registered to the item
     */
    public static void unsubscripteFrom(String name, ISubscription subscriptionObject) {
    	if (isDefined(name)) {
    		wallMap.get(name).unsubscribe(subscriptionObject);
    	}
    }
    
    /**
     * Returns a watcher for an item on the wall.
     * <br>If the item does not exist it will be registered and instantiated to the given default value.
     * @param <I>
     * 
     * @param name			Name of the item
     * @param defaultValue	Default value for the item if not found
     * 
     * @return	WallItem for the requested item
     */
    public static <I> WallItem<I> watchItem(String name, I defaultValue) {
    	if (!isDefined(name)) {
    		setItem(name, defaultValue);
    	}
    	return new WallItem<I>(wallMap.get(name));
    }
    
    
    /**
     * Gets the number of items on the wall.
     * 
     * @return size of the wall.
     */
	public int size() {
		return wallMap.size();
	}
	
	/**
	 * Checks if the wall is empty.
	 * 
	 * @return true if there are no items registered
	 */
	public boolean isEmpty() {
		return wallMap.isEmpty();
	}
	
	/**
	 * Checks if the given key has a mapping on this wall. 
	 * @param key		key whose presence in this wall is to be tested 
	 * @return true if this wall contains an entry for the specified key
	 * @see java.util.map.containsKey
	 */
	public boolean containsKey(String key) {
		return isDefined(key);
	}
	
	/**
	 * Returns an iterator over the items on this wall
	 * @return an iterator over the items on this wall.
	 */
	@Override
	public Iterator<Wall.Entry> iterator() {
		return new EntryIterator();
	}
	
	/**
	 * Returns an iterator over the values on this Wall.
	 * @return an iterator over the value on this wall.
	 */
	public Iterator valueIterator() {
		return new ValueIterator();
	}
	
	/**
	 * Returns an iterator over the keys on this wall.
	 * @see  java.util.set.iterator
	 * @return an iterator over the keys on this wall.
	 */
	public Iterator<String> keyIterator() {
		return wallMap.keySet().iterator();
	}
    
    public static class Entry<I> implements Map.Entry<String, I> {
    	private List<ISubscription<I>> watchers = new ArrayList<ISubscription<I>>(); 
    	private I value;
    	private final Class clazz;
    	private final String key;
    	
    	public Entry(String name, I val) {
    		value = val;
			clazz = val.getClass();
    		key = name;
    	}
    	
    	public I setValue(I val) {
    		if (!valueEquals(val)) {
	    		if (!acceptsType(val)) {
					throw new TypeConstraintException("New value must be of the same type as the field \"" + key + "\". " + val.getClass().toString() + " cannot be cast to " + clazz.toString());
	    		}
	    		boolean write = true;
	    		for (ISubscription i : watchers) {
	    			write &= i.valueChanged(key, value, val);
	    		}
	    		if (write) value = val;
    		}
    		return value;
    	}
    	
    	private boolean valueEquals(I other) {
    		return other == value || (value != null && value.equals(other));
    	}
    	
    	@Override
		public String getKey() {
			return key;
		}
    	
		@Override
    	public I getValue() {
    		return value;
    	}
    	
		protected String getType() {
			return clazz.getName();
		}
		
		public boolean acceptsType(Object val) {
			return val == null || clazz.isAssignableFrom(val.getClass());
		}
    	
    	private boolean isSubscribed(ISubscription<I> subscriptionObject) {
    		return watchers.contains(subscriptionObject);
    	}
    	
    	protected void subscribe(ISubscription<I> subscriptionObject) {
    		if (!isSubscribed(subscriptionObject)) {
    			watchers.add(subscriptionObject);
    		}
    	}
    	
    	protected void unsubscribe(ISubscription<I> subscriptionObject) {
    		if (isSubscribed(subscriptionObject)) {
    			watchers.remove(subscriptionObject);
    		}
    	}
    }
    
	private final class ValueIterator implements Iterator<Object> {
		private final Iterator<Wall.Entry> mappingsIter = wallMap.values().iterator();
		
		@Override
		public boolean hasNext() {
			return mappingsIter.hasNext();
		}

		@Override
		public Object next() {
			return mappingsIter.next().getValue();
		}

		@Override
		public void remove() {}
	}
	
	private final class EntryIterator implements Iterator<Wall.Entry> {
		private final Iterator<Map.Entry<String, Wall.Entry>> mappingsIter = wallMap.entrySet().iterator();
		
		@Override
		public boolean hasNext() {
			return mappingsIter.hasNext();
		}

		@Override
		public Wall.Entry next() {
			return mappingsIter.next().getValue();
		}

		@Override
		public void remove() {}
	}
}
