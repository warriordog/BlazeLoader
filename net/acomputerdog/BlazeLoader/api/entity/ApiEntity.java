package net.acomputerdog.BlazeLoader.api.entity;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.entity.EntityList;

/**
 * Api for entity-related functions
 */
public class ApiEntity {

    /**
     * Registers a custom entity type.
     * @param entityClass The entity class to register.
     * @param entityName The name of the entity to register.
     * @param entityId The entityId that is used to represent the entity over the network and in saves.
     */
    public static void registerEntityType(Class entityClass, String entityName, int entityId){
        EntityList.addMapping(entityClass, entityName, entityId);
    }

    /**
     * Registers a spawn egg for a given entity type.
     * @param entityId The entityID for this egg.
     * @param eggInfo The EntityEggInfo to register.
     */
    public static void registerEntityEggInfo(int entityId, EntityList.EntityEggInfo eggInfo){
        EntityList.entityEggs.put(entityId, eggInfo);
    }

    /**
     * Gets a free entity ID.
     * @return return a free entity ID.
     */
    public static int getFreeEntityId(){
        while(EntityList.getClassFromID(BlazeLoader.currFreeEntityId) == null){
            BlazeLoader.currFreeEntityId++;
        }
        int currId = BlazeLoader.currFreeEntityId;
        BlazeLoader.currFreeEntityId++;
        return currId;
    }
}
