package net.acomputerdog.BlazeLoader.util.obf;

import net.acomputerdog.OBFUtil.table.DirectOBFTableSRG;
import net.acomputerdog.OBFUtil.util.TargetType;

import java.util.HashMap;
import java.util.Map;

public class BLOBFTable extends DirectOBFTableSRG {
    private final Map<TargetType, Map<String, BLOBF>> obfMap = new HashMap<TargetType, Map<String, BLOBF>>();
    private final Map<TargetType, Map<String, BLOBF>> srgMap = new HashMap<TargetType, Map<String, BLOBF>>();
    private final Map<TargetType, Map<String, BLOBF>> mcpMap = new HashMap<TargetType, Map<String, BLOBF>>();
    private final Map<TargetType, Map<String, BLOBF>> smpMap = new HashMap<TargetType, Map<String, BLOBF>>();

    public BLOBFTable() {
        super();
        for (TargetType type : TargetType.values()) {
            obfMap.put(type, new HashMap<String, BLOBF>());
            srgMap.put(type, new HashMap<String, BLOBF>());
            mcpMap.put(type, new HashMap<String, BLOBF>());
            smpMap.put(type, new HashMap<String, BLOBF>());
        }
    }

    public BLOBF getOBF(String obfName, TargetType type) {
        BLOBF obf = obfMap.get(type).get(obfName);
        if (obf == null) {
            if (super.hasTypeObf(obfName, type)) {
                obf = new BLOBF(super.getSRGFromObfType(obfName, type), obfName, super.deobfType(obfName, type));
                obfMap.get(type).put(obf.obf, obf);
                srgMap.get(type).put(obf.srg, obf);
                mcpMap.get(type).put(obf.name, obf);
                smpMap.get(type).put(obf.simpleName, obf);
            } else {
                System.out.println("Missing mapping: " + obfName);
            }
        }
        return obf;
    }

    public BLOBF getSRG(String srgName, TargetType type) {
        BLOBF obf = srgMap.get(type).get(srgName);
        if (obf == null) {
            if (super.hasTypeSRG(srgName, type)) {
                obf = new BLOBF(srgName, super.getObfFromSRGType(srgName, type), super.getDeObfFromSRGType(srgName, type));
                obfMap.get(type).put(obf.obf, obf);
                srgMap.get(type).put(obf.srg, obf);
                mcpMap.get(type).put(obf.name, obf);
                smpMap.get(type).put(obf.simpleName, obf);
            }
        }
        return obf;
    }

    public BLOBF getMCP(String mcpName, TargetType type) {
        BLOBF obf = mcpMap.get(type).get(mcpName);
        if (obf == null) {
            if (super.hasTypeDeobf(mcpName, type)) {
                obf = new BLOBF(super.getSRGFromDeObfType(mcpName, type), super.obfType(mcpName, type), mcpName);
                obfMap.get(type).put(obf.obf, obf);
                srgMap.get(type).put(obf.srg, obf);
                mcpMap.get(type).put(obf.name, obf);
                smpMap.get(type).put(obf.simpleName, obf);
            }
        }
        return obf;
    }

    public BLOBF getSMP(String smpName, TargetType type) {
        return smpMap.get(type).get(smpName); //there is not enough information to create a full obfuscation map from just the SMPName
    }

}
