package net.minecraft.world;

import net.acomputerdog.BlazeLoader.mod.ModList;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * A server-side world.  Replaces WorldServerProxy.
 */
public class WorldServer extends World
{
    private static final Logger field_147491_a = LogManager.getLogger();
    private final MinecraftServer mcServer;
    private final EntityTracker theEntityTracker;
    private final PlayerManager thePlayerManager;
    private Set pendingTickListEntriesHashSet;

    /** All work to do in future ticks. */
    private TreeSet pendingTickListEntriesTreeSet;
    public ChunkProviderServer theChunkProviderServer;

    /** set by CommandServerSave{all,Off,On} */
    public boolean canNotSave;

    /** is false if there are no players */
    private boolean allPlayersSleeping;
    private int updateEntityTick;

    /**
     * the teleporter to use when the entity is being transferred into the dimension
     */
    private final Teleporter worldTeleporter;
    private final SpawnerAnimals animalSpawner = new SpawnerAnimals();

    /**
     * Double buffer of ServerBlockEventList[] for holding pending BlockEventData's
     */
    private WorldServer.ServerBlockEventList[] field_147490_S = new WorldServer.ServerBlockEventList[] {new WorldServer.ServerBlockEventList(null), new WorldServer.ServerBlockEventList(null)};
    private int field_147489_T;
    private static final WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.field_151055_y, 0, 1, 3, 10), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150344_f), 0, 1, 3, 10), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150364_r), 0, 1, 3, 10), new WeightedRandomChestContent(Items.field_151049_t, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151053_p, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151050_s, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151039_o, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151034_e, 0, 2, 3, 5), new WeightedRandomChestContent(Items.field_151025_P, 0, 2, 3, 3), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150363_s), 0, 1, 3, 10)};
    private List pendingTickListEntriesThisTick = new ArrayList();

    /** An IntHashMap of entity IDs (integers) to their Entity objects. */
    private IntHashMap entityIdMap;

    private static final String __OBFID = "CL_00001437";

    public WorldServer(MinecraftServer server, ISaveHandler saveHandler, String worldName, int dimension, WorldSettings worldSettings, Profiler profiler)
    {
        super(saveHandler, worldName, worldSettings, WorldProvider.getProviderForDimension(dimension), profiler);
        this.mcServer = server;
        this.theEntityTracker = new EntityTracker(this);
        this.thePlayerManager = new PlayerManager(this, server.getConfigurationManager().getViewDistance());

        if (this.entityIdMap == null)
        {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null)
        {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null)
        {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.worldTeleporter = new Teleporter(this);
        this.worldScoreboard = new ServerScoreboard(server);
        ScoreboardSaveData scoreboardSaveData = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

        if (scoreboardSaveData == null)
        {
            scoreboardSaveData = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", scoreboardSaveData);
        }

        scoreboardSaveData.func_96499_a(this.worldScoreboard);
        ((ServerScoreboard)this.worldScoreboard).func_96547_a(scoreboardSaveData);
    }

    /**
     * Runs a single tick for the world
     */
    public void tick()
    {
        ModList.eventTickServerWorld(this);

        super.tick();

        if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting != EnumDifficulty.HARD)
        {
            this.difficultySetting = EnumDifficulty.HARD;
        }

        this.provider.worldChunkMgr.cleanupCache();

        if (this.areAllPlayersAsleep())
        {
            if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
            {
                long newTime = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(newTime - newTime % 24000L);
            }

            this.wakeAllPlayers();
        }

        this.theProfiler.startSection("mobSpawner");

        if (this.getGameRules().getGameRuleBooleanValue("doMobSpawning"))
        {
            this.animalSpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
        }

        this.theProfiler.endStartSection("chunkSource");
        this.chunkProvider.unloadQueuedChunks();
        int maxSkyLight = this.calculateSkylightSubtracted(1.0F);

        if (maxSkyLight != this.skylightSubtracted)
        {
            this.skylightSubtracted = maxSkyLight;
        }

        this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
        {
            this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
        }

        this.theProfiler.endStartSection("tickPending");
        this.tickUpdates(false);
        this.theProfiler.endStartSection("tickBlocks");
        this.func_147456_g();
        this.theProfiler.endStartSection("chunkMap");
        this.thePlayerManager.updatePlayerInstances();
        this.theProfiler.endStartSection("village");
        this.villageCollectionObj.tick();
        this.villageSiegeObj.tick();
        this.theProfiler.endStartSection("portalForcer");
        this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
        this.theProfiler.endSection();
        this.func_147488_Z();
    }

    /**
     * only spawns creatures allowed by the chunkProvider
     */

    public BiomeGenBase.SpawnListEntry spawnRandomCreature(EnumCreatureType creatureType, int x, int y, int z)
    {
        List spawnTypes = this.getChunkProvider().getPossibleCreatures(creatureType, x, y, z);
        return spawnTypes != null && !spawnTypes.isEmpty() ? (BiomeGenBase.SpawnListEntry)WeightedRandom.getRandomItem(this.rand, spawnTypes) : null;
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag()
    {
        this.allPlayersSleeping = !this.playerEntities.isEmpty();

        for (Object playerObject : this.playerEntities) {
            EntityPlayer player = (EntityPlayer) playerObject;

            if (!player.isPlayerSleeping()) {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    protected void wakeAllPlayers()
    {
        this.allPlayersSleeping = false;

        for (Object playerEntity : this.playerEntities) {
            EntityPlayer player = (EntityPlayer) playerEntity;

            if (player.isPlayerSleeping()) {
                player.wakeUpPlayer(false, false, true);
            }
        }

        this.resetRainAndThunder();
    }

    private void resetRainAndThunder()
    {
        this.worldInfo.setRainTime(0);
        this.worldInfo.setRaining(false);
        this.worldInfo.setThunderTime(0);
        this.worldInfo.setThundering(false);
    }

    public boolean areAllPlayersAsleep()
    {
        if (this.allPlayersSleeping && !this.isRemote)
        {
            Iterator playerEntitiesIterator = this.playerEntities.iterator();
            EntityPlayer currPlayer;

            do
            {
                if (!playerEntitiesIterator.hasNext())
                {
                    return true;
                }

                currPlayer = (EntityPlayer)playerEntitiesIterator.next();
            }
            while (currPlayer.isPlayerFullyAsleep());

            return false;
        }
        else
        {
            return false;
        }
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setSpawnLocation()
    {
        if (this.worldInfo.getSpawnY() <= 0)
        {
            this.worldInfo.setSpawnY(64);
        }

        int spawnX = this.worldInfo.getSpawnX();
        int spawnZ = this.worldInfo.getSpawnZ();
        int currY = 0;

        while (this.func_147474_b(spawnX, spawnZ).func_149688_o() == Material.field_151579_a)
        {
            spawnX += this.rand.nextInt(8) - this.rand.nextInt(8);
            spawnZ += this.rand.nextInt(8) - this.rand.nextInt(8);
            ++currY;

            if (currY == 10000)
            {
                break;
            }
        }

        this.worldInfo.setSpawnX(spawnX);
        this.worldInfo.setSpawnZ(spawnZ);
    }

    /**
     * plays random cave ambient sounds and runs updateTick on random blocks within each chunk in the vacinity of a
     * player
     */
    protected void func_147456_g()
    {
        if(ModList.eventTickBlocksAndAmbiance(this)){
            super.func_147456_g();

            int var1 = 0;
            int var2 = 0;
            for (Object activeChunkSet : this.activeChunkSet) {
                ChunkCoordIntPair loc = (ChunkCoordIntPair) activeChunkSet;
                int chunkX = loc.chunkXPos * 16;
                int chunkZ = loc.chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                Chunk chunk = this.getChunkFromChunkCoords(loc.chunkXPos, loc.chunkZPos);
                this.func_147467_a(chunkX, chunkZ, chunk);
                this.theProfiler.endStartSection("tickChunk");
                chunk.func_150804_b(false);
                this.theProfiler.endStartSection("thunder");
                int randNum;
                int blockX;
                int blockZ;
                int precipitationHeight;

                if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    randNum = this.updateLCG >> 2;
                    blockX = chunkX + (randNum & 15);
                    blockZ = chunkZ + (randNum >> 8 & 15);
                    precipitationHeight = this.getPrecipitationHeight(blockX, blockZ);

                    if (this.canLightningStrikeAt(blockX, precipitationHeight, blockZ)) {
                        this.addWeatherEffect(new EntityLightningBolt(this, (double) blockX, (double) precipitationHeight, (double) blockZ));
                    }
                }

                this.theProfiler.endStartSection("iceandsnow");
                int blockID;

                if (this.rand.nextInt(16) == 0) {
                    this.updateLCG = this.updateLCG * 3 + 1013904223;
                    randNum = this.updateLCG >> 2;
                    blockX = randNum & 15;
                    blockZ = randNum >> 8 & 15;
                    precipitationHeight = this.getPrecipitationHeight(blockX + chunkX, blockZ + chunkZ);



                    if (this.isBlockFreezableNaturally(blockX + chunkX, precipitationHeight - 1, blockZ + chunkZ))
                    {
                        this.func_147449_b(blockX + chunkX, precipitationHeight - 1, blockZ + chunkZ, Blocks.field_150432_aD);
                    }

                    if (this.isRaining() && this.func_147478_e(blockX + chunkX, precipitationHeight, blockZ + chunkX, true))
                    {
                        this.func_147449_b(blockX + chunkX, precipitationHeight, blockZ + chunkX, Blocks.field_150431_aC);
                    }

                    if (this.isRaining())
                    {
                        BiomeGenBase var12 = this.getBiomeGenForCoords(blockX + chunkX, blockZ + chunkX);

                        if (var12.canSpawnLightningBolt())
                        {
                            this.func_147439_a(blockX + chunkX, precipitationHeight - 1, blockZ + chunkX).func_149639_l(this, blockX + chunkX, precipitationHeight - 1, blockZ + chunkX);
                        }
                    }
                }

                this.theProfiler.endStartSection("tickTiles");
                ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
                blockX = blockStorageArray.length;

                for (blockZ = 0; blockZ < blockX; ++blockZ) {
                    ExtendedBlockStorage blockStorage = blockStorageArray[blockZ];

                    if (blockStorage != null && blockStorage.getNeedsRandomTick()) {
                        for (int var20 = 0; var20 < 3; ++var20) {
                            this.updateLCG = this.updateLCG * 3 + 1013904223;
                            blockID = this.updateLCG >> 2;
                            int xPos = blockID & 15;
                            int zPos = blockID >> 8 & 15;
                            int yPos = blockID >> 16 & 15;
                            Block block = blockStorage.func_150819_a(xPos, yPos, zPos);

                            if (block != null && block.func_149653_t()) {
                                block.func_149674_a(this, xPos + chunkX, yPos + blockStorage.getYLocation(), zPos + chunkZ, this.rand);
                            }
                        }
                    }
                }

                this.theProfiler.endSection();
            }
        }
    }

    public boolean func_147477_a(int p_147477_1_, int p_147477_2_, int p_147477_3_, Block p_147477_4_)
    {
        NextTickListEntry var5 = new NextTickListEntry(p_147477_1_, p_147477_2_, p_147477_3_, p_147477_4_);
        return this.pendingTickListEntriesThisTick.contains(var5);
    }

    public void func_147464_a(int p_147464_1_, int p_147464_2_, int p_147464_3_, Block p_147464_4_, int p_147464_5_)
    {
        this.func_147454_a(p_147464_1_, p_147464_2_, p_147464_3_, p_147464_4_, p_147464_5_, 0);
    }

    public void func_147454_a(int p_147454_1_, int p_147454_2_, int p_147454_3_, Block p_147454_4_, int p_147454_5_, int p_147454_6_)
    {
        NextTickListEntry var7 = new NextTickListEntry(p_147454_1_, p_147454_2_, p_147454_3_, p_147454_4_);
        byte var8 = 0;

        if (this.scheduledUpdatesAreImmediate && p_147454_4_.func_149688_o() != Material.field_151579_a)
        {
            if (p_147454_4_.func_149698_L())
            {
                var8 = 8;

                if (this.checkChunksExist(var7.xCoord - var8, var7.yCoord - var8, var7.zCoord - var8, var7.xCoord + var8, var7.yCoord + var8, var7.zCoord + var8))
                {
                    Block var9 = this.func_147439_a(var7.xCoord, var7.yCoord, var7.zCoord);

                    if (var9.func_149688_o() != Material.field_151579_a && var9 == var7.func_151351_a())
                    {
                        var9.func_149674_a(this, var7.xCoord, var7.yCoord, var7.zCoord, this.rand);
                    }
                }

                return;
            }

            p_147454_5_ = 1;
        }

        if (this.checkChunksExist(p_147454_1_ - var8, p_147454_2_ - var8, p_147454_3_ - var8, p_147454_1_ + var8, p_147454_2_ + var8, p_147454_3_ + var8))
        {
            if (p_147454_4_.func_149688_o() != Material.field_151579_a)
            {
                var7.setScheduledTime((long)p_147454_5_ + this.worldInfo.getWorldTotalTime());
                var7.setPriority(p_147454_6_);
            }

            if (!this.pendingTickListEntriesHashSet.contains(var7))
            {
                this.pendingTickListEntriesHashSet.add(var7);
                this.pendingTickListEntriesTreeSet.add(var7);
            }
        }
    }

    public void func_147446_b(int p_147446_1_, int p_147446_2_, int p_147446_3_, Block p_147446_4_, int p_147446_5_, int p_147446_6_)
    {
        NextTickListEntry var7 = new NextTickListEntry(p_147446_1_, p_147446_2_, p_147446_3_, p_147446_4_);
        var7.setPriority(p_147446_6_);

        if (p_147446_4_.func_149688_o() != Material.field_151579_a)
        {
            var7.setScheduledTime((long)p_147446_5_ + this.worldInfo.getWorldTotalTime());
        }

        if (!this.pendingTickListEntriesHashSet.contains(var7))
        {
            this.pendingTickListEntriesHashSet.add(var7);
            this.pendingTickListEntriesTreeSet.add(var7);
        }
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities()
    {
        if (this.playerEntities.isEmpty())
        {
            if (this.updateEntityTick++ >= 1200)
            {
                return;
            }
        }
        else
        {
            this.resetUpdateEntityTick();
        }

        super.updateEntities();
    }

    /**
     * Resets the updateEntityTick field to 0
     */
    public void resetUpdateEntityTick()
    {
        this.updateEntityTick = 0;
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(boolean par1)
    {
        int var2 = this.pendingTickListEntriesTreeSet.size();

        if (var2 != this.pendingTickListEntriesHashSet.size())
        {
            throw new IllegalStateException("TickNextTick list out of synch");
        }
        else
        {
            if (var2 > 1000)
            {
                var2 = 1000;
            }

            this.theProfiler.startSection("cleaning");
            NextTickListEntry var4;

            for (int var3 = 0; var3 < var2; ++var3)
            {
                var4 = (NextTickListEntry)this.pendingTickListEntriesTreeSet.first();

                if (!par1 && var4.scheduledTime > this.worldInfo.getWorldTotalTime())
                {
                    break;
                }

                this.pendingTickListEntriesTreeSet.remove(var4);
                this.pendingTickListEntriesHashSet.remove(var4);
                this.pendingTickListEntriesThisTick.add(var4);
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("ticking");
            Iterator var14 = this.pendingTickListEntriesThisTick.iterator();

            while (var14.hasNext())
            {
                var4 = (NextTickListEntry)var14.next();
                var14.remove();
                byte var5 = 0;

                if (this.checkChunksExist(var4.xCoord - var5, var4.yCoord - var5, var4.zCoord - var5, var4.xCoord + var5, var4.yCoord + var5, var4.zCoord + var5))
                {
                    Block var6 = this.func_147439_a(var4.xCoord, var4.yCoord, var4.zCoord);

                    if (var6.func_149688_o() != Material.field_151579_a && Block.func_149680_a(var6, var4.func_151351_a()))
                    {
                        try
                        {
                            var6.func_149674_a(this, var4.xCoord, var4.yCoord, var4.zCoord, this.rand);
                        }
                        catch (Throwable var13)
                        {
                            CrashReport var8 = CrashReport.makeCrashReport(var13, "Exception while ticking a block");
                            CrashReportCategory var9 = var8.makeCategory("Block being ticked");
                            int var10;

                            try
                            {
                                var10 = this.getBlockMetadata(var4.xCoord, var4.yCoord, var4.zCoord);
                            }
                            catch (Throwable var12)
                            {
                                var10 = -1;
                            }

                            CrashReportCategory.func_147153_a(var9, var4.xCoord, var4.yCoord, var4.zCoord, var6, var10);
                            throw new ReportedException(var8);
                        }
                    }
                }
                else
                {
                    this.func_147464_a(var4.xCoord, var4.yCoord, var4.zCoord, var4.func_151351_a(), 0);
                }
            }

            this.theProfiler.endSection();
            this.pendingTickListEntriesThisTick.clear();
            return !this.pendingTickListEntriesTreeSet.isEmpty();
        }
    }

    public List getPendingBlockUpdates(Chunk chunk, boolean par2)
    {
        ArrayList var3 = null;
        ChunkCoordIntPair var4 = chunk.getChunkCoordIntPair();
        int var5 = (var4.chunkXPos << 4) - 2;
        int var6 = var5 + 16 + 2;
        int var7 = (var4.chunkZPos << 4) - 2;
        int var8 = var7 + 16 + 2;

        for (int var9 = 0; var9 < 2; ++var9)
        {
            Iterator var10;

            if (var9 == 0)
            {
                var10 = this.pendingTickListEntriesTreeSet.iterator();
            }
            else
            {
                var10 = this.pendingTickListEntriesThisTick.iterator();

                if (!this.pendingTickListEntriesThisTick.isEmpty())
                {
                    field_147491_a.debug("toBeTicked = " + this.pendingTickListEntriesThisTick.size());
                }
            }

            while (var10.hasNext())
            {
                NextTickListEntry var11 = (NextTickListEntry)var10.next();

                if (var11.xCoord >= var5 && var11.xCoord < var6 && var11.zCoord >= var7 && var11.zCoord < var8)
                {
                    if (par2)
                    {
                        this.pendingTickListEntriesHashSet.remove(var11);
                        var10.remove();
                    }

                    if (var3 == null)
                    {
                        var3 = new ArrayList();
                    }

                    var3.add(var11);
                }
            }
        }

        return var3;
    }

    /**
     * Will update the entity in the world if the chunk the entity is in is currently loaded or its forced to update.
     * Args: entity, forceUpdate
     */
    public void updateEntityWithOptionalForce(Entity entity, boolean doForce)
    {
        if (!this.mcServer.getCanSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterMob))
        {
            entity.setDead();
        }

        if (!this.mcServer.getCanSpawnNPCs() && entity instanceof INpc)
        {
            entity.setDead();
        }

        super.updateEntityWithOptionalForce(entity, doForce);
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider()
    {
        this.theChunkProviderServer = new ChunkProviderServer(this, this.saveHandler.getChunkLoader(this.provider), this.provider.createChunkGenerator());
        return this.theChunkProviderServer;
    }

    /**
     * pars: min x,y,z , max x,y,z
     */
    public List func_147486_a(int p_147486_1_, int p_147486_2_, int p_147486_3_, int p_147486_4_, int p_147486_5_, int p_147486_6_)
    {
        ArrayList var7 = new ArrayList();

        for (int var8 = 0; var8 < this.field_147482_g.size(); ++var8)
        {
            TileEntity var9 = (TileEntity)this.field_147482_g.get(var8);

            if (var9.field_145851_c >= p_147486_1_ && var9.field_145848_d >= p_147486_2_ && var9.field_145849_e >= p_147486_3_ && var9.field_145851_c < p_147486_4_ && var9.field_145848_d < p_147486_5_ && var9.field_145849_e < p_147486_6_)
            {
                var7.add(var9);
            }
        }

        return var7;
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean canMineBlock(EntityPlayer player, int x, int y, int z)
    {
        return !this.mcServer.isBlockProtected(this, x, y, z, player);
    }

    protected void initialize(WorldSettings settings)
    {
        if (this.entityIdMap == null)
        {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null)
        {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null)
        {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.createSpawnPosition(settings);
        super.initialize(settings);
    }

    /**
     * creates a spawn position at random within 256 blocks of 0,0
     */
    protected void createSpawnPosition(WorldSettings settings)
    {
        if (!this.provider.canRespawnHere())
        {
            this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
        }
        else
        {
            this.findingSpawnPoint = true;
            WorldChunkManager chunkManager = this.provider.worldChunkMgr;
            List spawnBiomes = chunkManager.getBiomesToSpawnIn();
            Random random = new Random(this.getSeed());
            ChunkPosition position = chunkManager.func_150795_a(0, 0, 256, spawnBiomes, random);
            int var6 = 0;
            int var7 = this.provider.getAverageGroundLevel();
            int var8 = 0;

            if (position != null)
            {
                var6 = position.field_151329_a;
                var8 = position.field_151328_c;
            }
            else
            {
                field_147491_a.warn("Unable to find spawn biome");
            }

            int var9 = 0;

            while (!this.provider.canCoordinateBeSpawn(var6, var8))
            {
                var6 += random.nextInt(64) - random.nextInt(64);
                var8 += random.nextInt(64) - random.nextInt(64);
                ++var9;

                if (var9 == 1000)
                {
                    break;
                }
            }

            this.worldInfo.setSpawnPosition(var6, var7, var8);
            this.findingSpawnPoint = false;

            if (settings.isBonusChestEnabled())
            {
                this.createBonusChest();
            }
        }
    }

    /**
     * Creates the bonus chest in the world.
     */
    protected void createBonusChest()
    {
        WorldGeneratorBonusChest chestGen = new WorldGeneratorBonusChest(bonusChestContent, 10);

        for (int var2 = 0; var2 < 10; ++var2)
        {
            int var3 = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int var4 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int var5 = this.getTopSolidOrLiquidBlock(var3, var4) + 1;

            if (chestGen.generate(this, this.rand, var3, var5, var4))
            {
                break;
            }
        }
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension.
     */
    public ChunkCoordinates getEntrancePortalLocation()
    {
        return this.provider.getEntrancePortalLocation();
    }

    /**
     * Saves all chunks to disk while updating progress bar.
     */
    public void saveAllChunks(boolean par1, IProgressUpdate progressUpdate) throws MinecraftException
    {
        if (this.chunkProvider.canSave())
        {
            if (progressUpdate != null)
            {
                progressUpdate.displayProgressMessage("Saving level");
            }

            this.saveLevel();

            if (progressUpdate != null)
            {
                progressUpdate.resetProgresAndWorkingMessage("Saving chunks");
            }

            this.chunkProvider.saveChunks(par1, progressUpdate);
        }
    }

    /**
     * saves chunk data - currently only called during execution of the Save All command
     */
    public void saveChunkData()
    {
        if (this.chunkProvider.canSave())
        {
            this.chunkProvider.saveExtraData();
        }
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException
    {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
        this.mapStorage.saveAllData();
    }

    public void onEntityAdded(Entity entity)
    {
        super.onEntityAdded(entity);
        this.entityIdMap.addKey(entity.func_145782_y(), entity);
        Entity[] entityParts = entity.getParts();

        if (entityParts != null)
        {
            for (Entity aVar2 : entityParts) {
                this.entityIdMap.addKey(aVar2.func_145782_y(), aVar2);
            }
        }
    }

    protected void onEntityRemoved(Entity entity)
    {
        super.onEntityRemoved(entity);
        this.entityIdMap.removeObject(entity.func_145782_y());
        Entity[] entityParts = entity.getParts();

        if (entityParts != null)
        {
            for (Entity aVar2 : entityParts) {
                this.entityIdMap.removeObject(aVar2.func_145782_y());
            }
        }
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int entityID)
    {
        return (Entity)this.entityIdMap.lookup(entityID);
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity entity)
    {
        if (super.addWeatherEffect(entity))
        {
            this.mcServer.getConfigurationManager().func_148541_a(entity.posX, entity.posY, entity.posZ, 512.0D, this.provider.dimensionId, new S2CPacketSpawnGlobalEntity(entity));
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity entity, byte par2)
    {
        this.getEntityTracker().func_151248_b(entity, new S19PacketEntityStatus(entity, par2));
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity entity, double x, double y, double z, float force, boolean flaming, boolean smoking)
    {
        Explosion var11 = new Explosion(this, entity, x, y, z, force);
        var11.isFlaming = flaming;
        var11.isSmoking = smoking;
        var11.doExplosionA();
        var11.doExplosionB(false);

        if (!smoking)
        {
            var11.affectedBlockPositions.clear();
        }

        for (Object playerEntity : this.playerEntities) {
            EntityPlayer var13 = (EntityPlayer) playerEntity;

            if (var13.getDistanceSq(x, y, z) < 4096.0D) {
                ((EntityPlayerMP) var13).playerNetServerHandler.func_147359_a(new S27PacketExplosion(x, y, z, force, var11.affectedBlockPositions, (Vec3) var11.func_77277_b().get(var13)));
            }
        }

        return var11;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    public void func_147452_c(int p_147452_1_, int p_147452_2_, int p_147452_3_, Block p_147452_4_, int p_147452_5_, int p_147452_6_)
    {
        BlockEventData var7 = new BlockEventData(p_147452_1_, p_147452_2_, p_147452_3_, p_147452_4_, p_147452_5_, p_147452_6_);
        Iterator var8 = this.field_147490_S[this.field_147489_T].iterator();
        BlockEventData var9;

        do
        {
            if (!var8.hasNext())
            {
                this.field_147490_S[this.field_147489_T].add(var7);
                return;
            }

            var9 = (BlockEventData)var8.next();
        }
        while (!var9.equals(var7));
    }

    /**
     * Send and apply locally all pending BlockEvents to each player with 64m radius of the event.
     */
    private void func_147488_Z()
    {
        while (!this.field_147490_S[this.field_147489_T].isEmpty())
        {
            int var1 = this.field_147489_T;
            this.field_147489_T ^= 1;
            Iterator var2 = this.field_147490_S[var1].iterator();

            while (var2.hasNext())
            {
                BlockEventData var3 = (BlockEventData)var2.next();

                if (this.func_147485_a(var3))
                {
                    this.mcServer.getConfigurationManager().func_148541_a((double)var3.func_151340_a(), (double)var3.func_151342_b(), (double)var3.func_151341_c(), 64.0D, this.provider.dimensionId, new S24PacketBlockAction(var3.func_151340_a(), var3.func_151342_b(), var3.func_151341_c(), var3.func_151337_f(), var3.func_151339_d(), var3.func_151338_e()));
                }
            }

            this.field_147490_S[var1].clear();
        }
    }

    /**
     * Called to apply a pending BlockEvent to apply to the current world.
     */
    private boolean func_147485_a(BlockEventData p_147485_1_)
    {
        Block var2 = this.func_147439_a(p_147485_1_.func_151340_a(), p_147485_1_.func_151342_b(), p_147485_1_.func_151341_c());
        return var2 == p_147485_1_.func_151337_f() ? var2.func_149696_a(this, p_147485_1_.func_151340_a(), p_147485_1_.func_151342_b(), p_147485_1_.func_151341_c(), p_147485_1_.func_151339_d(), p_147485_1_.func_151338_e()) : false;
    }

    /**
     * Syncs all changes to disk and wait for completion.
     */
    public void flush()
    {
        this.saveHandler.flush();
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather()
    {
        boolean var1 = this.isRaining();
        super.updateWeather();

        if (this.prevRainingStrength != this.rainingStrength)
        {
            this.mcServer.getConfigurationManager().func_148537_a(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.dimensionId);
        }

        if (this.prevThunderingStrength != this.thunderingStrength)
        {
            this.mcServer.getConfigurationManager().func_148537_a(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.dimensionId);
        }

        if (var1 != this.isRaining())
        {
            if (var1)
            {
                this.mcServer.getConfigurationManager().func_148540_a(new S2BPacketChangeGameState(2, 0));
            }
            else
            {
                this.mcServer.getConfigurationManager().func_148540_a(new S2BPacketChangeGameState(1, 0));
            }
        }
    }

    /**
     * Gets the MinecraftServer.
     */
    public MinecraftServer getMinecraftServer()
    {
        return this.mcServer;
    }

    /**
     * Gets the EntityTracker
     */
    public EntityTracker getEntityTracker()
    {
        return this.theEntityTracker;
    }

    public PlayerManager getPlayerManager()
    {
        return this.thePlayerManager;
    }

    public Teleporter getDefaultTeleporter()
    {
        return this.worldTeleporter;
    }

    public void func_147487_a(String p_147487_1_, double p_147487_2_, double p_147487_4_, double p_147487_6_, int p_147487_8_, double p_147487_9_, double p_147487_11_, double p_147487_13_, double p_147487_15_)
    {
        S2APacketParticles var17 = new S2APacketParticles(p_147487_1_, (float)p_147487_2_, (float)p_147487_4_, (float)p_147487_6_, (float)p_147487_9_, (float)p_147487_11_, (float)p_147487_13_, (float)p_147487_15_, p_147487_8_);

        for (int var18 = 0; var18 < this.playerEntities.size(); ++var18)
        {
            EntityPlayerMP var19 = (EntityPlayerMP)this.playerEntities.get(var18);
            ChunkCoordinates var20 = var19.getPlayerCoordinates();
            double var21 = p_147487_2_ - (double)var20.posX;
            double var23 = p_147487_4_ - (double)var20.posY;
            double var25 = p_147487_6_ - (double)var20.posZ;
            double var27 = var21 * var21 + var23 * var23 + var25 * var25;

            if (var27 <= 256.0D)
            {
                var19.playerNetServerHandler.func_147359_a(var17);
            }
        }
    }

    static class ServerBlockEventList extends ArrayList
    {
        private static final String __OBFID = "CL_00001439";

        private ServerBlockEventList() {}

        ServerBlockEventList(Object par1ServerBlockEvent)
        {
            this();
        }
    }
}
