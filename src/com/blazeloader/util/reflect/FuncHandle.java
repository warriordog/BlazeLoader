package com.blazeloader.util.reflect;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.blazeloader.bl.obf.BLOBF;

public class FuncHandle {
	protected MethodHandle target;
	protected MethodHandle factory;
	
	protected final boolean hasLambda;
	protected final boolean staticMethod;
	
	protected FuncHandle(Class interfaceType, Class context, Class returnType, String name, boolean isStatic, Class... pars) {
		staticMethod = isStatic;
		hasLambda = interfaceType != null;
		MethodType getter = MethodType.methodType(returnType, pars);
		lookupMethod(interfaceType, context, returnType, name, getter);
	}
	
	protected FuncHandle(Class interfaceType, boolean isStatic, BLOBF obf) {
		this(interfaceType, isStatic, obf.getValue());
	}
	
	protected FuncHandle(Class interfaceType, boolean isStatic, String descriptor) {
		String ref = descriptor.split("(")[0].trim();
		
		String[] methodRef = ref.split(".");
		String className = "";
		String methodName = "";
		for (int i = 0; i < methodRef.length; i++) {
			if (className.length() > 0) {
				className += ".";
			}
			if (i < methodRef.length - 1) {
				className += methodRef[i];
			} else {
				methodName = methodRef[i];
				break;
			}
		}
		descriptor = descriptor.replace(className + "." + methodName, "").trim();
		
		String returnType = descriptor.replace(descriptor.split(")")[0] + ")", "");
		if (returnType.indexOf('.') != -1 && returnType.startsWith("L")) {
			returnType = returnType.substring(1, returnType.length() - 1).replace("/", ".");
		}
		
		Class contextC = Interop.getDeclaredClass(className);
		Class returnTypeC = Interop.getDeclaredClass(returnType);
		
		staticMethod = isStatic;
		hasLambda = interfaceType != null;
		MethodType getter = MethodType.fromMethodDescriptorString(descriptor, Interop.loader());
		lookupMethod(interfaceType, contextC, returnTypeC, methodName, getter);
	}
	
	private void lookupMethod(Class interfaceType, Class context, Class returnType, String name, MethodType getter) {
		MethodHandles.Lookup caller = MethodHandles.lookup();
		try {
			if (staticMethod) {
				target = caller.findStatic(context, name, getter);
				if (interfaceType != null) {
					CallSite site = LambdaMetafactory.metafactory(caller, name, MethodType.methodType(interfaceType), getter, target, getter);
					factory = site.getTarget();
				}
			} else {
				target = caller.findVirtual(context, name, getter);
				if (interfaceType != null) {
					CallSite site = LambdaMetafactory.metafactory(caller, name, MethodType.methodType(interfaceType, context), getter, target, getter);
					factory = site.getTarget();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
			target = null;
		}
	}
	
	public void invalidate() {
		target = null;
		factory = null;
	}
	
	public boolean valid() {
		return target != null && (!hasLambda || factory != null);
	}
	
	public boolean supportsLambda() {
		return hasLambda;
	}
}
