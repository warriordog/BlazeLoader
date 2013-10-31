package net.minecraft.src;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Holds mappings of entities to ids, classes, names, and spawn eggs.
 */
public class EntityList
{
    /** Provides a mapping between entity classes and a string */
    private static Map<String, Class> stringToClassMapping = new HashMap<String, Class>();

    /** Provides a mapping between a string and an entity classes */
    private static Map<Class, String> classToStringMapping = new HashMap<Class, String>();

    /** provides a mapping between an entityID and an Entity Class */
    private static Map<Integer, Class> IDtoClassMapping = new HashMap<Integer, Class>();

    /** provides a mapping between an Entity Class and an entity ID */
    private static Map<Class, Integer> classToIDMapping = new HashMap<Class, Integer>();

    /** Maps entity names to their numeric identifiers */
    public static Map<String, Integer> stringToIDMapping = new HashMap<String, Integer>();

    /** This is a HashMap of the Creative Entity Eggs/Spawners. */
    public static HashMap<Integer, EntityEggInfo> entityEggs = new LinkedHashMap<Integer, EntityEggInfo>();

    /**
     * adds a mapping between Entity classes and both a string representation and an ID
     */
    public static void addMapping(Class entityClass, String entityName, int entityID)
    {
        stringToClassMapping.put(entityName, entityClass);
        classToStringMapping.put(entityClass, entityName);
        IDtoClassMapping.put(entityID, entityClass);
        classToIDMapping.put(entityClass, entityID);
        stringToIDMapping.put(entityName, entityID);
    }

    /**
     * Adds a entity mapping with egg info.
     */
    public static void addMapping(Class entityClass, String entityName, int entityID, int primaryColor, int secondaryColor)
    {
        addMapping(entityClass, entityName, entityID);
        entityEggs.put(entityID, new EntityEggInfo(entityID, primaryColor, secondaryColor));
    }

    /**
     * Create a new instance of an entity in the world by using the entity name.
     */
    public static Entity createEntityByName(String name, World world)
    {
        Entity var2 = null;

        try
        {
            Class var3 = stringToClassMapping.get(name);

            if (var3 != null)
            {
                var2 = (Entity)var3.getConstructor(new Class[] {World.class}).newInstance(world);
            }
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }

        return var2;
    }

    /**
     * create a new instance of an entity from NBT store
     */
    public static Entity createEntityFromNBT(NBTTagCompound NBTData, World world)
    {
        Entity var2 = null;

        if ("Minecart".equals(NBTData.getString("id")))
        {
            switch (NBTData.getInteger("Type"))
            {
                case 0:
                    NBTData.setString("id", "MinecartRideable");
                    break;

                case 1:
                    NBTData.setString("id", "MinecartChest");
                    break;

                case 2:
                    NBTData.setString("id", "MinecartFurnace");
            }

            NBTData.removeTag("Type");
        }

        try
        {
            Class var3 = stringToClassMapping.get(NBTData.getString("id"));

            if (var3 != null)
            {
                var2 = (Entity)var3.getConstructor(new Class[] {World.class}).newInstance(world);
            }
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }

        if (var2 != null)
        {
            var2.readFromNBT(NBTData);
        }
        else
        {
            world.getWorldLogAgent().logWarning("Skipping Entity with id " + NBTData.getString("id"));
        }

        return var2;
    }

    /**
     * Create a new instance of an entity in the world by using an entity ID.
     */
    public static Entity createEntityByID(int entityID, World world)
    {
        Entity var2 = null;

        try
        {
            Class var3 = getClassFromID(entityID);

            if (var3 != null)
            {
                var2 = (Entity)var3.getConstructor(new Class[] {World.class}).newInstance(world);
            }
        }
        catch (Exception var4)
        {
            var4.printStackTrace();
        }

        if (var2 == null)
        {
            world.getWorldLogAgent().logWarning("Skipping Entity with id " + entityID);
        }

        return var2;
    }

    /**
     * gets the entityID of a specific entity
     */
    public static int getEntityID(Entity entity)
    {
        Class var1 = entity.getClass();
        return classToIDMapping.containsKey(var1) ? classToIDMapping.get(var1) : 0;
    }

    /**
     * Return the class assigned to this entity ID.
     */
    public static Class getClassFromID(int par0)
    {
        return IDtoClassMapping.get(par0);
    }

    /**
     * Gets the string representation of a specific entity.
     */
    public static String getEntityString(Entity par0Entity)
    {
        return classToStringMapping.get(par0Entity.getClass());
    }

    /**
     * Finds the class using IDtoClassMapping and classToStringMapping
     */
    public static String getStringFromID(int par0)
    {
        Class var1 = getClassFromID(par0);
        return var1 != null ? classToStringMapping.get(var1) : null;
    }

    /**
     * Gets an entity ID from a String.
     * @param string The string identifying the entity.
     * @return Return then ID of the entity.
     */
    public static int getIDFromString(String string){
        return stringToIDMapping.get(string);
    }

    static
    {
        addMapping(EntityItem.class, "Item", 1);
        addMapping(EntityXPOrb.class, "XPOrb", 2);
        addMapping(EntityLeashKnot.class, "LeashKnot", 8);
        addMapping(EntityPainting.class, "Painting", 9);
        addMapping(EntityArrow.class, "Arrow", 10);
        addMapping(EntitySnowball.class, "Snowball", 11);
        addMapping(EntityLargeFireball.class, "Fireball", 12);
        addMapping(EntitySmallFireball.class, "SmallFireball", 13);
        addMapping(EntityEnderPearl.class, "ThrownEnderpearl", 14);
        addMapping(EntityEnderEye.class, "EyeOfEnderSignal", 15);
        addMapping(EntityPotion.class, "ThrownPotion", 16);
        addMapping(EntityExpBottle.class, "ThrownExpBottle", 17);
        addMapping(EntityItemFrame.class, "ItemFrame", 18);
        addMapping(EntityWitherSkull.class, "WitherSkull", 19);
        addMapping(EntityTNTPrimed.class, "PrimedTnt", 20);
        addMapping(EntityFallingSand.class, "FallingSand", 21);
        addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
        addMapping(EntityBoat.class, "Boat", 41);
        addMapping(EntityMinecartEmpty.class, "MinecartRideable", 42);
        addMapping(EntityMinecartChest.class, "MinecartChest", 43);
        addMapping(EntityMinecartFurnace.class, "MinecartFurnace", 44);
        addMapping(EntityMinecartTNT.class, "MinecartTNT", 45);
        addMapping(EntityMinecartHopper.class, "MinecartHopper", 46);
        addMapping(EntityMinecartMobSpawner.class, "MinecartSpawner", 47);
        addMapping(EntityLiving.class, "Mob", 48);
        addMapping(EntityMob.class, "Monster", 49);
        addMapping(EntityCreeper.class, "Creeper", 50, 894731, 0);
        addMapping(EntitySkeleton.class, "Skeleton", 51, 12698049, 4802889);
        addMapping(EntitySpider.class, "Spider", 52, 3419431, 11013646);
        addMapping(EntityGiantZombie.class, "Giant", 53);
        addMapping(EntityZombie.class, "Zombie", 54, 44975, 7969893);
        addMapping(EntitySlime.class, "Slime", 55, 5349438, 8306542);
        addMapping(EntityGhast.class, "Ghast", 56, 16382457, 12369084);
        addMapping(EntityPigZombie.class, "PigZombie", 57, 15373203, 5009705);
        addMapping(EntityEnderman.class, "Enderman", 58, 1447446, 0);
        addMapping(EntityCaveSpider.class, "CaveSpider", 59, 803406, 11013646);
        addMapping(EntitySilverfish.class, "Silverfish", 60, 7237230, 3158064);
        addMapping(EntityBlaze.class, "Blaze", 61, 16167425, 16775294);
        addMapping(EntityMagmaCube.class, "LavaSlime", 62, 3407872, 16579584);
        addMapping(EntityDragon.class, "EnderDragon", 63);
        addMapping(EntityWither.class, "WitherBoss", 64);
        addMapping(EntityBat.class, "Bat", 65, 4996656, 986895);
        addMapping(EntityWitch.class, "Witch", 66, 3407872, 5349438);
        addMapping(EntityPig.class, "Pig", 90, 15771042, 14377823);
        addMapping(EntitySheep.class, "Sheep", 91, 15198183, 16758197);
        addMapping(EntityCow.class, "Cow", 92, 4470310, 10592673);
        addMapping(EntityChicken.class, "Chicken", 93, 10592673, 16711680);
        addMapping(EntitySquid.class, "Squid", 94, 2243405, 7375001);
        addMapping(EntityWolf.class, "Wolf", 95, 14144467, 13545366);
        addMapping(EntityMooshroom.class, "MushroomCow", 96, 10489616, 12040119);
        addMapping(EntitySnowman.class, "SnowMan", 97);
        addMapping(EntityOcelot.class, "Ozelot", 98, 15720061, 5653556);
        addMapping(EntityIronGolem.class, "VillagerGolem", 99);
        addMapping(EntityHorse.class, "EntityHorse", 100, 12623485, 15656192);
        addMapping(EntityVillager.class, "Villager", 120, 5651507, 12422002);
        addMapping(EntityEnderCrystal.class, "EnderCrystal", 200);
    }
}
