package net.BlazeLoader.api.entity;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

import java.util.List;

/**
 * Api for entity-related functions
 */
public class ApiEntity {

    /**
     * Registers a custom entity type.
     *
     * @param entityClass The entity class to register.
     * @param entityName  The name of the entity to register.
     * @param entityId    The entityId that is used to represent the entity over the network and in saves.
     */
    public static void registerEntityType(Class entityClass, String entityName, int entityId) {
        EntityList.addMapping(entityClass, entityName, entityId);
    }

    /**
     * Registers a spawn egg for a given entity type.
     *
     * @param entityId The entityID for this egg.
     * @param eggInfo  The EntityEggInfo to register.
     */
    public static void registerEntityEggInfo(int entityId, EntityList.EntityEggInfo eggInfo) {
        EntityList.entityEggs.put(entityId, eggInfo);
    }

    /**
     * Re-registers an entity to use a different class
     *
     * @param oldC Original class
     * @param newC Replacement class
     */
    public static void swapEntityClass(Class oldC, Class newC) {
        EntityList.EntityRegistryEntry.getEntry(oldC).setEntityClass(newC);
        swapEntitySpawn(oldC, newC);
    }

    /**
     * Changes spawn lists to replace an entities class
     *
     * @param oldC Original class
     * @param newC Replacement class
     */
    public static void swapEntitySpawn(Class oldC, Class newC) {
        for (EnumCreatureType i : EnumCreatureType.values()) {
            swapEntitySpawn(oldC, newC, i);
        }
    }

    public static void swapEntitySpawn(Class o, Class c, EnumCreatureType e) {
        BiomeGenBase[] standardBiomes = BiomeGenBase.getBiomeGenArray();

        for (BiomeGenBase biome : standardBiomes) {
            if (biome != null) {
                List<SpawnListEntry> spawnableList = biome.getSpawnableList(e);
                if (spawnableList != null) {
                    for (SpawnListEntry entry : spawnableList) {
                        if (entry != null && entry.entityClass == o) {
                            entry.entityClass = c;
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets a free entity ID.
     *
     * @return return a free entity ID.
     */
    public static int getFreeEntityId() {
        while (EntityList.getClassFromID(BlazeLoader.currFreeEntityId) == null) {
            BlazeLoader.currFreeEntityId++;
        }
        int currId = BlazeLoader.currFreeEntityId;
        BlazeLoader.currFreeEntityId++;
        return currId;
    }

    /**
     * Gets the entity ID of the passed entity.
     *
     * @param entity The entity to get the ID from
     * @return Return the ID of the entity
     */
    public static int getEntityID(Entity entity) {
        return EntityList.getEntityID(entity);
    }

    /**
     * Gets an entity's class from an entity ID
     *
     * @param id The ID of the entity.
     * @return Return the class of the passed entity.
     */
    public static Class getEntityClassFromID(int id) {
        return EntityList.getClassFromID(id);
    }

    /**
     * Gets the type of an entity
     *
     * @param entity The entity who's type to get
     * @return Return the type of the passed entity.
     */
    public static String getEntityType(Entity entity) {
        return EntityList.getEntityString(entity);
    }

    /**
     * Gets the type of an entity from it's ID
     *
     * @param id The ID of the entity.
     * @return Return the type of the entity.
     */
    public static String getEntityTypeFromID(int id) {
        return EntityList.getStringFromID(id);
    }

    /**
     * Gets an entity ID from a String.
     *
     * @param type The string identifying the entity.
     * @return Return then ID of the entity.
     */
    public static int getEntityIDFromType(String type) {
        return EntityList.getIDFromString(type);
    }

    /**
     * Creates an Entity from it's entity ID
     *
     * @param id    The ID of the entity
     * @param world The world to spawn in
     * @return Return the spawned entity.
     */
    public static Entity createEntityByID(int id, World world) {
        return EntityList.createEntityByID(id, world);
    }

    /**
     * Creates an Entity from an NBT structure
     *
     * @param nbt   The NBT to load from
     * @param world The world to spawn in
     * @return Return the spawned entity.
     */
    public static Entity createEntityFromNBT(NBTTagCompound nbt, World world) {
        return EntityList.createEntityFromNBT(nbt, world);
    }

    /**
     * Creates an Entity from the specified type
     *
     * @param type  The type of entity to spawn
     * @param world The world to spawn in
     * @return Return the spawned entity.
     */
    public static Entity createEntityByType(String type, World world) {
        return EntityList.createEntityByName(type, world);
    }
}
