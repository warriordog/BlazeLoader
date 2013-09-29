package net.acomputerdog.BlazeLoader.mod;

/**
 * Superclass to be extended by mods that wish to use BlazeLoader functionality.  Methods have default implementations, but some should be overridden.
 */
public abstract class Mod {
    /**
     *  Returns ID used to identify this mod internally, even among different versions of the same mod.  Mods should override.
     *  --This should never be changed after the mod has been released!--
     * @return Returns the id of the mod.
     */
    public String getModId(){
        return this.getClass().getName();
    }

    /**
     * Returns the user-friendly name of the mod.  Mods should override.
     * --Can be changed among versions, so this should not be used to ID mods!--
     * @return Returns user-friendly name of the mod.
     */
    public String getModName(){
        return this.getClass().getSimpleName();
    }

    /**
     * Returns true if: obj != null and obj == this or obj.getModId() == this.getModId().
     * @param obj Object to compare to.
     * @return If obj is a mod of the same type as this mod.
     */
    @Override
    public boolean equals(Object obj) {
        if(obj == this)return true;
        if(obj == null)return false;
        if(obj instanceof Mod){
            return ((Mod)obj).getModId().equals(this.getModId());
        }else{
            return false;
        }
    }
}
