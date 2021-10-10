package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import static com.ebicep.warlords.player.WeaponsRarity.*;

import java.util.HashMap;

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
    HALBRED("Halbred", new ItemStack(Material.POTATO_ITEM), RARE),
    DEMONBLADE("Demonblade", new ItemStack(Material.IRON_AXE), RARE),

    // epic
    RUNEBLADE("Runeblade", new ItemStack(Material.STONE_HOE), EPIC),
    KATAR("Katar", new ItemStack(Material.RAW_BEEF), EPIC),
    TENDERIZER("Tenderizer", new ItemStack(Material.COOKED_CHICKEN), EPIC),
    FLAMEWEAVER("Flameweaver", new ItemStack(Material.GOLD_PICKAXE), EPIC),
    NETHERSTEEL_KATANA("Nethersteel Katan", new ItemStack(Material.RAW_CHICKEN), EPIC),
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

    // mythic
    // TODO: uncomment october 13th
    NEW_LEAF_SCYTHE("[REDACTED]", new ItemStack(Material.GHAST_TEAR), MYTHIC),
    NEW_LEAF_AXE("[REDACTED]", new ItemStack(Material.LEATHER), MYTHIC),
    NEW_LEAF_SWORD("[REDACTED]", new ItemStack(Material.INK_SACK, 1, (short) 4), MYTHIC),
    NEW_LEAF_SPEAR("[REDACTED]", new ItemStack(Material.INK_SACK, 1, (short) 3), MYTHIC),

    ;

    public final String name;
    public final ItemStack item;
    public final WeaponsRarity rarity;

    Weapons(String name, ItemStack item, WeaponsRarity rarity) {
        this.name = name;
        this.item = item;
        this.rarity = rarity;
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
    public static HashMap<Classes, Weapons> getSelected(OfflinePlayer player) {
        return Warlords.getPlayerSettings(player.getUniqueId()).getWeaponSkins();
    }

    @Deprecated
    public static Weapons getSelected(OfflinePlayer player, Classes classes) {
        return Warlords.getPlayerSettings(player.getUniqueId()).getWeaponSkins().getOrDefault(classes, FELFLAME_BLADE);
    }

    @Deprecated
    public static void setSelected(OfflinePlayer player, HashMap<Classes, Weapons> weaponSkins) {
        Warlords.getPlayerSettings(player.getUniqueId()).setWeaponSkins(weaponSkins);
    }

    @Deprecated
    public static void setSelected(OfflinePlayer player, Classes classes, Weapons weapon) {
        Warlords.getPlayerSettings(player.getUniqueId()).getWeaponSkins().put(classes, weapon);
    }

    public static Weapons getWeapon(String name) {
        for (Weapons value : Weapons.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        return FELFLAME_BLADE;
    }
}
