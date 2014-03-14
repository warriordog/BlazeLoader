package net.acomputerdog.BlazeLoader.api.item;


import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEmptyMap;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemShears;

/**
 * Api functions for items.
 */
public class ApiItem {

    /**
     * Registers an item in the game registry.
     *
     * @param id   The item ID
     * @param name The item name
     * @param item The item itself
     */
    public static void registerItem(int id, String name, Item item) {
        Item.itemRegistry.addObject(id, name, item);
    }
    
    public static void InitItems() {
        Items.iron_shovel = (Item)Item.itemRegistry.getObject("iron_shovel");
        Items.iron_pickaxe = (Item)Item.itemRegistry.getObject("iron_pickaxe");
        Items.iron_axe = (Item)Item.itemRegistry.getObject("iron_axe");
        Items.flint_and_steel = (Item)Item.itemRegistry.getObject("flint_and_steel");
        Items.apple = (Item)Item.itemRegistry.getObject("apple");
        Items.bow = (ItemBow)Item.itemRegistry.getObject("bow");
        Items.arrow = (Item)Item.itemRegistry.getObject("arrow");
        Items.coal = (Item)Item.itemRegistry.getObject("coal");
        Items.diamond = (Item)Item.itemRegistry.getObject("diamond");
        Items.iron_ingot = (Item)Item.itemRegistry.getObject("iron_ingot");
        Items.gold_ingot = (Item)Item.itemRegistry.getObject("gold_ingot");
        Items.iron_sword = (Item)Item.itemRegistry.getObject("iron_sword");
        Items.wooden_sword = (Item)Item.itemRegistry.getObject("wooden_sword");
        Items.wooden_shovel = (Item)Item.itemRegistry.getObject("wooden_shovel");
        Items.wooden_pickaxe = (Item)Item.itemRegistry.getObject("wooden_pickaxe");
        Items.wooden_axe = (Item)Item.itemRegistry.getObject("wooden_axe");
        Items.stone_sword = (Item)Item.itemRegistry.getObject("stone_sword");
        Items.stone_shovel = (Item)Item.itemRegistry.getObject("stone_shovel");
        Items.stone_pickaxe = (Item)Item.itemRegistry.getObject("stone_pickaxe");
        Items.stone_axe = (Item)Item.itemRegistry.getObject("stone_axe");
        Items.diamond_sword = (Item)Item.itemRegistry.getObject("diamond_sword");
        Items.diamond_shovel = (Item)Item.itemRegistry.getObject("diamond_shovel");
        Items.diamond_pickaxe = (Item)Item.itemRegistry.getObject("diamond_pickaxe");
        Items.diamond_axe = (Item)Item.itemRegistry.getObject("diamond_axe");
        Items.stick = (Item)Item.itemRegistry.getObject("stick");
        Items.bowl = (Item)Item.itemRegistry.getObject("bowl");
        Items.mushroom_stew = (Item)Item.itemRegistry.getObject("mushroom_stew");
        Items.golden_sword = (Item)Item.itemRegistry.getObject("golden_sword");
        Items.golden_shovel = (Item)Item.itemRegistry.getObject("golden_shovel");
        Items.golden_pickaxe = (Item)Item.itemRegistry.getObject("golden_pickaxe");
        Items.golden_axe = (Item)Item.itemRegistry.getObject("golden_axe");
        Items.string = (Item)Item.itemRegistry.getObject("string");
        Items.feather = (Item)Item.itemRegistry.getObject("feather");
        Items.gunpowder = (Item)Item.itemRegistry.getObject("gunpowder");
        Items.wooden_hoe = (Item)Item.itemRegistry.getObject("wooden_hoe");
        Items.stone_hoe = (Item)Item.itemRegistry.getObject("stone_hoe");
        Items.iron_hoe = (Item)Item.itemRegistry.getObject("iron_hoe");
        Items.diamond_hoe = (Item)Item.itemRegistry.getObject("diamond_hoe");
        Items.golden_hoe = (Item)Item.itemRegistry.getObject("golden_hoe");
        Items.wheat_seeds = (Item)Item.itemRegistry.getObject("wheat_seeds");
        Items.wheat = (Item)Item.itemRegistry.getObject("wheat");
        Items.bread = (Item)Item.itemRegistry.getObject("bread");
        Items.leather_helmet = (ItemArmor)Item.itemRegistry.getObject("leather_helmet");
        Items.leather_chestplate = (ItemArmor)Item.itemRegistry.getObject("leather_chestplate");
        Items.leather_leggings = (ItemArmor)Item.itemRegistry.getObject("leather_leggings");
        Items.leather_boots = (ItemArmor)Item.itemRegistry.getObject("leather_boots");
        Items.chainmail_helmet = (ItemArmor)Item.itemRegistry.getObject("chainmail_helmet");
        Items.chainmail_chestplate = (ItemArmor)Item.itemRegistry.getObject("chainmail_chestplate");
        Items.chainmail_leggings = (ItemArmor)Item.itemRegistry.getObject("chainmail_leggings");
        Items.chainmail_boots = (ItemArmor)Item.itemRegistry.getObject("chainmail_boots");
        Items.iron_helmet = (ItemArmor)Item.itemRegistry.getObject("iron_helmet");
        Items.iron_chestplate = (ItemArmor)Item.itemRegistry.getObject("iron_chestplate");
        Items.iron_leggings = (ItemArmor)Item.itemRegistry.getObject("iron_leggings");
        Items.iron_boots = (ItemArmor)Item.itemRegistry.getObject("iron_boots");
        Items.diamond_helmet = (ItemArmor)Item.itemRegistry.getObject("diamond_helmet");
        Items.diamond_chestplate = (ItemArmor)Item.itemRegistry.getObject("diamond_chestplate");
        Items.diamond_leggings = (ItemArmor)Item.itemRegistry.getObject("diamond_leggings");
        Items.diamond_boots = (ItemArmor)Item.itemRegistry.getObject("diamond_boots");
        Items.golden_helmet = (ItemArmor)Item.itemRegistry.getObject("golden_helmet");
        Items.golden_chestplate = (ItemArmor)Item.itemRegistry.getObject("golden_chestplate");
        Items.golden_leggings = (ItemArmor)Item.itemRegistry.getObject("golden_leggings");
        Items.golden_boots = (ItemArmor)Item.itemRegistry.getObject("golden_boots");
        Items.flint = (Item)Item.itemRegistry.getObject("flint");
        Items.porkchop = (Item)Item.itemRegistry.getObject("porkchop");
        Items.cooked_porkchop = (Item)Item.itemRegistry.getObject("cooked_porkchop");
        Items.painting = (Item)Item.itemRegistry.getObject("painting");
        Items.golden_apple = (Item)Item.itemRegistry.getObject("golden_apple");
        Items.sign = (Item)Item.itemRegistry.getObject("sign");
        Items.wooden_door = (Item)Item.itemRegistry.getObject("wooden_door");
        Items.bucket = (Item)Item.itemRegistry.getObject("bucket");
        Items.water_bucket = (Item)Item.itemRegistry.getObject("water_bucket");
        Items.lava_bucket = (Item)Item.itemRegistry.getObject("lava_bucket");
        Items.minecart = (Item)Item.itemRegistry.getObject("minecart");
        Items.saddle = (Item)Item.itemRegistry.getObject("saddle");
        Items.iron_door = (Item)Item.itemRegistry.getObject("iron_door");
        Items.redstone = (Item)Item.itemRegistry.getObject("redstone");
        Items.snowball = (Item)Item.itemRegistry.getObject("snowball");
        Items.boat = (Item)Item.itemRegistry.getObject("boat");
        Items.leather = (Item)Item.itemRegistry.getObject("leather");
        Items.milk_bucket = (Item)Item.itemRegistry.getObject("milk_bucket");
        Items.brick = (Item)Item.itemRegistry.getObject("brick");
        Items.clay_ball = (Item)Item.itemRegistry.getObject("clay_ball");
        Items.reeds = (Item)Item.itemRegistry.getObject("reeds");
        Items.paper = (Item)Item.itemRegistry.getObject("paper");
        Items.book = (Item)Item.itemRegistry.getObject("book");
        Items.slime_ball = (Item)Item.itemRegistry.getObject("slime_ball");
        Items.chest_minecart = (Item)Item.itemRegistry.getObject("chest_minecart");
        Items.furnace_minecart = (Item)Item.itemRegistry.getObject("furnace_minecart");
        Items.egg = (Item)Item.itemRegistry.getObject("egg");
        Items.compass = (Item)Item.itemRegistry.getObject("compass");
        Items.fishing_rod = (ItemFishingRod)Item.itemRegistry.getObject("fishing_rod");
        Items.clock = (Item)Item.itemRegistry.getObject("clock");
        Items.glowstone_dust = (Item)Item.itemRegistry.getObject("glowstone_dust");
        Items.fish = (Item)Item.itemRegistry.getObject("fish");
        Items.cooked_fished = (Item)Item.itemRegistry.getObject("cooked_fished");
        Items.dye = (Item)Item.itemRegistry.getObject("dye");
        Items.bone = (Item)Item.itemRegistry.getObject("bone");
        Items.sugar = (Item)Item.itemRegistry.getObject("sugar");
        Items.cake = (Item)Item.itemRegistry.getObject("cake");
        Items.bed = (Item)Item.itemRegistry.getObject("bed");
        Items.repeater = (Item)Item.itemRegistry.getObject("repeater");
        Items.cookie = (Item)Item.itemRegistry.getObject("cookie");
        Items.filled_map = (ItemMap)Item.itemRegistry.getObject("filled_map");
        Items.shears = (ItemShears)Item.itemRegistry.getObject("shears");
        Items.melon = (Item)Item.itemRegistry.getObject("melon");
        Items.pumpkin_seeds = (Item)Item.itemRegistry.getObject("pumpkin_seeds");
        Items.melon_seeds = (Item)Item.itemRegistry.getObject("melon_seeds");
        Items.beef = (Item)Item.itemRegistry.getObject("beef");
        Items.cooked_beef = (Item)Item.itemRegistry.getObject("cooked_beef");
        Items.chicken = (Item)Item.itemRegistry.getObject("chicken");
        Items.cooked_chicken = (Item)Item.itemRegistry.getObject("cooked_chicken");
        Items.rotten_flesh = (Item)Item.itemRegistry.getObject("rotten_flesh");
        Items.ender_pearl = (Item)Item.itemRegistry.getObject("ender_pearl");
        Items.blaze_rod = (Item)Item.itemRegistry.getObject("blaze_rod");
        Items.ghast_tear = (Item)Item.itemRegistry.getObject("ghast_tear");
        Items.gold_nugget = (Item)Item.itemRegistry.getObject("gold_nugget");
        Items.nether_wart = (Item)Item.itemRegistry.getObject("nether_wart");
        Items.potionitem = (ItemPotion)Item.itemRegistry.getObject("potion");
        Items.glass_bottle = (Item)Item.itemRegistry.getObject("glass_bottle");
        Items.spider_eye = (Item)Item.itemRegistry.getObject("spider_eye");
        Items.fermented_spider_eye = (Item)Item.itemRegistry.getObject("fermented_spider_eye");
        Items.blaze_powder = (Item)Item.itemRegistry.getObject("blaze_powder");
        Items.magma_cream = (Item)Item.itemRegistry.getObject("magma_cream");
        Items.brewing_stand = (Item)Item.itemRegistry.getObject("brewing_stand");
        Items.cauldron = (Item)Item.itemRegistry.getObject("cauldron");
        Items.ender_eye = (Item)Item.itemRegistry.getObject("ender_eye");
        Items.speckled_melon = (Item)Item.itemRegistry.getObject("speckled_melon");
        Items.spawn_egg = (Item)Item.itemRegistry.getObject("spawn_egg");
        Items.experience_bottle = (Item)Item.itemRegistry.getObject("experience_bottle");
        Items.fire_charge = (Item)Item.itemRegistry.getObject("fire_charge");
        Items.writable_book = (Item)Item.itemRegistry.getObject("writable_book");
        Items.written_book = (Item)Item.itemRegistry.getObject("written_book");
        Items.emerald = (Item)Item.itemRegistry.getObject("emerald");
        Items.item_frame = (Item)Item.itemRegistry.getObject("item_frame");
        Items.flower_pot = (Item)Item.itemRegistry.getObject("flower_pot");
        Items.carrot = (Item)Item.itemRegistry.getObject("carrot");
        Items.potato = (Item)Item.itemRegistry.getObject("potato");
        Items.baked_potato = (Item)Item.itemRegistry.getObject("baked_potato");
        Items.poisonous_potato = (Item)Item.itemRegistry.getObject("poisonous_potato");
        Items.map = (ItemEmptyMap)Item.itemRegistry.getObject("map");
        Items.golden_carrot = (Item)Item.itemRegistry.getObject("golden_carrot");
        Items.skull = (Item)Item.itemRegistry.getObject("skull");
        Items.carrot_on_a_stick = (Item)Item.itemRegistry.getObject("carrot_on_a_stick");
        Items.nether_star = (Item)Item.itemRegistry.getObject("nether_star");
        Items.pumpkin_pie = (Item)Item.itemRegistry.getObject("pumpkin_pie");
        Items.fireworks = (Item)Item.itemRegistry.getObject("fireworks");
        Items.firework_charge = (Item)Item.itemRegistry.getObject("firework_charge");
        Items.enchanted_book = (ItemEnchantedBook)Item.itemRegistry.getObject("enchanted_book");
        Items.comparator = (Item)Item.itemRegistry.getObject("comparator");
        Items.netherbrick = (Item)Item.itemRegistry.getObject("netherbrick");
        Items.quartz = (Item)Item.itemRegistry.getObject("quartz");
        Items.tnt_minecart = (Item)Item.itemRegistry.getObject("tnt_minecart");
        Items.hopper_minecart = (Item)Item.itemRegistry.getObject("hopper_minecart");
        Items.iron_horse_armor = (Item)Item.itemRegistry.getObject("iron_horse_armor");
        Items.golden_horse_armor = (Item)Item.itemRegistry.getObject("golden_horse_armor");
        Items.diamond_horse_armor = (Item)Item.itemRegistry.getObject("diamond_horse_armor");
        Items.lead = (Item)Item.itemRegistry.getObject("lead");
        Items.name_tag = (Item)Item.itemRegistry.getObject("name_tag");
        Items.command_block_minecart = (Item)Item.itemRegistry.getObject("command_block_minecart");
        Items.record_13 = (Item)Item.itemRegistry.getObject("record_13");
        Items.record_cat = (Item)Item.itemRegistry.getObject("record_cat");
        Items.record_blocks = (Item)Item.itemRegistry.getObject("record_blocks");
        Items.record_chirp = (Item)Item.itemRegistry.getObject("record_chirp");
        Items.record_far = (Item)Item.itemRegistry.getObject("record_far");
        Items.record_mall = (Item)Item.itemRegistry.getObject("record_mall");
        Items.record_mellohi = (Item)Item.itemRegistry.getObject("record_mellohi");
        Items.record_stal = (Item)Item.itemRegistry.getObject("record_stal");
        Items.record_strad = (Item)Item.itemRegistry.getObject("record_strad");
        Items.record_ward = (Item)Item.itemRegistry.getObject("record_ward");
        Items.record_11 = (Item)Item.itemRegistry.getObject("record_11");
        Items.record_wait = (Item)Item.itemRegistry.getObject("record_wait");
    }
}
