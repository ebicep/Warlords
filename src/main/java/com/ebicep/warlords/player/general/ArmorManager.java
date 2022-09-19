package com.ebicep.warlords.player.general;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.CosmeticSettings;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
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

    public static void resetArmor(Player player, WarlordsPlayer warlordsPlayer) {
        CosmeticSettings cosmeticSettings = warlordsPlayer.getCosmeticSettings();
        resetArmor(player, cosmeticSettings.getHelmet(), cosmeticSettings.getArmorSet(), warlordsPlayer.getTeam());
    }

    public static void resetArmor(Player player, Helmets helmet, ArmorSets armorSet, Team team) {
        boolean onBlueTeam = team == Team.BLUE;
        ItemStack[] armor = new ItemStack[4];

        armor[2] = new ItemBuilder(onBlueTeam ? armorSet.itemBlue : armorSet.itemRed)
                .name(onBlueTeam ? ChatColor.BLUE + armorSet.name : ChatColor.RED + armorSet.name)
                .lore(ARMOR_DESCRIPTION)
                .get();
        armor[3] = new ItemBuilder(onBlueTeam ? helmet.itemBlue : helmet.itemRed)
                .name(onBlueTeam ? ChatColor.BLUE + helmet.name : ChatColor.RED + helmet.name)
                .lore(HELMET_DESCRIPTION)
                .get();

        if (armorSet.name.contains("Simple")) {
            armor[2] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_CHESTPLATE.itemBlue, onBlueTeam))
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.SIMPLE_CHESTPLATE.name : ChatColor.RED + ArmorSets.SIMPLE_CHESTPLATE.name)
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[1] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_LEGGINGS.itemBlue, onBlueTeam))
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.SIMPLE_LEGGINGS.name : ChatColor.RED + ArmorSets.SIMPLE_LEGGINGS.name)
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_BOOTS.itemBlue, onBlueTeam))
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.SIMPLE_BOOTS.name : ChatColor.RED + ArmorSets.SIMPLE_BOOTS.name)
                    .lore(ARMOR_DESCRIPTION)
                    .get();
        } else if (armorSet.name.contains("Greater")) {
            armor[1] = new ItemBuilder(onBlueTeam ? ArmorSets.GREATER_LEGGINGS.itemBlue : ArmorSets.GREATER_LEGGINGS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.GREATER_LEGGINGS.name : ChatColor.RED + ArmorSets.GREATER_LEGGINGS.name)
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(onBlueTeam ? ArmorSets.GREATER_BOOTS.itemBlue : ArmorSets.GREATER_BOOTS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.GREATER_BOOTS.name : ChatColor.RED + ArmorSets.GREATER_BOOTS.name)
                    .lore(ARMOR_DESCRIPTION)
                    .get();
        } else if (armorSet.name.contains("Masterwork")) {
            armor[1] = new ItemBuilder(onBlueTeam ? ArmorSets.MASTERWORK_LEGGINGS.itemBlue : ArmorSets.MASTERWORK_LEGGINGS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.MASTERWORK_LEGGINGS.name : ChatColor.RED + ArmorSets.MASTERWORK_LEGGINGS.name)
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(onBlueTeam ? ArmorSets.MASTERWORK_BOOTS.itemBlue : ArmorSets.MASTERWORK_BOOTS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.MASTERWORK_BOOTS.name : ChatColor.RED + ArmorSets.MASTERWORK_BOOTS.name)
                    .lore(ARMOR_DESCRIPTION)
                    .get();
        }
        player.getInventory().setArmorContents(armor);
    }

    public enum Helmets {

        SIMPLE_MAGE_HELMET(
                "Simple Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.CARPET, 1, (short) 3),
                new ItemStack(Material.CARPET, 1, (short) 5)
        ),
        GREATER_MAGE_HELMET(
                "Greater Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.CARPET, 1, (short) 2),
                new ItemStack(Material.CARPET, 1, (short) 1)
        ),
        MASTERWORK_MAGE_HELMET(
                "Masterwork Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.CARPET, 1, (short) 6),
                new ItemStack(Material.CARPET, 1, (short) 10)
        ),
        LEGENDARY_MAGE_HELMET(
                "Legendary Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.RED_ROSE, 1, (short) 5),
                new ItemStack(Material.SAPLING, 1, (short) 5)
        ),

        SIMPLE_WARRIOR_HELMET(
                "Simple Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.CARPET, 1, (short) 15),
                new ItemStack(Material.CARPET, 1, (short) 11)
        ),
        GREATER_WARRIOR_HELMET(
                "Greater Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.CARPET, 1, (short) 12),
                new ItemStack(Material.CARPET, 1, (short) 9)
        ),
        MASTERWORK_WARRIOR_HELMET(
                "Masterwork Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.CARPET, 1, (short) 7),
                new ItemStack(Material.CARPET, 1, (short) 13)
        ),
        LEGENDARY_WARRIOR_HELMET(
                "Legendary Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.STONE_PLATE),
                new ItemStack(Material.WOOD_PLATE)
        ),

        SIMPLE_PALADIN_HELMET(
                "Simple Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.CARPET, 1, (short) 14),
                new ItemStack(Material.CARPET, 1, (short) 8)
        ),
        GREATER_PALADIN_HELMET(
                "Greater Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.CARPET),
                new ItemStack(Material.CARPET, 1, (short) 4)
        ),
        MASTERWORK_PALADIN_HELMET(
                "Masterwork Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.WOOD_STEP, 1, (short) 4),
                new ItemStack(Material.ACACIA_STAIRS)
        ),
        LEGENDARY_PALADIN_HELMET(
                "Legendary Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.DEAD_BUSH),
                new ItemStack(Material.RED_ROSE, 1, (short) 6)
        ),

        SIMPLE_SHAMAN_HELMET(
                "Simple Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.SAPLING, 1, (short) 4),
                new ItemStack(Material.RED_ROSE, 1, (short) 2)
        ),
        GREATER_SHAMAN_HELMET(
                "Greater Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.SAPLING, 1, (short) 2),
                new ItemStack(Material.CACTUS)
        ),
        MASTERWORK_SHAMAN_HELMET(
                "Masterwork Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.RED_ROSE, 1, (short) 8),
                new ItemStack(Material.YELLOW_FLOWER)
        ),
        LEGENDARY_SHAMAN_HELMET(
                "Legendary Shaman Helmet",
                Classes.SHAMAN,
                new ItemStack(Material.SAPLING),
                new ItemStack(Material.SAPLING, 1, (short) 1)
        ),

        SIMPLE_ROGUE_HELMET(
                "Simple Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.LOG, 1, (short) 0),
                new ItemStack(Material.WOOD, 1, (short) 0)
        ),
        GREATER_ROGUE_HELMET(
                "Greater Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.LOG, 1, (short) 1),
                new ItemStack(Material.WOOD, 1, (short) 1)
        ),
        MASTERWORK_ROGUE_HELMET(
                "Masterwork Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.LOG, 1, (short) 2),
                new ItemStack(Material.WOOD, 1, (short) 2)
        ),
        LEGENDARY_ROGUE_HELMET(
                "Legendary Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.LOG, 1, (short) 3),
                new ItemStack(Material.WOOD, 1, (short) 3)
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
        //TODO REMOVE
//        SIMPLE_CHESTPLATE_MAGE("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
//        GREATER_CHESTPLATE_MAGE("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
//        MASTERWORK_CHESTPLATE_MAGE("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
//        SIMPLE_CHESTPLATE_WARRIOR("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
//        GREATER_CHESTPLATE_WARRIOR("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
//        MASTERWORK_CHESTPLATE_WARRIOR("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
//        SIMPLE_CHESTPLATE_PALADIN("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
//        GREATER_CHESTPLATE_PALADIN("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
//        MASTERWORK_CHESTPLATE_PALADIN("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
//        SIMPLE_CHESTPLATE_SHAMAN("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
//        GREATER_CHESTPLATE_SHAMAN("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
//        MASTERWORK_CHESTPLATE_SHAMAN("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
//        SIMPLE_CHESTPLATE_ROGUE("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
//        GREATER_CHESTPLATE_ROGUE("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
//        MASTERWORK_CHESTPLATE_ROGUE("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
//

        SIMPLE_CHESTPLATE("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
        SIMPLE_LEGGINGS("Simple Leggings", new ItemStack(Material.LEATHER_LEGGINGS), new ItemStack(Material.LEATHER_LEGGINGS)),
        SIMPLE_BOOTS("Simple Boots", new ItemStack(Material.LEATHER_BOOTS), new ItemStack(Material.LEATHER_BOOTS)),
        GREATER_CHESTPLATE("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
        GREATER_LEGGINGS("Greater Leggings", new ItemStack(Material.CHAINMAIL_LEGGINGS), new ItemStack(Material.IRON_LEGGINGS)),
        GREATER_BOOTS("Greater Boots", new ItemStack(Material.CHAINMAIL_BOOTS), new ItemStack(Material.IRON_BOOTS)),
        MASTERWORK_CHESTPLATE("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
        MASTERWORK_LEGGINGS("Masterwork Leggings", new ItemStack(Material.DIAMOND_LEGGINGS), new ItemStack(Material.GOLD_LEGGINGS)),
        MASTERWORK_BOOTS("Masterwork Boots", new ItemStack(Material.DIAMOND_BOOTS), new ItemStack(Material.GOLD_BOOTS)),

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
