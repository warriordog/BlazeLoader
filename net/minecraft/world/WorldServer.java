package net.minecraft.world;

import net.acomputerdog.BlazeLoader.mod.ModList;
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
import net.minecraft.network.play.server.*;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
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

import java.util.*;

/**
 * A server-side world.  Replaces WorldServerProxy.
 */
public class WorldServer extends World {
    private static final Logger field_147491_a = LogManager.getLogger();
    private final MinecraftServer mcServer;
    private final EntityTracker theEntityTracker;
    private final PlayerManager thePlayerManager;
    private Set pendingTickListEntriesHashSet;

    /**
     * All work to do in future ticks.
     */
    private TreeSet pendingTickListEntriesTreeSet;
    public ChunkProviderServer theChunkProviderServer;

    /**
     * set by CommandServerSave{all,Off,On}
     */
    public boolean canNotSave;

    /**
     * is false if there are no players
     */
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
    private WorldServer.ServerBlockEventList[] field_147490_S = new WorldServer.ServerBlockEventList[]{new WorldServer.ServerBlockEventList(null), new WorldServer.ServerBlockEventList(null)};
    private int field_147489_T;
    private static final WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[]{new WeightedRandomChestContent(Items.field_151055_y, 0, 1, 3, 10), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150344_f), 0, 1, 3, 10), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150364_r), 0, 1, 3, 10), new WeightedRandomChestContent(Items.field_151049_t, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151053_p, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151050_s, 0, 1, 1, 3), new WeightedRandomChestContent(Items.field_151039_o, 0, 1, 1, 5), new WeightedRandomChestContent(Items.field_151034_e, 0, 2, 3, 5), new WeightedRandomChestContent(Items.field_151025_P, 0, 2, 3, 3), new WeightedRandomChestContent(Item.func_150898_a(Blocks.field_150363_s), 0, 1, 3, 10)};
    private List pendingTickListEntriesThisTick = new ArrayList();

    /**
     * An IntHashMap of entity IDs (integers) to their Entity objects.
     */
    private IntHashMap entityIdMap;
    private static final String __OBFID = "CL_00001437";

    public WorldServer(MinecraftServer p_i45284_1_, ISaveHandler p_i45284_2_, String p_i45284_3_, int p_i45284_4_, WorldSettings p_i45284_5_, Profiler p_i45284_6_) {
        super(p_i45284_2_, p_i45284_3_, p_i45284_5_, WorldProvider.getProviderForDimension(p_i45284_4_), p_i45284_6_);
        this.mcServer = p_i45284_1_;
        this.theEntityTracker = new EntityTracker(this);
        this.thePlayerManager = new PlayerManager(this, p_i45284_1_.getConfigurationManager().getViewDistance());

        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.worldTeleporter = new Teleporter(this);
        this.worldScoreboard = new ServerScoreboard(p_i45284_1_);
        ScoreboardSaveData var7 = (ScoreboardSaveData) this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

        if (var7 == null) {
            var7 = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", var7);
        }

        var7.func_96499_a(this.worldScoreboard);
        ((ServerScoreboard) this.worldScoreboard).func_96547_a(var7);
    }

    /**
     * Runs a single tick for the world
     */
    public void tick() {
        ModList.eventTickServerWorld(this);
        super.tick();

        if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting != EnumDifficulty.HARD) {
            this.difficultySetting = EnumDifficulty.HARD;
        }

        this.provider.worldChunkMgr.cleanupCache();

        if (this.areAllPlayersAsleep()) {
            if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
                long var1 = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(var1 - var1 % 24000L);
            }

            this.wakeAllPlayers();
        }

        this.theProfiler.startSection("mobSpawner");

        if (this.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
            this.animalSpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
        }

        this.theProfiler.endStartSection("chunkSource");
        this.chunkProvider.unloadQueuedChunks();
        int var3 = this.calculateSkylightSubtracted(1.0F);

        if (var3 != this.skylightSubtracted) {
            this.skylightSubtracted = var3;
        }

        this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);

        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
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
    public BiomeGenBase.SpawnListEntry spawnRandomCreature(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
        List var5 = this.getChunkProvider().getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
        return var5 != null && !var5.isEmpty() ? (BiomeGenBase.SpawnListEntry) WeightedRandom.getRandomItem(this.rand, var5) : null;
    }

    /**
     * Updates the flag that indicates whether or not all players in the world are sleeping.
     */
    public void updateAllPlayersSleepingFlag() {
        this.allPlayersSleeping = !this.playerEntities.isEmpty();

        for (Object playerEntity : this.playerEntities) {
            EntityPlayer var2 = (EntityPlayer) playerEntity;

            if (!var2.isPlayerSleeping()) {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    protected void wakeAllPlayers() {
        this.allPlayersSleeping = false;

        for (Object playerEntity : this.playerEntities) {
            EntityPlayer var2 = (EntityPlayer) playerEntity;

            if (var2.isPlayerSleeping()) {
                var2.wakeUpPlayer(false, false, true);
            }
        }

        this.resetRainAndThunder();
    }

    private void resetRainAndThunder() {
        this.worldInfo.setRainTime(0);
        this.worldInfo.setRaining(false);
        this.worldInfo.setThunderTime(0);
        this.worldInfo.setThundering(false);
    }

    public boolean areAllPlayersAsleep() {
        if (this.allPlayersSleeping && !this.isRemote) {
            Iterator var1 = this.playerEntities.iterator();
            EntityPlayer var2;

            do {
                if (!var1.hasNext()) {
                    return true;
                }

                var2 = (EntityPlayer) var1.next();
            }
            while (var2.isPlayerFullyAsleep());

            return false;
        } else {
            return false;
        }
    }

    /**
     * Sets a new spawn location by finding an uncovered block at a random (x,z) location in the chunk.
     */
    public void setSpawnLocation() {
        if (this.worldInfo.getSpawnY() <= 0) {
            this.worldInfo.setSpawnY(64);
        }

        int var1 = this.worldInfo.getSpawnX();
        int var2 = this.worldInfo.getSpawnZ();
        int var3 = 0;

        while (this.func_147474_b(var1, var2).func_149688_o() == Material.field_151579_a) {
            var1 += this.rand.nextInt(8) - this.rand.nextInt(8);
            var2 += this.rand.nextInt(8) - this.rand.nextInt(8);
            ++var3;

            if (var3 == 10000) {
                break;
            }
        }

        this.worldInfo.setSpawnX(var1);
        this.worldInfo.setSpawnZ(var2);
    }

    protected void func_147456_g() {
        ModList.eventTickBlocksAndAmbiance(this);

        super.func_147456_g();
        int var1 = 0;
        int var2 = 0;

        for (Object anActiveChunkSet : this.activeChunkSet) {
            ChunkCoordIntPair var4 = (ChunkCoordIntPair) anActiveChunkSet;
            int var5 = var4.chunkXPos * 16;
            int var6 = var4.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk var7 = this.getChunkFromChunkCoords(var4.chunkXPos, var4.chunkZPos);
            this.func_147467_a(var5, var6, var7);
            this.theProfiler.endStartSection("tickChunk");
            var7.func_150804_b(false);
            this.theProfiler.endStartSection("thunder");
            int var8;
            int var9;
            int var10;
            int var11;

            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                var8 = this.updateLCG >> 2;
                var9 = var5 + (var8 & 15);
                var10 = var6 + (var8 >> 8 & 15);
                var11 = this.getPrecipitationHeight(var9, var10);

                if (this.canLightningStrikeAt(var9, var11, var10)) {
                    this.addWeatherEffect(new EntityLightningBolt(this, (double) var9, (double) var11, (double) var10));
                }
            }

            this.theProfiler.endStartSection("iceandsnow");


            if (this.rand.nextInt(16) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                var8 = this.updateLCG >> 2;
                var9 = var8 & 15;
                var10 = var8 >> 8 & 15;
                var11 = this.getPrecipitationHeight(var9 + var5, var10 + var6);

                if (this.isBlockFreezableNaturally(var9 + var5, var11 - 1, var10 + var6)) {
                    this.func_147449_b(var9 + var5, var11 - 1, var10 + var6, Blocks.field_150432_aD);
                }

                if (this.isRaining() && this.func_147478_e(var9 + var5, var11, var10 + var6, true)) {
                    this.func_147449_b(var9 + var5, var11, var10 + var6, Blocks.field_150431_aC);
                }

                if (this.isRaining()) {
                    BiomeGenBase var12 = this.getBiomeGenForCoords(var9 + var5, var10 + var6);


                    if (var12.canSpawnLightningBolt()) {
                        this.func_147439_a(var9 + var5, var11 - 1, var10 + var6).func_149639_l(this, var9 + var5, var11 - 1, var10 + var6);
                    }
                }
            }

            this.theProfiler.endStartSection("tickBlocks");
            ExtendedBlockStorage[] var18 = var7.getBlockStorageArray();
            var9 = var18.length;

            for (var10 = 0; var10 < var9; ++var10) {
                ExtendedBlockStorage var20 = var18[var10];


                if (var20 != null && var20.getNeedsRandomTick()) {
                    for (int var19 = 0; var19 < 3; ++var19) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int var13 = this.updateLCG >> 2;
                        int var14 = var13 & 15;
                        int var15 = var13 >> 8 & 15;
                        int var16 = var13 >> 16 & 15;
                        ++var2;
                        Block var17 = var20.func_150819_a(var14, var16, var15);


                        if (var17.func_149653_t()) {
                            ++var1;
                            var17.func_149674_a(this, var14 + var5, var16 + var20.getYLocation(), var15 + var6, this.rand);
                        }
                    }
                }


            }

            this.theProfiler.endSection();
        }
    }

    public boolean func_147477_a(int p_147477_1_, int p_147477_2_, int p_147477_3_, Block p_147477_4_) {
        NextTickListEntry var5 = new NextTickListEntry(p_147477_1_, p_147477_2_, p_147477_3_, p_147477_4_);
        return this.pendingTickListEntriesThisTick.contains(var5);
    }

    public void func_147464_a(int p_147464_1_, int p_147464_2_, int p_147464_3_, Block p_147464_4_, int p_147464_5_) {
        this.func_147454_a(p_147464_1_, p_147464_2_, p_147464_3_, p_147464_4_, p_147464_5_, 0);
    }

    public void func_147454_a(int p_147454_1_, int p_147454_2_, int p_147454_3_, Block p_147454_4_, int p_147454_5_, int p_147454_6_) {
        NextTickListEntry var7 = new NextTickListEntry(p_147454_1_, p_147454_2_, p_147454_3_, p_147454_4_);
        byte var8 = 0;

        if (this.scheduledUpdatesAreImmediate && p_147454_4_.func_149688_o() != Material.field_151579_a) {
            if (p_147454_4_.func_149698_L()) {
                var8 = 8;

                if (this.checkChunksExist(var7.xCoord - var8, var7.yCoord - var8, var7.zCoord - var8, var7.xCoord + var8, var7.yCoord + var8, var7.zCoord + var8)) {
                    Block var9 = this.func_147439_a(var7.xCoord, var7.yCoord, var7.zCoord);

                    if (var9.func_149688_o() != Material.field_151579_a && var9 == var7.func_151351_a()) {
                        var9.func_149674_a(this, var7.xCoord, var7.yCoord, var7.zCoord, this.rand);
                    }
                }

                return;
            }

            p_147454_5_ = 1;
        }

        if (this.checkChunksExist(p_147454_1_ - var8, p_147454_2_ - var8, p_147454_3_ - var8, p_147454_1_ + var8, p_147454_2_ + var8, p_147454_3_ + var8)) {
            if (p_147454_4_.func_149688_o() != Material.field_151579_a) {
                var7.setScheduledTime((long) p_147454_5_ + this.worldInfo.getWorldTotalTime());
                var7.setPriority(p_147454_6_);
            }

            if (!this.pendingTickListEntriesHashSet.contains(var7)) {
                this.pendingTickListEntriesHashSet.add(var7);
                this.pendingTickListEntriesTreeSet.add(var7);
            }
        }
    }

    public void func_147446_b(int p_147446_1_, int p_147446_2_, int p_147446_3_, Block p_147446_4_, int p_147446_5_, int p_147446_6_) {
        NextTickListEntry var7 = new NextTickListEntry(p_147446_1_, p_147446_2_, p_147446_3_, p_147446_4_);
        var7.setPriority(p_147446_6_);

        if (p_147446_4_.func_149688_o() != Material.field_151579_a) {
            var7.setScheduledTime((long) p_147446_5_ + this.worldInfo.getWorldTotalTime());
        }

        if (!this.pendingTickListEntriesHashSet.contains(var7)) {
            this.pendingTickListEntriesHashSet.add(var7);
            this.pendingTickListEntriesTreeSet.add(var7);
        }
    }

    /**
     * Updates (and cleans up) entities and tile entities
     */
    public void updateEntities() {
        if (this.playerEntities.isEmpty()) {
            if (this.updateEntityTick++ >= 1200) {
                return;
            }
        } else {
            this.resetUpdateEntityTick();
        }

        super.updateEntities();
    }

    /**
     * Resets the updateEntityTick field to 0
     */
    public void resetUpdateEntityTick() {
        this.updateEntityTick = 0;
    }

    /**
     * Runs through the list of updates to run and ticks them
     */
    public boolean tickUpdates(boolean par1) {
        int var2 = this.pendingTickListEntriesTreeSet.size();

        if (var2 != this.pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        } else {
            if (var2 > 1000) {
                var2 = 1000;
            }

            this.theProfiler.startSection("cleaning");
            NextTickListEntry var4;

            for (int var3 = 0; var3 < var2; ++var3) {
                var4 = (NextTickListEntry) this.pendingTickListEntriesTreeSet.first();

                if (!par1 && var4.scheduledTime > this.worldInfo.getWorldTotalTime()) {
                    break;
                }

                this.pendingTickListEntriesTreeSet.remove(var4);
                this.pendingTickListEntriesHashSet.remove(var4);
                this.pendingTickListEntriesThisTick.add(var4);
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("ticking");
            Iterator var14 = this.pendingTickListEntriesThisTick.iterator();

            while (var14.hasNext()) {
                var4 = (NextTickListEntry) var14.next();
                var14.remove();
                byte var5 = 0;

                if (this.checkChunksExist(var4.xCoord - var5, var4.yCoord - var5, var4.zCoord - var5, var4.xCoord + var5, var4.yCoord + var5, var4.zCoord + var5)) {
                    Block var6 = this.func_147439_a(var4.xCoord, var4.yCoord, var4.zCoord);

                    if (var6.func_149688_o() != Material.field_151579_a && Block.func_149680_a(var6, var4.func_151351_a())) {
                        try {
                            var6.func_149674_a(this, var4.xCoord, var4.yCoord, var4.zCoord, this.rand);
                        } catch (Throwable var13) {
                            CrashReport var8 = CrashReport.makeCrashReport(var13, "Exception while ticking a block");
                            CrashReportCategory var9 = var8.makeCategory("Block being ticked");
                            int var10;

                            try {
                                var10 = this.getBlockMetadata(var4.xCoord, var4.yCoord, var4.zCoord);
                            } catch (Throwable var12) {
                                var10 = -1;
                            }

                            CrashReportCategory.func_147153_a(var9, var4.xCoord, var4.yCoord, var4.zCoord, var6, var10);
                            throw new ReportedException(var8);
                        }
                    }
                } else {
                    this.func_147464_a(var4.xCoord, var4.yCoord, var4.zCoord, var4.func_151351_a(), 0);
                }
            }

            this.theProfiler.endSection();
            this.pendingTickListEntriesThisTick.clear();
            return !this.pendingTickListEntriesTreeSet.isEmpty();
        }
    }

    public List getPendingBlockUpdates(Chunk par1Chunk, boolean par2) {
        ArrayList var3 = null;
        ChunkCoordIntPair var4 = par1Chunk.getChunkCoordIntPair();
        int var5 = (var4.chunkXPos << 4) - 2;
        int var6 = var5 + 16 + 2;
        int var7 = (var4.chunkZPos << 4) - 2;
        int var8 = var7 + 16 + 2;

        for (int var9 = 0; var9 < 2; ++var9) {
            Iterator var10;

            if (var9 == 0) {
                var10 = this.pendingTickListEntriesTreeSet.iterator();
            } else {
                var10 = this.pendingTickListEntriesThisTick.iterator();

                if (!this.pendingTickListEntriesThisTick.isEmpty()) {
                    field_147491_a.debug("toBeTicked = " + this.pendingTickListEntriesThisTick.size());
                }
            }

            while (var10.hasNext()) {
                NextTickListEntry var11 = (NextTickListEntry) var10.next();

                if (var11.xCoord >= var5 && var11.xCoord < var6 && var11.zCoord >= var7 && var11.zCoord < var8) {
                    if (par2) {
                        this.pendingTickListEntriesHashSet.remove(var11);
                        var10.remove();
                    }

                    if (var3 == null) {
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
    public void updateEntityWithOptionalForce(Entity par1Entity, boolean par2) {
        if (!this.mcServer.getCanSpawnAnimals() && (par1Entity instanceof EntityAnimal || par1Entity instanceof EntityWaterMob)) {
            par1Entity.setDead();
        }

        if (!this.mcServer.getCanSpawnNPCs() && par1Entity instanceof INpc) {
            par1Entity.setDead();
        }

        super.updateEntityWithOptionalForce(par1Entity, par2);
    }

    /**
     * Creates the chunk provider for this world. Called in the constructor. Retrieves provider from worldProvider?
     */
    protected IChunkProvider createChunkProvider() {
        IChunkLoader var1 = this.saveHandler.getChunkLoader(this.provider);
        this.theChunkProviderServer = new ChunkProviderServer(this, var1, this.provider.createChunkGenerator());
        return this.theChunkProviderServer;
    }

    /**
     * pars: min x,y,z , max x,y,z
     */
    public List func_147486_a(int p_147486_1_, int p_147486_2_, int p_147486_3_, int p_147486_4_, int p_147486_5_, int p_147486_6_) {
        ArrayList var7 = new ArrayList();

        for (Object aField_147482_g : this.field_147482_g) {
            TileEntity var9 = (TileEntity) aField_147482_g;

            if (var9.field_145851_c >= p_147486_1_ && var9.field_145848_d >= p_147486_2_ && var9.field_145849_e >= p_147486_3_ && var9.field_145851_c < p_147486_4_ && var9.field_145848_d < p_147486_5_ && var9.field_145849_e < p_147486_6_) {
                var7.add(var9);
            }
        }

        return var7;
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean canMineBlock(EntityPlayer par1EntityPlayer, int par2, int par3, int par4) {
        return !this.mcServer.isBlockProtected(this, par2, par3, par4, par1EntityPlayer);
    }

    protected void initialize(WorldSettings par1WorldSettings) {
        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.createSpawnPosition(par1WorldSettings);
        super.initialize(par1WorldSettings);
    }

    /**
     * creates a spawn position at random within 256 blocks of 0,0
     */
    protected void createSpawnPosition(WorldSettings par1WorldSettings) {
        if (!this.provider.canRespawnHere()) {
            this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
        } else {
            this.findingSpawnPoint = true;
            WorldChunkManager var2 = this.provider.worldChunkMgr;
            List var3 = var2.getBiomesToSpawnIn();
            Random var4 = new Random(this.getSeed());
            ChunkPosition var5 = var2.func_150795_a(0, 0, 256, var3, var4);
            int var6 = 0;
            int var7 = this.provider.getAverageGroundLevel();
            int var8 = 0;

            if (var5 != null) {
                var6 = var5.field_151329_a;
                var8 = var5.field_151328_c;
            } else {
                field_147491_a.warn("Unable to find spawn biome");
            }

            int var9 = 0;

            while (!this.provider.canCoordinateBeSpawn(var6, var8)) {
                var6 += var4.nextInt(64) - var4.nextInt(64);
                var8 += var4.nextInt(64) - var4.nextInt(64);
                ++var9;

                if (var9 == 1000) {
                    break;
                }
            }

            this.worldInfo.setSpawnPosition(var6, var7, var8);
            this.findingSpawnPoint = false;

            if (par1WorldSettings.isBonusChestEnabled()) {
                this.createBonusChest();
            }
        }
    }

    /**
     * Creates the bonus chest in the world.
     */
    protected void createBonusChest() {
        WorldGeneratorBonusChest var1 = new WorldGeneratorBonusChest(bonusChestContent, 10);

        for (int var2 = 0; var2 < 10; ++var2) {
            int var3 = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int var4 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int var5 = this.getTopSolidOrLiquidBlock(var3, var4) + 1;

            if (var1.generate(this, this.rand, var3, var5, var4)) {
                break;
            }
        }
    }

    /**
     * Gets the hard-coded portal location to use when entering this dimension.
     */
    public ChunkCoordinates getEntrancePortalLocation() {
        return this.provider.getEntrancePortalLocation();
    }

    /**
     * Saves all chunks to disk while updating progress bar.
     */
    public void saveAllChunks(boolean par1, IProgressUpdate par2IProgressUpdate) throws MinecraftException {
        if (this.chunkProvider.canSave()) {
            if (par2IProgressUpdate != null) {
                par2IProgressUpdate.displayProgressMessage("Saving level");
            }

            this.saveLevel();

            if (par2IProgressUpdate != null) {
                par2IProgressUpdate.resetProgresAndWorkingMessage("Saving chunks");
            }

            this.chunkProvider.saveChunks(par1, par2IProgressUpdate);
        }
    }

    /**
     * saves chunk data - currently only called during execution of the Save All command
     */
    public void saveChunkData() {
        if (this.chunkProvider.canSave()) {
            this.chunkProvider.saveExtraData();
        }
    }

    /**
     * Saves the chunks to disk.
     */
    protected void saveLevel() throws MinecraftException {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
        this.mapStorage.saveAllData();
    }

    protected void onEntityAdded(Entity par1Entity) {
        super.onEntityAdded(par1Entity);
        this.entityIdMap.addKey(par1Entity.func_145782_y(), par1Entity);
        Entity[] var2 = par1Entity.getParts();

        if (var2 != null) {
            for (Entity aVar2 : var2) {
                this.entityIdMap.addKey(aVar2.func_145782_y(), aVar2);
            }
        }
    }

    protected void onEntityRemoved(Entity par1Entity) {
        super.onEntityRemoved(par1Entity);
        this.entityIdMap.removeObject(par1Entity.func_145782_y());
        Entity[] var2 = par1Entity.getParts();

        if (var2 != null) {
            for (Entity aVar2 : var2) {
                this.entityIdMap.removeObject(aVar2.func_145782_y());
            }
        }
    }

    /**
     * Returns the Entity with the given ID, or null if it doesn't exist in this World.
     */
    public Entity getEntityByID(int par1) {
        return (Entity) this.entityIdMap.lookup(par1);
    }

    /**
     * adds a lightning bolt to the list of lightning bolts in this world.
     */
    public boolean addWeatherEffect(Entity par1Entity) {
        if (super.addWeatherEffect(par1Entity)) {
            this.mcServer.getConfigurationManager().func_148541_a(par1Entity.posX, par1Entity.posY, par1Entity.posZ, 512.0D, this.provider.dimensionId, new S2CPacketSpawnGlobalEntity(par1Entity));
            return true;
        } else {
            return false;
        }
    }

    /**
     * sends a Packet 38 (Entity Status) to all tracked players of that entity
     */
    public void setEntityState(Entity par1Entity, byte par2) {
        this.getEntityTracker().func_151248_b(par1Entity, new S19PacketEntityStatus(par1Entity, par2));
    }

    /**
     * returns a new explosion. Does initiation (at time of writing Explosion is not finished)
     */
    public Explosion newExplosion(Entity par1Entity, double par2, double par4, double par6, float par8, boolean par9, boolean par10) {
        Explosion var11 = new Explosion(this, par1Entity, par2, par4, par6, par8);
        var11.isFlaming = par9;
        var11.isSmoking = par10;
        var11.doExplosionA();
        var11.doExplosionB(false);

        if (!par10) {
            var11.affectedBlockPositions.clear();
        }

        for (Object playerEntity : this.playerEntities) {
            EntityPlayer var13 = (EntityPlayer) playerEntity;

            if (var13.getDistanceSq(par2, par4, par6) < 4096.0D) {
                ((EntityPlayerMP) var13).playerNetServerHandler.func_147359_a(new S27PacketExplosion(par2, par4, par6, par8, var11.affectedBlockPositions, (Vec3) var11.func_77277_b().get(var13)));
            }
        }

        return var11;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    public void func_147452_c(int p_147452_1_, int p_147452_2_, int p_147452_3_, Block p_147452_4_, int p_147452_5_, int p_147452_6_) {
        BlockEventData var7 = new BlockEventData(p_147452_1_, p_147452_2_, p_147452_3_, p_147452_4_, p_147452_5_, p_147452_6_);
        Iterator var8 = this.field_147490_S[this.field_147489_T].iterator();
        BlockEventData var9;

        do {
            if (!var8.hasNext()) {
                this.field_147490_S[this.field_147489_T].add(var7);
                return;
            }

            var9 = (BlockEventData) var8.next();
        }
        while (!var9.equals(var7));
    }

    /**
     * Send and apply locally all pending BlockEvents to each player with 64m radius of the event.
     */
    private void func_147488_Z() {
        while (!this.field_147490_S[this.field_147489_T].isEmpty()) {
            int var1 = this.field_147489_T;
            this.field_147489_T ^= 1;

            for (Object o : this.field_147490_S[var1]) {
                BlockEventData var3 = (BlockEventData) o;

                if (this.func_147485_a(var3)) {
                    this.mcServer.getConfigurationManager().func_148541_a((double) var3.func_151340_a(), (double) var3.func_151342_b(), (double) var3.func_151341_c(), 64.0D, this.provider.dimensionId, new S24PacketBlockAction(var3.func_151340_a(), var3.func_151342_b(), var3.func_151341_c(), var3.func_151337_f(), var3.func_151339_d(), var3.func_151338_e()));
                }
            }

            this.field_147490_S[var1].clear();
        }
    }

    /**
     * Called to apply a pending BlockEvent to apply to the current world.
     */
    private boolean func_147485_a(BlockEventData p_147485_1_) {
        Block var2 = this.func_147439_a(p_147485_1_.func_151340_a(), p_147485_1_.func_151342_b(), p_147485_1_.func_151341_c());
        return var2 == p_147485_1_.func_151337_f() ? var2.func_149696_a(this, p_147485_1_.func_151340_a(), p_147485_1_.func_151342_b(), p_147485_1_.func_151341_c(), p_147485_1_.func_151339_d(), p_147485_1_.func_151338_e()) : false;
    }

    /**
     * Syncs all changes to disk and wait for completion.
     */
    public void flush() {
        this.saveHandler.flush();
    }

    /**
     * Updates all weather states.
     */
    protected void updateWeather() {
        boolean var1 = this.isRaining();
        super.updateWeather();

        if (this.prevRainingStrength != this.rainingStrength) {
            this.mcServer.getConfigurationManager().func_148537_a(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.dimensionId);
        }

        if (this.prevThunderingStrength != this.thunderingStrength) {
            this.mcServer.getConfigurationManager().func_148537_a(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.dimensionId);
        }

        if (var1 != this.isRaining()) {
            if (var1) {
                this.mcServer.getConfigurationManager().func_148540_a(new S2BPacketChangeGameState(2, 0.0F));
            } else {
                this.mcServer.getConfigurationManager().func_148540_a(new S2BPacketChangeGameState(1, 0.0F));
            }

            this.mcServer.getConfigurationManager().func_148540_a(new S2BPacketChangeGameState(7, this.rainingStrength));
            this.mcServer.getConfigurationManager().func_148540_a(new S2BPacketChangeGameState(8, this.thunderingStrength));
        }
    }

    /**
     * Gets the MinecraftServer.
     */
    public MinecraftServer getMinecraftServer() {
        return this.mcServer;
    }

    /**
     * Gets the EntityTracker
     */
    public EntityTracker getEntityTracker() {
        return this.theEntityTracker;
    }

    public PlayerManager getPlayerManager() {
        return this.thePlayerManager;
    }

    public Teleporter getDefaultTeleporter() {
        return this.worldTeleporter;
    }

    public void func_147487_a(String p_147487_1_, double p_147487_2_, double p_147487_4_, double p_147487_6_, int p_147487_8_, double p_147487_9_, double p_147487_11_, double p_147487_13_, double p_147487_15_) {
        S2APacketParticles var17 = new S2APacketParticles(p_147487_1_, (float) p_147487_2_, (float) p_147487_4_, (float) p_147487_6_, (float) p_147487_9_, (float) p_147487_11_, (float) p_147487_13_, (float) p_147487_15_, p_147487_8_);

        for (Object playerEntity : this.playerEntities) {
            EntityPlayerMP var19 = (EntityPlayerMP) playerEntity;
            ChunkCoordinates var20 = var19.getPlayerCoordinates();
            double var21 = p_147487_2_ - (double) var20.posX;
            double var23 = p_147487_4_ - (double) var20.posY;
            double var25 = p_147487_6_ - (double) var20.posZ;
            double var27 = var21 * var21 + var23 * var23 + var25 * var25;

            if (var27 <= 256.0D) {
                var19.playerNetServerHandler.func_147359_a(var17);
            }
        }
    }

    static class ServerBlockEventList extends ArrayList {
        private static final String __OBFID = "CL_00001439";

        private ServerBlockEventList() {
        }

        ServerBlockEventList(Object par1ServerBlockEvent) {
            this();
        }
    }
}
