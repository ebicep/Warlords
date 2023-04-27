package com.ebicep.warlords.player.general;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.CosmeticSettings;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class ArmorManager {

    public static final String HELMET_DESCRIPTION = "§7A cosmetic item for your head.\n§7Each class has a different piece of headgear.";
    public static final String ARMOR_DESCRIPTION = "§7Cosmetic armor to complement your hat.\n§7The armor pieces are the same for each class.";

    public static void resetArmor(Player player) {
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player);
        Specializations selectedSpec = playerSettings.getSelectedSpec();
        resetArmor(player, playerSettings.getHelmet(selectedSpec), playerSettings.getArmorSet(selectedSpec), playerSettings.getWantedTeam());
    }

    public static void resetArmor(Player player, Helmets helmet, ArmorSets armorSet, Team team) {
        boolean onBlueTeam = team == Team.BLUE;
        ItemStack[] armor = new ItemStack[4];

        armor[2] = new ItemBuilder(onBlueTeam ? armorSet.itemBlue : armorSet.itemRed)
                .name(Component.text(armorSet.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                .loreLEGACY(ARMOR_DESCRIPTION)
                .get();
        armor[3] = new ItemBuilder(onBlueTeam ? helmet.itemBlue : helmet.itemRed)
                .name(Component.text(onBlueTeam ? ChatColor.BLUE + helmet.name : ChatColor.RED + helmet.name))
                .loreLEGACY(HELMET_DESCRIPTION)
                .get();

        if (armorSet.name.contains("Simple")) {
            armor[2] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_CHESTPLATE.itemBlue, onBlueTeam))
                    .name(Component.text(ArmorSets.SIMPLE_CHESTPLATE.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION)
                    .get();
            armor[1] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_LEGGINGS.itemBlue, onBlueTeam))
                    .name(Component.text(ArmorSets.SIMPLE_LEGGINGS.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_BOOTS.itemBlue, onBlueTeam))
                    .name(Component.text(ArmorSets.SIMPLE_BOOTS.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION)
                    .get();
        } else if (armorSet.name.contains("Greater")) {
            armor[1] = new ItemBuilder(onBlueTeam ? ArmorSets.GREATER_LEGGINGS.itemBlue : ArmorSets.GREATER_LEGGINGS.itemRed)
                    .name(Component.text(ArmorSets.GREATER_LEGGINGS.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(onBlueTeam ? ArmorSets.GREATER_BOOTS.itemBlue : ArmorSets.GREATER_BOOTS.itemRed)
                    .name(Component.text(ArmorSets.GREATER_BOOTS.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION)
                    .get();
        } else if (armorSet.name.contains("Masterwork")) {
            armor[1] = new ItemBuilder(onBlueTeam ? ArmorSets.MASTERWORK_LEGGINGS.itemBlue : ArmorSets.MASTERWORK_LEGGINGS.itemRed)
                    .name(Component.text(ArmorSets.MASTERWORK_LEGGINGS.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(onBlueTeam ? ArmorSets.MASTERWORK_BOOTS.itemBlue : ArmorSets.MASTERWORK_BOOTS.itemRed)
                    .name(Component.text(ArmorSets.MASTERWORK_BOOTS.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION)
                    .get();
        }
        player.getInventory().setArmorContents(armor);
    }

    public static void resetArmor(Player player, WarlordsPlayer warlordsPlayer) {
        CosmeticSettings cosmeticSettings = warlordsPlayer.getCosmeticSettings();
        resetArmor(player, cosmeticSettings.getHelmet(), cosmeticSettings.getArmorSet(), warlordsPlayer.getTeam());
    }

    public enum Helmets {

        SIMPLE_MAGE_HELMET(
                "Simple Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.LIGHT_BLUE_CARPET),
                new ItemStack(Material.LIME_CARPET)
        ),
        GREATER_MAGE_HELMET(
                "Greater Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.MAGENTA_CARPET),
                new ItemStack(Material.ORANGE_CARPET)
        ),
        MASTERWORK_MAGE_HELMET(
                "Masterwork Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.PINK_CARPET),
                new ItemStack(Material.PURPLE_CARPET)
        ),
        LEGENDARY_MAGE_HELMET(
                "Legendary Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.ORANGE_TULIP),
                new ItemStack(Material.DARK_OAK_SAPLING)
        ),

        SIMPLE_WARRIOR_HELMET(
                "Simple Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.BLACK_CARPET),
                new ItemStack(Material.BLUE_CARPET)
        ),
        GREATER_WARRIOR_HELMET(
                "Greater Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.BROWN_CARPET),
                new ItemStack(Material.CYAN_CARPET)
        ),
        MASTERWORK_WARRIOR_HELMET(
                "Masterwork Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.GRAY_CARPET),
                new ItemStack(Material.GREEN_CARPET)
        ),
        LEGENDARY_WARRIOR_HELMET(
                "Legendary Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.STONE_PRESSURE_PLATE),
                new ItemStack(Material.OAK_PRESSURE_PLATE)
        ),

        SIMPLE_PALADIN_HELMET(
                "Simple Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.RED_CARPET),
                new ItemStack(Material.LIGHT_GRAY_CARPET)
        ),
        GREATER_PALADIN_HELMET(
                "Greater Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.WHITE_CARPET),
                new ItemStack(Material.YELLOW_CARPET)
        ),
        MASTERWORK_PALADIN_HELMET(
                "Masterwork Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.ACACIA_SLAB),
                new ItemStack(Material.ACACIA_STAIRS)
        ),
        LEGENDARY_PALADIN_HELMET(
                "Legendary Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.DEAD_BUSH),
                new ItemStack(Material.WHITE_TULIP)
        ),

        SIMPLE_SHAMAN_HELMET(
                "Simple Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.ACACIA_SAPLING),
                new ItemStack(Material.ALLIUM)
        ),
        GREATER_SHAMAN_HELMET(
                "Greater Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.BIRCH_SAPLING),
                new ItemStack(Material.CACTUS)
        ),
        MASTERWORK_SHAMAN_HELMET(
                "Masterwork Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.OXEYE_DAISY),
                new ItemStack(Material.DANDELION)
        ),
        LEGENDARY_SHAMAN_HELMET(
                "Legendary Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.OAK_SAPLING),
                new ItemStack(Material.SPRUCE_SAPLING)
        ),

        SIMPLE_ROGUE_HELMET(
                "Simple Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.OAK_LOG),
                new ItemStack(Material.OAK_WOOD)
        ),
        GREATER_ROGUE_HELMET(
                "Greater Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.SPRUCE_LOG),
                new ItemStack(Material.SPRUCE_WOOD)
        ),
        MASTERWORK_ROGUE_HELMET(
                "Masterwork Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.BIRCH_LOG),
                new ItemStack(Material.BIRCH_WOOD)
        ),
        LEGENDARY_ROGUE_HELMET(
                "Legendary Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.JUNGLE_LOG),
                new ItemStack(Material.JUNGLE_WOOD)
        ),

        ;

        public static final Helmets[] VALUES = values();
        public final String name;
        public final Classes classes;
        public final ItemStack itemRed;
        public final ItemStack itemBlue;

        Helmets(String name, Classes classes, ItemStack itemRed, ItemStack itemBlue) {
            this.name = name;
            this.classes = classes;
            this.itemRed = itemRed;
            this.itemBlue = itemBlue;
        }

    }

    public enum ArmorSets {

        SIMPLE_CHESTPLATE("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
        SIMPLE_LEGGINGS("Simple Leggings", new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_LEGGINGS)),
        SIMPLE_BOOTS("Simple Boots", new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_BOOTS)),
        GREATER_CHESTPLATE("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
        GREATER_LEGGINGS("Greater Leggings", new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_LEGGINGS)),
        GREATER_BOOTS("Greater Boots", new ItemStack(Material.CHAINMAIL_BOOTS), new ItemStack(Material.IRON_BOOTS)),
        MASTERWORK_CHESTPLATE("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLDEN_CHESTPLATE)),
        MASTERWORK_LEGGINGS("Masterwork Leggings", new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.GOLDEN_LEGGINGS)),
        MASTERWORK_BOOTS("Masterwork Boots", new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.GOLDEN_BOOTS)),

        ;

        public static final ArmorSets[] VALUES = values();
        public final String name;
        public final ItemStack itemRed;
        public final ItemStack itemBlue;

        ArmorSets(String name, ItemStack itemRed, ItemStack itemBlue) {
            this.name = name;
            this.itemRed = itemRed;
            this.itemBlue = itemBlue;
        }

        public static ItemStack applyColor(ItemStack itemStack, boolean blueColor) {
            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            if (blueColor) {
                leatherArmorMeta.setColor(Color.fromRGB(51, 76, 178));
            } else {
                leatherArmorMeta.setColor(Color.fromRGB(153, 51, 51));
            }
            itemStack.setItemMeta(leatherArmorMeta);
            return itemStack;
        }

    }

}
