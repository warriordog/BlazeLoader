package net.acomputerdog.BlazeLoader.api.entity;

import net.acomputerdog.BlazeLoader.main.BlazeLoader;
import net.minecraft.src.EntityEggInfo;
import net.minecraft.src.EntityList;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

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
        for(Method m : EntityList.class.getDeclaredMethods()){
            Class[] args = m.getParameterTypes();
            if(args.length == 3 && Class.class.isAssignableFrom(args[0]) && String.class.isAssignableFrom(args[1]) && int.class.isAssignableFrom(args[2])){
                try{
                    m.setAccessible(true);
                    m.invoke(null, entityClass, entityName, entityId);
                }catch(ReflectiveOperationException e){
                    throw new RuntimeException("Could not get entity registration method!", e);
                }
            }
        }
    }

    /**
     * Registers a spawn egg for a given entity type.
     * @param entityId The entityID for this egg.
     * @param eggInfo The EntityEggInfo to register.
     */
    public static void registerEntityEggInfo(int entityId, EntityEggInfo eggInfo){
        for(Field f : EntityList.class.getDeclaredFields()){
            if(LinkedHashMap.class.isAssignableFrom(f.getType())){
                try{
                    f.setAccessible(true);
                    ((LinkedHashMap)f.get(null)).put(entityId, eggInfo);
                }catch(ReflectiveOperationException e){
                    throw new RuntimeException("Could not get entity egg list field!", e);
                }
            }
        }
    }

    /**
     * Gets a free entity ID.
     * @return return a free entity ID.
     */
    public static int getFreeEntityId(){
        while(EntityList.getClassFromID(BlazeLoader.freeEntityId) == null){
            BlazeLoader.freeEntityId++;
        }
        int currId = BlazeLoader.freeEntityId;
        BlazeLoader.freeEntityId++;
        return currId;
    }
}
