package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.Utils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Math.round;

import static com.ebicep.warlords.menu.Menu.ACTION_CLOSE_MENU;
import static com.ebicep.warlords.menu.Menu.ACTION_DO_NOTHING;
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
    private static final ItemStack MENU_ABILITY_DESCRIPTION = new ItemBuilder(Material.BOOK)
            .name(ChatColor.GREEN + "Class Information")
            .lore("§7Preview of your ability \ndescriptions and specialization \nstats.")
            .get();
    private static final ItemStack MENU_ARCADE = new ItemBuilder(Material.GOLD_BLOCK)
            .name(ChatColor.GREEN + "Mini Games")
            .lore("§7Try your luck in rerolling or\nopening skin shards here!\n")
            .get();

    private static final String[] legendaryNames = new String[]{"Warlord", "Vanquisher", "Champion"};
    private static final String[] mythicNames = new String[]{"Mythical", "Ascendant", "Brilliant"};
    private static final Map<WeaponsRarity, List<Weapons>> weaponByRarity = Stream.of(Weapons.values()).collect(Collectors.groupingBy(Weapons::getRarity));
    private static final Random random = new Random();
    private static final Map<UUID, Long> openWeaponCooldown = new HashMap<>();

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
            long experience = ExperienceManager.getExperienceForClass(player.getUniqueId(), group);
            int level = (int) ExperienceManager.calculateLevelFromExp(experience);
            lore.add(ExperienceManager.getProgressString(experience, level + 1));
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click here to select a " + group.name + "\n" + ChatColor.YELLOW + "specialization");
            ItemStack item = new ItemBuilder(group.item)
                    .name(ChatColor.GOLD + group.name + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" +  ExperienceManager.getLevelString(level) + ChatColor.DARK_GRAY + "]")
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
        menu.setItem(4, 2, MENU_ABILITY_DESCRIPTION, (n, e) -> openLobbyAbilityMenu(player));
        menu.setItem(4, 4, MENU_ARCADE, (n, e) -> openArcadeMenu(player));
        menu.openForPlayer(player);
    }

    public static void openClassMenu(Player player, ClassesGroup selectedGroup) {
        Classes selectedClass = getSelected(player);
        Menu menu = new Menu(selectedGroup.name, 9 * 4);
        List<Classes> values = selectedGroup.subclasses;
        for (int i = 0; i < values.size(); i++) {
            Classes subClass = values.get(i);
            ItemBuilder builder = new ItemBuilder(subClass.specType.itemStack)
                    .name(ChatColor.GREEN + "Specialization: " + subClass.name + " " + ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), subClass)) + ChatColor.DARK_GRAY + "]")
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(subClass.description);
            lore.add("");
            long experience = ExperienceManager.getExperienceForSpec(player.getUniqueId(), subClass);
            int level = (int) ExperienceManager.calculateLevelFromExp(experience);
            lore.add(ExperienceManager.getProgressString(experience, level + 1));
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
                        player.sendMessage(ChatColor.WHITE + "Spec: §6" + subClass);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                        setSelected(player, subClass);
                        ArmorManager.resetArmor(player, subClass, Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam());
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
        Classes selectedClass = getSelected(player);
        Weapons selectedWeapon = Weapons.getSelected(player, selectedClass);
        Menu menu = new Menu("Weapon Skin Selector", 9 * 6);
        List<Weapons> values = new ArrayList<>(Arrays.asList(Weapons.values()));
        for (int i = (pageNumber - 1) * 21; i < pageNumber * 21 && i < values.size(); i++) {
            Weapons weapon = values.get(i);
            ItemBuilder builder;

            if (weapon.isUnlocked) {

                builder = new ItemBuilder(weapon.item)
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
            } else {
                builder = new ItemBuilder(Material.BARRIER).name(ChatColor.RED + "Locked Weapon Skin");
            }

            menu.setItem(
                    (i - (pageNumber - 1) * 21) % 7 + 1,
                    (i - (pageNumber - 1) * 21) / 7 + 1,
                    builder.get(),
                    (n, e) -> {
                        if (weapon.isUnlocked) {
                            player.sendMessage(ChatColor.GREEN + "You have changed your " + ChatColor.AQUA + selectedClass.name + ChatColor.GREEN + "'s weapon skin to: §b" + weapon.name + "!");
                            Weapons.setSelected(player, selectedClass, weapon);
                            openWeaponMenu(player, pageNumber);
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            AbstractPlayerClass apc = selectedClass.create.get();
                            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                                    .getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item)).name("§aWeapon Skin Preview")
                                    .lore("")
                                    .get());
                        } else {
                            player.sendMessage(ChatColor.RED + "This weapon skin has not been unlocked yet!");
                        }
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

        Menu menu = new Menu("Settings", 9 * 4);
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
        menu.setItem(
                1,
                1,
                MENU_SETTINGS_PARTICLE_QUALITY,
                (n, e) -> openParticleQualityMenu(player)
        );

        menu.setItem(4, 3, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
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
                        Bukkit.getServer().dispatchCommand(player, "pq " + particleQuality.name());
                        openParticleQualityMenu(player);
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openTeamMenu(Player player) {
        Team selectedTeam = Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam();
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
                            ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedClass(), team);
                            Warlords.getPlayerSettings(player.getUniqueId()).setWantedTeam(team);
                        }
                        openTeamMenu(player);
                    }
            );
        }

        menu.setItem(4, 3, Menu.MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openLobbyAbilityMenu(Player player) {
        Menu menu = new Menu("Class Information", 9);
        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
        Classes selectedClass = playerSettings.getSelectedClass();
        AbstractPlayerClass apc = selectedClass.create.get();

        ItemBuilder icon = new ItemBuilder(selectedClass.specType.itemStack);
        icon.name(ChatColor.GREEN + selectedClass.name);
        icon.lore(
                selectedClass.description,
                "",
                "§6Specialization Stats:",
                "",
                "§7Health: §a" + apc.getMaxHealth(),
                "§7Energy: §a" + apc.getMaxEnergy() + " §7/ §a+" + apc.getEnergyPerSec() + " §7per sec §7/ §a+" + apc.getEnergyOnHit() + " §7per hit",
                "",
                "§7Damage Reduction: §e" + apc.getDamageResistance() + "%"
        );

        ClassesSkillBoosts selectedBoost = playerSettings.getClassesSkillBoosts();
        if (apc.getWeapon().getClass() == selectedBoost.ability) {
            if (selectedBoost != ClassesSkillBoosts.PROTECTOR_STRIKE) {
                apc.getWeapon().boostSkill();
            }
        } else if (apc.getRed().getClass() == selectedBoost.ability) {
            apc.getRed().boostSkill();
        } else if (apc.getPurple().getClass() == selectedBoost.ability) {
            apc.getPurple().boostSkill();
        } else if (apc.getBlue().getClass() == selectedBoost.ability) {
            apc.getBlue().boostSkill();
        } else if (apc.getOrange().getClass() == selectedBoost.ability) {
            apc.getOrange().boostOrange();
        }

        apc.getWeapon().updateDescription(player);
        apc.getRed().updateDescription(player);
        apc.getPurple().updateDescription(player);
        apc.getBlue().updateDescription(player);
        apc.getOrange().updateDescription(player);

        menu.setItem(0, icon.get(), ACTION_DO_NOTHING);
        menu.setItem(2, apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedClass, Weapons.FELFLAME_BLADE).item), ACTION_DO_NOTHING);
        menu.setItem(3, apc.getRed().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 1)), ACTION_DO_NOTHING);
        menu.setItem(4, apc.getPurple().getItem(new ItemStack(Material.GLOWSTONE_DUST)), ACTION_DO_NOTHING);
        menu.setItem(5, apc.getBlue().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 10)), ACTION_DO_NOTHING);
        menu.setItem(6, apc.getOrange().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 14)), ACTION_DO_NOTHING);
        menu.setItem(8, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));

        menu.openForPlayer(player);
    }

    private static double map(double value, double min, double max) {
        return value * (max - min) + min;
    }

    public static void openArcadeMenu(Player player) {
        Menu menu = new Menu("Mini Games", 9 * 4);

        ItemBuilder icon = new ItemBuilder(Material.GOLD_INGOT);
        icon.name(ChatColor.GREEN + "Weapon Roller");
        icon.lore(
                "§7Is RNG with you today?"
        );

        menu.setItem(3, 1, icon.get(), (m, e) -> {
            double difficulty = 1;
            double base = random.nextDouble() * (1 - difficulty);

            double meleeDamageMin = random.nextDouble() * difficulty + base;
            double meleeDamageMax = random.nextDouble() * difficulty + base;
            double critChance = random.nextDouble() * difficulty + base;
            double critMultiplier = random.nextDouble() * difficulty + base;
            double skillBoost = random.nextDouble() * difficulty + base;
            double health = random.nextDouble() * difficulty + base;
            double energy = random.nextDouble() * difficulty + base;
            double cooldown = random.nextDouble() * difficulty + base;
            double speed = random.nextDouble() * difficulty + base;

            double score =
                    (
                            meleeDamageMin +
                                    meleeDamageMax +
                                    critChance +
                                    critMultiplier +
                                    skillBoost +
                                    health +
                                    energy +
                                    cooldown +
                                    speed
                    ) / 9;

            meleeDamageMin = map(meleeDamageMin, 122, 132);
            meleeDamageMax = map(meleeDamageMax, 166, 179);
            critChance = map(critChance, 15, 25);
            critMultiplier = map(critMultiplier, 180, 200);
            skillBoost = map(skillBoost, 13, 20);
            health = map(health, 500, 800);
            energy = map(energy, 30, 35);
            cooldown = map(cooldown, 7, 13);
            speed = map(speed, 7, 13);

            if (meleeDamageMin > meleeDamageMax) {
                double temp = meleeDamageMin;
                meleeDamageMin = meleeDamageMax;
                meleeDamageMax = temp;
            }

            String displayScore = "§7Your weapon score is §a" + Utils.formatOptionalTenths(score * 100);

            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
            Classes selectedClass = playerSettings.getSelectedClass();
            AbstractPlayerClass apc = selectedClass.create.get();

            ItemStack weapon = new ItemStack(Weapons.FELFLAME_BLADE.item);
            ItemMeta weaponMeta = weapon.getItemMeta();
            weaponMeta.setDisplayName("§6Warlord's Felflame of the " + apc.getWeapon().getName());
            ArrayList<String> weaponLore = new ArrayList<>();
            weaponLore.add("§7Damage: §c" + round(meleeDamageMin) + "§7-§c" + round(meleeDamageMax));
            weaponLore.add("§7Crit Chance: §c" + round(critChance) + "%");
            weaponLore.add("§7Crit Multiplier: §c" + round(critMultiplier) + "%");
            weaponLore.add("");
            String classNamePath = apc.getClass().getGenericSuperclass().getTypeName();
            weaponLore.add("§a" + classNamePath.substring(classNamePath.indexOf("Abstract") + 8) + " (" + apc.getClass().getSimpleName() + "):");
            weaponLore.add("§aIncreases the damage you");
            weaponLore.add("§adeal with " + apc.getWeapon().getName() + " by §c" + round(skillBoost) + "%");
            weaponLore.add("");
            weaponLore.add("§7Health: §a+" + round(health));
            weaponLore.add("§7Max Energy: §a+" + round(energy));
            weaponLore.add("§7Cooldown Reduction: §a+" + round(cooldown) + "%");
            weaponLore.add("§7Speed: §a+" + round(speed) + "%");
            weaponLore.add("");
            weaponLore.add("§3CRAFTED");
            weaponLore.add("");
            weaponLore.add(displayScore);
            weaponLore.add("");
            weaponLore.add("§7Left-click to roll again!");
            weaponMeta.setLore(weaponLore);
            weapon.setItemMeta(weaponMeta);
            m.getInventory().setItem(e.getRawSlot(), weapon);

            if (score > 0.85) {
                Bukkit.broadcastMessage("§6" + player.getDisplayName() + " §frolled a weapon with a total score of §6" + Utils.formatOptionalTenths(score * 100) + "§f!");
            }

            if (score < 0.15) {
                Bukkit.broadcastMessage("§6" + player.getDisplayName() + " §frolled a weapon with a total score of §c" + Utils.formatOptionalTenths(score * 100) + "§f!");
            }
        });

        ItemBuilder icon2 = new ItemBuilder(Material.SULPHUR);
        icon2.name(ChatColor.GREEN + "Skin Shard Roller");
        icon2.lore(
                "§7Is RNG with you to give everyone a new awesome skin?",
                "",
                "§7Left-click to roll 10 skin shards!"
        );

        menu.setItem(5, 1, icon2.get(), (m, e) -> {

            Long weaponCooldown = openWeaponCooldown.get(player.getUniqueId());

            Map<WeaponsRarity, Integer> foundWeaponCount = new EnumMap<>(WeaponsRarity.class);

            for(WeaponsRarity rarity : WeaponsRarity.values()) {
                foundWeaponCount.put(rarity, 0);
            }

            if (Bukkit.getOnlinePlayers().size() >= 16) {

                if (weaponCooldown == null || weaponCooldown < System.currentTimeMillis()) {
                    openWeaponCooldown.put(player.getUniqueId(), System.currentTimeMillis() + 8 * 60 * 1000);
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                    for (int i = 0; i < 10; i++) {
                        String legendaryName = legendaryNames[random.nextInt(legendaryNames.length)];
                        String mythicName = mythicNames[random.nextInt(mythicNames.length)];

                        double chance = random.nextDouble() * 100;

                        WeaponsRarity rarity;

                        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                        Classes selectedClass = playerSettings.getSelectedClass();

                        if (chance < 96.32) {
                            rarity = WeaponsRarity.RARE;
                        } else if (chance < 96.32 + 3) {
                            rarity = WeaponsRarity.EPIC;
                        } else if (chance < 96.32 + 3 + 0.6) {
                            rarity = WeaponsRarity.LEGENDARY;
                        } else {
                            rarity = WeaponsRarity.MYTHIC;
                        }

                        foundWeaponCount.compute(rarity, (key, value) -> value == null ? 1 : value + 1);
                        List<Weapons> weapons = weaponByRarity.get(rarity);

                        Weapons weapon = weapons.get(random.nextInt(weapons.size()));
                        String message = rarity.getWeaponChatColor() + legendaryName + "'s " + weapon.getName() + " of the " + selectedClass.name;
                        String mythicMessage = rarity.getWeaponChatColor() + "§l" + mythicName + " " + weapon.getName() + " of the " + selectedClass.name;

                        if (rarity == WeaponsRarity.EPIC) {
                            Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " §fgot lucky and found " + message);
                        }

                        if (rarity == WeaponsRarity.LEGENDARY) {
                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(player.getLocation(), "legendaryfind", 1, 1);
                            }
                            Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " §fgot lucky and found " + message);
                            player.getWorld().spigot().strikeLightningEffect(player.getLocation(), false);
                        }

                        if (rarity == WeaponsRarity.MYTHIC) {
                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(player.getLocation(), "legendaryfind", 500, 0.8f);
                                player1.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 500, 0.8f);
                            }
                            Bukkit.broadcastMessage(ChatColor.AQUA + player.getDisplayName() + " §fgot lucky and found " + mythicMessage);

                            for (int j = 0; j < 10; j++) {
                                player.getWorld().spigot().strikeLightningEffect(player.getLocation(), false);
                            }
                        }

                        if (!weapon.isUnlocked) {
                            weapon.isUnlocked = true;
                            Warlords.getInstance().saveWeaponConfig();
                            Bukkit.broadcastMessage("");
                            Bukkit.broadcastMessage("§l" + rarity.getWeaponChatColor() + weapon.getName() + " §l§fis now unlocked for everyone!");
                            Bukkit.broadcastMessage("");
                        } else {
                            if (rarity == WeaponsRarity.MYTHIC) {
                                Bukkit.broadcastMessage("");
                                Bukkit.broadcastMessage("§l" + rarity.getWeaponChatColor() + weapon.getName() + " §fwas already found! Unlucky!");
                                Bukkit.broadcastMessage("");
                            }
                        }
                    }

                    player.sendMessage("");
                    player.sendMessage("§7You found:");
                    player.sendMessage("§7Rare: §9" + foundWeaponCount.get(WeaponsRarity.RARE));
                    player.sendMessage("§7Epic: §5" + foundWeaponCount.get(WeaponsRarity.EPIC));
                    player.sendMessage("§7Legendary: §6" + foundWeaponCount.get(WeaponsRarity.LEGENDARY));
                    player.sendMessage("§7Mythic: §c" + foundWeaponCount.get(WeaponsRarity.MYTHIC));
                } else {
                    long remainingTime = (weaponCooldown - System.currentTimeMillis()) / 1000;
                    long remainingTimeinMinutes = remainingTime / 60;
                    player.sendMessage(ChatColor.RED + "Please wait " + (remainingTime > 60 ? remainingTimeinMinutes + " minutes" : remainingTime + " seconds") + " before opening weapons again!");
                }
            } else {
                player.sendMessage(ChatColor.RED + "There must be at least 16 players online to roll skin shards!");
            }
        });

        menu.setItem(4, 3, MENU_BACK_PREGAME, (n, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }
}