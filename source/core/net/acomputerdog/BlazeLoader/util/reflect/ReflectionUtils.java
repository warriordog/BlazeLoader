package net.acomputerdog.BlazeLoader.util.reflect;

import java.lang.reflect.Method;

/**
 * Api functions to simplify reflective tasks.
 */
public class ReflectionUtils {

    /**
     * Dynamically invokes a method, even if it is inaccessible.
     *
     * @param method The method instance to invoke.
     * @param args   Arguments to pass to the method.
     * @return Returns whatever is returned by the invoked method, or null if nothing is returned.
     */
    public static Object invokeMethod(MethodInstance method, Object... args) {
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null!");
        }
        try {
            Method m = method.boundMethod;
            m.setAccessible(true);
            return m.invoke(method.boundObject, args);
        } catch (Exception e) {
            throw new RuntimeException("Could not invoke method: " + method.boundMethod.getName(), e);
        }
    }

    /**
     * Invokes a method named in a string, even if it is inaccessible.
     *
     * @param method The full name of the method to invoke.  Should be in the form of [fully_qualified_class_name].[method_name] OR [method_name] if object is not null.
     *               Example: "net.acomputerdog.BlazeLoader.util.reflect.ApiReflect.invokeMethod".
     *               Can NOT be null!
     * @param object The object to invoke on, or null if method is static.  Can be null if method is static and full name is given.
     * @param args   Arguments to pass to the method.
     * @return Returns whatever is returned by the invoked method, or null if nothing is returned.
     */
    public static Object invokeMethod(String method, Object object, Object... args) {
        if (method == null) {
            throw new IllegalArgumentException("method cannot be null!");
        }
        if (object != null) {
            String[] methodComponents = method.split(".");
            String methodName = methodComponents[methodComponents.length - 1];
            try {
                Method theMethod = object.getClass().getDeclaredMethod(methodName);
                theMethod.setAccessible(true);
                return theMethod.invoke(object, args);
            } catch (Exception e) {
                throw new RuntimeException("Could not invoke method: " + methodName, e);
            }
        } else {
            String[] methodComponents = method.split(".");
            String methodName = methodComponents[methodComponents.length - 1];
            try {
                StringBuilder builder = new StringBuilder();
                for (int index = 0; index < methodComponents.length - 2; index++) { //must skip last index!
                    builder.append(methodComponents[index]);
                }
                Class theClass = Class.forName(builder.toString());
                Method theMethod = theClass.getDeclaredMethod(methodName);
                theMethod.setAccessible(true);
                return theMethod.invoke(null, args);
            } catch (Exception e) {
                throw new RuntimeException("Could not invoke method: " + methodName, e);
            }
        }
    }

    public static <I> FieldInstance<I> getField(Class declaringClass, Object instance, int index) {
        return new FieldInstance<I>(declaringClass.getDeclaredFields()[index], instance);
    }
}
