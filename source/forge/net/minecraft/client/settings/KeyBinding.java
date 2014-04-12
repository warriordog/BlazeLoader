package net.minecraft.client.settings;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.acomputerdog.BlazeLoader.event.EventHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IntHashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SideOnly(Side.CLIENT)
public class KeyBinding implements Comparable {
    public static final List keybindArray = new ArrayList();
    private static final IntHashMap hash = new IntHashMap();
    private static final Set keybindSet = new HashSet();
    private final String keyDescription;
    private final int keyCodeDefault;
    private final String keyCategory;
    private int keyCode;
    /**
     * because _303 wanted me to call it that(Caironater)
     */
    private boolean pressed;
    private int pressTime;
    private static final String __OBFID = "CL_00000628";

    public static void onTick(int par0) {
        if (par0 != 0) {
            KeyBinding keybinding = (KeyBinding) hash.lookup(par0);

            if (keybinding != null) {
                ++keybinding.pressTime;
            }
        }
    }

    public static void setKeyBindState(int par0, boolean par1) {
        if (par0 != 0) {
            KeyBinding keybinding = (KeyBinding) hash.lookup(par0);

            if (keybinding != null) {
                if (keybinding.pressed != par1) {
                    keybinding.pressed = par1;
                    EventHandler.eventKey(keybinding);
                }
            }
        }
    }

    public static void unPressAllKeys() {

        for (Object aKeybindArray : keybindArray) {
            KeyBinding keybinding = (KeyBinding) aKeybindArray;
            keybinding.unpressKey();
        }
    }

    public static void resetKeyBindingArrayAndHash() {
        hash.clearMap();

        for (Object aKeybindArray : keybindArray) {
            KeyBinding keybinding = (KeyBinding) aKeybindArray;
            hash.addKey(keybinding.keyCode, keybinding);
        }
    }

    public static Set getKeybinds() {
        return keybindSet;
    }

    public KeyBinding(String p_i45001_1_, int p_i45001_2_, String p_i45001_3_) {
        this.keyDescription = p_i45001_1_;
        this.keyCode = p_i45001_2_;
        this.keyCodeDefault = p_i45001_2_;
        this.keyCategory = p_i45001_3_;
        keybindArray.add(this);
        hash.addKey(p_i45001_2_, this);
        keybindSet.add(p_i45001_3_);
    }

    public boolean getIsKeyPressed() {
        return this.pressed;
    }

    public String getKeyCategory() {
        return this.keyCategory;
    }

    public boolean isPressed() {
        if (this.pressTime == 0) {
            return false;
        } else {
            --this.pressTime;
            return true;
        }
    }

    private void unpressKey() {
        this.pressTime = 0;
        if (pressed) {
            pressed = false;
            EventHandler.eventKey(this);
        }
    }

    public String getKeyDescription() {
        return this.keyDescription;
    }

    public int getKeyCodeDefault() {
        return this.keyCodeDefault;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public void setKeyCode(int p_151462_1_) {
        this.keyCode = p_151462_1_;
    }

    public int compareTo(KeyBinding p_151465_1_) {
        int i = I18n.format(this.keyCategory, new Object[0]).compareTo(I18n.format(p_151465_1_.keyCategory));

        if (i == 0) {
            i = I18n.format(this.keyDescription, new Object[0]).compareTo(I18n.format(p_151465_1_.keyDescription));
        }

        return i;
    }

    public int compareTo(Object par1Obj) {
        return this.compareTo((KeyBinding) par1Obj);
    }
}