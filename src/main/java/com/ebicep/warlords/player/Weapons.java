package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static com.ebicep.warlords.player.WeaponsRarity.*;

public enum Weapons {

    // common
    BLUDGEON("Bludgeon", new ItemStack(Material.RABBIT_STEW), COMMON),
    TRAINING_SWORD("Training Sword", new ItemStack(Material.STONE_AXE), COMMON),
    CLAWS("Claws", new ItemStack(Material.MUTTON), COMMON),
    SCIMITAR("Scimitar", new ItemStack(Material.RAW_FISH, 1, (short) 1), COMMON),
    ORC_AXE("Orc Axe", new ItemStack(Material.PUMPKIN_PIE), COMMON),
    HATCHET("Hatchet", new ItemStack(Material.GOLD_HOE), COMMON),
    PIKE("Pike", new ItemStack(Material.ROTTEN_FLESH), COMMON),
    HAMMER("Hammer", new ItemStack(Material.IRON_SPADE), COMMON),
    WALKING_STICK("Walking Stick", new ItemStack(Material.STONE_PICKAXE), COMMON),
    STEEL_SWORD("Steel Sword", new ItemStack(Material.WOOD_AXE), COMMON),

    // rare
    WORLD_TREE_BRANCH("World Tree Branch", new ItemStack(Material.IRON_PICKAXE), RARE),
    GEM_AXE("Gem Axe", new ItemStack(Material.DIAMOND_HOE), RARE),
    DOUBLEAXE("Doubleaxe", new ItemStack(Material.COOKED_FISH), RARE),
    MANDIBLES("Mandibles", new ItemStack(Material.PORK), RARE),
    GOLDEN_GLADIUS("Golden Gladius", new ItemStack(Material.RAW_FISH, 1, (short) 3), RARE),
    STONE_MALLET("Stone Mallet", new ItemStack(Material.GOLD_SPADE), RARE),
    CUDGEL("Cudgel", new ItemStack(Material.COOKED_RABBIT), RARE),
    VENOMSTRIKE("Venomstrike", new ItemStack(Material.GOLD_AXE), RARE),
    HALBERD("Halberd", new ItemStack(Material.POTATO_ITEM), RARE),
    DEMONBLADE("Demonblade", new ItemStack(Material.IRON_AXE), RARE),

    // epic
    RUNEBLADE("Runeblade", new ItemStack(Material.STONE_HOE), EPIC),
    KATAR("Katar", new ItemStack(Material.RAW_BEEF), EPIC),
    TENDERIZER("Tenderizer", new ItemStack(Material.COOKED_CHICKEN), EPIC),
    FLAMEWEAVER("Flameweaver", new ItemStack(Material.GOLD_PICKAXE), EPIC),
    NETHERSTEEL_KATANA("Nethersteel Katana", new ItemStack(Material.RAW_CHICKEN), EPIC),
    RUNIC_AXE("Runic Axe", new ItemStack(Material.BREAD), EPIC),
    NOMEGUSTA("Nomegusta", new ItemStack(Material.WOOD_SPADE), EPIC),
    LUNAR_RELIC("Lunar Relic", new ItemStack(Material.MUSHROOM_SOUP), EPIC),
    DIVINE_REACH("Divine Reach", new ItemStack(Material.MELON), EPIC),
    GEMCRUSHER("Gemcrusher", new ItemStack(Material.DIAMOND_SPADE), EPIC),
    ELVEN_GREATSWORD("Elven Greatsword", new ItemStack(Material.IRON_HOE), EPIC),
    HAMMER_OF_LIGHT("Hammer of Light", new ItemStack(Material.STRING), EPIC),
    MAGMASWORD("Magmasword", new ItemStack(Material.RAW_FISH, 1, (short) 2), EPIC),
    DIAMONDSPARK("Diamondspark", new ItemStack(Material.DIAMOND_AXE), EPIC),
    ZWEIREAPER("Zweireaper", new ItemStack(Material.WOOD_HOE), EPIC),

    // legendary
    VOID_EDGE("Void Edge", new ItemStack(Material.GOLDEN_CARROT), LEGENDARY),
    FELFLAME_BLADE("Felflame Blade", new ItemStack(Material.COOKED_FISH, 1, (short) 1), LEGENDARY),
    AMARANTH("Amaranth", new ItemStack(Material.COOKED_MUTTON), LEGENDARY),
    ARMBLADE("Armblade", new ItemStack(Material.COOKED_BEEF), LEGENDARY),
    GEMINI("Gemini", new ItemStack(Material.GRILLED_PORK), LEGENDARY),
    DRAKEFANG("Drakefang", new ItemStack(Material.STONE_SPADE), LEGENDARY),
    ABBADON("Abbadon", new ItemStack(Material.WOOD_PICKAXE), LEGENDARY),
    FROSTBITE("Frostbite", new ItemStack(Material.RAW_FISH), LEGENDARY),
    BROCCOMACE("Broccomace", new ItemStack(Material.BAKED_POTATO), LEGENDARY),
    VOID_TWIG("Void Twig", new ItemStack(Material.DIAMOND_PICKAXE), LEGENDARY),
    RUBY_THORN("Ruby Thorn", new ItemStack(Material.POISONOUS_POTATO), LEGENDARY),
    ENDERFIST("Enderfist", new ItemStack(Material.APPLE), LEGENDARY),

    // wl 2 exclusive
    NEW_LEAF_SCYTHE("Daphne's Harvest", new ItemStack(Material.GHAST_TEAR), LEGENDARY),
    NEW_LEAF_AXE("Fate of Daphne", new ItemStack(Material.LEATHER), LEGENDARY),
    NEW_LEAF_SWORD("Canopy's Jade Edge", new ItemStack(Material.INK_SACK, 1, (short) 4), LEGENDARY),
    NEW_LEAF_SPEAR("Daphne's Viper", new ItemStack(Material.INK_SACK, 1, (short) 3), LEGENDARY),
    SILVER_PHANTASM_SCYTHE("Tenth Plague", new ItemStack(Material.PRISMARINE_CRYSTALS), LEGENDARY),
    SILVER_PHANTASM_SWORD("Hyperion's Awakening", new ItemStack(Material.PRISMARINE_SHARD), LEGENDARY),
    SILVER_PHANTASM_SWORD_2("Blazeguard", new ItemStack(Material.FLINT), LEGENDARY),
    SILVER_PHANTASM_SWORD_3("Venom", new ItemStack(Material.COAL), LEGENDARY),
    SILVER_PHANTASM_SWORD_4("Lilium", new ItemStack(Material.STICK), LEGENDARY),
    SILVER_PHANTASM_HAMMER("Wrath of Aether", new ItemStack(Material.QUARTZ), LEGENDARY),
    SILVER_PHANTASM_STAFF("Wit of Oblivion", new ItemStack(Material.IRON_INGOT), LEGENDARY),
    SILVER_PHANTASM_STAFF_2("Lament", new ItemStack(Material.INK_SACK), LEGENDARY),
    SILVER_PHANTASM_SCIMITAR("Bloodquench", new ItemStack(Material.CARROT_ITEM), LEGENDARY),
    SILVER_PHANTASM_TRIDENT("Torment", new ItemStack(Material.COOKIE), LEGENDARY),
    SILVER_PHANTASM_SAWBLADE("Slayer", new ItemStack(Material.RABBIT), LEGENDARY),
    CANDY_CANE("Candy Slapper", new ItemStack(Material.SUGAR), LEGENDARY),

    // mythic
    FABLED_HEROICS_SCYTHE("Ghostly Sickles", new ItemStack(Material.SPIDER_EYE), MYTHIC),
    FABLED_HEROICS_SWORD("Nichirin", new ItemStack(Material.WHEAT), MYTHIC),
    FABLED_HEROICS_SWORD_2("Bumbleblade", new ItemStack(Material.NETHER_BRICK_ITEM), MYTHIC),

    ;

    public final String name;
    public final ItemStack item;
    public final WeaponsRarity rarity;
    public boolean isUnlocked;

    Weapons(String name, ItemStack item, WeaponsRarity rarity) {
        this.name = name;
        this.item = item;
        this.rarity = rarity;
        this.isUnlocked = rarity != MYTHIC;
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
    }

    public WeaponsRarity getRarity() {
        return this.rarity;
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public static Weapons getSelected(OfflinePlayer player, Specializations specializations) {
        return Warlords.getPlayerSettings(player.getUniqueId()).getWeaponSkins().getOrDefault(specializations, FELFLAME_BLADE);
    }

    @Deprecated
    public static void setSelected(OfflinePlayer player, Specializations specializations, Weapons weapon) {
        Warlords.getPlayerSettings(player.getUniqueId()).getWeaponSkins().put(specializations, weapon);
    }

    public static Weapons getWeapon(String name) {
        if(name == null) {
            return FELFLAME_BLADE;
        }
        for (Weapons value : Weapons.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return FELFLAME_BLADE;
    }
}
