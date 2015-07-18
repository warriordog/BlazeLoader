package com.blazeloader.util.reflect;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaConversionException;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.blazeloader.bl.obf.BLOBF;

public class FuncHandle {
	protected MethodHandle target;
	protected MethodHandle factory;
	
	protected final boolean hasLambda;
	protected final boolean staticMethod;
	
	protected String funcName;
	protected String parameterString;
	
	protected final String declaringContext;
	
	protected FuncHandle(Class interfaceType, Class context, Class returnType, String name, boolean isStatic, Class... pars) {
		staticMethod = isStatic;
		hasLambda = interfaceType != null;
		declaringContext = context.getCanonicalName();
		MethodType getter = MethodType.methodType(isConstr(name) ? void.class : returnType, pars);
		lookupMethod(interfaceType, context, name, getter);
	}
	
	protected FuncHandle(Class interfaceType, boolean isStatic, BLOBF obf) {
		this(interfaceType, isStatic, obf.getValue());
	}
	
	protected FuncHandle(Class interfaceType, boolean isStatic, String descriptor) {
		String ref = descriptor.split("\\(")[0].trim();
		
		String[] methodRef = ref.split("\\.");
		String className = "";
		String methodName = "";
		for (int i = 0; i < methodRef.length; i++) {
			if (i < methodRef.length - 1) {
				if (className.length() > 0) {
					className += ".";
				}
				className += methodRef[i];
			} else {
				methodName = methodRef[i];
				break;
			}
		}
		ref = descriptor.replace(className + "." + methodName, "").trim();
		
		Class contextC = Interop.getDeclaredClass(className);
		
		staticMethod = isStatic;
		hasLambda = interfaceType != null;
		declaringContext = className;
		MethodType getter = MethodType.fromMethodDescriptorString(ref, Interop.loader());
		lookupMethod(interfaceType, contextC, methodName, getter);
	}
	
	private final Lookup trustedLookup(Lookup caller) {
		try {
			Field privilaged = Lookup.class.getDeclaredField("IMPL_LOOKUP");
			privilaged.setAccessible(true);
			Lookup trusted = (Lookup)privilaged.get(caller);
			return trusted.in(caller.lookupClass());
		} catch (Throwable e) {}
		return caller;
	}
	
	private void lookupMethod(Class interfaceType, Class context, String name, MethodType getter) {
		funcName = name;
		Lookup caller = trustedLookup(MethodHandles.lookup().in(context));
		
		try {
			target = findMethod(caller, context, name, getter);
			if (!isConstr(name)) {
				if (interfaceType != null) {
					MethodType fact;
					if (staticMethod) {
						fact = MethodType.methodType(interfaceType);
					} else {
						fact = MethodType.methodType(interfaceType, context);
					}
					try {
						buildFactory(name, caller, fact, getter);
					} catch (Throwable e) {
						e.printStackTrace();
						System.out.println("Unable to create factory, lambda disabled for " + target);
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			target = null;
		}
	}
	
	private MethodHandle findMethod(MethodHandles.Lookup caller, Class context, String name, MethodType getter) throws Throwable {
		try {
			if (isConstr(name)) {
				return caller.findConstructor(context, getter);
			} else if (staticMethod) {
				return caller.findStatic(context, name, getter);
			} else {
				return caller.findVirtual(context, name, getter);
			}
		} catch (Throwable e) {
			if (isConstr(name)) {
				Constructor c = context.getConstructor(getter.parameterArray());
				c.setAccessible(true);
				return caller.unreflectConstructor(c);
			} else {
				Method m = context.getDeclaredMethod(name, getter.parameterArray());
				m.setAccessible(true);
				return caller.unreflect(m);
			}
		}
	}
	
	private void buildFactory(String name, MethodHandles.Lookup caller, MethodType fact, MethodType getter) throws LambdaConversionException, IllegalAccessException {
		CallSite site = LambdaMetafactory.metafactory(caller, name, fact, getter, target, getter);
		factory = site.getTarget();
	}
	
	private String buildParameterString() {
		String parameterTypes = "";
		if (target != null) {
			Class[] pars = target.type().parameterArray();
			for (int i = 0; i < pars.length; i++) {
				if (!parameterTypes.isEmpty()) {
					parameterTypes += ",";
				}
				parameterTypes += " " + pars[i].getName() + "par" + i;
			}
		}
		return parameterTypes;
	}
	
	public void invalidate() {
		target = null;
		factory = null;
	}
	
	public boolean valid() {
		return target != null && (!hasLambda || factory != null);
	}
	
	public boolean supportsLambda() {
		return hasLambda && factory != null;
	}
	
	public boolean isConstr() {
		return isConstr(funcName);
	}
	
	public String descriptor() {
		return declaringContext + "." + funcName + " " + (target == null ? "[unbound]" : target.type().toMethodDescriptorString());
	}
	
	public String toString() {
		String returnType = target == null ? "Object" : target.type().returnType().getSimpleName();
		if (parameterString == null) {
			parameterString = buildParameterString();
		}
		return (staticMethod ? "static " : "") + returnType + " " + funcName + " (" + parameterString + ");";
	}
	
	public static boolean isConstr(String name) {
		return "<init>".contentEquals(name);
	}
}
