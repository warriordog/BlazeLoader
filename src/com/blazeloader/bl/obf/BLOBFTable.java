package com.blazeloader.bl.obf;

import net.acomputerdog.OBFUtil.table.DirectOBFTableSRG;
import net.acomputerdog.OBFUtil.util.TargetType;

import java.util.HashMap;
import java.util.Map;

/**
 * BlazeLoader OBFTable that allows converting stored data into BLOBFs.
 * Provided methods automatically cache calls, so repeated calls with the same parameters will return the same BLOBF object.
 */
public class BLOBFTable extends DirectOBFTableSRG {
    private final Map<TargetType, Map<String, BLOBF>> obfNameMap = new HashMap<TargetType, Map<String, BLOBF>>();
    private final Map<TargetType, Map<String, BLOBF>> srgNameMap = new HashMap<TargetType, Map<String, BLOBF>>();
    private final Map<TargetType, Map<String, BLOBF>> mcpNameMap = new HashMap<TargetType, Map<String, BLOBF>>();

    public BLOBFTable() {
        super();
        for (TargetType type : TargetType.values()) {
            obfNameMap.put(type, new HashMap<String, BLOBF>());
            srgNameMap.put(type, new HashMap<String, BLOBF>());
            mcpNameMap.put(type, new HashMap<String, BLOBF>());
        }
    }

    public BLOBF getOBF(String obfName, TargetType type) {
        BLOBF obf = obfNameMap.get(type).get(obfName);
        if (obf == null) {
            if (super.hasTypeObf(obfName, type)) {
                obf = new BLOBF(obfName, super.getSRGFromObfType(obfName, type), super.deobfType(obfName, type));
                obfNameMap.get(type).put(obf.obf, obf);
                srgNameMap.get(type).put(obf.srg, obf);
                mcpNameMap.get(type).put(obf.name, obf);
            } else {
            	invalidateResult(obfName, type);
            }
        }
        return obf;
    }

    public BLOBF getSRG(String srgName, TargetType type) {
        BLOBF obf = srgNameMap.get(type).get(srgName);
        if (obf == null) {
            if (super.hasTypeSRG(srgName, type)) {
                obf = new BLOBF(super.getObfFromSRGType(srgName, type), srgName, super.getDeObfFromSRGType(srgName, type));
                obfNameMap.get(type).put(obf.obf, obf);
                srgNameMap.get(type).put(obf.srg, obf);
                mcpNameMap.get(type).put(obf.name, obf);
            } else {
            	invalidateResult(srgName, type);
            }
        }
        return obf;
    }

    public BLOBF getMCP(String mcpName, TargetType type) {
        BLOBF obf = mcpNameMap.get(type).get(mcpName);
        if (obf == null) {
            if (super.hasTypeDeobf(mcpName, type)) {
                obf = new BLOBF(super.obfType(mcpName, type), super.getSRGFromDeObfType(mcpName, type), mcpName);
                obfNameMap.get(type).put(obf.obf, obf);
                srgNameMap.get(type).put(obf.srg, obf);
                mcpNameMap.get(type).put(obf.name, obf);
            } else {
            	invalidateResult(mcpName, type);
            }
        }
        return obf;
    }
    
    private void invalidateResult(String name, TargetType type) {
    	throw new RuntimeException("Unrecognised Obfuscation String: " + name + " for TargetType: " + type.toString());
    }
}
