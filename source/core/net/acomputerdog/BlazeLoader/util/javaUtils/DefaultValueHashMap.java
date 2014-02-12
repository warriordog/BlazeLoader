package net.acomputerdog.BlazeLoader.util.javaUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * A HashMap with the added method public V get(K key, V def) that allows the user to
 * specify a default value to return if key is not defined.
 *
 * @param <K> The key type
 * @param <V> The value type
 */
public class DefaultValueHashMap<K, V> extends HashMap<K, V> {
    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and load factor.
     *
     * @param initialCapacity the initial capacity
     * @param loadFactor      the load factor
     * @throws IllegalArgumentException if the initial capacity is negative
     *                                  or the load factor is nonpositive
     */
    public DefaultValueHashMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the default initial capacity
     * (16) and the default load factor (0.75).
     */
    public DefaultValueHashMap() {
        super();
    }

    /**
     * Constructs an empty <tt>HashMap</tt> with the specified initial
     * capacity and the default load factor (0.75).
     *
     * @param initialCapacity the initial capacity.
     * @throws IllegalArgumentException if the initial capacity is negative.
     */
    public DefaultValueHashMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Constructs a new <tt>HashMap</tt> with the same mappings as the
     * specified <tt>Map</tt>.  The <tt>HashMap</tt> is created with
     * default load factor (0.75) and an initial capacity sufficient to
     * hold the mappings in the specified <tt>Map</tt>.
     *
     * @param m the map whose mappings are to be placed in this map
     * @throws NullPointerException if the specified map is null
     */
    public DefaultValueHashMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    /**
     * Returns the value to which the specified key is mapped,
     * or {@code null} if this map contains no mapping for the key.
     * <p/>
     * <p>More formally, if this map contains a mapping from a key
     * {@code k} to a value {@code v} such that {@code (key==null ? k==null :
     * key.equals(k))}, then this method returns {@code v}; otherwise
     * it returns {@code null}.  (There can be at most one such mapping.)
     * <p/>
     * <p>A return value of {@code null} does not <i>necessarily</i>
     * indicate that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to {@code null}.
     * The {@link #containsKey containsKey} operation may be used to
     * distinguish these two cases.
     *
     * @param key The key to get the value for
     * @param def The default value to return if key is not defined.
     * @see #put(Object, Object)
     */
    public V get(K key, V def) {
        if (super.containsKey(key)) {
            return super.get(key);
        } else {
            return def;
        }
    }
}
