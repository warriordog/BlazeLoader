package com.blazeloader.event.listeners.client;

import com.blazeloader.bl.mod.BLMod;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;

/**
 * Interface for mods that handle game events not handled by vanilla.  Override events are only called if the game is unable to handle the event on it's own.
 */
public interface OverrideListener extends BLMod {

    /**
     * Creates a spawn packet for the given entity.
     *
     * @param entity    The entity to create the spawn packet for.
     * @param isHandled True if another mod has already created a packet for this entity.
     * @return Return a spawn packet for the given entity, or null if none exists.
     */
    public S0EPacketSpawnObject onCreateSpawnPacket(Entity entity, boolean isHandled);

    /**
     * Adds an entity to an entity tracker.
     *
     * @param tracker   The tracker to add the entity to.
     * @param entity    The entity to add.
     * @param isHandled True if another mod has already handled the event.
     * @return Return true if the entity was added, false otherwise.
     */
    public boolean onAddEntityToTracker(EntityTracker tracker, Entity entity, boolean isHandled);

    /**
     * Provides a hook to replace a particle before it gets spawned.
     *
     * @param particleId	Id of the particle being spawned
     * @param x            	The x-location to spawn at.
     * @param y            	The y-location to spawn at.
     * @param z            	The z-location to spawn at.
     * @param xOffset		The x velocity
     * @param yOffset		The y velocity
     * @param zOffset		The z velocity
     * @param currParticle The particle that the previous mod generated.  Set to null if no mod other has generated a particle yet
     * @return A generated particle
     */
    public EntityFX onSpawnParticle(int particleId, double x, double y, double z, double p1, double p2, double p3, EntityFX currParticle);


    /**
     * Called to allow a mod to display a gui for a custom container
     *
     * @param player         The player accessing the container
     * @return Return true if container has been handled
     */
    public boolean onContainerOpened(AbstractClientPlayer player, ContainerOpenedEventArgs e);

    /**
     * Contains args for a ContainerOpenedEvent
     */
    public static class ContainerOpenedEventArgs {
    	public final boolean hasSlots;
    	public final int slotsCount;
    	
    	public final String inventoryId;
    	public final IChatComponent inventoryTitle;

        public final int posX;
        public final int posY;
        public final int posZ;

        public ContainerOpenedEventArgs(EntityPlayer player, S2DPacketOpenWindow packet) {
            hasSlots = packet.hasSlots();
            slotsCount = packet.getSlotCount();
            inventoryId = packet.getGuiId();
            inventoryTitle = packet.getWindowTitle();
            
            posX = MathHelper.floor_double(player.posX);
            posY = MathHelper.floor_double(player.posY);
            posZ = MathHelper.floor_double(player.posZ);
        }
    }
}
