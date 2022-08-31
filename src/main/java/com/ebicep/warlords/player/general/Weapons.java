package com.ebicep.warlords.player.general;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.ebicep.warlords.player.general.WeaponsRarity.*;

public enum Weapons {

    // common
    BLUDGEON("Bludgeon", new ItemStack(Material.RABBIT_STEW), COMMON, WeaponsPvE.COMMON),
    TRAINING_SWORD("Training Sword", new ItemStack(Material.STONE_AXE), COMMON, WeaponsPvE.COMMON),
    CLAWS("Claws", new ItemStack(Material.MUTTON), COMMON, WeaponsPvE.COMMON),
    SCIMITAR("Scimitar", new ItemStack(Material.RAW_FISH, 1, (short) 1), COMMON, WeaponsPvE.COMMON),
    ORC_AXE("Orc Axe", new ItemStack(Material.PUMPKIN_PIE), COMMON, WeaponsPvE.COMMON),
    HATCHET("Hatchet", new ItemStack(Material.GOLD_HOE), COMMON, WeaponsPvE.COMMON),
    PIKE("Pike", new ItemStack(Material.ROTTEN_FLESH), COMMON, WeaponsPvE.COMMON),
    HAMMER("Hammer", new ItemStack(Material.IRON_SPADE), COMMON, WeaponsPvE.COMMON),
    WALKING_STICK("Walking Stick", new ItemStack(Material.STONE_PICKAXE), COMMON, WeaponsPvE.COMMON),
    STEEL_SWORD("Steel Sword", new ItemStack(Material.WOOD_AXE), COMMON, WeaponsPvE.COMMON),

    // rare
    WORLD_TREE_BRANCH("World Tree Branch", new ItemStack(Material.IRON_PICKAXE), RARE, WeaponsPvE.RARE),
    GEM_AXE("Gem Axe", new ItemStack(Material.DIAMOND_HOE), RARE, WeaponsPvE.RARE),
    DOUBLEAXE("Doubleaxe", new ItemStack(Material.COOKED_FISH), RARE, WeaponsPvE.RARE),
    MANDIBLES("Mandibles", new ItemStack(Material.PORK), RARE, WeaponsPvE.RARE),
    GOLDEN_GLADIUS("Golden Gladius", new ItemStack(Material.RAW_FISH, 1, (short) 3), RARE, WeaponsPvE.RARE),
    STONE_MALLET("Stone Mallet", new ItemStack(Material.GOLD_SPADE), RARE, WeaponsPvE.RARE),
    CUDGEL("Cudgel", new ItemStack(Material.COOKED_RABBIT), RARE, WeaponsPvE.RARE),
    VENOMSTRIKE("Venomstrike", new ItemStack(Material.GOLD_AXE), RARE, WeaponsPvE.RARE),
    HALBERD("Halberd", new ItemStack(Material.POTATO_ITEM), RARE, WeaponsPvE.RARE),
    DEMONBLADE("Demonblade", new ItemStack(Material.IRON_AXE), RARE, WeaponsPvE.RARE),

    // epic
    RUNEBLADE("Runeblade", new ItemStack(Material.STONE_HOE), EPIC, WeaponsPvE.EPIC),
    KATAR("Katar", new ItemStack(Material.RAW_BEEF), EPIC, WeaponsPvE.EPIC),
    TENDERIZER("Tenderizer", new ItemStack(Material.COOKED_CHICKEN), EPIC, WeaponsPvE.EPIC),
    FLAMEWEAVER("Flameweaver", new ItemStack(Material.GOLD_PICKAXE), EPIC, WeaponsPvE.EPIC),
    NETHERSTEEL_KATANA("Nethersteel Katana", new ItemStack(Material.RAW_CHICKEN), EPIC, WeaponsPvE.EPIC),
    RUNIC_AXE("Runic Axe", new ItemStack(Material.BREAD), EPIC, WeaponsPvE.EPIC),
    NOMEGUSTA("Nomegusta", new ItemStack(Material.WOOD_SPADE), EPIC, WeaponsPvE.EPIC),
    LUNAR_RELIC("Lunar Relic", new ItemStack(Material.MUSHROOM_SOUP), EPIC, WeaponsPvE.EPIC),
    DIVINE_REACH("Divine Reach", new ItemStack(Material.MELON), EPIC, WeaponsPvE.EPIC),
    GEMCRUSHER("Gemcrusher", new ItemStack(Material.DIAMOND_SPADE), EPIC, WeaponsPvE.EPIC),
    ELVEN_GREATSWORD("Elven Greatsword", new ItemStack(Material.IRON_HOE), EPIC, WeaponsPvE.EPIC),
    HAMMER_OF_LIGHT("Hammer of Light", new ItemStack(Material.STRING), EPIC, WeaponsPvE.EPIC),
    MAGMASWORD("Magmasword", new ItemStack(Material.RAW_FISH, 1, (short) 2), EPIC, WeaponsPvE.EPIC),
    DIAMONDSPARK("Diamondspark", new ItemStack(Material.DIAMOND_AXE), EPIC, WeaponsPvE.EPIC),
    ZWEIREAPER("Zweireaper", new ItemStack(Material.WOOD_HOE), EPIC, WeaponsPvE.EPIC),

    // legendary
    VOID_EDGE("Void Edge", new ItemStack(Material.GOLDEN_CARROT), LEGENDARY, WeaponsPvE.LEGENDARY),
    FELFLAME_BLADE("Felflame Blade", new ItemStack(Material.COOKED_FISH, 1, (short) 1), LEGENDARY, WeaponsPvE.LEGENDARY),
    AMARANTH("Amaranth", new ItemStack(Material.COOKED_MUTTON), LEGENDARY, WeaponsPvE.LEGENDARY),
    ARMBLADE("Armblade", new ItemStack(Material.COOKED_BEEF), LEGENDARY, WeaponsPvE.LEGENDARY),
    GEMINI("Gemini", new ItemStack(Material.GRILLED_PORK), LEGENDARY, WeaponsPvE.LEGENDARY),
    DRAKEFANG("Drakefang", new ItemStack(Material.STONE_SPADE), LEGENDARY, WeaponsPvE.LEGENDARY),
    ABBADON("Abbadon", new ItemStack(Material.WOOD_PICKAXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    FROSTBITE("Frostbite", new ItemStack(Material.RAW_FISH), LEGENDARY, WeaponsPvE.LEGENDARY),
    BROCCOMACE("Broccomace", new ItemStack(Material.BAKED_POTATO), LEGENDARY, WeaponsPvE.LEGENDARY),
    VOID_TWIG("Void Twig", new ItemStack(Material.DIAMOND_PICKAXE), LEGENDARY, WeaponsPvE.LEGENDARY),
    RUBY_THORN("Ruby Thorn", new ItemStack(Material.POISONOUS_POTATO), LEGENDARY, WeaponsPvE.LEGENDARY),
    ENDERFIST("Enderfist", new ItemStack(Material.APPLE), LEGENDARY, WeaponsPvE.LEGENDARY),

    // wl 2 exclusive
    NEW_LEAF_SCYTHE("Daphne's Harvest", new ItemStack(Material.GHAST_TEAR), LEGENDARY, WeaponsPvE.LEGENDARY),
    NEW_LEAF_AXE("Fate of Daphne", new ItemStack(Material.LEATHER), LEGENDARY, WeaponsPvE.LEGENDARY),
    NEW_LEAF_SWORD("Canopy's Jade Edge", new ItemStack(Material.INK_SACK, 1, (short) 4), LEGENDARY, WeaponsPvE.LEGENDARY),
    NEW_LEAF_SPEAR("Daphne's Viper", new ItemStack(Material.INK_SACK, 1, (short) 3), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SCYTHE("Tenth Plague", new ItemStack(Material.PRISMARINE_CRYSTALS), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD("Hyperion's Awakening", new ItemStack(Material.PRISMARINE_SHARD), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD_2("Blazeguard", new ItemStack(Material.FLINT), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD_3("Venom", new ItemStack(Material.COAL), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SWORD_4("Lilium", new ItemStack(Material.STICK), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_HAMMER("Wrath of Aether", new ItemStack(Material.QUARTZ), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_STAFF("Wit of Oblivion", new ItemStack(Material.IRON_INGOT), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_STAFF_2("Lament", new ItemStack(Material.INK_SACK), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SCIMITAR("Bloodquench", new ItemStack(Material.CARROT_ITEM), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_TRIDENT("Torment", new ItemStack(Material.COOKIE), LEGENDARY, WeaponsPvE.LEGENDARY),
    SILVER_PHANTASM_SAWBLADE("Slayer", new ItemStack(Material.RABBIT), LEGENDARY, WeaponsPvE.LEGENDARY),
    CANDY_CANE("Candy Slapper", new ItemStack(Material.SUGAR), LEGENDARY, WeaponsPvE.LEGENDARY),

    // mythic
    FABLED_HEROICS_SCYTHE("Ghostly Sickles", new ItemStack(Material.SPIDER_EYE), ASCENDANT, WeaponsPvE.LEGENDARY),
    FABLED_HEROICS_SWORD("Nichirin", new ItemStack(Material.WHEAT), ASCENDANT, WeaponsPvE.LEGENDARY),
    FABLED_HEROICS_SWORD_2("Bumbleblade", new ItemStack(Material.NETHER_BRICK_ITEM), LEGENDARY, WeaponsPvE.LEGENDARY),
    FABLED_HEROICS_DRUMSTICK("Cornelius", new ItemStack(Material.SUGAR_CANE), LEGENDARY, WeaponsPvE.LEGENDARY),

    ;

    public static final Weapons[] VALUES = values();
    private final String name;
    private final ItemStack item;
    public final WeaponsRarity rarity;
    public final WeaponsPvE weaponsPvE;
    public boolean isUnlocked;

    Weapons(String name, ItemStack item, WeaponsRarity rarity, WeaponsPvE weaponsPvE) {
        this.name = name;
        this.item = item;
        this.rarity = rarity;
        this.isUnlocked = rarity != ASCENDANT;
        this.weaponsPvE = weaponsPvE;
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
        return Warlords.getPlayerSettings(player.getUniqueId()).getWeaponSkin();
    }

    @Deprecated
    public static void setSelected(OfflinePlayer player, Specializations specializations, Weapons weapon) {
        Warlords.getPlayerSettings(player.getUniqueId()).getWeaponSkins().put(specializations, weapon);
    }

    public static Weapons getWeapon(String name) {
        if(name == null) {
            return FELFLAME_BLADE;
        }
        for (Weapons value : Weapons.VALUES) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return FELFLAME_BLADE;
    }

    public static Weapons getRandomWeaponFromRarity(WeaponsRarity rarity) {
        List<Weapons> weapons = new ArrayList<>();
        for (Weapons value : Weapons.VALUES) {
            if (value.rarity == rarity) {
                weapons.add(value);
            }
        }
        return weapons.get(ThreadLocalRandom.current().nextInt(weapons.size()));
    }

    public ItemStack getItem() {
        return item;
    }

    public int getCost() {
        return 0;
    }
}
