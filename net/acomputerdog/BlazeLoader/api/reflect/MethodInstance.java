package net.acomputerdog.BlazeLoader.api.reflect;

import java.lang.reflect.Method;

/**
 * Class to bind a Method to a containing Object.
 */
public class MethodInstance {
    protected Method boundMethod;
    protected Object boundObject;

    /**
     * Creates a new MethodInstance.
     *
     * @param method The method to bind to.  Can not be null.
     * @param object The object containing the bound method.  Can be null for static methods.
     */
    public MethodInstance(Method method, Object object) {
        if (method == null) {
            throw new IllegalArgumentException("The bound method cannot be null!");
        }
        this.boundMethod = method;
        this.boundObject = object;
    }

    /**
     * Gets the bound Method.
     *
     * @return Return the bound Method.
     */
    public Method getMethod() {
        return boundMethod;
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
