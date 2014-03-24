package net.acomputerdog.BlazeLoader.event;

import net.acomputerdog.BlazeLoader.api.gui.ContainerOpenedEventArgs;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;

/**
 * Interface for mods that handle client-specific events
 */
public interface ClientEventHandler {

    /**
     * Called when a GUI is about to be displayed.
     *
     * @param oldGui  The current GUI.
     * @param newGui  The GUI being displayed.
     * @param allowed Set to true if the GUI will be displayed, false if another mod has disabled it.
     * @return Return true to allow the GUI, false to block it.
     */
    public boolean eventDisplayGui(GuiScreen oldGui, GuiScreen newGui, boolean allowed);

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
