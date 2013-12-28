package net.minecraft.entity;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.*;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Tracks an entity and manages client updates.
 */
public class EntityTracker
{
    private static final Logger field_151249_a = LogManager.getLogger();
    private final WorldServer theWorld;

    /**
     * List of tracked entities, used for iteration operations on tracked entities.
     */
    private Set trackedEntities = new HashSet();
    private IntHashMap trackedEntityIDs = new IntHashMap();
    private int entityViewDistance;
    private static final String __OBFID = "CL_00001431";

    public EntityTracker(WorldServer par1WorldServer)
    {
        this.theWorld = par1WorldServer;
        this.entityViewDistance = par1WorldServer.getMinecraftServer().getConfigurationManager().getEntityViewDistance();
    }

    /**
     * if entity is a player sends all tracked events to the player, otherwise, adds with a visibility and update arate
     * based on the class type
     */
    public void addEntityToTracker(Entity par1Entity)
    {
        if (par1Entity instanceof EntityPlayerMP)
        {
            this.addEntityToTracker(par1Entity, 512, 2);
            EntityPlayerMP var2 = (EntityPlayerMP)par1Entity;

            for (Object trackedEntity : this.trackedEntities) {
                EntityTrackerEntry var4 = (EntityTrackerEntry) trackedEntity;

                if (var4.myEntity != var2) {
                    var4.tryStartWachingThis(var2);
                }
            }
        }
        else if (par1Entity instanceof EntityFishHook)
        {
            this.addEntityToTracker(par1Entity, 64, 5, true);
        }
        else if (par1Entity instanceof EntityArrow)
        {
            this.addEntityToTracker(par1Entity, 64, 20, false);
        }
        else if (par1Entity instanceof EntitySmallFireball)
        {
            this.addEntityToTracker(par1Entity, 64, 10, false);
        }
        else if (par1Entity instanceof EntityFireball)
        {
            this.addEntityToTracker(par1Entity, 64, 10, false);
        }
        else if (par1Entity instanceof EntitySnowball)
        {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityEnderPearl)
        {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityEnderEye)
        {
            this.addEntityToTracker(par1Entity, 64, 4, true);
        }
        else if (par1Entity instanceof EntityEgg)
        {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityPotion)
        {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityExpBottle)
        {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityFireworkRocket)
        {
            this.addEntityToTracker(par1Entity, 64, 10, true);
        }
        else if (par1Entity instanceof EntityItem)
        {
            this.addEntityToTracker(par1Entity, 64, 20, true);
        }
        else if (par1Entity instanceof EntityMinecart)
        {
            this.addEntityToTracker(par1Entity, 80, 3, true);
        }
        else if (par1Entity instanceof EntityBoat)
        {
            this.addEntityToTracker(par1Entity, 80, 3, true);
        }
        else if (par1Entity instanceof EntitySquid)
        {
            this.addEntityToTracker(par1Entity, 64, 3, true);
        }
        else if (par1Entity instanceof EntityWither)
        {
            this.addEntityToTracker(par1Entity, 80, 3, false);
        }
        else if (par1Entity instanceof EntityBat)
        {
            this.addEntityToTracker(par1Entity, 80, 3, false);
        }
        else if (par1Entity instanceof IAnimals)
        {
            this.addEntityToTracker(par1Entity, 80, 3, true);
        }
        else if (par1Entity instanceof EntityDragon)
        {
            this.addEntityToTracker(par1Entity, 160, 3, true);
        }
        else if (par1Entity instanceof EntityTNTPrimed)
        {
            this.addEntityToTracker(par1Entity, 160, 10, true);
        }
        else if (par1Entity instanceof EntityFallingBlock)
        {
            this.addEntityToTracker(par1Entity, 160, 20, true);
        }
        else if (par1Entity instanceof EntityHanging)
        {
            this.addEntityToTracker(par1Entity, 160, Integer.MAX_VALUE, false);
        }
        else if (par1Entity instanceof EntityXPOrb)
        {
            this.addEntityToTracker(par1Entity, 160, 20, true);
        }
        else if (par1Entity instanceof EntityEnderCrystal)
        {
            this.addEntityToTracker(par1Entity, 256, Integer.MAX_VALUE, false);
        }else{
            ModList.addEntityToTracker(this, par1Entity);
        }
    }

    public void addEntityToTracker(Entity par1Entity, int par2, int par3)
    {
        this.addEntityToTracker(par1Entity, par2, par3, false);
    }

    public void addEntityToTracker(Entity par1Entity, int par2, final int par3, boolean par4)
    {
        if (par2 > this.entityViewDistance)
        {
            par2 = this.entityViewDistance;
        }

        try
        {
            if (this.trackedEntityIDs.containsItem(par1Entity.func_145782_y()))
            {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry var5 = new EntityTrackerEntry(par1Entity, par2, par3, par4);
            this.trackedEntities.add(var5);
            this.trackedEntityIDs.addKey(par1Entity.func_145782_y(), var5);
            var5.sendEventsToPlayers(this.theWorld.playerEntities);
        }
        catch (Throwable var11)
        {
            CrashReport var6 = CrashReport.makeCrashReport(var11, "Adding entity to track");
            CrashReportCategory var7 = var6.makeCategory("Entity To Track");
            var7.addCrashSection("Tracking range", par2 + " blocks");
            var7.addCrashSectionCallable("Update interval", new Callable()
            {
                private static final String __OBFID = "CL_00001432";
                public String call()
                {
                    String var1 = "Once per " + par3 + " ticks";

                    if (par3 == Integer.MAX_VALUE)
                    {
                        var1 = "Maximum (" + var1 + ")";
                    }

                    return var1;
                }
            });
            par1Entity.addEntityCrashInfo(var7);
            CrashReportCategory var8 = var6.makeCategory("Entity That Is Already Tracked");
            ((EntityTrackerEntry)this.trackedEntityIDs.lookup(par1Entity.func_145782_y())).myEntity.addEntityCrashInfo(var8);

            try
            {
                throw new ReportedException(var6);
            }
            catch (ReportedException var10)
            {
                field_151249_a.error("\"Silently\" catching entity tracking error.", var10);
            }
        }
    }

    public void removeEntityFromAllTrackingPlayers(Entity par1Entity)
    {
        if (par1Entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP var2 = (EntityPlayerMP)par1Entity;

            for (Object trackedEntity : this.trackedEntities) {
                EntityTrackerEntry var4 = (EntityTrackerEntry) trackedEntity;
                var4.removeFromWatchingList(var2);
            }
        }

        EntityTrackerEntry var5 = (EntityTrackerEntry)this.trackedEntityIDs.removeObject(par1Entity.func_145782_y());

        if (var5 != null)
        {
            this.trackedEntities.remove(var5);
            var5.informAllAssociatedPlayersOfItemDestruction();
        }
    }

    public void updateTrackedEntities()
    {
        ArrayList var1 = new ArrayList();

        for (Object trackedEntity1 : this.trackedEntities) {
            EntityTrackerEntry var3 = (EntityTrackerEntry) trackedEntity1;
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
    public void func_151247_a(Entity p_151247_1_, Packet p_151247_2_)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIDs.lookup(p_151247_1_.func_145782_y());

        if (var3 != null)
        {
            var3.func_151259_a(p_151247_2_);
        }
    }

    public void func_151248_b(Entity p_151248_1_, Packet p_151248_2_)
    {
        EntityTrackerEntry var3 = (EntityTrackerEntry)this.trackedEntityIDs.lookup(p_151248_1_.func_145782_y());

        if (var3 != null)
        {
            var3.func_151261_b(p_151248_2_);
        }
    }

    public void removePlayerFromTrackers(EntityPlayerMP par1EntityPlayerMP)
    {

        for (Object trackedEntity : this.trackedEntities) {
            EntityTrackerEntry var3 = (EntityTrackerEntry) trackedEntity;
            var3.removePlayerFromTracker(par1EntityPlayerMP);
        }
    }

    public void func_85172_a(EntityPlayerMP par1EntityPlayerMP, Chunk par2Chunk)
    {

        for (Object trackedEntity : this.trackedEntities) {
            EntityTrackerEntry var4 = (EntityTrackerEntry) trackedEntity;

            if (var4.myEntity != par1EntityPlayerMP && var4.myEntity.chunkCoordX == par2Chunk.xPosition && var4.myEntity.chunkCoordZ == par2Chunk.zPosition) {
                var4.tryStartWachingThis(par1EntityPlayerMP);
            }
        }
    }
}
