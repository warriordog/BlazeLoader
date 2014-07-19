package net.acomputerdog.BlazeLoader.util.obf;

import com.mumfrey.liteloader.core.runtime.Obf;
import net.acomputerdog.BlazeLoader.version.Version;
import net.acomputerdog.OBFUtil.parse.types.BLOBFParser;
import net.acomputerdog.OBFUtil.util.TargetType;

import java.util.regex.Pattern;

//TODO: Make compatible with Mumfrey's obfuscation system
public class BLOBF extends Obf {
    private static final String PERIOD = Pattern.quote(".");

    public final String simpleName;

    /**
     * @param obfName
     * @param seargeName
     * @param mcpName
     */
    public BLOBF(String obfName, String seargeName, String mcpName) {
        super(seargeName, obfName, mcpName);
        String[] nameParts = mcpName.split(PERIOD);
        this.simpleName = nameParts.length > 0 ? nameParts[nameParts.length - 1] : mcpName;
    }

    /**
     * @param obfName
     * @param seargeName
     * @param mcpName
     */
    public BLOBF(String obfName, String seargeName, String mcpName, String obfDesc, String seargeDesc, String mcpDesc) {
        this(seargeName, obfName, mcpName);

    }

    public String getValue() {
        if (!Version.isGameObfuscated()) {
            return super.name;
        }
        if (Version.isForgeInstalled()) {
            return super.srg;
        }
        return super.obf;
    }

    //-----------------------------[Static Stuff]------------------------------------

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

    public static BLOBF getOBF(String obfName, TargetType type) {
        return OBF.getOBF(obfName, type);
    }

    public static BLOBF getSRG(String srgName, TargetType type) {
        return OBF.getSRG(srgName, type);
    }

    public static BLOBF getMCP(String mcpName, TargetType type) {
        return OBF.getMCP(mcpName, type);
    }

    public static BLOBF getSMP(String smpName, TargetType type) {
        return OBF.getSMP(smpName, type);
    }

    public static BLOBF getClassOBF(String obfName) {
        return getOBF(obfName, TargetType.CLASS);
    }

    public static BLOBF getPackageOBF(String obfName) {
        return getOBF(obfName, TargetType.PACKAGE);
    }

    public static BLOBF getMethodOBF(String obfName) {
        return getOBF(obfName, TargetType.METHOD);
    }

    public static BLOBF getFieldOBF(String obfName) {
        return getOBF(obfName, TargetType.FIELD);
    }

    public static BLOBF getClassSRG(String srgName) {
        return getSRG(srgName, TargetType.CLASS);
    }

    public static BLOBF getPackageSRG(String srgName) {
        return getSRG(srgName, TargetType.PACKAGE);
    }

    public static BLOBF getMethodSRG(String srgName) {
        return getSRG(srgName, TargetType.METHOD);
    }

    public static BLOBF getFieldSRG(String srgName) {
        return getSRG(srgName, TargetType.FIELD);
    }

    public static BLOBF getClassMCP(String mcpName) {
        return getMCP(mcpName, TargetType.CLASS);
    }

    public static BLOBF getPackageMCP(String mcpName) {
        return getMCP(mcpName, TargetType.PACKAGE);
    }

    public static BLOBF getMethodMCP(String mcpName) {
        return getMCP(mcpName, TargetType.METHOD);
    }

    public static BLOBF getFieldMCP(String mcpName) {
        return getMCP(mcpName, TargetType.FIELD);
    }

    public static BLOBF getClassSMP(String smpName) {
        return getSMP(smpName, TargetType.CLASS);
    }

    public static BLOBF getPackageSMP(String smpName) {
        return getSMP(smpName, TargetType.PACKAGE);
    }

    public static BLOBF getMethodSMP(String smpName) {
        return getSMP(smpName, TargetType.METHOD);
    }

    public static BLOBF getFieldSMP(String smpName) {
        return getSMP(smpName, TargetType.FIELD);
    }
}
