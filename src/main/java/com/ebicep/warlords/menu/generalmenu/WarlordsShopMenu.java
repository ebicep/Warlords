package com.ebicep.warlords.menu.generalmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.MapSymmetryMarker;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.player.general.ArmorManager.*;
import static com.ebicep.warlords.player.general.Settings.*;
import static com.ebicep.warlords.player.general.Specializations.APOTHECARY;

public class WarlordsShopMenu {
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

    public static void openMainMenu(Player player) {
        Specializations selectedSpec = Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec();

        Menu menu = new Menu("Warlords Shop", 9 * 6);
        Classes[] values = Classes.values();
        for (int i = 0; i < values.length; i++) {
            Classes group = values[i];
            List<String> lore = new ArrayList<>();
            lore.add(group.description);
            lore.add("");
            lore.add(ChatColor.GOLD + "Specializations:");
            for (Specializations subClass : group.subclasses) {
                lore.add((subClass == selectedSpec ? ChatColor.GREEN : ChatColor.GRAY) + subClass.name);
            }
            lore.add("");
            long experience = ExperienceManager.getExperienceForClass(player.getUniqueId(), group);
            int level = (int) ExperienceManager.calculateLevelFromExp(experience);
            lore.add(ExperienceManager.getProgressString(experience, level + 1));
            lore.add("");
            lore.add(ChatColor.YELLOW + "Click here to select a " + group.name + "\n" + ChatColor.YELLOW + "specialization");
            ItemStack item = new ItemBuilder(group.item)
                    .name(ChatColor.GOLD + group.name + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(level) + ChatColor.DARK_GRAY + "]")
                    .lore(lore)
                    .get();
            menu.setItem(
                    9 / 2 - values.length / 2 + i * 2 - 2,
                    1,
                    item,
                    (m, e) -> {
                        openClassMenu(player, group);
                    }
            );
        }
        menu.setItem(1, 3, MENU_SKINS, (m, e) -> openWeaponMenu(player, 1));
        menu.setItem(3, 3, MENU_ARMOR_SETS, (m, e) -> openArmorMenu(player, 1));
        menu.setItem(5, 3, MENU_BOOSTS, (m, e) -> openSkillBoostMenu(player, selectedSpec));
        menu.setItem(7, 3, MENU_SETTINGS, (m, e) -> openSettingsMenu(player));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.setItem(4, 2, MENU_ABILITY_DESCRIPTION, (m, e) -> openLobbyAbilityMenu(player));
        menu.openForPlayer(player);
    }

    public static void openClassMenu(Player player, Classes selectedGroup) {
        Specializations selectedSpec = Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec();
        Menu menu = new Menu(selectedGroup.name, 9 * 4);
        List<Specializations> values = selectedGroup.subclasses;
        for (int i = 0; i < values.size(); i++) {
            Specializations spec = values.get(i);
            ItemBuilder builder = new ItemBuilder(spec.specType.itemStack)
                    .name(ChatColor.GREEN + "Specialization: " + spec.name + " " + ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + ExperienceManager.getLevelString(ExperienceManager.getLevelForSpec(player.getUniqueId(), spec)) + ChatColor.DARK_GRAY + "] " + ExperienceManager.getPrestigeLevelString(player.getUniqueId(), spec))
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(spec.description);
            lore.add("");
            long experience = ExperienceManager.getExperienceForSpec(player.getUniqueId(), spec);
            int level = (int) ExperienceManager.calculateLevelFromExp(experience);
            lore.add(ExperienceManager.getProgressString(experience, level + 1));
            lore.add("");
            if (spec == selectedSpec) {
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
                    (m, e) -> {
                        player.sendMessage(ChatColor.GREEN + "You have changed your specialization to: §b" + spec.name);
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                        ArmorManager.resetArmor(player, spec, Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam());
                        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                        playerSettings.setSelectedSpec(spec);

                        AbstractPlayerClass apc = spec.create.get();
                        player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                                .getOrDefault(spec, Weapons.FELFLAME_BLADE).getItem())).name("§aWeapon Skin Preview")
                                .lore("")
                                .get());

                        openClassMenu(player, selectedGroup);

                        if (DatabaseManager.playerService == null) return;
                        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                        databasePlayer.setLastSpec(spec);
                        DatabaseManager.updatePlayerAsync(databasePlayer);
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));

        menu.openForPlayer(player);
    }

    public static void openSkillBoostMenu(Player player, Specializations selectedSpec) {
        SkillBoosts selectedBoost = Warlords.getPlayerSettings(player.getUniqueId()).getSkillBoostForClass();
        Menu menu = new Menu("Skill Boost", 9 * 6);
        List<SkillBoosts> values = selectedSpec.skillBoosts;
        for (int i = 0; i < values.size(); i++) {
            SkillBoosts skillBoost = values.get(i);
            ItemBuilder builder = new ItemBuilder(selectedSpec.specType.itemStack)
                    .name(skillBoost == selectedBoost ? ChatColor.GREEN + skillBoost.name + " (" + selectedSpec.name + ")" : ChatColor.RED + skillBoost.name + " (" + selectedSpec.name + ")")
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            lore.add(skillBoost == selectedBoost ? skillBoost.selectedDescription : skillBoost.description);
            lore.add("");
            if (skillBoost == selectedBoost) {
                lore.add(ChatColor.GREEN + "Currently selected!");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "Click to select!");
            }
            builder.lore(lore);
            menu.setItem(
                    i + 2,
                    3,
                    builder.get(),
                    (m, e) -> {
                        player.sendMessage(ChatColor.GREEN + "You have changed your weapon boost to: §b" + skillBoost.name + "!");
                        Warlords.getPlayerSettings(player.getUniqueId()).setSkillBoostForSelectedSpec(skillBoost);
                        openSkillBoostMenu(player, selectedSpec);

                        if (DatabaseManager.playerService == null) return;
                        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                        databasePlayer.getSpec(selectedSpec).setSkillBoost(skillBoost);
                        DatabaseManager.updatePlayerAsync(databasePlayer);
                    }
            );
        }

        //showing change of ability
        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
        AbstractPlayerClass apc = selectedSpec.create.get();
        AbstractPlayerClass apc2 = selectedSpec.create.get();
        if (apc2.getWeapon().getClass() == selectedBoost.ability) {
            apc2.getWeapon().boostSkill(selectedBoost, apc2);
            apc.getWeapon().updateDescription(player);
            apc2.getWeapon().updateDescription(player);
            menu.setItem(3, 1, apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()), ACTION_DO_NOTHING);
            menu.setItem(5, 1, apc2.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()), ACTION_DO_NOTHING);
        } else if (apc2.getRed().getClass() == selectedBoost.ability) {
            apc2.getRed().boostSkill(selectedBoost, apc2);
            apc.getRed().updateDescription(player);
            apc2.getRed().updateDescription(player);
            menu.setItem(3, 1, apc.getRed().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 1)), ACTION_DO_NOTHING);
            menu.setItem(5, 1, apc2.getRed().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 1)), ACTION_DO_NOTHING);
        } else if (apc2.getPurple().getClass() == selectedBoost.ability) {
            apc2.getPurple().boostSkill(selectedBoost, apc2);
            apc.getPurple().updateDescription(player);
            apc2.getPurple().updateDescription(player);
            menu.setItem(3, 1, apc.getPurple().getItem(new ItemStack(Material.GLOWSTONE_DUST)), ACTION_DO_NOTHING);
            menu.setItem(5, 1, apc2.getPurple().getItem(new ItemStack(Material.GLOWSTONE_DUST)), ACTION_DO_NOTHING);
        } else if (apc2.getBlue().getClass() == selectedBoost.ability) {
            apc2.getBlue().boostSkill(selectedBoost, apc2);
            apc.getBlue().updateDescription(player);
            apc2.getBlue().updateDescription(player);
            menu.setItem(3, 1, apc.getBlue().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 10)), ACTION_DO_NOTHING);
            menu.setItem(5, 1, apc2.getBlue().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 10)), ACTION_DO_NOTHING);
        } else if (apc2.getOrange().getClass() == selectedBoost.ability) {
            apc2.getOrange().boostSkill(selectedBoost, apc2);
            apc.getOrange().updateDescription(player);
            apc2.getOrange().updateDescription(player);
            menu.setItem(3, 1, apc.getOrange().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 14)), ACTION_DO_NOTHING);
            menu.setItem(5, 1, apc2.getOrange().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 14)), ACTION_DO_NOTHING);
        }
        menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openWeaponMenu(Player player, int pageNumber) {
        Specializations selectedSpec = Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec();
        Weapons selectedWeapon = Weapons.getSelected(player, selectedSpec);
        Menu menu = new Menu("Weapon Skin Selector", 9 * 6);
        List<Weapons> values = new ArrayList<>(Arrays.asList(Weapons.values()));
        for (int i = (pageNumber - 1) * 21; i < pageNumber * 21 && i < values.size(); i++) {
            Weapons weapon = values.get(i);
            ItemBuilder builder;

            if (weapon.isUnlocked) {

                builder = new ItemBuilder(weapon.getItem())
                        .name(ChatColor.GREEN + weapon.getName())
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
                    (m, e) -> {
                        if (weapon.isUnlocked) {
                            player.sendMessage(ChatColor.GREEN + "You have changed your " + ChatColor.AQUA + selectedSpec.name + ChatColor.GREEN + "'s weapon skin to: §b" + weapon.getName() + "!");
                            Weapons.setSelected(player, selectedSpec, weapon);
                            openWeaponMenu(player, pageNumber);
                            PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
                            AbstractPlayerClass apc = selectedSpec.create.get();
                            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                                    .getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem())).name("§aWeapon Skin Preview")
                                    .lore("")
                                    .get());

                            if (DatabaseManager.playerService == null) return;
                            DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                            databasePlayer.getSpec(selectedSpec).setWeapon(weapon);
                            DatabaseManager.updatePlayerAsync(databasePlayer);
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
                    (m, e) -> openWeaponMenu(player, pageNumber + 1));
        } else if (pageNumber == 2) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openWeaponMenu(player, pageNumber - 1));
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openWeaponMenu(player, pageNumber + 1));
        } else if (pageNumber == 3) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openWeaponMenu(player, pageNumber - 1));
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openWeaponMenu(player, pageNumber + 1));
        } else if (pageNumber == 4) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openWeaponMenu(player, pageNumber - 1));
        }

        menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openArmorMenu(Player player, int pageNumber) {
        boolean onBlueTeam = Warlords.getGameManager().getPlayerGame(player.getUniqueId()).map(g -> g.getPlayerTeam(player.getUniqueId())).orElse(Team.BLUE) == Team.BLUE;
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
                    (m, e) -> {
                        player.sendMessage(ChatColor.YELLOW + "Selected: " + ChatColor.GREEN + helmet.name);
                        if (
                                helmet == Helmets.SIMPLE_MAGE_HELMET ||
                                        helmet == Helmets.GREATER_MAGE_HELMET ||
                                        helmet == Helmets.MASTERWORK_MAGE_HELMET ||
                                        helmet == Helmets.LEGENDARY_MAGE_HELMET
                        ) {
                            Helmets.setSelectedMage(player, helmet);
                        } else if (
                                helmet == Helmets.SIMPLE_WARRIOR_HELMET ||
                                helmet == Helmets.GREATER_WARRIOR_HELMET ||
                                helmet == Helmets.MASTERWORK_WARRIOR_HELMET ||
                                helmet == Helmets.LEGENDARY_WARRIOR_HELMET
                        ) {
                            Helmets.setSelectedWarrior(player, helmet);
                        } else if (
                                helmet == Helmets.SIMPLE_PALADIN_HELMET ||
                                helmet == Helmets.GREATER_PALADIN_HELMET ||
                                helmet == Helmets.MASTERWORK_PALADIN_HELMET ||
                                helmet == Helmets.LEGENDARY_PALADIN_HELMET
                        ) {
                            Helmets.setSelectedPaladin(player, helmet);
                        } else if (
                                helmet == Helmets.SIMPLE_SHAMAN_HELMET ||
                                helmet == Helmets.GREATER_SHAMAN_HELMET ||
                                helmet == Helmets.MASTERWORK_SHAMAN_HELMET ||
                                helmet == Helmets.LEGENDARY_SHAMAN_HELMET
                        ) {
                            Helmets.setSelectedShaman(player, helmet);
                        } else if (
                                helmet == Helmets.SIMPLE_ROGUE_HELMET ||
                                        helmet == Helmets.GREATER_ROGUE_HELMET ||
                                        helmet == Helmets.MASTERWORK_ROGUE_HELMET ||
                                        helmet == Helmets.LEGENDARY_ROGUE_HELMET
                        ) {
                            Helmets.setSelectedRogue(player, helmet);
                        }
                        ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec(), Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam());

                        openArmorMenu(player, pageNumber);

                        if (DatabaseManager.playerService == null) return;
                        List<Helmets> selectedHelmets = Helmets.getSelected(player);
                        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                        databasePlayer.getMage().setHelmet(selectedHelmets.get(0));
                        databasePlayer.getWarrior().setHelmet(selectedHelmets.get(1));
                        databasePlayer.getPaladin().setHelmet(selectedHelmets.get(2));
                        databasePlayer.getShaman().setHelmet(selectedHelmets.get(3));
                        databasePlayer.getRogue().setHelmet(selectedHelmets.get(4));
                        DatabaseManager.updatePlayerAsync(databasePlayer);
                    }
            );
        }
        List<ArmorSets> armorSets = Arrays.asList(ArmorSets.values());
        int xPosition = 1;
        for (int i = (pageNumber - 1) * 6; i < pageNumber * 6; i++) {
            if (pageNumber == 3 && i == 15) {
                break;
            }
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
                    (m, e) -> {
                        player.sendMessage(ChatColor.YELLOW + "Selected: " + ChatColor.GREEN + armorSet.name);
                        if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_MAGE || armorSet == ArmorSets.GREATER_CHESTPLATE_MAGE || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_MAGE) {
                            ArmorSets.setSelectedMage(player, armorSet);
                        } else if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_WARRIOR || armorSet == ArmorSets.GREATER_CHESTPLATE_WARRIOR || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_WARRIOR) {
                            ArmorSets.setSelectedWarrior(player, armorSet);
                        } else if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_PALADIN || armorSet == ArmorSets.GREATER_CHESTPLATE_PALADIN || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_PALADIN) {
                            ArmorSets.setSelectedPaladin(player, armorSet);
                        } else if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_SHAMAN || armorSet == ArmorSets.GREATER_CHESTPLATE_SHAMAN || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_SHAMAN) {
                            ArmorSets.setSelectedShaman(player, armorSet);
                        } else if (armorSet == ArmorSets.SIMPLE_CHESTPLATE_ROGUE || armorSet == ArmorSets.GREATER_CHESTPLATE_ROGUE || armorSet == ArmorSets.MASTERWORK_CHESTPLATE_ROGUE) {
                            ArmorSets.setSelectedRogue(player, armorSet);
                        }

                        openArmorMenu(player, pageNumber);

                        if (DatabaseManager.playerService == null) return;
                        List<ArmorSets> armorSetsList = ArmorSets.getSelected(player);
                        DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(player.getUniqueId());
                        databasePlayer.getMage().setArmor(armorSetsList.get(0));
                        databasePlayer.getWarrior().setArmor(armorSetsList.get(1));
                        databasePlayer.getPaladin().setArmor(armorSetsList.get(2));
                        databasePlayer.getShaman().setArmor(armorSetsList.get(3));
                        databasePlayer.getRogue().setArmor(armorSetsList.get(4));
                        DatabaseManager.updatePlayerAsync(databasePlayer);
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
                    (m, e) -> openArmorMenu(player, pageNumber + 1));
        } else if (pageNumber == 2) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openArmorMenu(player, pageNumber + 1));
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openArmorMenu(player, pageNumber - 1));
        } else if (pageNumber == 3) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openArmorMenu(player, pageNumber - 1));
        }

        menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openSettingsMenu(Player player) {
        Powerup selectedPowerup = Powerup.getSelected(player);
        HotkeyMode selectedHotkeyMode = HotkeyMode.getSelected(player);

        Menu menu = new Menu("Settings", 9 * 4);
        menu.setItem(
                3,
                1,
                selectedHotkeyMode.item,
                (m, e) -> {
                    player.sendMessage(selectedHotkeyMode == HotkeyMode.NEW_MODE ? ChatColor.GREEN + "Hotkey Mode " + ChatColor.AQUA + "Classic " + ChatColor.GREEN + "enabled." : ChatColor.GREEN + "Hotkey Mode " + ChatColor.YELLOW + "NEW " + ChatColor.GREEN + "enabled.");
                    HotkeyMode.setSelected(player, selectedHotkeyMode == HotkeyMode.NEW_MODE ? HotkeyMode.CLASSIC_MODE : HotkeyMode.NEW_MODE);
                    openSettingsMenu(player);
                }
        );
        menu.setItem(
                1,
                1,
                MENU_SETTINGS_PARTICLE_QUALITY,
                (m, e) -> openParticleQualityMenu(player)
        );

        menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
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
                    (m, e) -> {
                        Bukkit.getServer().dispatchCommand(player, "pq " + particleQuality.name());
                        openParticleQualityMenu(player);
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openTeamMenu(Player player) {
        Team selectedTeam = Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam();
        Menu menu = new Menu("Team Selector", 9 * 4);
        List<Team> values = new ArrayList<>(Arrays.asList(Team.values()));
        for (int i = 0; i < values.size(); i++) {
            Team team = values.get(i);
            ItemBuilder builder = new ItemBuilder(team.getItem())
                    .name(team.teamColor() + team.getName())
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
                    (m, e) -> {
                        if (selectedTeam != team) {
                            player.sendMessage(ChatColor.GREEN + "You have joined the " + team.teamColor() + team.getName() + ChatColor.GREEN + " team!");
                            Optional<Game> playerGame = Warlords.getGameManager().getPlayerGame(player.getUniqueId());
                            if (playerGame.isPresent()) {
                                Game game = playerGame.get();
                                Team oldTeam = game.getPlayerTeam(player.getUniqueId());
                                game.setPlayerTeam(player, team);
                                LobbyLocationMarker randomLobbyLocation = LobbyLocationMarker.getRandomLobbyLocation(game, team);
                                if (randomLobbyLocation != null) {
                                    Location teleportDestination = MapSymmetryMarker.getSymmetry(game)
                                            .getOppositeLocation(game, oldTeam, team, player.getLocation(), randomLobbyLocation.getLocation());
                                    player.teleport(teleportDestination);
                                    Warlords.setRejoinPoint(player.getUniqueId(), teleportDestination);
                                }
                            }
                            ArmorManager.resetArmor(player, Warlords.getPlayerSettings(player.getUniqueId()).getSelectedSpec(), team);
                            Warlords.getPlayerSettings(player.getUniqueId()).setWantedTeam(team);
                        }
                        openTeamMenu(player);
                    }
            );
        }

        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openLobbyAbilityMenu(Player player) {
        Menu menu = new Menu("Class Information", 9);
        PlayerSettings playerSettings = Warlords.getPlayerSettings(player.getUniqueId());
        Specializations selectedSpec = playerSettings.getSelectedSpec();
        AbstractPlayerClass apc = selectedSpec.create.get();

        ItemBuilder icon = new ItemBuilder(selectedSpec.specType.itemStack);
        icon.name(ChatColor.GREEN + selectedSpec.name);
        icon.lore(
                selectedSpec.description,
                "",
                "§6Specialization Stats:",
                "",
                "§7Health: §a" + apc.getMaxHealth(),
                "§7Energy: §a" + apc.getMaxEnergy() + " §7/ §a+" + apc.getEnergyPerSec() + " §7per sec §7/ §a+" + apc.getEnergyOnHit() + " §7per hit",
                "",
                selectedSpec == APOTHECARY ? "§7Speed: §e10%" : null,
                apc.getDamageResistance() == 0 ? "§7Damage Reduction: §cNone" : "§7Damage Reduction: §e" + apc.getDamageResistance() + "%"
        );

        SkillBoosts selectedBoost = playerSettings.getSkillBoostForClass();
        if (selectedBoost != null) {
            if (apc.getWeapon().getClass() == selectedBoost.ability) {
                apc.getWeapon().boostSkill(selectedBoost, apc);
            } else if (apc.getRed().getClass() == selectedBoost.ability) {
                apc.getRed().boostSkill(selectedBoost, apc);
            } else if (apc.getPurple().getClass() == selectedBoost.ability) {
                apc.getPurple().boostSkill(selectedBoost, apc);
            } else if (apc.getBlue().getClass() == selectedBoost.ability) {
                apc.getBlue().boostSkill(selectedBoost, apc);
            } else if (apc.getOrange().getClass() == selectedBoost.ability) {
                apc.getOrange().boostSkill(selectedBoost, apc);
            }
        }

        apc.getWeapon().updateDescription(player);
        apc.getRed().updateDescription(player);
        apc.getPurple().updateDescription(player);
        apc.getBlue().updateDescription(player);
        apc.getOrange().updateDescription(player);

        menu.setItem(0, icon.get(), ACTION_DO_NOTHING);
        menu.setItem(2, apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()), ACTION_DO_NOTHING);
        menu.setItem(3, apc.getRed().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 1)), ACTION_DO_NOTHING);
        menu.setItem(4, apc.getPurple().getItem(new ItemStack(Material.GLOWSTONE_DUST)), ACTION_DO_NOTHING);
        menu.setItem(5, apc.getBlue().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 10)), ACTION_DO_NOTHING);
        menu.setItem(6, apc.getOrange().getItem(new ItemStack(Material.INK_SACK, 1, (byte) 14)), ACTION_DO_NOTHING);
        menu.setItem(8, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));

        menu.openForPlayer(player);
    }
}