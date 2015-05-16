package com.blazeloader.bl.interop;

import com.blazeloader.util.reflect.Func;
import com.blazeloader.util.reflect.Reflect;
import com.blazeloader.util.version.Versions;

public final class ForgeModloader {
	private static Func<?, ForgeMLAccess, ?> _getInstance;
	private static Func<?, ForgeMLAccess, Void> _exitJava;
	
	private static Object getFMLCommonHandler() {
		if (Versions.isForgeInstalled()) {
			//return net.minecraftforge.fml.common.FMLCommonHandler.instance();
			if (_getInstance == null) {
				_getInstance = Reflect.lookupStaticMethod(ForgeMLAccess.class, "net.minecraftforge.fml.common.FMLCommonHandler.instance ()Lnet/minecraftforge/fml/common/FMLCommonHandler;");
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
			if (instance != null) {
				//instance.exitJava(exitCode, false);
				if (_exitJava == null) {
					_exitJava = Reflect.lookupMethod(ForgeMLAccess.class, "net.minecraftforge.fml.common.FMLCommonHandler.exitJava (IZ)V");
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
}
