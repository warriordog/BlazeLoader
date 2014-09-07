package com.blazeloader.api.direct.server.api.block;

/**
 * Notification flags for block set events.  Can be added together.
 */
public enum ENotificationType {
    /**
     * Do send the change to clients.  Almost always wanted.
     */
    NOTIFY_CLIENTS(2),

    /**
     * Trigger a block update.
     */
    BLOCK_UPDATE(1),

    /**
     * Prevent the block from being rendered a second time.
     */
    PREVENT_RERENDER(4);

    protected int flag;

    ENotificationType(int flag) {
        this.flag = flag;
    }

    /**
     * Gets the notification flag.
     *
     * @return Gets the notification flag.
     */
    public int getType() {
        return flag;
    }

    /**
     * Adds this ENotificationType to another.
     *
     * @param type The other ENotificationType to add to.
     * @return Return the value of the two ENotificationTypes added together.
     */
    public int add(ENotificationType type) {
        return flag + type.getType();
    }
}
