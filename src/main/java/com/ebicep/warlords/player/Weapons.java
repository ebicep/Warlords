package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Weapons {

    BLUDGEON("Bludgeon", new ItemStack(Material.RABBIT_STEW)),
    TRAINING_SWORD("Training Sword", new ItemStack(Material.STONE_AXE)),
    CLAWS("Claws", new ItemStack(Material.MUTTON)),
    SCIMITAR("Scimitar", new ItemStack(Material.RAW_FISH, 1, (short) 1)),
    ORC_AXE("Orc Axe", new ItemStack(Material.PUMPKIN_PIE)),
    HATCHET("Hatchet", new ItemStack(Material.GOLD_HOE)),
    PIKE("Pike", new ItemStack(Material.ROTTEN_FLESH)),
    HAMMER("Hammer", new ItemStack(Material.IRON_SPADE)),
    WALKING_STICK("Walking Stick", new ItemStack(Material.STONE_PICKAXE)),
    STEEL_SWORD("Steel Sword", new ItemStack(Material.WOOD_AXE)),

    WORLD_TREE_BRANCH("World Tree Branch", new ItemStack(Material.IRON_PICKAXE)),
    GEM_AXE("Gem Axe", new ItemStack(Material.DIAMOND_HOE)),
    DOUBLEAXE("Doubleaxe", new ItemStack(Material.COOKED_FISH)),
    MANDIBLES("Mandibles", new ItemStack(Material.PORK)),
    GOLDEN_GLADIUS("Golden Gladius", new ItemStack(Material.RAW_FISH, 1, (short) 3)),
    STONE_MALLET("Stone Mallet", new ItemStack(Material.GOLD_SPADE)),
    CUDGEL("Cudgel", new ItemStack(Material.COOKED_RABBIT)),
    VENOMSTRIKE("Venomstrike", new ItemStack(Material.GOLD_AXE)),
    HALBRED("Halbred", new ItemStack(Material.POTATO_ITEM)),
    DEMONBLADE("Demonblade", new ItemStack(Material.IRON_AXE)),

    RUNEBLADE("Runeblade", new ItemStack(Material.STONE_HOE)),
    KATAR("Katar", new ItemStack(Material.RAW_BEEF)),
    TENDERIZER("Tenderizer", new ItemStack(Material.COOKED_CHICKEN)),
    FLAMEWEAVER("Flameweaver", new ItemStack(Material.GOLD_PICKAXE)),
    NETHERSTEEL_KATANA("Nethersteel Katan", new ItemStack(Material.RAW_CHICKEN)),
    RUNIC_AXE("Runic Axe", new ItemStack(Material.BREAD)),
    NOMEGUSTA("Nomegusta", new ItemStack(Material.WOOD_SPADE)),
    LUNAR_RELIC("Lunar Relic", new ItemStack(Material.MUSHROOM_SOUP)),
    DIVINE_REACH("Divine Reach", new ItemStack(Material.MELON)),
    GEMCRUSHER("Gemcrusher", new ItemStack(Material.DIAMOND_SPADE)),
    ELVEN_GREATSWORD("Elven Greatsword", new ItemStack(Material.IRON_HOE)),
    HAMMER_OF_LIGHT("Hammer of Light", new ItemStack(Material.STRING)),
    MAGMASWORD("Magmasword", new ItemStack(Material.RAW_FISH, 1, (short) 2)),
    DIAMONDSPARK("Diamondspark", new ItemStack(Material.DIAMOND_AXE)),
    ZWEIREAPER("Zweireaper", new ItemStack(Material.WOOD_HOE)),

    VOID_EDGE("Void Edge", new ItemStack(Material.GOLDEN_CARROT)),
    FELFLAME_BLADE("Felflame Blade", new ItemStack(Material.COOKED_FISH, 1, (short) 1)),
    AMARANTH("Amaranth", new ItemStack(Material.COOKED_MUTTON)),
    ARMBLADE("Armblade", new ItemStack(Material.COOKED_BEEF)),
    GEMINI("Gemini", new ItemStack(Material.GRILLED_PORK)),
    DRAKEFANG("Drakefang", new ItemStack(Material.STONE_SPADE)),
    ABBADON("Abbadon", new ItemStack(Material.WOOD_PICKAXE)),
    FROSTBITE("Frostbite", new ItemStack(Material.RAW_FISH)),
    BROCCOMACE("Broccomace", new ItemStack(Material.BAKED_POTATO)),
    VOID_TWIG("Void Twig", new ItemStack(Material.DIAMOND_PICKAXE)),
    RUBY_THORN("Ruby Thorn", new ItemStack(Material.POISONOUS_POTATO)),
    ENDERFIST("Enderfist", new ItemStack(Material.APPLE)),

    ;

    public final String name;
    public final ItemStack item;

    Weapons(String name, ItemStack item) {
        this.name = name;
        this.item = item;
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
    }

    @Deprecated
    public static Weapons getSelected(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).getWeapon();
    }

    @Deprecated
    public static void setSelected(OfflinePlayer player, Weapons selectedWeapon) {
        Warlords.getPlayerSettings(player.getUniqueId()).setWeapon(selectedWeapon);
    }

    public static Weapons getWeapon(String name) {
        for (Weapons value : Weapons.values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return FELFLAME_BLADE;
    }
}
