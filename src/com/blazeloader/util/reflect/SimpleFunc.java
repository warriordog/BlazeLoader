package com.blazeloader.util.reflect;

import com.blazeloader.bl.obf.BLOBF;

public class SimpleFunc<I, R> extends Function {
	public SimpleFunc(Class<I> context, Class<R> returnType, String name, Class... pars) {
		this(context, returnType, name, false, pars);
	}
	
	public SimpleFunc(Class<I> context, Class<R> returnType, String name, boolean isStatic, Class... pars) {
		super(null, context, returnType, name, isStatic, pars);
	}
	
	public SimpleFunc(BLOBF obf) {
		this(false, obf);
	}
	
	public SimpleFunc(boolean isStatic, BLOBF obf) {
		super(null, isStatic, obf);
	}
	
	public SimpleFunc(String descriptor) {
		this(false, descriptor);
	}
	
	public SimpleFunc(boolean isStatic, String descriptor) {
		super(null, isStatic, descriptor);
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
		return (R)handle.target.invokeWithArguments(args);
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
		if (handle.staticMethod) {
			return call(args);
		}
		return (R)handle.target.bindTo(instance).invokeWithArguments(args);
	}
}
