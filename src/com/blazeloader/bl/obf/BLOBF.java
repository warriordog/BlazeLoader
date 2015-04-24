package com.blazeloader.bl.obf;

import com.blazeloader.util.version.Versions;
import com.mumfrey.liteloader.core.runtime.Obf;

import net.acomputerdog.OBFUtil.parse.types.BLOBFParser;
import net.acomputerdog.OBFUtil.util.TargetType;
import net.acomputerdog.core.java.Patterns;

/**
 * BlazeLoader extension of LL's Obf class
 */
public class BLOBF extends Obf {

    /**
     * The simple (mcp, no package) name of this BLOBF
     */
    public final String simpleName;

    /**
     * Creates a new BLOBF.
     *
     * @param obfName    The obfuscated name of the class
     * @param seargeName The searge name of the class
     * @param mcpName    The deobfuscated (mcp) name of the class
     */
    public BLOBF(String obfName, String seargeName, String mcpName) {
        super(seargeName, obfName, mcpName);
        String[] nameParts = mcpName.split(Patterns.PERIOD);
        this.simpleName = nameParts.length > 0 ? nameParts[nameParts.length - 1] : mcpName;
    }

    /**
     * Gets the obf/srg/mcp name of this class based on the current obfuscation mode.
     *
     * @return Return the mcp name if the game is deobfuscated, return the srg name if forge is installed, return the obf name if the game is obfuscated.
     */
    public String getValue() {
        if (!Versions.isGameObfuscated()) {
            return super.name;
        }
        if (Versions.isForgeInstalled()) {
            return super.srg;
        }
        return super.obf;
    }

    //-----------------------------[Static Stuff]------------------------------------

    /**
     * BL's central obfuscation table, contains all raw package, class, method, and field obfuscation mappings.
     */
    public static final BLOBFTable OBF = loadOBF();

    private static BLOBFTable loadOBF() {
        try {
            BLOBFTable obf = new BLOBFTable();
            //TODO: Add support for loading server obfuscation tables
            new BLOBFParser(false).loadEntries(BLOBF.class.getResourceAsStream("/conf/minecraft_client.obf"), obf, true);
            return obf;
        } catch (Exception e) {
            throw new RuntimeException("Unable to load obfuscation table; BlazeLoader cannot start!", e);
        }
    }

    /**
     * Gets a BLOBF from an obfuscated name.
     *
     * @param obfName The obfuscated name.
     * @param type    The type of object to get.
     * @param level	  The obfuscation level MCP/SRG/OBF
     * @return Return a BLOBF created from an obfuscated name.
     */
    public static BLOBF getOBF(String obfName, TargetType type, OBFLevel level) {
        return OBF.getBLOBF(obfName, type, level);
    }

    /**
     * Gets a BLOBF from an obfuscated class name
     *
     * @param obfName The obfuscated name.
     * @param level	  The obfuscation level MCP/SRG/OBF
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getClass(String obfName, OBFLevel level) {
        return getOBF(obfName, TargetType.CLASS, level);
    }
    
    public static BLOBF getConstructor(String className, OBFLevel level, String... params) {
    	return OBF.getConstructor(className, level, params);
    }

    /**
     * Gets a BLOBF from an obfuscated package name
     *
     * @param obfName The obfuscated name.
     * @param level	  The obfuscation level MCP/SRG/OBF
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getPackage(String obfName, OBFLevel level) {
        return getOBF(obfName, TargetType.PACKAGE, level);
    }

    /**
     * Gets a BLOBF from an obfuscated method name
     *
     * @param obfName The obfuscated name.
     * @param level	  The obfuscation level MCP/SRG/OBF
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getMethod(String obfName, OBFLevel level) {
        return getOBF(obfName, TargetType.METHOD, level);
    }

    /**
     * Gets a BLOBF from an obfuscated field name
     *
     * @param obfName The obfuscated name.
     * @param level	  The obfuscation level MCP/SRG/OBF
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getField(String obfName, OBFLevel level) {
        return getOBF(obfName, TargetType.FIELD, level);
    }
}
