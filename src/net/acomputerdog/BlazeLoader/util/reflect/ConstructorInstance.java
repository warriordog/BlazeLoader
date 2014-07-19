package net.acomputerdog.BlazeLoader.util.reflect;

import java.lang.reflect.Constructor;

/**
 * Class to bind a Constructor to a containing Object.
 */
@Deprecated
public class ConstructorInstance {
    protected Constructor boundConstructor;
    protected Object boundObject;

    /**
     * Creates a new ConstructorInstance.
     *
     * @param constructor The constructor to bind to.  Can not be null.
     * @param object      The object containing the bound constructor.  Cannot be null.
     */
    public ConstructorInstance(Constructor constructor, Object object) {
        if (constructor == null) {
            throw new IllegalArgumentException("The bound constructor cannot be null!");
        }
        if (object == null) {
            throw new IllegalArgumentException("The bound object cannot be null!");
        }
        this.boundConstructor = constructor;
        this.boundObject = object;
    }

    /**
     * Gets the bound Constructor.
     *
     * @return Return the bound Constructor.
     */
    public Constructor getConstructor() {
        return boundConstructor;
    }

    /**
     * Gets the bound Object.
     *
     * @return Return the bound Object.
     */
    public Object getObject() {
        return boundObject;
    }
}
