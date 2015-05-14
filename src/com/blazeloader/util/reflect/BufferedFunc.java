package com.blazeloader.util.reflect;

import com.blazeloader.bl.obf.BLOBF;

/**
 * A buffered version of Func permanently bound to a given instance
 * 
 * @param <I> The class host for this method
 * @param <T> The interface type containing the method signature used.
 * @param <R> The return type for this method
 */
public class BufferedFunc<I, T, R> extends Function<I, T, R> {
	
	private final I instance;
	private T lambda;
	
	public BufferedFunc(Class<T> interfaceType, I context, Class<R> returnType, String name, Class... pars) {
		super(interfaceType, (Class<I>)context.getClass(), returnType, name, false, pars);
		instance = context;
		init();
	}
	
	public BufferedFunc(Class<T> interfaceType, I context, BLOBF obf) {
		super(interfaceType, false ,obf);
		instance = context;
		init();
	}
	
	protected BufferedFunc(I context, Function<I, T, R> other) {
		super(other);
		instance = context;
		init();
	}
	
	private void init() {
		try {
			lambda = (T)handle.factory.bindTo(instance).invoke();
		} catch (Throwable e) {
			lambda = null;
			handle.factory = null;
		}
	}
	
	/**
	 * Invokes the underlying method with the given arguments and instance context.
	 * 
	 * @param args			Object array of arguments.
	 * <p>
	 * Note: Calling a method through its lambda can be considerably faster than using call or apply depending on usage.
	 * 
	 * @return	The returned result of the method
	 * @throws Throwable if there is any error.
	 */
	public R apply(Object... args) throws Throwable {
		return (R)handle.target.bindTo(instance).invokeWithArguments(args);
	}
	
	/**
	 * Gets a lambda object built from the given interface class with the underlying method as its implementation.
	 * <p>
	 * Note: Calling a method through its lambda can be considerably faster than using call or apply depending on usage.
	 * 
	 * @return lambda T
	 * @throws Throwable if there is any error.
	 */
	public T getLambda() {
		return lambda;
	}
	
	public void invalidate() {
		super.invalidate();
		lambda = null;
	}
	
	public boolean valid() {
		return super.valid() && lambda != null;
	}
	
	/**
	 * Creates a new BufferedFunc bound to the given instance
	 */
	public BufferedFunc<I, T, R> newWithInstance(I newInstance) {
		return new BufferedFunc(newInstance, this);
	}
}
