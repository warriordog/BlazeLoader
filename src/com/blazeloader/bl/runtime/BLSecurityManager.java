package com.blazeloader.bl.runtime;

import java.lang.reflect.Field;
import java.security.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.blazeloader.bl.main.BLMain;

/**
 * WIP.
 * <p>
 * A custom SecurityManager put in place to allow BlazeLoader (and other packages if they so desire)
 * to exit the JVM without being wrist slapped by Forge Modloader.
 * <p>
 * Required because Forge consists of a bunch of self entitled know-it-all control freaks who think their way is best,
 * says anyone who doesn't agree with them is wrong, and are so immersed in their own delusion
 * that they think they must play 'Modding Police' for all of us 'lower pond scum'.
 */
public final class BLSecurityManager extends SecurityManager {
	private static final BLSecurityManager instance = setSecurityManager();
	
	private SecurityManager wrapped;
	private final List<String> packageExceptions;
	
	/**
	 * Replace the existing security manager with one that permits calls from inside blazeloader.
	 */
	private static BLSecurityManager setSecurityManager() {
		SecurityManager manager = System.getSecurityManager();
		if (!(manager instanceof BLSecurityManager)) {
			return (new BLSecurityManager(manager)).set();
		}
		return null;
	}
	
	private synchronized BLSecurityManager set() {
		try {
			Field security = System.class.getField("SecurityManager");
			security.setAccessible(true);
			security.set(null, null); // Remove the existing security manager.
			System.setSecurityManager(this);
			BLMain.LOGGER_MAIN.logInfo("Setting recurity manager...");
			return this;
		} catch (Throwable e) {
			BLMain.LOGGER_MAIN.logFatal("Exception whist registering security manager: ", e);
		}
		return null;
	}
	
	/**
	 * Registers a package as an exception to FML's check.
	 * @param packageString		The package that wants to call System.exit
	 */
	public static void addVMExitException(String packageString) {
		if (instance != null) {
			instance.addPackageException(packageString);
		}
	}
	
	private BLSecurityManager(SecurityManager old) {
		super();
		wrapped = old;
		packageExceptions = new ArrayList<String>();
	}
	
	private void addPackageException(String packageString) {
		packageExceptions.add(packageString);
	}
	
	public void checkPermission(Permission perm) {
		String permName = perm.getName() != null ? perm.getName() : "missing";
		if ("setSecurityManager".equals(permName)) {
			throw new SecurityException("Cannot replace the BL security manager");
		} else {
			Class[] classContexts = getClassContext();
			
	        String callingClass = classContexts.length > 3 ? classContexts[4].getName() : "none";
	        String callingParent = classContexts.length > 4 ? classContexts[5].getName() : "none";
			if ("createSecurityManager".equals(permName)) {
				if (callingClass.startsWith("net.minecraftforge.fml")) {
					System.out.println("FML is attempting to replace the BL security manager. Changes will be reverted shortly");
					Calendar cal = Calendar.getInstance();
					cal.set(Calendar.SECOND, cal.get(Calendar.SECOND + 1));
					(new Timer()).schedule(new TimerTask() {
						@Override
						public void run() {
							set();
						}
					}, cal.getTime());
				}
			} else if (permName.startsWith("exitVM")) {
				if (wrapped != null) {
		            for (String i : packageExceptions) {
						if (callingClass.startsWith(i)) {
							super.checkPermission(perm);
							return;
						}
					}
	            }
			}
		}
		wrapped.checkPermission(perm);
		super.checkPermission(perm);
	}
}
