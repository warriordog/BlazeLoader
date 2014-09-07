package com.blazeloader.api.direct.client.event;

import com.blazeloader.api.core.base.mod.BLMod;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Interface for mods that handle game events not handled by vanilla.  Override events are only called if the game is unable to handle the event on it's own.
 */
public interface OverrideEventClientHandler extends BLMod {

    /**
     * Creates a spawn packet for the given entity.
     *
     * @param entity    The entity to create the spawn packet for.
     * @param isHandled True if another mod has already created a packet for this entity.
     * @return Return a spawn packet for the given entity, or null if none exists.
     */
    public S0EPacketSpawnObject overrideCreateSpawnPacket(Entity entity, boolean isHandled);

    /**
     * Adds an entity to an entity tracker.
     *
     * @param tracker   The tracker to add the entity to.
     * @param entity    The entity to add.
     * @param isHandled True if another mod has already handled the event.
     * @return Return true if the entity was added, false otherwise.
     */
    public boolean overrideAddEntityToTracker(EntityTracker tracker, Entity entity, boolean isHandled);

    /**
     * Spawns a particle into thw world.
     *
     * @param name         The name of the particle to spawn.
     * @param world        The world to spawn in.
     * @param x            The x-location to spawn at.
     * @param y            The y-location to spawn at.
     * @param z            The z-location to spawn at.
     * @param p1           Parameter 1
     * @param p2           Parameter 1
     * @param p3           Parameter 1
     * @param currParticle The particle that the previous mod generated.  Set to null if no mod has generated an particle
     * @return A generated particle, or param currParticle to disable behavior
     */
    public EntityFX overrideSpawnParticle(String name, World world, double x, double y, double z, double p1, double p2, double p3, EntityFX currParticle);


    /**
     * ~WIP~
     * Called to allow a mod to display a gui for a custom container
     *
     * @param player         The player accessing the container
     * @param containerClass Class of container being accessed
     * @return Return true if container has been handled
     */
    public boolean overrideContainerOpen(EntityClientPlayerMP player, Class containerClass, ContainerOpenedEventArgs e);

    /**
     * Contains args for a ContainerOpenedEvent
     */
    public static class ContainerOpenedEventArgs {
        public final boolean locked;
        public final String invName;
        public final int slotsCount;

        public final int posX;
        public final int posY;
        public final int posZ;

        public ContainerOpenedEventArgs(EntityPlayer player, S2DPacketOpenWindow packet) {
            locked = packet.func_148900_g();
            invName = packet.func_148902_e().split(":?:")[1];
            slotsCount = packet.func_148898_f();

            posX = MathHelper.floor_double(player.posX);
            posY = MathHelper.floor_double(player.posY);
            posZ = MathHelper.floor_double(player.posZ);
        }
    }
}
