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
            
    public BLOBF getConstructor(String obfClass, OBFLevel level, String... parameterClasses) {
    	String obfName = obfClass + ".<init> (";
    	for (int i = 0; i < parameterClasses.length; i++) {
    		obfName += parameterClasses[i];
    	}
    	obfName += ")V";
    	BLOBF obf = getMapping(level).get(TargetType.CONSTRUCTOR).get(obfName);
    	if (obf == null) {
    		String srg = getSRGFromType(obfClass, TargetType.CLASS, level) + ".<init> (";
    		String obfsc = getObfFromType(obfClass, TargetType.CLASS, level) + ".<init> (";
    		String mcp = getMCPFromType(obfClass, TargetType.CLASS, level) + ".<init> (";
    		for (int i = 0; i < parameterClasses.length; i++) {
    			 //Only try to parse things we know. i.e. Minecraft classes
    			if (parameterClasses[i].endsWith(";") && hasType(parameterClasses[i], TargetType.CLASS, level)) {
	    			srg += getSRGFromType(parameterClasses[i], TargetType.CLASS, level);
	    			obfsc += getMCPFromType(parameterClasses[i], TargetType.CLASS, level);
	    			mcp += getMCPFromType(parameterClasses[i], TargetType.CLASS, level);
    			} else {
    				srg += parameterClasses[i];
    				obfsc += parameterClasses[i];
    				mcp += parameterClasses[i];
    			}
    		}
    		obf = recordOBF(TargetType.CONSTRUCTOR, new BLOBF(obfsc + ")V", srg + ")V", mcp + ")V"));
    	}
    	return obf;
    }
    
    public BLOBF getBLOBF(String name, TargetType type, OBFLevel level) {
    	BLOBF result = getMapping(level).get(type).get(name);
    	if (result == null) {
    		if (hasType(name, type, level)) {
    			result = recordOBF(type, new BLOBF(getObfFromType(name, type, level), getSRGFromType(name, type, level), getMCPFromType(name, type, level)));
    		} else {
    			throw new RuntimeException("Unrecognised Obfuscation String: " + level.toString() + "@" + name + " for TargetType: " + type.toString());
    		}
    	}
    	return result;
    }
    
    public boolean hasType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case SRG: return hasTypeSRG(name, type);
			case MCP: return hasTypeDeobf(name, type);
			default: return hasTypeObf(name, type);
		}
    }
    
    private BLOBF recordOBF(TargetType type, BLOBF obf) {
    	obfNameMap.get(type).put(obf.obf, obf);
        srgNameMap.get(type).put(obf.srg, obf);
        mcpNameMap.get(type).put(obf.name, obf);
    	return obf;
    }
    
    public Map<TargetType, Map<String, BLOBF>> getMapping(OBFLevel level) {
    	switch (level) {
    		case SRG: return srgNameMap;
    		case MCP: return mcpNameMap;
    		default: return obfNameMap;
    	}
    }
    
    public String getObfFromType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case SRG: return getObfFromSRGType(name, type);
			case MCP: return obfType(name, type);
			default: return name;
		}
    }
    
    public String getSRGFromType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case OBF: return getSRGFromObfType(name, type);
			case MCP: return getSRGFromDeObfType(name, type);
			default: return name;
		}
    }
    
    public String getMCPFromType(String name, TargetType type, OBFLevel level) {
    	switch (level) {
			case SRG: return getDeObfFromSRGType(name, type);
			case OBF: return getObfFromSRGType(getSRGFromDeObfType(name, type), type);
			default: return name;
		}
    }
}
