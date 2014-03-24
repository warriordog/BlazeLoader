package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.api.gui.ContainerOpenedEventArgs;
import net.minecraft.client.entity.EntityClientPlayerMP;

/**
 * Interface for mods that handle inventory events
 */
public interface InventoryEventHandler {
    /**
     * ~WIP~
     * Called to allow a mod to display a gui for a custom container
     *
     * @param player         The player accessing the container
     * @param containerClass Class of container being accessed
     * @return Return true if container has been handled
     */
    public boolean eventContainerOpen(EntityClientPlayerMP player, Class containerClass, ContainerOpenedEventArgs e);
}
