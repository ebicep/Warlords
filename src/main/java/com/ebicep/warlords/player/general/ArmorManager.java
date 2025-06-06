package com.ebicep.warlords.player.general;

import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.player.ingame.CosmeticSettings;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.List;

public class ArmorManager {

    public static final List<Component> HELMET_DESCRIPTION = WordWrap.wrap(Component.text("A cosmetic item for your head. Each class has a different piece of headgear.",
                    NamedTextColor.GRAY
            ), 160
    );
    public static final List<Component> ARMOR_DESCRIPTION = WordWrap.wrap(Component.text("Cosmetic armor to complement your hat. The armor pieces are the same for each class.",
                    NamedTextColor.GRAY
            ), 160
    );

    public static void resetArmor(Player player) {
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player);
        Specializations selectedSpec = playerSettings.getSelectedSpec();
        resetArmor(player, playerSettings.getHelmet(selectedSpec), playerSettings.getArmorSet(selectedSpec), playerSettings.getWantedTeam());
    }

    public static void resetArmor(Player player, Helmets helmet, ArmorSets armorSet, Team team) {
        player.getInventory().setArmorContents(getArmor(helmet, armorSet, team));
    }

    public static ItemStack[] getArmor(Helmets helmet, ArmorSets armorSet, Team team) {
        boolean onBlueTeam = team == Team.BLUE;
        ItemStack[] armor = new ItemStack[4];

        NamedTextColor color = onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED;
        armor[2] = new ItemBuilder(armorSet.getItem(team))
                .name(Component.text(armorSet.name, color))
                .lore(ARMOR_DESCRIPTION)
                .get();
        armor[3] = new ItemBuilder(helmet.getItem(team))
                .name(Component.text(helmet.name, color))
                .lore(HELMET_DESCRIPTION)
                .get();

        if (armorSet.name.contains("Simple")) {
            armor[2] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_CHESTPLATE.itemBlue, onBlueTeam))
                    .name(Component.text(ArmorSets.SIMPLE_CHESTPLATE.name, color))
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[1] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_LEGGINGS.itemBlue, onBlueTeam))
                    .name(Component.text(ArmorSets.SIMPLE_LEGGINGS.name, color))
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_BOOTS.itemBlue, onBlueTeam))
                    .name(Component.text(ArmorSets.SIMPLE_BOOTS.name, color))
                    .lore(ARMOR_DESCRIPTION)
                    .get();
        } else if (armorSet.name.contains("Greater")) {
            armor[1] = new ItemBuilder(ArmorSets.GREATER_LEGGINGS.getItem(team))
                    .name(Component.text(ArmorSets.GREATER_LEGGINGS.name, color))
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(ArmorSets.GREATER_BOOTS.getItem(team))
                    .name(Component.text(ArmorSets.GREATER_BOOTS.name, color))
                    .lore(ARMOR_DESCRIPTION)
                    .get();
        } else if (armorSet.name.contains("Masterwork")) {
            armor[1] = new ItemBuilder(ArmorSets.MASTERWORK_LEGGINGS.getItem(team))
                    .name(Component.text(ArmorSets.MASTERWORK_LEGGINGS.name, color))
                    .lore(ARMOR_DESCRIPTION)
                    .get();
            armor[0] = new ItemBuilder(ArmorSets.MASTERWORK_BOOTS.getItem(team))
                    .name(Component.text(ArmorSets.MASTERWORK_BOOTS.name, color))
                    .lore(ARMOR_DESCRIPTION)
                    .get();
        }

        return armor;
    }

    public static void resetArmor(Player player, WarlordsPlayer warlordsPlayer) {
        CosmeticSettings cosmeticSettings = warlordsPlayer.getCosmeticSettings();
        resetArmor(player, cosmeticSettings.getHelmet(), cosmeticSettings.getArmorSet(), warlordsPlayer.getTeam());
    }

    public enum Helmets {

        SIMPLE_MAGE_HELMET(
                "Simple Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.GREEN_CANDLE),
                new ItemStack(Material.LIME_CARPET)
        ),
        GREATER_MAGE_HELMET(
                "Greater Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.LIME_CANDLE),
                new ItemStack(Material.BLACK_CANDLE)
        ),
        MASTERWORK_MAGE_HELMET(
                "Masterwork Mage Helmet",
                Classes.MAGE,
                new ItemStack(Material.PINK_CANDLE),
                new ItemStack(Material.GRAY_CANDLE)
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
                new ItemStack(Material.YELLOW_CANDLE),
                new ItemStack(Material.CANDLE)
        ),
        GREATER_WARRIOR_HELMET(
                "Greater Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.LIGHT_BLUE_CANDLE),
                new ItemStack(Material.BLUE_CANDLE)
        ),
        MASTERWORK_WARRIOR_HELMET(
                "Masterwork Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.BROWN_CANDLE),
                new ItemStack(Material.ORANGE_CARPET)
        ),
        LEGENDARY_WARRIOR_HELMET(
                "Legendary Warrior Helmet",
                Classes.WARRIOR,
                new ItemStack(Material.MAGENTA_CANDLE),
                new ItemStack(Material.LIGHT_GRAY_CANDLE)
        ),

        SIMPLE_PALADIN_HELMET(
                "Simple Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.BEETROOT),
                new ItemStack(Material.BEETROOT_SOUP)
        ),
        GREATER_PALADIN_HELMET(
                "Greater Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.NAUTILUS_SHELL),
                new ItemStack(Material.RESIN_CLUMP)
        ),
        MASTERWORK_PALADIN_HELMET(
                "Masterwork Paladin Helmet",
                Classes.PALADIN,
                new ItemStack(Material.RED_CANDLE),
                new ItemStack(Material.PURPLE_CANDLE)
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
                new ItemStack(Material.OXIDIZED_COPPER_DOOR),
                new ItemStack(Material.MANGROVE_DOOR)
        ),
        GREATER_ROGUE_HELMET(
                "Greater Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.ACACIA_DOOR),
                new ItemStack(Material.BIRCH_DOOR)
        ),
        MASTERWORK_ROGUE_HELMET(
                "Masterwork Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.JUNGLE_DOOR),
                new ItemStack(Material.PALE_OAK_DOOR)
        ),
        LEGENDARY_ROGUE_HELMET(
                "Legendary Rogue Helmet",
                Classes.ROGUE,
                new ItemStack(Material.SPRUCE_DOOR),
                new ItemStack(Material.IRON_DOOR)
        ),

        SIMPLE_ARCANIST_HELMET(
                "Simple Arcanist Helmet",
                Classes.ARCANIST,
                new ItemStack(Material.COPPER_DOOR),
                new ItemStack(Material.OAK_DOOR)
        ),
        GREATER_ARCANIST_HELMET(
                "Greater Arcanist Helmet",
                Classes.ARCANIST,
                new ItemStack(Material.WEATHERED_COPPER_DOOR),
                new ItemStack(Material.DARK_OAK_DOOR)
        ),
        MASTERWORK_ARCANIST_HELMET(
                "Masterwork Arcanist Helmet",
                Classes.ARCANIST,
                new ItemStack(Material.EXPOSED_COPPER_DOOR),
                new ItemStack(Material.BAMBOO_DOOR)
        ),
        LEGENDARY_ARCANIST_HELMET(
                "Legendary Arcanist Helmet",
                Classes.ARCANIST,
                new ItemStack(Material.CHERRY_DOOR),
                new ItemStack(Material.CYAN_CANDLE)
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

        public ItemStack getItem(Team team) {
            return team == Team.BLUE ? itemBlue : itemRed;
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

        public final String name;
        public final ItemStack itemRed;
        public final ItemStack itemBlue;

        ArmorSets(String name, ItemStack itemRed, ItemStack itemBlue) {
            this.name = name;
            this.itemRed = itemRed;
            this.itemBlue = itemBlue;
        }

        public ItemStack getItem(Team team) {
            return team == Team.BLUE ? itemBlue : itemRed;
        }

    }

}
