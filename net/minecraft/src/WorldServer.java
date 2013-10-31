package net.minecraft.src;

import net.acomputerdog.BlazeLoader.mod.ModList;
import net.minecraft.server.MinecraftServer;

import java.util.*;

/**
 * A server-side world.  Replaces WorldServerProxy.
 */
public class WorldServer extends World
{
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
    private ServerBlockEventList[] blockEventCache = new ServerBlockEventList[] {new ServerBlockEventList(null), new ServerBlockEventList(null)};

    /**
     * The index into the blockEventCache; either 0, or 1, toggled in sendBlockEventPackets  where all BlockEvent are
     * applied locally and send to clients.
     */
    private int blockEventCacheIndex;
    private static final WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[] {new WeightedRandomChestContent(Item.stick.itemID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.planks.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Block.wood.blockID, 0, 1, 3, 10), new WeightedRandomChestContent(Item.axeStone.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.axeWood.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.pickaxeStone.itemID, 0, 1, 1, 3), new WeightedRandomChestContent(Item.pickaxeWood.itemID, 0, 1, 1, 5), new WeightedRandomChestContent(Item.appleRed.itemID, 0, 2, 3, 5), new WeightedRandomChestContent(Item.bread.itemID, 0, 2, 3, 3)};
    private List pendingTickListEntriesThisTick = new ArrayList();

    /** An IntHashMap of entity IDs (integers) to their Entity objects. */
    private IntHashMap entityIdMap;

    public WorldServer(MinecraftServer server, ISaveHandler saveHandler, String worldName, int dimension, WorldSettings worldSettings, Profiler profiler, ILogAgent logger)
    {
        super(saveHandler, worldName, worldSettings, WorldProvider.getProviderForDimension(dimension), profiler, logger);
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

        if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting < 3)
        {
            this.difficultySetting = 3;
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
        this.theProfiler.endStartSection("tickTiles");
        this.tickBlocksAndAmbiance();
        this.theProfiler.endStartSection("chunkMap");
        this.thePlayerManager.updatePlayerInstances();
        this.theProfiler.endStartSection("village");
        this.villageCollectionObj.tick();
        this.villageSiegeObj.tick();
        this.theProfiler.endStartSection("portalForcer");
        this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
        this.theProfiler.endSection();
        this.sendAndApplyBlockEvents();
    }

    /**
     * only spawns creatures allowed by the chunkProvider
     */
    public SpawnListEntry spawnRandomCreature(EnumCreatureType creatureType, int x, int y, int z)
    {
        List spawnTypes = this.getChunkProvider().getPossibleCreatures(creatureType, x, y, z);
        return spawnTypes != null && !spawnTypes.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, spawnTypes) : null;
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

    public void wakeAllPlayers()
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

        while (this.getFirstUncoveredBlock(spawnX, spawnZ) == 0)
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
    protected void tickBlocksAndAmbiance()
    {
        super.tickBlocksAndAmbiance();

        for (Object activeChunkSet : this.activeChunkSet) {
            ChunkCoordIntPair loc = (ChunkCoordIntPair) activeChunkSet;
            int chunkX = loc.chunkXPos * 16;
            int chunkZ = loc.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunk = this.getChunkFromChunkCoords(loc.chunkXPos, loc.chunkZPos);
            this.moodSoundAndLightCheck(chunkX, chunkZ, chunk);
            this.theProfiler.endStartSection("tickChunk");
            chunk.updateSkylight();
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

                if (this.isBlockFreezableNaturally(blockX + chunkX, precipitationHeight - 1, blockZ + chunkZ)) {
                    this.setBlock(blockX + chunkX, precipitationHeight - 1, blockZ + chunkZ, Block.ice.blockID);
                }

                if (this.isRaining() && this.canSnowAt(blockX + chunkX, precipitationHeight, blockZ + chunkZ)) {
                    this.setBlock(blockX + chunkX, precipitationHeight, blockZ + chunkZ, Block.snow.blockID);
                }

                if (this.isRaining()) {
                    BiomeGenBase biomeGen = this.getBiomeGenForCoords(blockX + chunkX, blockZ + chunkZ);

                    if (biomeGen.canSpawnLightningBolt()) {
                        blockID = this.getBlockId(blockX + chunkX, precipitationHeight - 1, blockZ + chunkZ);

                        if (blockID != 0) {
                            Block.blocksList[blockID].fillWithRain(this, blockX + chunkX, precipitationHeight - 1, blockZ + chunkZ);
                        }
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
                        int blockID_2 = blockStorage.getExtBlockID(xPos, yPos, zPos);
                        Block block = Block.blocksList[blockID_2];

                        if (block != null && block.getTickRandomly()) {
                            block.updateTick(this, xPos + chunkX, yPos + blockStorage.getYLocation(), zPos + chunkZ, this.rand);
                        }
                    }
                }
            }

            this.theProfiler.endSection();
        }
    }

    /**
     * Returns true if the given block will receive a scheduled tick in this tick. Args: X, Y, Z, blockID
     */
    public boolean isBlockTickScheduledThisTick(int x, int y, int z, int blockID)
    {
        return this.pendingTickListEntriesThisTick.contains(new NextTickListEntry(x, y, z, blockID));
    }

    /**
     * Schedules a tick to a block with a delay (Most commonly the tick rate)
     */
    public void scheduleBlockUpdate(int x, int y, int z, int blockID, int delay)
    {
        this.scheduleBlockUpdateWithPriority(x, y, z, blockID, delay, 0);
    }

    public void scheduleBlockUpdateWithPriority(int x, int y, int z, int blockID, int delay, int priority)
    {
        NextTickListEntry tickListEntry = new NextTickListEntry(x, y, z, blockID);
        byte var8 = 0;

        if (this.scheduledUpdatesAreImmediate && blockID > 0)
        {
            if (Block.blocksList[blockID].func_82506_l())
            {
                var8 = 8;

                if (this.checkChunksExist(tickListEntry.xCoord - var8, tickListEntry.yCoord - var8, tickListEntry.zCoord - var8, tickListEntry.xCoord + var8, tickListEntry.yCoord + var8, tickListEntry.zCoord + var8))
                {
                    int var9 = this.getBlockId(tickListEntry.xCoord, tickListEntry.yCoord, tickListEntry.zCoord);

                    if (var9 == tickListEntry.blockID && var9 > 0)
                    {
                        Block.blocksList[var9].updateTick(this, tickListEntry.xCoord, tickListEntry.yCoord, tickListEntry.zCoord, this.rand);
                    }
                }

                return;
            }

            delay = 1;
        }

        if (this.checkChunksExist(x - var8, y - var8, z - var8, x + var8, y + var8, z + var8))
        {
            if (blockID > 0)
            {
                tickListEntry.setScheduledTime((long) delay + this.worldInfo.getWorldTotalTime());
                tickListEntry.setPriority(priority);
            }

            if (!this.pendingTickListEntriesHashSet.contains(tickListEntry))
            {
                this.pendingTickListEntriesHashSet.add(tickListEntry);
                this.pendingTickListEntriesTreeSet.add(tickListEntry);
            }
        }
    }

    /**
     * Schedules a block update from the saved information in a chunk. Called when the chunk is loaded.
     */
    public void scheduleBlockUpdateFromLoad(int x, int y, int z, int blockID, int delay, int priority)
    {
        NextTickListEntry var7 = new NextTickListEntry(x, y, z, blockID);
        var7.setPriority(priority);

        if (blockID > 0)
        {
            var7.setScheduledTime((long)delay + this.worldInfo.getWorldTotalTime());
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
                    int var6 = this.getBlockId(var4.xCoord, var4.yCoord, var4.zCoord);

                    if (var6 > 0 && Block.isAssociatedBlockID(var6, var4.blockID))
                    {
                        try
                        {
                            Block.blocksList[var6].updateTick(this, var4.xCoord, var4.yCoord, var4.zCoord, this.rand);
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

                            CrashReportCategory.addBlockCrashInfo(var9, var4.xCoord, var4.yCoord, var4.zCoord, var6, var10);
                            throw new ReportedException(var8);
                        }
                    }
                }
                else
                {
                    this.scheduleBlockUpdate(var4.xCoord, var4.yCoord, var4.zCoord, var4.blockID, 0);
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
                    System.out.println(this.pendingTickListEntriesThisTick.size());
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
    public List getAllTileEntityInBox(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
    {
        ArrayList entities = new ArrayList();

        for (Object tileEntityList : this.loadedTileEntityList) {
            TileEntity currEntity = (TileEntity) tileEntityList;

            if (currEntity.xCoord >= minX && currEntity.yCoord >= minY && currEntity.zCoord >= minZ && currEntity.xCoord < maxX && currEntity.yCoord < maxY && currEntity.zCoord < maxZ) {
                entities.add(currEntity);
            }
        }

        return entities;
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
            ChunkPosition position = chunkManager.findBiomePosition(0, 0, 256, spawnBiomes, random);
            int var6 = 0;
            int var7 = this.provider.getAverageGroundLevel();
            int var8 = 0;

            if (position != null)
            {
                var6 = position.x;
                var8 = position.z;
            }
            else
            {
                this.getWorldLogAgent().logWarning("Unable to find spawn biome");
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
    public void saveLevel() throws MinecraftException
    {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
        this.mapStorage.saveAllData();
    }

    public void onEntityAdded(Entity entity)
    {
        super.onEntityAdded(entity);
        this.entityIdMap.addKey(entity.entityId, entity);
        Entity[] entityParts = entity.getParts();

        if (entityParts != null)
        {
            for (Entity aVar2 : entityParts) {
                this.entityIdMap.addKey(aVar2.entityId, aVar2);
            }
        }
    }

    public void onEntityRemoved(Entity entity)
    {
        super.onEntityRemoved(entity);
        this.entityIdMap.removeObject(entity.entityId);
        Entity[] entityParts = entity.getParts();

        if (entityParts != null)
        {
            for (Entity aVar2 : entityParts) {
                this.entityIdMap.removeObject(aVar2.entityId);
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
            this.mcServer.getConfigurationManager().sendToAllNear(entity.posX, entity.posY, entity.posZ, 512.0D, this.provider.dimensionId, new Packet71Weather(entity));
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
        this.getEntityTracker().sendPacketToAllAssociatedPlayers(entity, new Packet38EntityStatus(entity.entityId, par2));
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
                ((EntityPlayerMP) var13).playerNetServerHandler.sendPacketToPlayer(new Packet60Explosion(x, y, z, force, var11.affectedBlockPositions, (Vec3) var11.func_77277_b().get(var13)));
            }
        }

        return var11;
    }

    /**
     * Adds a block event with the given Args to the blockEventCache. During the next tick(), the block specified will
     * have its onBlockEvent handler called with the given parameters. Args: X,Y,Z, BlockID, EventID, EventParameter
     */
    public void addBlockEvent(int x, int y, int z, int blockID, int eventID, int eventData)
    {
        BlockEventData var7 = new BlockEventData(x, y, z, blockID, eventID, eventData);
        Iterator var8 = this.blockEventCache[this.blockEventCacheIndex].iterator();
        BlockEventData var9;

        do
        {
            if (!var8.hasNext())
            {
                this.blockEventCache[this.blockEventCacheIndex].add(var7);
                return;
            }

            var9 = (BlockEventData)var8.next();
        }
        while (!var9.equals(var7));
    }

    /**
     * Send and apply locally all pending BlockEvents to each player with 64m radius of the event.
     */
    private void sendAndApplyBlockEvents()
    {
        while (!this.blockEventCache[this.blockEventCacheIndex].isEmpty())
        {
            int var1 = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;

            for (Object o : this.blockEventCache[var1]) {
                BlockEventData var3 = (BlockEventData) o;

                if (this.onBlockEventReceived(var3)) {
                    this.mcServer.getConfigurationManager().sendToAllNear((double) var3.getX(), (double) var3.getY(), (double) var3.getZ(), 64.0D, this.provider.dimensionId, new Packet54PlayNoteBlock(var3.getX(), var3.getY(), var3.getZ(), var3.getBlockID(), var3.getEventID(), var3.getEventParameter()));
                }
            }

            this.blockEventCache[var1].clear();
        }
    }

    /**
     * Called to apply a pending BlockEvent to apply to the current world.
     */
    private boolean onBlockEventReceived(BlockEventData blockEventData)
    {
        int var2 = this.getBlockId(blockEventData.getX(), blockEventData.getY(), blockEventData.getZ());
        return var2 == blockEventData.getBlockID() ? Block.blocksList[var2].onBlockEventReceived(this, blockEventData.getX(), blockEventData.getY(), blockEventData.getZ(), blockEventData.getEventID(), blockEventData.getEventParameter()) : false;
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

        if (var1 != this.isRaining())
        {
            if (var1)
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(2, 0));
            }
            else
            {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new Packet70GameEvent(1, 0));
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
}
