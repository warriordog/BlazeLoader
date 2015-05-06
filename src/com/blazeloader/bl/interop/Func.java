package com.blazeloader.bl.interop;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * A Function, of course.
 * <p>
 * Uses a combination of {@code MethodHandle} and {@code LamdaMetaFactory} to gain quicker access to methods out of reach.
 * 
 * @param <T> The interface type containing the method signature used.
 */
public class Func<T, R> {
	private MethodHandle target;
	private MethodHandle factory;
	
	private final boolean staticMethod;
	
	public Func(Class<T> interfaceType, Class context, Class<R> returnType, String name, Class... pars) {
		this(interfaceType, context, returnType, name, false, pars);
	}
	
	public Func(Class<T> interfaceType, Class context, Class<R> returnType, String name, boolean isStatic, Class... pars) {
		staticMethod = isStatic;
		MethodType getter = MethodType.methodType(returnType, pars);
		MethodHandles.Lookup caller = MethodHandles.lookup();
		try {
			if (isStatic) {
				target = caller.findStatic(context, name, getter);
				CallSite site = LambdaMetafactory.metafactory(caller, name, MethodType.methodType(interfaceType), getter, target, getter);
				factory = site.getTarget();
			} else {
				target = caller.findVirtual(context, name, getter);
				CallSite site = LambdaMetafactory.metafactory(caller, name, MethodType.methodType(interfaceType, context), getter, target, getter);
				factory = site.getTarget();
			}
		} catch (Throwable e) {
			e.printStackTrace();
			target = null;
		}
	}
	
	/**
	 * Invokes the underlying method with the given arguments and null context.
	 * @param args			Object array of arguments.
	 * <p>
	 * Note: Calling a method through its lambda can be considerably faster than using call or apply depending on usage.
	 * 
	 * @return	The returned result of the method
	 * @throws Throwable if there is any error.
	 */
	public R call(Object... args) throws Throwable {
		return (R)target.invokeWithArguments(args);
	}
	
	/**
	 * Invokes the underlying method with the given arguments and instance context.
	 * 
	 * @param instance		The instance to bind to
	 * @param args			Object array of arguments.
	 * <p>
	 * Note: Calling a method through its lambda can be considerably faster than using call or apply depending on usage.
	 * 
	 * @return	The returned result of the method
	 * @throws Throwable if there is any error.
	 */
	public R apply(Object instance, Object... args) throws Throwable {
		if (staticMethod) {
			return (R)target.invokeWithArguments(args);
		}
		return (R)target.bindTo(instance).invokeWithArguments(args);
	}
	
	/**
	 * Gets a lambda object built from the given interface class with the underlying method as its implementation.
	 * <p>
	 * Note: Calling a method through its lambda can be considerably faster than using call or apply depending on usage.
	 * 
	 * @param instance		An instance to bind to.
	 * 
	 * @return lambda T
	 * @throws Throwable if there is any error.
	 */
	public T getLambda(Object instance) throws Throwable {
		return (T)factory.bindTo(instance).invoke();
	}
	
	/**
	 * Marks this as broken.
	 */
	public void invalidate() {
		target = null;
		factory = null;
	}
	
	/**
	 * Checks if this is still a functional method.
	 * @return true if you can safely call it.
	 */
	public boolean valid() {
		return target != null && factory != null;
	}
}