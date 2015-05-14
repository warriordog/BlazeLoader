package com.blazeloader.util.reflect;

import com.blazeloader.bl.obf.BLOBF;

public abstract class Function<I, T, R> {
	
	protected final FuncHandle handle;
	
	protected Function(Class<T> interfaceType, Class<I> context, Class<R> returnType, String name, boolean isStatic, Class... pars) {
		handle = new FuncHandle(interfaceType, context, returnType, name, isStatic, pars);
	}
	
	protected Function(Class<T> interfaceType, boolean isStatic, BLOBF obf) {
		handle = new FuncHandle(interfaceType, isStatic, obf);
	}
	
	protected Function(Class<T> interfaceType, boolean isStatic, String descriptor) {
		handle = new FuncHandle(interfaceType, isStatic, descriptor);
	}
	
	protected Function(Function<I, T,R> other) {
		handle = other.handle;
	}
	
	/**`
	 * Marks this as broken.
	 */
	public void invalidate() {
		handle.invalidate();
	}
	
	/**
	 * Checks if this is still a functional method.
	 * @return true if you can safely call it.
	 */
	public boolean valid() {
		return handle.valid();
	}
	
	/**
	 * Checks if this function was created with an interface for constructing a lambda.
	 * @return
	 */
	public boolean supportsLambda() {
		return handle.supportsLambda();
	}
}
