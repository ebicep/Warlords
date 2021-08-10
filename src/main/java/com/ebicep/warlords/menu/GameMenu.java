package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.player.ArmorManager.*;
import static com.ebicep.warlords.player.Classes.*;
import static com.ebicep.warlords.player.Settings.*;

public class GameMenu {
    private static final ItemStack MENU_BACK_PREGAME = new ItemBuilder(Material.ARROW)
            .name(ChatColor.GREEN + "Back")
            .lore(ChatColor.GRAY + "To Pre-game Menu")
            .get();
    private static final ItemStack MENU_SKINS = new ItemBuilder(Material.PAINTING)
            .name(ChatColor.GREEN + "Weapon Skin Selector")
            .lore("§7Change the cosmetic appearance\n§7of your weapon to better suit\n§7your tastes.", "", "§eClick to change weapon skin!")
            .get();
    private static final ItemStack MENU_ARMOR_SETS = new ItemBuilder(Material.DIAMOND_HELMET)
            .name(ChatColor.AQUA + "Armor Sets " + ChatColor.GRAY + "& " + ChatColor.AQUA + "Helmets " + ChatColor.GOLD + "(Cosmetic)")
            .lore("§7Equip your favorite armor\n§7sets or class helmets")
            .get();
    private static final ItemStack MENU_BOOSTS = new ItemBuilder(Material.BOOKSHELF)
            .name(ChatColor.AQUA + "Weapon Skill Boost")
            .lore("§7Choose which of your skills you\n§7want your equipped weapon to boost.", "", "§eClick to change skill boost!")
            .get();
    private static final ItemStack MENU_SETTINGS = new ItemBuilder(Material.NETHER_STAR)
            .name(ChatColor.AQUA + "Settings")
            .lore("§7Allows you to toggle different settings\n§7options.", "", "§eClick to edit your settings.")
            .get();
    private static final ItemStack MENU_SETTINGS_PARTICLE_QUALITY = new ItemBuilder(Material.NETHER_STAR)
            .name(ChatColor.GREEN + "Particle Quality")
            .lore("§7Allows you to control, or\n§7disable, particles and the\n§7amount of them.")
            .get();


    public static void openMainMenu(Player player) {
        Classes selectedClass = getSelected(player);

        Menu menu = new Menu("Warlords Shop", 9 * 6);
        ClassesGroup[] values = ClassesGroup.values();
        for (int i = 0; i < values.length; i++) {
            ClassesGroup group = values[i];
            List<String> lore = new ArrayList<>();
            lore.add(group.description);
            lore.add("");
            lore.add(ChatColor.GOLD + "Specializations:");
            for (Classes subClass : group.subclasses) {
                lore.add((subClass == selectedClass ? ChatColor.GREEN : ChatColor.GRAY) + subClass.name);
            }
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click here to select a " + group.name + "\n" + ChatColor.YELLOW + "specialization");
            ItemStack item = new ItemBuilder(group.item)
                    .name(ChatColor.GOLD + group.name + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv90" + ChatColor.DARK_GRAY + "]")
                    .lore(lore)
                    .get();
            menu.setItem(
                    9 / 2 - values.length / 2 + i * 2 - 1,
                    1,
                    item,
                    (n, e) -> {
                        openClassMenu(player, group);
                    }
            );
        }
        menu.setItem(1, 3, MENU_SKINS, (n, e) -> openWeaponMenu(player, 1));
        menu.setItem(3, 3, MENU_ARMOR_SETS, (n, e) -> openArmorMenu(player, 1));
        menu.setItem(5, 3, MENU_BOOSTS, (n, e) -> openSkillBoostMenu(player, selectedClass));
        menu.setItem(7, 3, MENU_SETTINGS, (n, e) -> openSettingsMenu(player));
        menu.setItem(4, 5, Menu.MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openClassMenu(Player player, ClassesGroup selectedGroup) {
        Classes selectedClass = getSelected(player);
        Menu menu = new Menu(selectedGroup.name, 9 * 4);
        List<Classes> values = selectedGroup.subclasses;
        for (int i = 0; i < values.size(); i++) {
            Classes subClass = values.get(i);
            ItemBuilder builder = new ItemBuilder(subClass.specType.itemStack)
                    .name(ChatColor.GREEN + "Specialization: " + subClass.name)
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(subClass.description);
            lore.add("");
            if (subClass == selectedClass) {
                lore.add(ChatColor.GREEN + ">>> ACTIVE <<<");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "> Click to activate <");
            }
            builder.lore(lore);
            menu.setItem(
                    9 / 2 - values.size() / 2 + i * 2 - 1,
                    1,
                    builder.get(),
                    (n, e) -> {
                        player.sendMessage(ChatColor.WHITE + "Class: §6" + subClass);
                        setSelected(player, subClass);
                        ArmorManager.resetArmor(player, subClass, Warlords.getPlayerSettings(player.getUniqueId()).wantedTeam());
                        openClassMenu(player, selectedGroup);
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));

        menu.openForPlayer(player);
    }

    public static void openSkillBoostMenu(Player player, Classes selectedGroup) {
        Classes selectedClass = getSelected(player);
        ClassesSkillBoosts selectedBoost = getSelectedBoost(player);
        Menu menu = new Menu("Skill Boost", 9 * 4);
        List<ClassesSkillBoosts> values = selectedGroup.skillBoosts;
        for (int i = 0; i < values.size(); i++) {
            ClassesSkillBoosts subClass = values.get(i);
            ItemBuilder builder = new ItemBuilder(getSelected(player).specType.itemStack)
                    .name(subClass == selectedBoost ? ChatColor.GREEN + subClass.name + " (" + selectedClass.name + ")" : ChatColor.RED + subClass.name + " (" + selectedClass.name + ")")
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(subClass == selectedBoost ? subClass.selectedDescription : subClass.description);
            lore.add("");
            if (subClass == selectedBoost) {
                lore.add(ChatColor.GREEN + "Currently selected!");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "Click to select!");
            }
            builder.lore(lore);
            menu.setItem(
                    6 - values.size() + i * 2 - 1,
                    1,
                    builder.get(),
                    (n, e) -> {
                        player.sendMessage(ChatColor.GREEN + "You have changed your weapon boost to: §b" + subClass.name + "!");
                        setSelectedBoost(player, subClass);
                        openSkillBoostMenu(player, selectedGroup);
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openWeaponMenu(Player player, int pageNumber) {
        Weapons selectedWeapon = Weapons.getSelected(player);
        Menu menu = new Menu("Weapon Skin Selector", 9 * 6);
        List<Weapons> values = new ArrayList<>(Arrays.asList(Weapons.values()));
        for (int i = (pageNumber - 1) * 21; i < pageNumber * 21 && i < values.size(); i++) {
            Weapons weapon = values.get(i);
            ItemBuilder builder = new ItemBuilder(weapon.item)
                    .name(ChatColor.GREEN + weapon.name)
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            if (weapon == selectedWeapon) {
                lore.add(ChatColor.GREEN + "Currently selected!");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "Click to select!");
            }
            builder.lore(lore);
            menu.setItem(
                    (i - (pageNumber - 1) * 21) % 7 + 1,
                    (i - (pageNumber - 1) * 21) / 7 + 1,
                    builder.get(),
                    (n, e) -> {
                        player.sendMessage(ChatColor.GREEN + "You have changed your weapon skin to: §b" + weapon.name + "!");
                        Weapons.setSelected(player, weapon);
                        openWeaponMenu(player, pageNumber);
                    }
            );
        }
        if (pageNumber == 1) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (n, e) -> openWeaponMenu(player, pageNumber + 1));
        } else if (pageNumber == 2) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (n, e) -> openWeaponMenu(player, pageNumber - 1));
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (n, e) -> openWeaponMenu(player, pageNumber + 1));
        } else if (pageNumber == 3) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (n, e) -> openWeaponMenu(player, pageNumber - 1));
        }

        menu.setItem(4, 5, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openArmorMenu(Player player, int pageNumber) {
        boolean onBlueTeam = Warlords.game.getPlayerTeamOrNull(player.getUniqueId()) == Team.BLUE;
        List<Helmets> selectedHelmet = Helmets.getSelected(player);
        List<ArmorSets> selectedArmorSet = ArmorSets.getSelected(player);
        Menu menu = new Menu("Armor Sets & Helmets", 9 * 6);
        List<Helmets> helmets = Arrays.asList(Helmets.values());
        for (int i = (pageNumber - 1) * 8; i < pageNumber * 8 && i < helmets.size(); i++) {
            Helmets helmet = helmets.get(i);
            ItemBuilder builder = new ItemBuilder(onBlueTeam ? helmet.itemBlue : helmet.itemRed)
                    .name(onBlueTeam ? ChatColor.BLUE + helmet.name : ChatColor.RED + helmet.name)
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(helmetDescription);
            lore.add("");
            if (selectedHelmet.contains(helmet)) {
                lore.add(ChatColor.GREEN + ">>> ACTIVE <<<");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "> Click to activate! <");
            }
            builder.lore(lore);
            menu.setItem(
                    (i - (pageNumber - 1) * 8) + 1,
                    2,
                    builder.get(),
                    (n, e) -> {
                        player.sendMessage(ChatColor.YELLOW + "Selected: " + ChatColor.GREEN + helmet.name);
                        if (helmet == Helmets.SIMPLE_MAGE_HELMET || helmet == Helmets.GREATER_MAGE_HELMET || helmet == Helmets.MASTERWORK_MAGE_HELMET || helmet == Helmets.LEGENDARY_MAGE_HELMET) {
                            Helmets.setSelectedMage(player, helmet);
                        } else if (helmet == Helmets.SIMPLE_WARRIOR_HELMET || helmet == Helmets.GREATER_WARRIOR_HELMET || helmet == Helmets.MASTERWORK_WARRIOR_HELMET || helmet == Helmets.LEGENDARY_WARRIOR_HELMET) {
                            Helmets.setSelectedWarrior(player, helmet);
                        } else if (helmet == Helmets.SIMPLE_PALADIN_HELMET || helmet == Helmets.GREATER_PALADIN_HELMET || helmet == Helmets.MASTERWORK_PALADIN_HELMET || helmet == Helmets.LEGENDARY_PALADIN_HELMET) {
                            Helmets.setSelectedPaladin(player, helmet);
                        } else if (helmet == Helmets.SIMPLE_SHAMAN_HELMET || helmet == Helmets.GREATER_SHAMAN_HELMET || helmet == Helmets.MASTERWORK_SHAMAN_HELMET || helmet == Helmets.LEGENDARY_SHAMAN_HELMET) {
                            Helmets.setSelectedShaman(player, helmet);
                        }
                        openArmorMenu(player, pageNumber);
                    }
            );
        }
        List<ArmorSets> armorSets = Arrays.asList(ArmorSets.values());
        int xPosition = 1;
        for (int i = (pageNumber - 1) * 6; i < pageNumber * 6; i++) {
            ArmorSets armorSet = armorSets.get(i);
            ItemBuilder builder = new ItemBuilder(i % 3 == 0 ? ArmorSets.applyColor(armorSet.itemBlue, onBlueTeam) : armorSet.itemBlue)
                    .name(onBlueTeam ? ChatColor.BLUE + armorSet.name : ChatColor.RED + armorSet.name)
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(armorDescription);
            lore.add("");
            if (selectedArmorSet.contains(armorSet)) {
                lore.add(ChatColor.GREEN + ">>> ACTIVE <<<");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "> Click to activate! <");
            }
            builder.lore(lore);
            menu.setItem(
                    xPosition,
                    3,
                    builder.get(),
                    (n, e) -> {
                        player.sendMessage(ChatColor.YELLOW + "Selected: " + ChatColor.GREEN + armorSet.name);
                        if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_MAGE || armorSet == ArmorSets.GREATER_CHESTPLATE_MAGE || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_MAGE) {
                            ArmorSets.setSelectedMage(player, armorSet);
                        } else if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_WARRIOR || armorSet == ArmorSets.GREATER_CHESTPLATE_WARRIOR || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_WARRIOR) {
                            ArmorSets.setSelectedWarrior(player, armorSet);
                        } else if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_PALADIN || armorSet == ArmorSets.GREATER_CHESTPLATE_PALADIN || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_PALADIN) {
                            ArmorSets.setSelectedPaladin(player, armorSet);
                        } else if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_SHAMAN || armorSet == ArmorSets.GREATER_CHESTPLATE_SHAMAN || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_SHAMAN) {
                            ArmorSets.setSelectedShaman(player, armorSet);
                        }
                        openArmorMenu(player, pageNumber);
                    }
            );
            if (xPosition == 3) {
                xPosition += 2;
            } else {
                xPosition++;
            }
        }

        if (pageNumber == 1) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (n, e) -> openArmorMenu(player, pageNumber + 1));
        } else if (pageNumber == 2) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (n, e) -> openArmorMenu(player, pageNumber - 1));
        }

        menu.setItem(4, 5, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openSettingsMenu(Player player) {
        Powerup selectedPowerup = Powerup.getSelected(player);
        HotkeyMode selectedHotkeyMode = HotkeyMode.getSelected(player);

        Menu menu = new Menu("Settings", 9 * 6);
//        menu.setItem(
//                1,
//                1,
//                new ItemBuilder(selectedPowerup.item)
//                        .name(Settings.powerupsName)
//                        .lore(Settings.powerupsDescription, "", selectedPowerup == Powerup.ENERGY ? ChatColor.GREEN + ">>> ACTIVE <<<" : ChatColor.YELLOW + "> Click to activate! <")
//                        .flags(ItemFlag.HIDE_ENCHANTS)
//                        .get(),
//                (n, e) -> {
//                    player.sendMessage(selectedPowerup == Powerup.DAMAGE ? ChatColor.GREEN + "You have enabled energy powerups!" : ChatColor.RED + "You have disabled energy powerups!");
//                    Powerup.setSelected(player, selectedPowerup == Powerup.DAMAGE ? Powerup.ENERGY : Powerup.DAMAGE);
//                    openSettingsMenu(player);
//                }
//        );
        menu.setItem(
                3,
                1,
                selectedHotkeyMode.item,
                (n, e) -> {
                    player.sendMessage(selectedHotkeyMode == HotkeyMode.NEW_MODE ? ChatColor.GREEN + "Hotkey Mode " + ChatColor.AQUA + "Classic " + ChatColor.GREEN + "enabled." : ChatColor.GREEN + "Hotkey Mode " + ChatColor.YELLOW + "NEW " + ChatColor.GREEN + "enabled.");
                    HotkeyMode.setSelected(player, selectedHotkeyMode == HotkeyMode.NEW_MODE ? HotkeyMode.CLASSIC_MODE : HotkeyMode.NEW_MODE);
                    openSettingsMenu(player);
                }
        );

        menu.setItem(1, 3, MENU_SETTINGS_PARTICLE_QUALITY, (n, e) -> openParticleQualityMenu(player));
        menu.setItem(4, 5, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openParticleQualityMenu(Player player) {
        ParticleQuality selectedParticleQuality = ParticleQuality.getSelected(player);

        Menu menu = new Menu("Particle Quality", 9 * 4);

        ParticleQuality[] particleQualities = ParticleQuality.values();
        for (int i = 0; i < particleQualities.length; i++) {
            ParticleQuality particleQuality = particleQualities[i];

            menu.setItem(
                    i + 3,
                    1,
                    new ItemBuilder(particleQuality.item)
                            .lore(particleQuality.description, "", selectedParticleQuality == particleQuality ? ChatColor.GREEN + "SELECTED" : ChatColor.YELLOW + "Click to select!")
                            .flags(ItemFlag.HIDE_ENCHANTS)
                            .get(),
                    (n, e) -> {
                        player.sendMessage(ChatColor.GREEN + "Particle quality set to " + particleQuality.name());
                        ParticleQuality.setSelected(player, particleQuality);
                        openParticleQualityMenu(player);
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openTeamMenu(Player player) {
        Team selectedTeam = Warlords.getPlayerSettings(player.getUniqueId()).wantedTeam();
        Menu menu = new Menu("Team Selector", 9 * 4);
        List<Team> values = new ArrayList<>(Arrays.asList(Team.values()));
        for (int i = 0; i < values.size(); i++) {
            Team team = values.get(i);
            ItemBuilder builder = new ItemBuilder(team.item)
                    .name(team.teamColor() + team.name)
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            if (team == selectedTeam) {
                lore.add(ChatColor.GREEN + "Currently selected!");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "Click to select!");
            }
            builder.lore(lore);
            menu.setItem(
                    9 / 2 - values.size() % 2 + i * 2 - 1,
                    1,
                    builder.get(),
                    (n, e) -> {
                        if (selectedTeam != team) {
                            player.sendMessage(ChatColor.GREEN + "You have joined the " + team.teamColor() + team.name + ChatColor.GREEN + " team!");
                            Warlords.game.setPlayerTeam(player, team);
                            ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).selectedClass(), team);
                            Warlords.getPlayerSettings(player.getUniqueId()).wantedTeam(team);
                        }
                        openTeamMenu(player);
                    }
            );
        }

        menu.setItem(4, 3, Menu.MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSkillTreeMenu(Player player) {
        Menu menu = new Menu("Skill Tree", 9 * 6);
        menu.setItem(1, 1,
                new ItemBuilder(getClassesGroup(getSelected(player)).item)
                        .name(ChatColor.GREEN + "Class Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        menu.setItem(3, 1,
                new ItemBuilder(Weapons.getSelected(player).item)
                        .name(ChatColor.GREEN + "Weapon Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        menu.setItem(5, 1,
                new ItemBuilder(Material.BANNER)
                        .name(ChatColor.GREEN + "Flag Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        menu.setItem(7, 1,
                new ItemBuilder(Material.GOLD_BARDING)
                        .name(ChatColor.GREEN + "Horse Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        Dye redDye = new Dye();
        redDye.setColor(DyeColor.RED);
        menu.setItem(1, 3,
                new ItemBuilder(redDye.toItemStack(1))
                        .name(ChatColor.GREEN + "Red Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        menu.setItem(3, 3,
                new ItemBuilder(Material.GLOWSTONE_DUST)
                        .name(ChatColor.GREEN + "Purple Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        Dye limeDye = new Dye();
        limeDye.setColor(DyeColor.LIME);
        menu.setItem(5, 3,
                new ItemBuilder(limeDye.toItemStack(1))
                        .name(ChatColor.GREEN + "Blue Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        Dye orangeDye = new Dye();
        orangeDye.setColor(DyeColor.ORANGE);
        menu.setItem(7, 3,
                new ItemBuilder(orangeDye.toItemStack(1))
                        .name(ChatColor.GREEN + "Orange Upgrades")
                        .get(),
                (n, e) -> {
                }
        );
        menu.setItem(4, 5, Menu.MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }
}