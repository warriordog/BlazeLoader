package net.minecraft.world.gen;

import cpw.mods.fml.common.registry.GameRegistry;
import net.acomputerdog.BlazeLoader.api.world.ApiWorld;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.*;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ForgeChunkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class ChunkProviderServer implements IChunkProvider {
    private static final Logger logger = LogManager.getLogger();
    /**
     * used by unload100OldestChunks to iterate the loadedChunkHashMap for unload (underlying assumption, first in,
     * first out)
     */
    private Set chunksToUnload = new HashSet();
    private Chunk defaultEmptyChunk;
    private IChunkProvider currentChunkProvider;
    public IChunkLoader currentChunkLoader;
    /**
     * if this is false, the defaultEmptyChunk will be returned by the provider
     */
    public boolean loadChunkOnProvideRequest = true;
    private LongHashMap loadedChunkHashMap = new LongHashMap();
    private List loadedChunks = new ArrayList();
    private WorldServer worldObj;
    private static final String __OBFID = "CL_00001436";

    public ChunkProviderServer(WorldServer par1WorldServer, IChunkLoader par2IChunkLoader, IChunkProvider par3IChunkProvider) {
        this.defaultEmptyChunk = new EmptyChunk(par1WorldServer, 0, 0);
        this.worldObj = par1WorldServer;
        this.currentChunkLoader = par2IChunkLoader;
        this.currentChunkProvider = par3IChunkProvider;
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
    public boolean chunkExists(int par1, int par2) {
        return this.loadedChunkHashMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
    }

    /**
     * marks chunk for unload by "unload100OldestChunks"  if there is no spawn point, or if the center of the chunk is
     * outside 200 blocks (x or z) of the spawn
     */
    public void unloadChunksIfNotNearSpawn(int par1, int par2) {
        if (this.worldObj.provider.canRespawnHere() && DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
            ChunkCoordinates chunkcoordinates = this.worldObj.getSpawnPoint();
            int k = par1 * 16 + 8 - chunkcoordinates.posX;
            int l = par2 * 16 + 8 - chunkcoordinates.posZ;
            short short1 = 128;

            if (k < -short1 || k > short1 || l < -short1 || l > short1) {
                this.chunksToUnload.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par1, par2)));
            }
        } else {
            this.chunksToUnload.add(Long.valueOf(ChunkCoordIntPair.chunkXZ2Int(par1, par2)));
        }
    }

    /**
     * marks all chunks for unload, ignoring those near the spawn
     */
    public void unloadAllChunks() {
        Iterator iterator = this.loadedChunks.iterator();

        while (iterator.hasNext()) {
            Chunk chunk = (Chunk) iterator.next();
            this.unloadChunksIfNotNearSpawn(chunk.xPosition, chunk.zPosition);
        }
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(int par1, int par2) {
        long k = ChunkCoordIntPair.chunkXZ2Int(par1, par2);
        this.chunksToUnload.remove(Long.valueOf(k));
        Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(k);

        if (chunk == null) {
            chunk = ForgeChunkManager.fetchDormantChunk(k, this.worldObj);
            if (chunk == null) {
                chunk = this.safeLoadChunk(par1, par2);
            }

            if (chunk == null) {
                if (this.currentChunkProvider == null) {
                    chunk = this.defaultEmptyChunk;
                } else {
                    try {
                        chunk = this.currentChunkProvider.provideChunk(par1, par2);
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception generating new chunk");
                        CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
                        crashreportcategory.addCrashSection("Location", String.format("%d,%d", new Object[]{Integer.valueOf(par1), Integer.valueOf(par2)}));
                        crashreportcategory.addCrashSection("Position hash", Long.valueOf(k));
                        crashreportcategory.addCrashSection("Generator", this.currentChunkProvider.makeString());
                        throw new ReportedException(crashreport);
                    }
                }
            }

            this.loadedChunkHashMap.add(k, chunk);
            this.loadedChunks.add(chunk);
            chunk.onChunkLoad();
            chunk.populateChunk(this, this, par1, par2);
        }

        return chunk;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(int par1, int par2) {
        Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(par1, par2));
        return chunk == null ? (!this.worldObj.findingSpawnPoint && !this.loadChunkOnProvideRequest ? this.defaultEmptyChunk : this.loadChunk(par1, par2)) : chunk;
    }

    /**
     * used by loadChunk, but catches any exceptions if the load fails.
     */
    private Chunk safeLoadChunk(int par1, int par2) {
        if (this.currentChunkLoader == null) {
            return null;
        } else {
            try {
                Chunk chunk = this.currentChunkLoader.loadChunk(this.worldObj, par1, par2);

                if (chunk != null) {
                    chunk.lastSaveTime = this.worldObj.getTotalWorldTime();

                    if (this.currentChunkProvider != null) {
                        this.currentChunkProvider.recreateStructures(par1, par2);
                    }
                }

                return chunk;
            } catch (Exception exception) {
                logger.error("Couldn\'t load chunk", exception);
                return null;
            }
        }
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    private void safeSaveExtraChunkData(Chunk par1Chunk) {
        if (this.currentChunkLoader != null) {
            try {
                this.currentChunkLoader.saveExtraChunkData(this.worldObj, par1Chunk);
            } catch (Exception exception) {
                logger.error("Couldn\'t save entities", exception);
            }
        }
    }

    /**
     * used by saveChunks, but catches any exceptions if the save fails.
     */
    private void safeSaveChunk(Chunk par1Chunk) {
        if (this.currentChunkLoader != null) {
            try {
                par1Chunk.lastSaveTime = this.worldObj.getTotalWorldTime();
                this.currentChunkLoader.saveChunk(this.worldObj, par1Chunk);
            } catch (IOException ioexception) {
                logger.error("Couldn\'t save chunk", ioexception);
            } catch (MinecraftException minecraftexception) {
                logger.error("Couldn\'t save chunk; already in use by another instance of Minecraft?", minecraftexception);
            }
        }
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(IChunkProvider par1IChunkProvider, int par2, int par3) {
        Chunk chunk = this.provideChunk(par2, par3);

        if (!chunk.isTerrainPopulated) {
            chunk.func_150809_p();

            if (this.currentChunkProvider != null) {
                this.currentChunkProvider.populate(par1IChunkProvider, par2, par3);
                ApiWorld.generateChunk(worldObj, par2, par3);
                GameRegistry.generateWorld(par2, par3, worldObj, currentChunkProvider, par1IChunkProvider);
                chunk.setChunkModified();
            }
        }
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(boolean par1, IProgressUpdate par2IProgressUpdate) {
        int i = 0;

        for (int j = 0; j < this.loadedChunks.size(); ++j) {
            Chunk chunk = (Chunk) this.loadedChunks.get(j);

            if (par1) {
                this.safeSaveExtraChunkData(chunk);
            }

            if (chunk.needsSaving(par1)) {
                this.safeSaveChunk(chunk);
                chunk.isModified = false;
                ++i;

                if (i == 24 && !par1) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData() {
        if (this.currentChunkLoader != null) {
            this.currentChunkLoader.saveExtraData();
        }
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        if (!this.worldObj.levelSaving) {
            for (ChunkCoordIntPair forced : this.worldObj.getPersistentChunks().keySet()) {
                this.chunksToUnload.remove(ChunkCoordIntPair.chunkXZ2Int(forced.chunkXPos, forced.chunkZPos));
            }

            for (int i = 0; i < 100; ++i) {
                if (!this.chunksToUnload.isEmpty()) {
                    Long olong = (Long) this.chunksToUnload.iterator().next();
                    Chunk chunk = (Chunk) this.loadedChunkHashMap.getValueByKey(olong.longValue());
                    chunk.onChunkUnload();
                    this.safeSaveChunk(chunk);
                    this.safeSaveExtraChunkData(chunk);
                    this.chunksToUnload.remove(olong);
                    this.loadedChunkHashMap.remove(olong.longValue());
                    this.loadedChunks.remove(chunk);
                    ForgeChunkManager.putDormantChunk(ChunkCoordIntPair.chunkXZ2Int(chunk.xPosition, chunk.zPosition), chunk);
                    if (loadedChunks.size() == 0 && ForgeChunkManager.getPersistentChunksFor(this.worldObj).size() == 0 && !DimensionManager.shouldLoadSpawn(this.worldObj.provider.dimensionId)) {
                        DimensionManager.unloadWorld(this.worldObj.provider.dimensionId);
                        return currentChunkProvider.unloadQueuedChunks();
                    }
                }
            }

            if (this.currentChunkLoader != null) {
                this.currentChunkLoader.chunkTick();
            }
        }

        return this.currentChunkProvider.unloadQueuedChunks();
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return !this.worldObj.levelSaving;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "ServerChunkCache: " + this.loadedChunkHashMap.getNumHashElements() + " Drop: " + this.chunksToUnload.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int par2, int par3, int par4) {
        return this.currentChunkProvider.getPossibleCreatures(par1EnumCreatureType, par2, par3, par4);
    }

    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_) {
        return this.currentChunkProvider.func_147416_a(p_147416_1_, p_147416_2_, p_147416_3_, p_147416_4_, p_147416_5_);
    }

    public int getLoadedChunkCount() {
        return this.loadedChunkHashMap.getNumHashElements();
    }

    public void recreateStructures(int par1, int par2) {
    }
}
