package net.acomputerdog.BlazeLoader.util.config;

import net.acomputerdog.BlazeLoader.api.base.ApiBase;
import net.acomputerdog.BlazeLoader.mod.Mod;

import java.io.File;
import java.io.IOException;

/**
 * An auto-saving and auto-loading config file for mods.  Saves to the registered config directory.
 * To use ModConfig, mods should create a class that extends ModConfig and add fields to be set.
 * All fields not marked as transient will be saved and loaded.  All primitives and Strings will be
 * saved to a Properties-format file.  Make SURE that values are set in the constructor, NOT hardcoded into the class!
 * Otherwise the compiler will REMOVE the fields and convert them to constants!
 * Also be sure to call "loadSettings()" at the end of your constructor or the settings will not be loaded.
 * A sample constructor looks like this:
 *
 * public SomeModConfig(Mod owner){
 *     super(owner);
 *     valueOfSomething = 42;
 *     nameOfSomething = "some name";
 *     valueOfSomethingElse = .12345;
 *     loadSettings();
 * }
 */
public abstract class ModConfig {
    protected transient Mod owner;
    protected transient File configFile;

    /**
     * Creates a new config file using fileName as the filename.
     * @param owner The mod that created this config.
     * @param fileName The filename to use.
     */
    public ModConfig(Mod owner, String fileName){
        if(owner == null){
            throw new IllegalArgumentException("owner cannot be null!");
        }
        if(fileName == null){
            throw new IllegalArgumentException("fileName cannot be null!");
        }
        this.owner = owner;
        this.configFile = new File(ApiBase.configDir, fileName);
        if(!configFile.exists()){
            try {
                if(!(new File(configFile.getParent()).mkdirs() || new File(configFile.getName()).createNewFile())){
                    throw new RuntimeException("Could not create new file!");
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not create new file!", e);
            }
        }
        ConfigList.addConfig(this);
    }

    /**
     * Loads this ModConfig from the config directory, or creates it if it does not exist.
     */
    public void loadConfig(){
        ConfigList.loadConfig(this);
    }

    /**
     * Saves this ModConfig to the config directory.
     */
    public void saveConfig(){
        ConfigList.saveConfig(this);
    }

    /**
     * Get the file to save to.
     * @return Return the File to save to.
     */
    public File getSaveFile(){
        return this.configFile;
    }

    /**
     * Gets the owner of the ModConfig.
     * @return Return the Mod that owns this config.
     */
    public Mod getOwner(){
        return this.owner;
    }
}
