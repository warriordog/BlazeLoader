package com.blazeloader.api.block;

/**
 * Notification flags for block set events. Can be added together.
 */
public class UpdateType implements Cloneable {
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
	 * <i><br>BLOCK_UPDATE.and(NOTIFY_CLIENT)</i>
	 */
	public static final UpdateType UPDATE_AND_NOTIFY = new ConstUpdateType(0x011);//3
	
    protected int value;
    
    protected UpdateType(int flag) {
		value = flag;
	}
	
    /**
     * Adds the flags of the given NotificationType to this one.
     * <p>Note that the default, constant values are read only and thus will not be modified. It is recommended to always use the returned value.
     * 
     * @param other The UpdateType to add to this one
     * 
     * @returns the resulting UpdateType for chaining
     */
    public UpdateType and(UpdateType other) {
    	if (!has(other)) {
    		value = value | other.value;
    	}
    	return this;
    }
    
    /**
     * Flips this NotificationType2 to represent the inverse of its current value
     * <p>Note that the default, constant values are read only and thus will not be modified. It is recommended to always use the returned value.
     * 
     * @returns the resulting UpdateType for chaining
     */
    public UpdateType not() {
    	value = ~value;
    	return this;
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
    
    /**
     * Checks is this UpdateType is identical to the given one.
     * 
     * @param other The UpdateType to compare this one to.
     */
    public boolean equals(UpdateType other) {
    	return value == other.value;
    }
    
    public boolean equals(Object other) {
    	return (other instanceof Integer && value == (int)other) || (other instanceof UpdateType) && equals((UpdateType)other);
    }
    
    private static class ConstUpdateType extends UpdateType {
    	private ConstUpdateType(int flag) {
    		super(flag);
    	}
    	
    	/**
    	 * No modifications allowed.<p>Instead creates a new one with the combined values.
    	 */
        public UpdateType and(UpdateType other) {
        	return has(other) ? this : new UpdateType(value | other.value);
        }
        
    	/**
    	 * No modifications allowed.<p>Instead creates a new one with the inverted value.
    	 */
        public UpdateType not(UpdateType other) {
        	return new UpdateType(~value);
        }
    }
}
