package com.blazeloader.util.reflect;

import com.blazeloader.bl.obf.BLOBF;

public class Constr<I> extends Function<I, Object, Void> {
	public Constr(Class<I> context, Class... pars) {
		super(null, context, void.class, "<init>", false, pars);
	}
	
	public Constr(BLOBF obf) {
		super(null, false, obf);
		if (!handle.isConstr()) {
			throw new IllegalArgumentException(handle + " is not a valid Constructor!");
		}
	}
	
	public Constr(String descriptor) {
		super(null, false, descriptor);
		if (!handle.isConstr()) {
			throw new IllegalArgumentException(handle + " is not a valid Constructor!");
		}
	}
	
	/**
	 * Creates a new instance of an object using the underlying constructor.
	 */
	public I call(Object... args) throws Throwable {
		return (I)handle.target.invokeWithArguments(args);
	}
}
