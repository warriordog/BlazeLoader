package net.BlazeLoader.util.config;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Contains a list of ModConfigs.  Handles reflection necessary to load/save fields.
 */
public class ConfigList {
    private static List<ModConfig> configs = new ArrayList<ModConfig>();

    public static void addConfig(ModConfig config) {
        configs.add(config);
    }

    public static void loadConfig(ModConfig config) {
        try {
            File loadFile = config.getSaveFile();
            if (!loadFile.exists()) {
                saveConfig(config);
            }
            Properties properties = new Properties();
            properties.load(new FileInputStream(loadFile));
            for (Field f : config.getClass().getDeclaredFields()) {
                if (!Modifier.isTransient(f.getModifiers())) {
                    f.setAccessible(true);
                    loadField(f, properties, config);
                }
            }
        } catch (Exception e) {
            BlazeLoader.getLogger().logError("Could not load config for mod " + config.getOwner().getModName());
            e.printStackTrace();
        }
    }

    public static void saveConfig(ModConfig config) {
        Properties saveFile = new Properties();
        for (Field f : config.getClass().getDeclaredFields()) {
            if (!Modifier.isTransient(f.getModifiers())) {
                f.setAccessible(true);
                saveField(f, saveFile, config);
            }
        }
        try {
            saveFile.store(new BufferedOutputStream(new FileOutputStream(config.configFile)), config.getOwner().getModName());
        } catch (IOException e) {
            BlazeLoader.getLogger().logError("Could not save config for mod: " + config.getOwner().getModName());
            e.printStackTrace();
        }
    }

    private static void saveField(Field field, Properties properties, ModConfig config) {
        try {
            if (int.class.isAssignableFrom(field.getType()) || byte.class.isAssignableFrom(field.getType()) || boolean.class.isAssignableFrom(field.getType()) ||
                    long.class.isAssignableFrom(field.getType()) || double.class.isAssignableFrom(field.getType()) || float.class.isAssignableFrom(field.getType()) ||
                    float.class.isAssignableFrom(field.getType()) || char.class.isAssignableFrom(field.getType()) || short.class.isAssignableFrom(field.getType()) ||
                    String.class.isAssignableFrom(field.getType())) {
                Object value = field.get(config);
                if (value != null) {
                    properties.setProperty(field.getName(), value.toString());
                } else {
                    BlazeLoader.getLogger().logWarning("Skipping null field " + field.getName());
                }
            } else {
                BlazeLoader.getLogger().logWarning("Skipping invalid config field " + field.getName() + " from mod " + config.getOwner().getModName());
            }
        } catch (Exception e) {
            BlazeLoader.getLogger().logError("Error saving config field " + field.getName() + " from mod " + config.getOwner().getModName());
            e.printStackTrace();
        }
    }

    private static void loadField(Field field, Properties properties, ModConfig config) {
        try {
            Object value = field.get(config);
            if (value != null) {
                if (int.class.isAssignableFrom(field.getType())) {
                    field.set(config, Integer.parseInt(properties.getProperty(field.getName(), value.toString())));
                } else if (byte.class.isAssignableFrom(field.getType())) {
                    field.set(config, Byte.parseByte(properties.getProperty(field.getName(), value.toString())));
                } else if (boolean.class.isAssignableFrom(field.getType())) {
                    field.set(config, Boolean.parseBoolean(properties.getProperty(field.getName(), value.toString())));
                } else if (long.class.isAssignableFrom(field.getType())) {
                    field.set(config, Long.parseLong(properties.getProperty(field.getName(), value.toString())));
                } else if (double.class.isAssignableFrom(field.getType())) {
                    field.set(config, Double.parseDouble(properties.getProperty(field.getName(), value.toString())));
                } else if (float.class.isAssignableFrom(field.getType())) {
                    field.set(config, Float.parseFloat(properties.getProperty(field.getName(), value.toString())));
                } else if (char.class.isAssignableFrom(field.getType())) {
                    field.set(config, (properties.getProperty(field.getName(), value.toString())).toCharArray()[0]);
                } else if (short.class.isAssignableFrom(field.getType())) {
                    field.set(config, Short.parseShort(properties.getProperty(field.getName(), value.toString())));
                } else if (String.class.isAssignableFrom(field.getType())) {
                    field.set(config, properties.getProperty(field.getName(), value.toString()));
                } else {
                    BlazeLoader.getLogger().logWarning("Skipping invalid config field " + field.getName() + " from mod " + config.getOwner().getModName());
                }
            } else {
                BlazeLoader.getLogger().logWarning("Skipping null field " + field.getName());
            }
        } catch (Exception e) {
            BlazeLoader.getLogger().logError("Error loading config field " + field.getName() + " from mod " + config.getOwner().getModName());
            e.printStackTrace();
        }
    }

    public static void saveAllConfigs() {
        for (ModConfig config : configs) {
            try {
                config.saveConfig();
            } catch (Exception e) {
                BlazeLoader.getLogger().logError("Unknown error saving config for mod " + config.getOwner().getModName());
                e.printStackTrace();
            }
        }
    }
}
