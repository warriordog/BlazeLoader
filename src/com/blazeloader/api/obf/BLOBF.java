package com.blazeloader.api.obf;

import com.blazeloader.api.version.Versions;
import com.mumfrey.liteloader.core.runtime.Obf;
import net.acomputerdog.OBFUtil.parse.types.BLOBFParser;
import net.acomputerdog.OBFUtil.util.TargetType;

import java.util.regex.Pattern;

/**
 * BlazeLoader extension of LL's Obf class
 */
public class BLOBF extends Obf {
    private static final String PERIOD = Pattern.quote(".");

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
        String[] nameParts = mcpName.split(PERIOD);
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
     * @return Return a BLOBF created from an obfuscated name.
     */
    public static BLOBF getOBF(String obfName, TargetType type) {
        return OBF.getOBF(obfName, type);
    }

    /**
     * Gets a BLOBF from a searge name.
     *
     * @param srgName The searge name.
     * @param type    The type of object to get.
     * @return Return a BLOBF created from a searge name.
     */
    public static BLOBF getSRG(String srgName, TargetType type) {
        return OBF.getSRG(srgName, type);
    }

    /**
     * Gets a BLOBF from an mcp name.
     *
     * @param mcpName The mcp name.
     * @param type    The type of object to get.
     * @return Return a BLOBF created from an mcp name.
     */
    public static BLOBF getMCP(String mcpName, TargetType type) {
        return OBF.getMCP(mcpName, type);
    }

    /**
     * Gets a BLOBF from an obfuscated class name
     *
     * @param obfName The obfuscated name.
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getClassOBF(String obfName) {
        return getOBF(obfName, TargetType.CLASS);
    }

    /**
     * Gets a BLOBF from an obfuscated package name
     *
     * @param obfName The obfuscated name.
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getPackageOBF(String obfName) {
        return getOBF(obfName, TargetType.PACKAGE);
    }

    /**
     * Gets a BLOBF from an obfuscated method name
     *
     * @param obfName The obfuscated name.
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getMethodOBF(String obfName) {
        return getOBF(obfName, TargetType.METHOD);
    }

    /**
     * Gets a BLOBF from an obfuscated field name
     *
     * @param obfName The obfuscated name.
     * @return Return a BLOBF representing obfName
     */
    public static BLOBF getFieldOBF(String obfName) {
        return getOBF(obfName, TargetType.FIELD);
    }

    /**
     * Gets a BLOBF from a searge class name
     *
     * @param srgName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getClassSRG(String srgName) {
        return getSRG(srgName, TargetType.CLASS);
    }

    /**
     * Gets a BLOBF from a searge package name
     *
     * @param srgName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getPackageSRG(String srgName) {
        return getSRG(srgName, TargetType.PACKAGE);
    }

    /**
     * Gets a BLOBF from a searge method name
     *
     * @param srgName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getMethodSRG(String srgName) {
        return getSRG(srgName, TargetType.METHOD);
    }

    /**
     * Gets a BLOBF from a searge field name
     *
     * @param srgName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getFieldSRG(String srgName) {
        return getSRG(srgName, TargetType.FIELD);
    }

    /**
     * Gets a BLOBF from an mcp class name
     *
     * @param mcpName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getClassMCP(String mcpName) {
        return getMCP(mcpName, TargetType.CLASS);
    }

    /**
     * Gets a BLOBF from an mcp package name
     *
     * @param mcpName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getPackageMCP(String mcpName) {
        return getMCP(mcpName, TargetType.PACKAGE);
    }

    /**
     * Gets a BLOBF from an mcp method name
     *
     * @param mcpName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getMethodMCP(String mcpName) {
        return getMCP(mcpName, TargetType.METHOD);
    }

    /**
     * Gets a BLOBF from an mcp field name
     *
     * @param mcpName The obfuscated name.
     * @return Return a BLOBF representing srgName
     */
    public static BLOBF getFieldMCP(String mcpName) {
        return getMCP(mcpName, TargetType.FIELD);
    }
}
