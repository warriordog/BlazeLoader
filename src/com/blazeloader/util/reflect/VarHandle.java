package com.blazeloader.util.reflect;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import com.blazeloader.bl.obf.BLOBF;

public class VarHandle {

	protected MethodHandle get;
	protected MethodHandle set;
	
	protected String varName;
	
	protected final boolean staticField;
	
	protected final String declaringContext;
	
	public VarHandle(Class declarer, Class type, boolean isStatic, String name) {
		staticField = isStatic;
		declaringContext = declarer.getCanonicalName();
		lookupVariable(declarer, type, name);
	}
	
	public VarHandle(boolean isStatic, BLOBF obf) {
		this(isStatic, obf.getValue());
	}
	
	public VarHandle(boolean isStatic, String descriptor) {
		staticField = isStatic;
		String[] methodRef = descriptor.split(".");
		String className = "";
		String fieldName = "";
		for (int i = 0; i < methodRef.length; i++) {
			if (className.length() > 0) {
				className += ".";
			}
			if (i < methodRef.length - 1) {
				className += methodRef[i];
			} else {
				fieldName = methodRef[i];
				break;
			}
		}
		
		String returnType = descriptor.replace(className + "." + fieldName, "").trim();
		if (returnType.indexOf('.') != -1 && returnType.startsWith("L")) {
			returnType = returnType.substring(1, returnType.length() - 1).replace("/", ".");
		}
		
		Class declarer = Interop.getDeclaredClass(className);
		Class type = returnType.isEmpty() ? null : Interop.getDeclaredClass(returnType);
		
		declaringContext = className;
		lookupVariable(declarer, type, fieldName);
	}
	
	private void lookupVariable(Class declarer, Class type, String name) {
		varName = name;
		try {
			MethodHandles.Lookup lookup = MethodHandles.lookup();
			
			if (type != null) {
				try {
					if (staticField) {
						get = lookup.findStaticGetter(declarer, name, type);
						set = lookup.findStaticSetter(declarer, name, type);
					} else {
						get = lookup.findGetter(declarer, name, type);
						set = lookup.findSetter(declarer, name, type);
					}
				} catch (Throwable e) {
					type = null;
				}
			}
			
			if (type == null) {
				Field field = declarer.getDeclaredField(name);
				if (field != null) {
					field.setAccessible(true);
					get = lookup.unreflectGetter(field);
					set = lookup.unreflectSetter(field);
				}
			}
		} catch (Throwable e) {
			get = set = null;
		}
	}
	
	public void invalidate() {
		get = null;
		set = null;
	}
	
	public boolean valid() {
		return get != null && set != null;
	}
	
	public String descriptor() {
		return declaringContext + "." + varName;
	}
	
	public String toString() {
		return (get == null ? "Object" : get.type().returnType().getSimpleName()) + " " + varName + ";";
	}
}
