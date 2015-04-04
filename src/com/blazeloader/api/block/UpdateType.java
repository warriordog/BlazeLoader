package com.blazeloader.api.block;

/**
 * Notification flags for block set events. Can be added together.
 */
public class UpdateType implements Cloneable {
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
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof UpdateType)) return false;
        return this.value == ((UpdateType) other).value;
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
