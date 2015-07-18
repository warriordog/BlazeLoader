package com.blazeloader.api.block;

import com.blazeloader.util.config.IStringable;

/**
 * Notification flags for block set events. Can be added together.
 */
public class UpdateType implements Cloneable, IStringable<UpdateType> {
	/**
	 * Do not send any updates or notifications.
	 * <p>
	 * This is useful for technical blocks where you don't want any changes to propagate.
	 */
	public static final UpdateType NONE = new ConstUpdateType(0x000);//0
	
    /**
     * Send an update event to neighboring blocks.
     */
	public static final UpdateType BLOCK_UPDATE = new ConstUpdateType(0x001);//1
    /**
     * Notify clients of a change. Almost always wanted.
     */
	public static final UpdateType NOTIFY_CLIENT = new ConstUpdateType(0x010);//2
    /**
     * Prevent the block from being re-rendered after an update.
     */
	public static final UpdateType PREVENT_RERENDER = new ConstUpdateType(0x100);//4
	/**
	 * Send a block update and notify clients.
	 * <p>
	 * This is the default for most uses and equivalent to:
	 * <br>
	 * {@code BLOCK_UPDATE.and(NOTIFY_CLIENT)}
	 */
	public static final UpdateType UPDATE_AND_NOTIFY = new ConstUpdateType(0x011);//3
	
    protected int value;
    
    protected UpdateType(int flag) {
		value = flag;
	}
	
    /**
     * Adds the flags of the given UpdateType to this one.
     * <p>Note that the default, constant values are read only and thus will not be modified. It is recommended to always use the returned value.
     * 
     * @param other The UpdateType to add to this one
     * 
     * @returns the resulting UpdateType for chaining
     */
    public UpdateType and(UpdateType other) {
    	if (!has(other)) {
    		value |= other.value;
    	}
    	return this;
    }
    
    /**
     * Removes the given UpdateType from this one.
     * <p>Note that the default, constant values are read only and thus will not be modified. It is recommended to always use the returned value.
     * 
     * @param other The UpdateType to subtract from this one
     * 
     * @returns the resulting UpdateType for chaining
     */
    public UpdateType subtract(UpdateType other) {
    	if (has(other)) {
    		value &= ~other.value;
    	}
    	return this;
    }
    
    /**
     * Flips this UpdateType to represent the inverse of its current value
     * <p>Note that the default, constant values are read only and thus will not be modified. It is recommended to always use the returned value.
     * 
     * @returns the resulting UpdateType for chaining
     */
    public UpdateType not() {
    	value = ~value;
    	return this;
    }
    
    public final int getValue() {
    	return value;
    }
    
    /**
     * Checks if this UpdateType contains the specified one.
     * 
     * @param other The UpdateType that is supposedly in this
     * 
     * @return true if other is in this.
     */
    public boolean has(UpdateType other) {
    	return equals(other) || (value & other.value) != 0;
    }
    
    public boolean equals(Object other) {
    	if (other != null) {
	        return (other == this) || ((other instanceof UpdateType) && (((UpdateType)other).value == value));
    	}
        return false;
    }
    
    public Object clone() {
    	return new UpdateType(value);
    }
    
    public String toString() {
    	String result = "";
    	if (has(UPDATE_AND_NOTIFY)) {
    		result += " UPDATE_AND_NOTIFY";
    	} else {
			if (has(BLOCK_UPDATE)) result += " BLOCK_UPDATE";
			if (has(NOTIFY_CLIENT)) result += " NOTIFY_CLIENT";
    	}
    	if (has(PREVENT_RERENDER)) result += " PREVENT_RERENDER";
    	return result.trim();
    }
    
	@Override
	public UpdateType fromString(String string) {
		int value = NONE.value;
		if (string != null) {
			string = string.trim().toUpperCase();
			if (string.contains("UPDATE_AND_NOTIFY")) {
				value |= UPDATE_AND_NOTIFY.value;
			} else {
				if (string.contains("BLOCK_UPDATE")) value |= BLOCK_UPDATE.value;
				if (string.contains("NOTIFY_CLIENT")) value |= NOTIFY_CLIENT.value;
			}
			if (string.contains("PREVENT_RERENDER")) value |= PREVENT_RERENDER.value;
		}
		return new UpdateType(value);
	}

    private static class ConstUpdateType extends UpdateType {
    	private ConstUpdateType(int flag) {
    		super(flag);
    	}
    	
    	/**
    	 * No modifications allowed.<p>Instead creates a new one with the modified value.
    	 */
        public UpdateType and(UpdateType other) {
        	return has(other) ? this : new UpdateType(value | other.value);
        }
        
    	/**
    	 * No modifications allowed.<p>Instead creates a new one with the modified value.
    	 */
        public UpdateType subtract(UpdateType other) {
        	return has(other) ? new UpdateType(value & ~other.value) : this;
        }
        
    	/**
    	 * No modifications allowed.<p>Instead creates a new one with the inverted value.
    	 */
        public UpdateType not() {
        	return new UpdateType(~value);
        }
    }
}
