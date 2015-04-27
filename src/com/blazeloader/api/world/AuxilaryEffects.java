package com.blazeloader.api.world;

/**
 * All of the Auxilary effects used in minecraft for World.spawnAuxEffects
 */
public enum AuxilaryEffects {
	BLOCK_BREAK(2001),
	DISPENSE_BLOCK(1000),
	DISPENSE_PROJECTILE(1002),
	DISPENSE_FIRE(1009),
	DISPENSER_EMPTY(1001),
	DISPENSER_FULL(2005),
	DISPENSE_PARTICLES(2000),
	RECORD_DROP(1005),
	FALL(1022),
	THROWABLE_LAND(2002),
	BONEMEAL(2005),
	REPAIR_ITEM(1020),
	EYE_OF_ENDER(2003),
	MOB_SPAWN(2004),
	DOOR_SLAM(1010),
	
	UNKNOWN(0);
	
	private final int id;
	
	AuxilaryEffects(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public static AuxilaryEffects fromId(int id) {
		for (AuxilaryEffects i : values()) {
			if (i.id == id) {
				return i;
			}
		}
		return UNKNOWN;
	}
}
