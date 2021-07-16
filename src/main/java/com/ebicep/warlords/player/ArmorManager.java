package com.ebicep.warlords.player;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.ebicep.warlords.player.Classes.*;

public class ArmorManager {

    public static final String helmetDescription = "§7A cosmetic item for your head.\n§7Each class has a different piece of headgear.";
    public static final String armorDescription = "§7Cosmetic armor to complement your hat.\n§7The armor pieces are the same for each class.";

    public enum Helmets {

        SIMPLE_MAGE_HELMET("Simple Mage Helmet", new ItemStack(Material.CARPET, 1, (short) 3), new ItemStack(Material.CARPET, 1, (short) 5)),
        GREATER_MAGE_HELMET("Greater Mage Helmet", new ItemStack(Material.CARPET, 1, (short) 2), new ItemStack(Material.CARPET, 1, (short) 1)),
        MASTERWORK_MAGE_HELMET("Masterwork Mage Helmet", new ItemStack(Material.CARPET, 1, (short) 6), new ItemStack(Material.CARPET, 1, (short) 10)),
        LEGENDARY_MAGE_HELMET("Legendary Mage Helmet", new ItemStack(Material.RED_ROSE, 1, (short) 5), new ItemStack(Material.SAPLING, 1, (short) 5)),

        SIMPLE_WARRIOR_HELMET("Simple Warrior Helmet", new ItemStack(Material.CARPET, 1, (short) 15), new ItemStack(Material.CARPET, 1, (short) 11)),
        GREATER_WARRIOR_HELMET("Greater Warrior Helmet", new ItemStack(Material.CARPET, 1, (short) 12), new ItemStack(Material.CARPET, 1, (short) 9)),
        MASTERWORK_WARRIOR_HELMET("Masterwork Warrior Helmet", new ItemStack(Material.CARPET, 1, (short) 7), new ItemStack(Material.CARPET, 1, (short) 13)),
        LEGENDARY_WARRIOR_HELMET("Legendary Warrior Helmet", new ItemStack(Material.STONE_PLATE), new ItemStack(Material.WOOD_PLATE)),

        SIMPLE_PALADIN_HELMET("Simple Paladin Helmet", new ItemStack(Material.CARPET, 1, (short) 14), new ItemStack(Material.CARPET, 1, (short) 8)),
        GREATER_PALADIN_HELMET("Greater Paladin Helmet", new ItemStack(Material.CARPET), new ItemStack(Material.CARPET, 1, (short) 4)),
        MASTERWORK_PALADIN_HELMET("Masterwork Paladin Helmet", new ItemStack(Material.WOOD_STEP, 1, (short) 4), new ItemStack(Material.ACACIA_STAIRS)),
        LEGENDARY_PALADIN_HELMET("Legendary Paladin Helmet", new ItemStack(Material.DEAD_BUSH), new ItemStack(Material.RED_ROSE, 1, (short) 6)),

        SIMPLE_SHAMAN_HELMET("Simple Shaman Helmet", new ItemStack(Material.SAPLING, 1, (short) 4), new ItemStack(Material.RED_ROSE, 1, (short) 2)),
        GREATER_SHAMAN_HELMET("Greater Shaman Helmet", new ItemStack(Material.SAPLING, 1, (short) 2), new ItemStack(Material.CACTUS)),
        MASTERWORK_SHAMAN_HELMET("Masterwork Shaman Helmet", new ItemStack(Material.RED_ROSE, 1, (short) 8), new ItemStack(Material.YELLOW_FLOWER)),
        LEGENDARY_SHAMAN_HELMET("Legendary Shaman Helmet", new ItemStack(Material.SAPLING), new ItemStack(Material.SAPLING, 1, (short) 1)),

        ;

        public final String name;
        public final ItemStack itemRed;
        public final ItemStack itemBlue;

        Helmets(String name, ItemStack itemRed, ItemStack itemBlue) {
            this.name = name;
            this.itemRed = itemRed;
            this.itemBlue = itemBlue;
        }

        public static Helmets getMageHelmet(String name) {
            for (Helmets value : Helmets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_MAGE_HELMET;
        }

        public static Helmets getWarriorHelmet(String name) {
            for (Helmets value : Helmets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_WARRIOR_HELMET;
        }

        public static Helmets getPaladinHelmet(String name) {
            for (Helmets value : Helmets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_PALADIN_HELMET;
        }

        public static Helmets getShamanHelmet(String name) {
            for (Helmets value : Helmets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_SHAMAN_HELMET;
        }

        public static List<Helmets> getSelected(Player player) {
            List<Helmets> armorSets = new ArrayList<>();
            armorSets.add(player.getMetadata("selected-helmet-mage").stream()
                    .map(v -> v.value() instanceof Helmets ? (Helmets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_MAGE_HELMET));
            armorSets.add(player.getMetadata("selected-helmet-warrior").stream()
                    .map(v -> v.value() instanceof Helmets ? (Helmets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_WARRIOR_HELMET));
            armorSets.add(player.getMetadata("selected-helmet-paladin").stream()
                    .map(v -> v.value() instanceof Helmets ? (Helmets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_PALADIN_HELMET));
            armorSets.add(player.getMetadata("selected-helmet-shaman").stream()
                    .map(v -> v.value() instanceof Helmets ? (Helmets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_SHAMAN_HELMET));
            return armorSets;
        }

        public static void setSelectedMage(Player player, Helmets selectedHelmet) {
            player.removeMetadata("selected-helmet-mage", Warlords.getInstance());
            player.setMetadata("selected-helmet-mage", new FixedMetadataValue(Warlords.getInstance(), selectedHelmet));
        }

        public static void setSelectedWarrior(Player player, Helmets selectedHelmet) {
            player.removeMetadata("selected-helmet-warrior", Warlords.getInstance());
            player.setMetadata("selected-helmet-warrior", new FixedMetadataValue(Warlords.getInstance(), selectedHelmet));
        }

        public static void setSelectedPaladin(Player player, Helmets selectedHelmet) {
            player.removeMetadata("selected-helmet-paladin", Warlords.getInstance());
            player.setMetadata("selected-helmet-paladin", new FixedMetadataValue(Warlords.getInstance(), selectedHelmet));
        }

        public static void setSelectedShaman(Player player, Helmets selectedHelmet) {
            player.removeMetadata("selected-helmet-shaman", Warlords.getInstance());
            player.setMetadata("selected-helmet-shaman", new FixedMetadataValue(Warlords.getInstance(), selectedHelmet));
        }
    }

    public enum ArmorSets {

        SIMPLE_CHESTPLATE_MAGE("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
        GREATER_CHESTPLATE_MAGE("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
        MASTERWORK_CHESTPLATE_MAGE("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
        SIMPLE_CHESTPLATE_WARRIOR("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
        GREATER_CHESTPLATE_WARRIOR("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
        MASTERWORK_CHESTPLATE_WARRIOR("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
        SIMPLE_CHESTPLATE_PALADIN("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
        GREATER_CHESTPLATE_PALADIN("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
        MASTERWORK_CHESTPLATE_PALADIN("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),
        SIMPLE_CHESTPLATE_SHAMAN("Simple Chestplate", new ItemStack(Material.LEATHER_CHESTPLATE), new ItemStack(Material.LEATHER_CHESTPLATE)),
        GREATER_CHESTPLATE_SHAMAN("Greater Chestplate", new ItemStack(Material.CHAINMAIL_CHESTPLATE), new ItemStack(Material.IRON_CHESTPLATE)),
        MASTERWORK_CHESTPLATE_SHAMAN("Masterwork Chestplate", new ItemStack(Material.DIAMOND_CHESTPLATE), new ItemStack(Material.GOLD_CHESTPLATE)),


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

        public final String name;
        public final ItemStack itemRed;
        public final ItemStack itemBlue;

        ArmorSets(String name, ItemStack itemRed, ItemStack itemBlue) {
            this.name = name;
            this.itemRed = itemRed;
            this.itemBlue = itemBlue;
        }

        public static ArmorSets getMageArmor(String name) {
            for (ArmorSets value : ArmorSets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_CHESTPLATE_MAGE;
        }

        public static ArmorSets getWarriorArmor(String name) {
            for (ArmorSets value : ArmorSets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_CHESTPLATE_WARRIOR;
        }

        public static ArmorSets getPaladinArmor(String name) {
            for (ArmorSets value : ArmorSets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_CHESTPLATE_PALADIN;
        }

        public static ArmorSets getShamanArmor(String name) {
            for (ArmorSets value : ArmorSets.values()) {
                if (value.name.equals(name)) {
                    return value;
                }
            }
            return SIMPLE_CHESTPLATE_SHAMAN;
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

        public static List<ArmorSets> getSelected(Player player) {
            List<ArmorSets> armorSets = new ArrayList<>();
            armorSets.add(player.getMetadata("selected-armor-mage").stream()
                    .map(v -> v.value() instanceof ArmorSets ? (ArmorSets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_CHESTPLATE_MAGE));
            armorSets.add(player.getMetadata("selected-armor-warrior").stream()
                    .map(v -> v.value() instanceof ArmorSets ? (ArmorSets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_CHESTPLATE_WARRIOR));
            armorSets.add(player.getMetadata("selected-armor-paladin").stream()
                    .map(v -> v.value() instanceof ArmorSets ? (ArmorSets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_CHESTPLATE_PALADIN));
            armorSets.add(player.getMetadata("selected-armor-shaman").stream()
                    .map(v -> v.value() instanceof ArmorSets ? (ArmorSets) v.value() : null)
                    .filter(Objects::nonNull)
                    .findAny()
                    .orElse(SIMPLE_CHESTPLATE_SHAMAN));
            return armorSets;
        }

        public static void setSelectedMage(Player player, ArmorSets selectedArmorSet) {
            player.removeMetadata("selected-armor-mage", Warlords.getInstance());
            player.setMetadata("selected-armor-mage", new FixedMetadataValue(Warlords.getInstance(), selectedArmorSet));
        }

        public static void setSelectedWarrior(Player player, ArmorSets selectedArmorSet) {
            player.removeMetadata("selected-armor-warrior", Warlords.getInstance());
            player.setMetadata("selected-armor-warrior", new FixedMetadataValue(Warlords.getInstance(), selectedArmorSet));
        }

        public static void setSelectedPaladin(Player player, ArmorSets selectedArmorSet) {
            player.removeMetadata("selected-armor-paladin", Warlords.getInstance());
            player.setMetadata("selected-armor-paladin", new FixedMetadataValue(Warlords.getInstance(), selectedArmorSet));
        }

        public static void setSelectedShaman(Player player, ArmorSets selectedArmorSet) {
            player.removeMetadata("selected-armor-shaman", Warlords.getInstance());
            player.setMetadata("selected-armor-shaman", new FixedMetadataValue(Warlords.getInstance(), selectedArmorSet));
        }
    }

    public static void resetArmor(Player player, Classes selectedClass, Team team) {
        boolean onBlueTeam = team == Team.BLUE;
        ItemStack[] armor = new ItemStack[4];
        if (selectedClass == PYROMANCER || selectedClass == CRYOMANCER || selectedClass == AQUAMANCER) {
            armor[2] = new ItemBuilder(onBlueTeam ? ArmorSets.getSelected(player).get(0).itemBlue : ArmorSets.getSelected(player).get(0).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.getSelected(player).get(0).name : ChatColor.RED + ArmorSets.getSelected(player).get(0).name)
                    .lore(armorDescription)
                    .get();
            armor[3] = new ItemBuilder(onBlueTeam ? Helmets.getSelected(player).get(0).itemBlue : Helmets.getSelected(player).get(0).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + Helmets.getSelected(player).get(0).name : ChatColor.RED + Helmets.getSelected(player).get(0).name)
                    .lore(helmetDescription)
                    .get();
        } else if (selectedClass == BERSERKER || selectedClass == DEFENDER || selectedClass == REVENANT) {
            armor[2] = new ItemBuilder(onBlueTeam ? ArmorSets.getSelected(player).get(1).itemBlue : ArmorSets.getSelected(player).get(1).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.getSelected(player).get(1).name : ChatColor.RED + ArmorSets.getSelected(player).get(1).name)
                    .lore(armorDescription)
                    .get();
            armor[3] = new ItemBuilder(onBlueTeam ? Helmets.getSelected(player).get(1).itemBlue : Helmets.getSelected(player).get(1).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + Helmets.getSelected(player).get(1).name : ChatColor.RED + Helmets.getSelected(player).get(1).name)
                    .lore(helmetDescription)
                    .get();
        } else if (selectedClass == AVENGER || selectedClass == CRUSADER || selectedClass == PROTECTOR) {
            armor[2] = new ItemBuilder(onBlueTeam ? ArmorSets.getSelected(player).get(2).itemBlue : ArmorSets.getSelected(player).get(2).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.getSelected(player).get(2).name : ChatColor.RED + ArmorSets.getSelected(player).get(2).name)
                    .lore(armorDescription)
                    .get();
            armor[3] = new ItemBuilder(onBlueTeam ? Helmets.getSelected(player).get(2).itemBlue : Helmets.getSelected(player).get(2).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + Helmets.getSelected(player).get(2).name : ChatColor.RED + Helmets.getSelected(player).get(2).name)
                    .lore(helmetDescription)
                    .get();
        } else if (selectedClass == THUNDERLORD || selectedClass == SPIRITGUARD || selectedClass == EARTHWARDEN) {
            armor[2] = new ItemBuilder(onBlueTeam ? ArmorSets.getSelected(player).get(3).itemBlue : ArmorSets.getSelected(player).get(3).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.getSelected(player).get(3).name : ChatColor.RED + ArmorSets.getSelected(player).get(3).name)
                    .lore(armorDescription)
                    .get();
            armor[3] = new ItemBuilder(onBlueTeam ? Helmets.getSelected(player).get(3).itemBlue : Helmets.getSelected(player).get(3).itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + Helmets.getSelected(player).get(3).name : ChatColor.RED + Helmets.getSelected(player).get(3).name)
                    .lore(helmetDescription)
                    .get();
        }
        String name = Arrays.stream(ArmorSets.values()).filter(o -> o.name.equals(ChatColor.stripColor(armor[2].getItemMeta().getDisplayName()))).collect(Collectors.toList()).get(0).name;
        if (name.contains("Simple")) {
            armor[2] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_CHESTPLATE.itemBlue, onBlueTeam))
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.SIMPLE_CHESTPLATE.name : ChatColor.RED + ArmorSets.SIMPLE_CHESTPLATE.name)
                    .lore(armorDescription)
                    .get();
            armor[1] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_LEGGINGS.itemBlue, onBlueTeam))
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.SIMPLE_LEGGINGS.name : ChatColor.RED + ArmorSets.SIMPLE_LEGGINGS.name)
                    .lore(armorDescription)
                    .get();
            armor[0] = new ItemBuilder(ArmorSets.applyColor(ArmorSets.SIMPLE_BOOTS.itemBlue, onBlueTeam))
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.SIMPLE_BOOTS.name : ChatColor.RED + ArmorSets.SIMPLE_BOOTS.name)
                    .lore(armorDescription)
                    .get();
        } else if (name.contains("Greater")) {
            armor[1] = new ItemBuilder(onBlueTeam ? ArmorSets.GREATER_LEGGINGS.itemBlue : ArmorSets.GREATER_LEGGINGS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.GREATER_LEGGINGS.name : ChatColor.RED + ArmorSets.GREATER_LEGGINGS.name)
                    .lore(armorDescription)
                    .get();
            armor[0] = new ItemBuilder(onBlueTeam ? ArmorSets.GREATER_BOOTS.itemBlue : ArmorSets.GREATER_BOOTS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.GREATER_BOOTS.name : ChatColor.RED + ArmorSets.GREATER_BOOTS.name)
                    .lore(armorDescription)
                    .get();
        } else if (name.contains("Masterwork")) {
            armor[1] = new ItemBuilder(onBlueTeam ? ArmorSets.MASTERWORK_LEGGINGS.itemBlue : ArmorSets.MASTERWORK_LEGGINGS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.MASTERWORK_LEGGINGS.name : ChatColor.RED + ArmorSets.MASTERWORK_LEGGINGS.name)
                    .lore(armorDescription)
                    .get();
            armor[0] = new ItemBuilder(onBlueTeam ? ArmorSets.MASTERWORK_BOOTS.itemBlue : ArmorSets.MASTERWORK_BOOTS.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + ArmorSets.MASTERWORK_BOOTS.name : ChatColor.RED + ArmorSets.MASTERWORK_BOOTS.name)
                    .lore(armorDescription)
                    .get();
        }
        player.getInventory().setArmorContents(armor);
    }

}
