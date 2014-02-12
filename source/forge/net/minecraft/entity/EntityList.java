package net.minecraft.entity;

import cpw.mods.fml.common.FMLLog;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class EntityList {
    private static final Logger logger = LogManager.getLogger();
    /**
     * Provides a mapping between entity classes and a string
     */
    public static Map<String, Class> stringToClassMapping = new HashMap<String, Class>();
    /**
     * Provides a mapping between a string and an entity classes
     */
    public static Map<Class, String> classToStringMapping = new HashMap<Class, String>();
    /**
     * provides a mapping between an entityID and an Entity Class
     */
    public static Map<Integer, Class> IDtoClassMapping = new HashMap<Integer, Class>();
    /**
     * provides a mapping between an Entity Class and an entity ID
     */
    private static Map<Class, Integer> classToIDMapping = new HashMap<Class, Integer>();
    /**
     * Maps entity names to their numeric identifiers
     */
    private static Map<String, Integer> stringToIDMapping = new HashMap<String, Integer>();
    /**
     * This is a HashMap of the Creative Entity Eggs/Spawners.
     */
    public static HashMap<Integer, EntityList.EntityEggInfo> entityEggs = new LinkedHashMap<Integer, EntityList.EntityEggInfo>();
    private static final String __OBFID = "CL_00001538";

    /**
     * adds a mapping between Entity classes and both a string representation and an ID
     */
    public static void addMapping(Class par0Class, String par1Str, int par2) {
        stringToClassMapping.put(par1Str, par0Class);
        classToStringMapping.put(par0Class, par1Str);
        IDtoClassMapping.put(par2, par0Class);
        classToIDMapping.put(par0Class, par2);
        stringToIDMapping.put(par1Str, par2);
    }

    /**
     * Adds a entity mapping with egg info.
     */
    public static void addMapping(Class par0Class, String par1Str, int par2, int par3, int par4) {
        addMapping(par0Class, par1Str, par2);
        entityEggs.put(par2, new EntityList.EntityEggInfo(par2, par3, par4));
    }

    /**
     * Create a new instance of an entity in the world by using the entity name.
     */
    public static Entity createEntityByName(String par0Str, World par1World) {
        Entity entity = null;

        try {
            Class oclass = stringToClassMapping.get(par0Str);

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(new Class[]{World.class}).newInstance(par1World);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return entity;
    }

    /**
     * create a new instance of an entity from NBT store
     */
    public static Entity createEntityFromNBT(NBTTagCompound par0NBTTagCompound, World par1World) {
        Entity entity = null;

        if ("Minecart".equals(par0NBTTagCompound.getString("id"))) {
            switch (par0NBTTagCompound.getInteger("Type")) {
                case 0:
                    par0NBTTagCompound.setString("id", "MinecartRideable");
                    break;
                case 1:
                    par0NBTTagCompound.setString("id", "MinecartChest");
                    break;
                case 2:
                    par0NBTTagCompound.setString("id", "MinecartFurnace");
            }

            par0NBTTagCompound.removeTag("Type");
        }

        Class oclass = null;
        try {
            oclass = stringToClassMapping.get(par0NBTTagCompound.getString("id"));

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(new Class[]{World.class}).newInstance(par1World);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity != null) {
            try {
                entity.readFromNBT(par0NBTTagCompound);
            } catch (Exception e) {
                FMLLog.log(Level.ERROR, e,
                        "An Entity %s(%s) has thrown an exception during loading, its state cannot be restored. Report this to the mod author",
                        par0NBTTagCompound.getString("id"), oclass.getName());
                entity = null;
            }
        } else {
            logger.warn("Skipping Entity with id " + par0NBTTagCompound.getString("id"));
        }

        return entity;
    }

    /**
     * Create a new instance of an entity in the world by using an entity ID.
     */
    public static Entity createEntityByID(int par0, World par1World) {
        Entity entity = null;

        try {
            Class oclass = getClassFromID(par0);

            if (oclass != null) {
                entity = (Entity) oclass.getConstructor(new Class[]{World.class}).newInstance(par1World);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (entity == null) {
            logger.warn("Skipping Entity with id " + par0);
        }

        return entity;
    }

    /**
     * gets the entityID of a specific entity
     */
    public static int getEntityID(Entity par0Entity) {
        Class oclass = par0Entity.getClass();
        return classToIDMapping.containsKey(oclass) ? classToIDMapping.get(oclass) : 0;
    }

    /**
     * Return the class assigned to this entity ID.
     */
    public static Class getClassFromID(int par0) {
        return IDtoClassMapping.get(par0);
    }

    /**
     * Gets the string representation of a specific entity.
     */
    public static String getEntityString(Entity par0Entity) {
        return classToStringMapping.get(par0Entity.getClass());
    }

    /**
     * Finds the class using IDtoClassMapping and classToStringMapping
     */
    public static String getStringFromID(int par0) {
        Class oclass = getClassFromID(par0);
        return oclass != null ? classToStringMapping.get(oclass) : null;
    }

    public static void func_151514_a() {
    }

    public static Set func_151515_b() {
        return Collections.unmodifiableSet(stringToIDMapping.keySet());
    }

    static {
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
        addMapping(EntityFallingBlock.class, "FallingSand", 21);
        addMapping(EntityFireworkRocket.class, "FireworksRocketEntity", 22);
        addMapping(EntityBoat.class, "Boat", 41);
        addMapping(EntityMinecartEmpty.class, "MinecartRideable", 42);
        addMapping(EntityMinecartChest.class, "MinecartChest", 43);
        addMapping(EntityMinecartFurnace.class, "MinecartFurnace", 44);
        addMapping(EntityMinecartTNT.class, "MinecartTNT", 45);
        addMapping(EntityMinecartHopper.class, "MinecartHopper", 46);
        addMapping(EntityMinecartMobSpawner.class, "MinecartSpawner", 47);
        addMapping(EntityMinecartCommandBlock.class, "MinecartCommandBlock", 40);
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

    public static class EntityEggInfo {
        /**
         * The entityID of the spawned mob
         */
        public final int spawnedID;
        /**
         * Base color of the egg
         */
        public final int primaryColor;
        /**
         * Color of the egg spots
         */
        public final int secondaryColor;
        public final StatBase field_151512_d;
        public final StatBase field_151513_e;
        private static final String __OBFID = "CL_00001539";

        public EntityEggInfo(int par1, int par2, int par3) {
            this.spawnedID = par1;
            this.primaryColor = par2;
            this.secondaryColor = par3;
            this.field_151512_d = StatList.func_151182_a(this);
            this.field_151513_e = StatList.func_151176_b(this);
        }
    }
}
