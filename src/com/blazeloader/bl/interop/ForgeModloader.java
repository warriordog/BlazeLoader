package com.blazeloader.bl.interop;

import com.blazeloader.util.reflect.Func;
import com.blazeloader.util.reflect.Interop;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.version.Versions;

public final class ForgeModloader {
	
	private static final Class common = Interop.getDeclaredClass("net.minecraftforge.fml.common.FMLCommonHandler");
	
	private static Func<?, ForgeMLAccess, ?> _getInstance;
	private static Func<?, ForgeMLAccess, Void> _exitJava;
	
	private static Object getFMLCommonHandler() {
		if (Versions.isForgeInstalled()) {
			//return net.minecraftforge.fml.common.FMLCommonHandler.instance();
			if (_getInstance == null) {
				_getInstance = Reflect.lookupStaticMethod(ForgeMLAccess.class, common, common, "instance");
			}
			if (_getInstance.valid()) {
				try {
					return _getInstance.call();
				} catch (Throwable e) {
					_getInstance.invalidate();
				}
			}
		}
		return null;
	}
	
	public static void exitJVM(int exitCode) {
		if (Versions.isForgeInstalled()) {
			Object instance = getFMLCommonHandler();
			//instance.exitJava(exitCode, false);
			if (instance != null) {
				if (_exitJava == null) {
					_exitJava = Reflect.lookupMethod(ForgeMLAccess.class, common, void.class, "exitJava", int.class, boolean.class);
				}
				if (_exitJava.valid()) {
					try {
						_exitJava.getLambda(instance).exitJava(exitCode, false);
					} catch (Throwable e) {
						_exitJava.invalidate();
					}
					return;
				}
			}
		}
		System.exit(exitCode);
	}
	
	private interface ForgeMLAccess {
		
		public void exitJava(int exitCode, boolean hardExit);
	}
}
