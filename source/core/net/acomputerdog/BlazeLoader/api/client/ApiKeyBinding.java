package net.acomputerdog.BlazeLoader.api.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import net.acomputerdog.BlazeLoader.api.general.ApiGeneral;
import net.acomputerdog.BlazeLoader.mod.Mod;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;

public class ApiKeyBinding {
	private static final List<KeyBinding> bindings = new ArrayList<KeyBinding>();
	private static final Map<Mod, List<KeyBinding>> bindingsRegister = new HashMap<Mod, List<KeyBinding>>();
	
	/**
	 * Gets an array of all KeyBindings registered in he game
	 * @return an array of KeyBinding objects
	 */
	public static KeyBinding[] getKeyBindings() {
		return ((List<KeyBinding>)KeyBinding.keybindArray).toArray(new KeyBinding[0]);
	}
	
	/**
	 * Gets an array of all keyBindings added by mods.
	 * @return an array of KeyBindings added using the ApiKeyBinding.registerKeyBinding method
	 */
	public static KeyBinding[] getModBindings() {
		return bindings.toArray(new KeyBinding[0]);
	}
	
	/**
	 * Gets an array of KeyBinding objects associated with a given Mod
	 * @param mod
	 * @return an array of KeyBinding objects added by the specified mod
	 */
	public static KeyBinding[] getBindingsForMod(Mod mod) {
		if (bindingsRegister.containsKey(mod)) {
			return bindingsRegister.get(mod).toArray(new KeyBinding[0]);
		}
		return new KeyBinding[0];
	}
		
	/**
	 * Registers a KeyBinding to be listed in the keyBindings gui
	 * Will return the KeyBinding for the mod to keep and check against the KeyBinding received in a key event
	 * to determine if they should handle the press.
	 * Mods should call this from their load method to ensure keybindings are correctly loaded from
	 * the games config file
	 * @param binding KeyBinding to be registered
	 * @return passed in KeyBinding
	 */
	public static KeyBinding registerKeyBinding(Mod mod, KeyBinding binding) {
		if (!bindings.contains(binding)) {
			bindings.add(binding);
			if (!bindingsRegister.containsKey(mod)) {
				bindingsRegister.put(mod, new ArrayList<KeyBinding>());
			}
			if (!bindingsRegister.get(mod).contains(binding)) {
				bindingsRegister.get(mod).add(binding);
			}
		}
		return binding;
	}
}
