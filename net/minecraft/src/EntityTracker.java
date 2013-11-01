package net.minecraft.src;

import net.acomputerdog.BlazeLoader.mod.ModList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Tracks an entity and manages client updates.
 */
public class EntityTracker
{
    private final WorldServer theWorld;

    /**
     * List of tracked entities, used for iteration operations on tracked entities.
     */
    private Set<EntityTrackerEntry> trackedEntities = new HashSet<EntityTrackerEntry>();
    private IntHashMap trackedEntityIDs = new IntHashMap();
    private int entityViewDistance;

    public EntityTracker(WorldServer worldServer)
    {
        this.theWorld = worldServer;
        this.entityViewDistance = worldServer.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
    }

    /**
     * if entity is a player sends all tracked events to the player, otherwise, adds with a visibility and update arate
     * based on the class type
     */
    public void addEntityToTracker(Entity entity)
    {
        if (entity instanceof EntityPlayerMP)
        {
            this.addEntityToTracker(entity, 512, 2);
            EntityPlayerMP var2 = (EntityPlayerMP)entity;

            for (Object trackedEntity : this.trackedEntities) {
                EntityTrackerEntry var4 = (EntityTrackerEntry) trackedEntity;

                if (var4.myEntity != var2) {
                    var4.tryStartWachingThis(var2);
                }
            }
        }
        else if (entity instanceof EntityFishHook)
        {
            this.addEntityToTracker(entity, 64, 5, true);
        }
        else if (entity instanceof EntityArrow)
        {
            this.addEntityToTracker(entity, 64, 20, false);
        }
        else if (entity instanceof EntitySmallFireball)
        {
            this.addEntityToTracker(entity, 64, 10, false);
        }
        else if (entity instanceof EntityFireball)
        {
            this.addEntityToTracker(entity, 64, 10, false);
        }
        else if (entity instanceof EntitySnowball)
        {
            this.addEntityToTracker(entity, 64, 10, true);
        }
        else if (entity instanceof EntityEnderPearl)
        {
            this.addEntityToTracker(entity, 64, 10, true);
        }
        else if (entity instanceof EntityEnderEye)
        {
            this.addEntityToTracker(entity, 64, 4, true);
        }
        else if (entity instanceof EntityEgg)
        {
            this.addEntityToTracker(entity, 64, 10, true);
        }
        else if (entity instanceof EntityPotion)
        {
            this.addEntityToTracker(entity, 64, 10, true);
        }
        else if (entity instanceof EntityExpBottle)
        {
            this.addEntityToTracker(entity, 64, 10, true);
        }
        else if (entity instanceof EntityFireworkRocket)
        {
            this.addEntityToTracker(entity, 64, 10, true);
        }
        else if (entity instanceof EntityItem)
        {
            this.addEntityToTracker(entity, 64, 20, true);
        }
        else if (entity instanceof EntityMinecart)
        {
            this.addEntityToTracker(entity, 80, 3, true);
        }
        else if (entity instanceof EntityBoat)
        {
            this.addEntityToTracker(entity, 80, 3, true);
        }
        else if (entity instanceof EntitySquid)
        {
            this.addEntityToTracker(entity, 64, 3, true);
        }
        else if (entity instanceof EntityWither)
        {
            this.addEntityToTracker(entity, 80, 3, false);
        }
        else if (entity instanceof EntityBat)
        {
            this.addEntityToTracker(entity, 80, 3, false);
        }
        else if (entity instanceof IAnimals)
        {
            this.addEntityToTracker(entity, 80, 3, true);
        }
        else if (entity instanceof EntityDragon)
        {
            this.addEntityToTracker(entity, 160, 3, true);
        }
        else if (entity instanceof EntityTNTPrimed)
        {
            this.addEntityToTracker(entity, 160, 10, true);
        }
        else if (entity instanceof EntityFallingSand)
        {
            this.addEntityToTracker(entity, 160, 20, true);
        }
        else if (entity instanceof EntityHanging)
        {
            this.addEntityToTracker(entity, 160, Integer.MAX_VALUE, false);
        }
        else if (entity instanceof EntityXPOrb)
        {
            this.addEntityToTracker(entity, 160, 20, true);
        }
        else if (entity instanceof EntityEnderCrystal)
        {
            this.addEntityToTracker(entity, 256, Integer.MAX_VALUE, false);
        }else{
            ModList.addEntityToTracker(this, entity);
        }
    }

    public void addEntityToTracker(Entity entity, int viewDistance, int updateFrequency)
    {
        this.addEntityToTracker(entity, viewDistance, updateFrequency, false);
    }

    public void addEntityToTracker(Entity entity, int viewDistance, int updateFrequency, boolean updateVelocity)
    {
        if (viewDistance > this.entityViewDistance)
        {
            viewDistance = this.entityViewDistance;
        }

        try
        {
            if (this.trackedEntityIDs.containsItem(entity.entityId))
            {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry var5 = new EntityTrackerEntry(entity, viewDistance, updateFrequency, updateVelocity);
            this.trackedEntities.add(var5);
            this.trackedEntityIDs.addKey(entity.entityId, var5);
            var5.sendEventsToPlayers(this.theWorld.playerEntities);
        }
        catch (Throwable var11)
        {
            CrashReport var6 = CrashReport.makeCrashReport(var11, "Adding entity to track");
            CrashReportCategory var7 = var6.makeCategory("Entity To Track");
            var7.addCrashSection("Tracking range", viewDistance + " blocks");
            var7.addCrashSectionCallable("Update interval", new CallableEntityTracker(this, updateFrequency));
            entity.addEntityCrashInfo(var7);
            CrashReportCategory var8 = var6.makeCategory("Entity That Is Already Tracked");
            ((EntityTrackerEntry)this.trackedEntityIDs.lookup(entity.entityId)).myEntity.addEntityCrashInfo(var8);

            try
            {
                throw new ReportedException(var6);
            }
            catch (ReportedException var10)
            {
                System.err.println("\"Silently\" catching entity tracking error.");
                var10.printStackTrace();
            }
        }
    }

    public void removeEntityFromAllTrackingPlayers(Entity entity)
    {
        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP var2 = (EntityPlayerMP)entity;

            for (Object trackedEntity : this.trackedEntities) {
                EntityTrackerEntry var4 = (EntityTrackerEntry) trackedEntity;
                var4.removeFromWatchingList(var2);
            }
        }

        EntityTrackerEntry var5 = (EntityTrackerEntry)this.trackedEntityIDs.removeObject(entity.entityId);

        if (var5 != null)
        {
            this.trackedEntities.remove(var5);
            var5.informAllAssociatedPlayersOfItemDestruction();
        }
    }

    public void updateTrackedEntities()
    {
        ArrayList<Entity> var1 = new ArrayList<Entity>();

        for (Object trackedEntity : this.trackedEntities) {
            EntityTrackerEntry var3 = (EntityTrackerEntry) trackedEntity;
            var3.sendLocationToAllClients(this.theWorld.playerEntities);

            if (var3.playerEntitiesUpdated && var3.myEntity instanceof EntityPlayerMP) {
                var1.add(var3.myEntity);
            }
        }

        for (Object aVar1 : var1) {
            EntityPlayerMP var7 = (EntityPlayerMP) aVar1;

            for (Object trackedEntity : this.trackedEntities) {
                EntityTrackerEntry var5 = (EntityTrackerEntry) trackedEntity;

                if (var5.myEntity != var7) {
                    var5.tryStartWachingThis(var7);
                }
            }
        }
    }

    /**
     * does not send the packet to the entity if the entity is a player
     */
    public void sendPacketToAllPlayersTrackingEntity(Entity entity, Packet packet)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIDs.lookup(entity.entityId);

        if (var3 != null)
        {
            var3.sendPacketToAllTrackingPlayers(packet);
        }
    }

    /**
     * sends to the entity if the entity is a player
     */
    public void sendPacketToAllAssociatedPlayers(Entity entity, Packet packet)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIDs.lookup(entity.entityId);

        if (var3 != null)
        {
            var3.sendPacketToAllAssociatedPlayers(packet);
        }
    }

    public void removePlayerFromTrackers(EntityPlayerMP player)
    {

        for (Object trackedEntity : this.trackedEntities) {
            EntityTrackerEntry var3 = (EntityTrackerEntry) trackedEntity;
            var3.removePlayerFromTracker(player);
        }
    }

    public void trackEntitiesInChunk(EntityPlayerMP player, Chunk chunk)
    {

        for (Object trackedEntity : this.trackedEntities) {
            EntityTrackerEntry var4 = (EntityTrackerEntry) trackedEntity;

            if (var4.myEntity != player && var4.myEntity.chunkCoordX == chunk.xPosition && var4.myEntity.chunkCoordZ == chunk.zPosition) {
                var4.tryStartWachingThis(player);
            }
        }
    }
}
