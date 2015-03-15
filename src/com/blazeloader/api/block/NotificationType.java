package com.blazeloader.api.block;

/**
 * Notification flags for block set events.  Can be added together.
 */
public enum NotificationType {
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

    NotificationType(int flag) {
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
    public int add(NotificationType type) {
        return flag + type.getType();
    }
}
