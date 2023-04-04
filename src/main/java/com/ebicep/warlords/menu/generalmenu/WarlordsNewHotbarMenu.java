package com.ebicep.warlords.menu.generalmenu;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabaseSpecialization;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.PlayerHotBarItemListener;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.pve.Currencies;
import com.ebicep.warlords.pve.Spendable;
import com.ebicep.warlords.pve.commands.AbilityTreeCommand;
import com.ebicep.warlords.pve.items.menu.ItemEquipMenu;
import com.ebicep.warlords.pve.mobs.MobDrops;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.rewards.types.LevelUpReward;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.player.general.ArmorManager.ARMOR_DESCRIPTION;
import static com.ebicep.warlords.player.general.ArmorManager.HELMET_DESCRIPTION;
import static com.ebicep.warlords.player.general.ExperienceManager.getLevelString;
import static com.ebicep.warlords.player.general.Specializations.APOTHECARY;
import static com.ebicep.warlords.util.bukkit.ItemBuilder.*;

public class WarlordsNewHotbarMenu {

    public static class SelectionMenu {

        public static final int LEVELS_PER_PAGE = 25;

        public static void openWarlordsMenu(Player player) {
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                Menu menu = new Menu("Warlords Menu", 9 * 6);

                Classes[] classes = Classes.VALUES;
                for (int i = 0, classesLength = classes.length; i < classesLength; i++) {
                    Classes value = classes[i];

                    long classExperience = ExperienceManager.getExperienceForClass(player.getUniqueId(), value);
                    int classLevel = (int) ExperienceManager.calculateLevelFromExp(classExperience);

                    ItemBuilder itemBuilder = new ItemBuilder(value.item)
                            .name(ChatColor.GOLD + value.name + ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + "Lv" + classLevel + ChatColor.DARK_GRAY + "]")
                            .lore(
                                    WordWrap.wrapWithNewline(ChatColor.GRAY + value.description, 150),
                                    "",
                                    ChatColor.GOLD + "Class Stats:",
                                    ExperienceManager.getProgressString(classExperience, classLevel + 1),
                                    "",
                                    ChatColor.GOLD + "Spec Stats:"
                            );


                    List<String> specLore = new ArrayList<>();
                    boolean hasRewards = false;
                    for (Specializations spec : value.subclasses) {
                        DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                        int prestige = databasePlayerSpec.getPrestige();
                        int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                        long experience = databasePlayerSpec.getExperience();

                        specLore.add((databasePlayer.getLastSpec() == spec ? ChatColor.GREEN : ChatColor.GRAY) + spec.name +
                                ChatColor.DARK_GRAY + " [" + ChatColor.GRAY + getLevelString(level) + ChatColor.DARK_GRAY + "] " +
                                ExperienceManager.getPrestigeLevelString(prestige));
                        specLore.add(ExperienceManager.getProgressStringWithPrestige(experience, level + 1, prestige));
                        specLore.add("");

                        for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                            if (prestigeCheck == prestige) {
                                for (int levelCheck = 1; levelCheck <= level; levelCheck++) {
                                    if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestige)) {
                                        hasRewards = true;
                                        break;
                                    }
                                }
                            } else {
                                for (int levelCheck = 1; levelCheck <= 100; levelCheck++) {
                                    if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                        hasRewards = true;
                                        break;
                                    }
                                }
                            }
                            if (hasRewards) {
                                break;
                            }
                        }
                    }

                    itemBuilder.addLore(specLore);
                    itemBuilder.addLore(
                            WordWrap.wrapWithNewline(ChatColor.YELLOW + "Click here to select a " + value.name + ChatColor.YELLOW + " specialization or claim rewards",
                                    170
                            )
                    );
                    if (hasRewards) {
                        itemBuilder.addLore("", ChatColor.GREEN + "You have unclaimed rewards!");
                        itemBuilder.enchant(Enchantment.OXYGEN, 1);
                        itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                    }
                    menu.setItem(
                            9 / 2 - classes.length / 2 + i * 2 - 2,
                            1,
                            itemBuilder.get(),
                            (m, e) -> openLevelingRewardsMenuForClass(player, databasePlayer, value)
                    );
                }

                menu.setItem(2, 3, PlayerHotBarItemListener.PVP_MENU, (m, e) -> PvPMenu.openPvPMenu(player));
                menu.setItem(4, 3, PlayerHotBarItemListener.SETTINGS_MENU, (m, e) -> SettingsMenu.openSettingsMenu(player));
                menu.setItem(6, 3, PlayerHotBarItemListener.PVE_MENU, (m, e) -> PvEMenu.openPvEMenu(player));
                menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
                menu.openForPlayer(player);
            });
        }

        public static void openLevelingRewardsMenuForClass(Player player, DatabasePlayer databasePlayer, Classes classes) {
            Menu menu = new Menu(classes.name, 9 * 4);

            List<Specializations> values = classes.subclasses;
            for (int i = 0; i < values.size(); i++) {
                Specializations spec = values.get(i);
                DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                int prestige = databasePlayerSpec.getPrestige();
                int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                long experience = databasePlayerSpec.getExperience();

                boolean hasRewards = false;
                for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                    if (prestigeCheck == prestige) {
                        for (int levelCheck = 1; levelCheck <= level; levelCheck++) {
                            if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestige)) {
                                hasRewards = true;
                                break;
                            }
                        }
                    } else {
                        for (int levelCheck = 1; levelCheck <= 100; levelCheck++) {
                            if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                hasRewards = true;
                                break;
                            }
                        }
                    }
                    if (hasRewards) {
                        break;
                    }
                }

                ItemBuilder itemBuilder = new ItemBuilder(spec.specType.itemStack)
                        .name(ChatColor.GOLD + spec.name + " " + ChatColor.DARK_GRAY + "[" +
                                ChatColor.GRAY + "Lv" + getLevelString(level) + ChatColor.DARK_GRAY + "] " +
                                ExperienceManager.getPrestigeLevelString(prestige)
                        )
                        .lore(
                                ExperienceManager.getProgressStringWithPrestige(experience, level + 1, prestige),
                                "",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" +
                                        ChatColor.GREEN + " to select this specialization.",
                                ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GREEN + " to claim rewards."
                        );
                if (hasRewards) {
                    itemBuilder.addLore("", ChatColor.GREEN + "You have unclaimed rewards!");
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    itemBuilder.flags(ItemFlag.HIDE_ENCHANTS);
                }
                menu.setItem(
                        9 / 2 - values.size() / 2 + i * 2 - 1,
                        1,
                        itemBuilder
                                .get(),
                        (m, e) -> {
                            if (e.isLeftClick()) {
                                player.sendMessage(ChatColor.GREEN + "You have changed your specialization to: §b" + spec.name);
                                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 2);
                                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                                playerSettings.setSelectedSpec(spec);
                                if (!player.getWorld().getName().equals("MainLobby")) {
                                    ArmorManager.resetArmor(player);
                                }

                                AbstractPlayerClass apc = spec.create.get();
                                player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings
                                        .getWeaponSkins()
                                        .getOrDefault(spec, Weapons.FELFLAME_BLADE)
                                        .getItem()))
                                        .name("§aWeapon Skin Preview")
                                        .lore("")
                                        .get()
                                );
                                openLevelingRewardsMenuForClass(player, databasePlayer, classes);
                                databasePlayer.setLastSpec(spec);

                                DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                            } else if (e.isRightClick()) {
                                openLevelingRewardsMenuForSpec(player,
                                        databasePlayer, spec,
                                        1,
                                        databasePlayerSpec.getPrestige()
                                );
                            }
                        }
                );
            }

            menu.setItem(3, 3, MENU_BACK, (m, e) -> openWarlordsMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openLevelingRewardsMenuForSpec(
                Player player,
                DatabasePlayer databasePlayer,
                Specializations spec,
                int page,
                int selectedPrestige
        ) {
            Menu menu = new Menu(spec.name, 9 * 6);

            DatabaseSpecialization databaseSpecialization = databasePlayer.getSpec(spec);
            int currentPrestige = databasePlayer.getSpec(spec).getPrestige();
            int level = ExperienceManager.getLevelFromExp(databasePlayer.getSpec(spec).getExperience());
            long experience = databasePlayer.getSpec(spec).getExperience();

            menu.setItem(
                    4,
                    0,
                    new ItemBuilder(spec.specType.itemStack)
                            .name(ChatColor.GOLD + spec.name + " " + ChatColor.DARK_GRAY + "[" + ChatColor.GRAY + "Lv" + getLevelString(
                                    level) + ChatColor.DARK_GRAY + "] " + ExperienceManager.getPrestigeLevelString(currentPrestige))
                            .lore(ExperienceManager.getProgressStringWithPrestige(experience, level + 1, currentPrestige))
                            .get(),
                    (m, e) -> {
                    }
            );

            for (int i = 0; i <= LEVELS_PER_PAGE; i++) {
                int section = i * page;
                if (section == 0) {
                    continue;
                }
                int column = (i - 1) % 9;
                int row = (i - 1) / 9 + 1;

                if (i >= 19) {
                    column++;
                }

                int menuLevel = i + ((page - 1) * LEVELS_PER_PAGE);
                LinkedHashMap<Spendable, Long> rewardForLevel = LevelUpReward.getRewardForLevel(menuLevel);
                List<String> lore = rewardForLevel.entrySet()
                                                  .stream()
                                                  .map(currenciesLongEntry -> {
                                                      Spendable spendable = currenciesLongEntry.getKey();
                                                      Long value = currenciesLongEntry.getValue();
                                                      return spendable.getChatColor()
                                                                      .toString() + value + " " + spendable.getName() + (spendable != Currencies.FAIRY_ESSENCE && value != 1 ? "s" : "");
                                                  }).collect(Collectors.toList());
                lore.add(0, "");
                lore.add("");
                AtomicBoolean claimed = new AtomicBoolean(false);
                boolean currentPrestigeSelected = selectedPrestige != currentPrestige;
                if (menuLevel <= level || currentPrestigeSelected) {
                    claimed.set(databaseSpecialization.hasLevelUpReward(menuLevel, selectedPrestige));
                    if (claimed.get()) {
                        lore.add(ChatColor.GREEN + "Claimed!");
                    } else {
                        lore.add(ChatColor.YELLOW + "Click to claim!");
                    }
                } else {
                    lore.add(ChatColor.RED + "You can't claim this yet!");
                }
                menu.setItem(
                        column,
                        row,
                        new ItemBuilder(Material.STAINED_GLASS_PANE,
                                1,
                                menuLevel <= level || currentPrestigeSelected ? claimed.get() ? (short) 5 : (short) 4 : (short) 15
                        )
                                .name((menuLevel <= level ? ChatColor.GREEN : ChatColor.RED) + "Level Reward " + menuLevel)
                                .lore(lore)
                                .get(),
                        (m, e) -> {
                            if (menuLevel <= level || currentPrestigeSelected) {
                                if (claimed.get()) {
                                    player.sendMessage(ChatColor.RED + "You already claimed this reward!");
                                } else {
                                    rewardForLevel.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount));
                                    databaseSpecialization.addLevelUpReward(new LevelUpReward(rewardForLevel, menuLevel, selectedPrestige));
                                    player.sendMessage(ChatColor.GREEN + "You claimed the reward for level " + menuLevel + "!");
                                    DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
                                    openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page, selectedPrestige);
                                }
                            } else {
                                player.sendMessage(ChatColor.RED + "You can't claim this reward yet!");
                            }
                        }
                );
            }

            if (currentPrestige != 0) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.HOPPER)
                        .name(ChatColor.GREEN + "Click to Cycle Between Prestige Rewards");
                List<String> lore = new ArrayList<>();
                for (int i = 0; i <= currentPrestige; i++) {
                    lore.add((i == selectedPrestige ? ChatColor.AQUA : ChatColor.GRAY) + "Prestige " + i);
                }
                itemBuilder.lore(lore);
                menu.setItem(5, 5,
                        itemBuilder.get(),
                        (m, e) -> {
                            if (selectedPrestige == currentPrestige) {
                                openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page, 0);
                            } else {
                                openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page, selectedPrestige + 1);
                            }
                        }
                );
            }

            if (page - 1 > 0) {
                menu.setItem(
                        0,
                        3,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Previous Page")
                                .lore(ChatColor.YELLOW + "Page " + (page - 1))
                                .get(),
                        (m, e) -> openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page - 1, selectedPrestige)
                );
            }
            if (page + 1 < 5) {
                menu.setItem(
                        8,
                        3,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Next Page")
                                .lore(ChatColor.YELLOW + "Page " + (page + 1))
                                .get(),
                        (m, e) -> openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page + 1, selectedPrestige)
                );

            }


            menu.setItem(3, 5, MENU_BACK, (m, e) -> openLevelingRewardsMenuForClass(player, databasePlayer, Specializations.getClass(spec)));
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }
    }

    public static class PvPMenu {

        public static final ItemStack MENU_SKINS = new ItemBuilder(Material.PAINTING)
                .name(ChatColor.GREEN + "Weapon Skin Selector")
                .lore("§7Change the cosmetic appearance\n§7of your weapon to better suit\n§7your tastes.", "", "§eClick to change weapon skin!")
                .get();
        public static final ItemStack MENU_ARMOR_SETS = new ItemBuilder(Material.DIAMOND_HELMET)
                .name(ChatColor.AQUA + "Armor Sets " + ChatColor.GRAY + "& " + ChatColor.AQUA + "Helmets " + ChatColor.GOLD + "(Cosmetic)")
                .lore(
                        "§7Equip your favorite armor\n§7sets or class helmets",
                        "",
                        ChatColor.YELLOW + "Click to equip!"
                )
                .get();
        public static final ItemStack MENU_BOOSTS = new ItemBuilder(Material.BOOKSHELF)
                .name(ChatColor.AQUA + "Weapon Skill Boost")
                .lore("§7Choose which of your skills you\n§7want your equipped weapon to boost.",
                        "",
                        "§cWARNING: §7This does not apply to PvE.",
                        "",
                        "§eClick to change skill boost!"
                )
                .get();
        public static final ItemStack MENU_BACK_PVP = new ItemBuilder(Material.ARROW)
                .name(ChatColor.GREEN + "Back")
                .lore(ChatColor.GRAY + "To PvP Menu")
                .get();
        public static final ItemStack MENU_ABILITY_DESCRIPTION = new ItemBuilder(Material.BOOK)
                .name(ChatColor.GREEN + "Class Information")
                .lore(
                        "§7Preview of your ability \ndescriptions and specialization \nstats.",
                        "",
                        ChatColor.YELLOW + "Click to preview!"
                )
                .get();

        public static void openPvPMenu(Player player) {
            Menu menu = new Menu("PvP Menu", 9 * 4);

            menu.setItem(1, 1, MENU_ABILITY_DESCRIPTION, (m, e) -> openLobbyAbilityMenu(player));
            menu.setItem(2, 1, MENU_SKINS, (m, e) -> openWeaponMenu(player, 1));
            menu.setItem(3, 1, MENU_ARMOR_SETS, (m, e) -> openArmorMenu(player, 1));
            menu.setItem(4, 1, MENU_BOOSTS, (m, e) -> openSkillBoostMenu(player, PlayerSettings.getPlayerSettings(player.getUniqueId()).getSelectedSpec()));

            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);

            menu.setItem(3, 3, MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(player));
            menu.openForPlayer(player);
        }

        public static void openWeaponMenu(Player player, int pageNumber) {
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
            Specializations selectedSpec = playerSettings.getSelectedSpec();
            Weapons selectedWeapon = playerSettings.getWeaponSkinForSelectedSpec();
            Menu menu = new Menu("Weapon Skin Selector", 9 * 6);
            List<Weapons> values = new ArrayList<>(Arrays.asList(Weapons.VALUES));
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
                                playerSettings.getWeaponSkins().put(selectedSpec, weapon);
                                openWeaponMenu(player, pageNumber);
                                AbstractPlayerClass apc = selectedSpec.create.get();
                                player.getInventory().setItem(1, new ItemBuilder(apc
                                        .getWeapon()
                                        .getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()))
                                        .name("§aWeapon Skin Preview")
                                        .lore("")
                                        .get()
                                );
                                DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> databasePlayer.getSpec(selectedSpec).setWeapon(weapon));
                            } else {
                                player.sendMessage(ChatColor.RED + "This weapon skin has not been unlocked yet!");
                            }
                        }
                );
            }

            if (pageNumber > 1) {
                menu.setItem(
                        0,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Previous Page")
                                .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                                .get(),
                        (m, e) -> openWeaponMenu(player, pageNumber - 1)
                );
            }
            if (values.size() > pageNumber * 21) {
                menu.setItem(
                        8,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Next Page")
                                .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                                .get(),
                        (m, e) -> openWeaponMenu(player, pageNumber + 1)
                );
            }


            menu.setItem(4, 5, MENU_BACK_PVP, (m, e) -> openPvPMenu(player));
            menu.openForPlayer(player);
        }

        public static void openLobbyAbilityMenu(Player player) {
            Menu menu = new Menu("Class Information", 9);
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
            Specializations selectedSpec = playerSettings.getSelectedSpec();
            AbstractPlayerClass apc = selectedSpec.create.get();

            ItemBuilder icon = new ItemBuilder(selectedSpec.specType.itemStack);
            icon.name(ChatColor.GREEN + selectedSpec.name);
            icon.lore(
                    selectedSpec.description,
                    "",
                    "§6Specialization Stats:",
                    "",
                    "§7Health: §a" + NumberFormat.formatOptionalHundredths(apc.getMaxHealth()),
                    "§7Energy: §a" + NumberFormat.formatOptionalHundredths(apc.getMaxEnergy()) + " §7/ §a+" + NumberFormat.formatOptionalHundredths(
                            apc.getEnergyPerSec()) + " §7per sec §7/ §a+" + NumberFormat.formatOptionalHundredths(apc.getEnergyPerHit()) + " §7per hit",
                    "",
                    selectedSpec == APOTHECARY ? "§7Speed: §e10%" : null,
                    apc.getDamageResistance() == 0 ? "§7Damage Reduction: §cNone" : "§7Damage Reduction: §e" + apc.getDamageResistance() + "%"
            );

            SkillBoosts selectedBoost = playerSettings.getSkillBoostForClass();
            if (selectedBoost != null) {
                for (AbstractAbility ability : apc.getAbilities()) {
                    if (ability.getClass() == selectedBoost.ability) {
                        ability.boostSkill(selectedBoost, apc);
                        break;
                    }
                }
            }

            apc.getWeapon().updateDescription(player);
            apc.getRed().updateDescription(player);
            apc.getPurple().updateDescription(player);
            apc.getBlue().updateDescription(player);
            apc.getOrange().updateDescription(player);

            menu.setItem(0, icon.get(), ACTION_DO_NOTHING);
            menu.setItem(2,
                    apc.getWeapon()
                       .getItem(playerSettings.getWeaponSkins()
                                              .getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE)
                                              .getItem()),
                    ACTION_DO_NOTHING
            );
            menu.setItem(3, apc.getRed().getItem(RED_ABILITY), ACTION_DO_NOTHING);
            menu.setItem(4, apc.getPurple().getItem(PURPLE_ABILITY), ACTION_DO_NOTHING);
            menu.setItem(5, apc.getBlue().getItem(BLUE_ABILITY), ACTION_DO_NOTHING);
            menu.setItem(6, apc.getOrange().getItem(ORANGE_ABILITY), ACTION_DO_NOTHING);
            menu.setItem(8, MENU_BACK_PVP, (m, e) -> openPvPMenu(player));

            menu.openForPlayer(player);
        }

        public static void openArmorMenu(Player player, int pageNumber) {
            boolean onBlueTeam = Warlords.getGameManager()
                                         .getPlayerGame(player.getUniqueId())
                                         .map(g -> g.getPlayerTeam(player.getUniqueId()))
                                         .orElse(Team.BLUE) == Team.BLUE;
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
            List<ArmorManager.Helmets> selectedHelmet = playerSettings.getHelmets();

            Menu menu = new Menu("Armor Sets & Helmets", 9 * 6);

            ArmorManager.Helmets[] helmets = ArmorManager.Helmets.VALUES;
            for (int i = (pageNumber - 1) * 8; i < pageNumber * 8 && i < helmets.length; i++) {
                ArmorManager.Helmets helmet = helmets[i];
                ItemBuilder builder = new ItemBuilder(onBlueTeam ? helmet.itemBlue : helmet.itemRed)
                        .name(onBlueTeam ? ChatColor.BLUE + helmet.name : ChatColor.RED + helmet.name)
                        .lore(HELMET_DESCRIPTION, "")
                        .flags(ItemFlag.HIDE_ENCHANTS);
                if (selectedHelmet.contains(helmet)) {
                    builder.addLore(ChatColor.GREEN + ">>> ACTIVE <<<");
                    builder.enchant(Enchantment.OXYGEN, 1);
                } else {
                    builder.addLore(ChatColor.YELLOW + "> Click to activate! <");
                }
                menu.setItem(
                        (i - (pageNumber - 1) * 8) + 1,
                        2,
                        builder.get(),
                        (m, e) -> {
                            player.sendMessage(ChatColor.YELLOW + "Selected: " + ChatColor.GREEN + helmet.name);
                            playerSettings.setHelmet(helmet.classes, helmet);
                            ArmorManager.resetArmor(player);
                            openArmorMenu(player, pageNumber);
                        }
                );
            }
            int xPosition = 1;
            for (int i = (pageNumber - 1) * 6; i < pageNumber * 6; i++) {
                if (pageNumber == 3 && i == 15) {
                    break;
                }
                ArmorManager.ArmorSets armorSet = ArmorManager.ArmorSets.VALUES[(i % 3) * 3];
                Classes classes = Classes.VALUES[i / 3];
                ItemBuilder builder = new ItemBuilder(i % 3 == 0 ? ArmorManager.ArmorSets.applyColor(armorSet.itemBlue, onBlueTeam) : armorSet.itemBlue)
                        .name(onBlueTeam ? ChatColor.BLUE + armorSet.name : ChatColor.RED + armorSet.name)
                        .lore(ARMOR_DESCRIPTION, "")
                        .flags(ItemFlag.HIDE_ENCHANTS);
                if (playerSettings.getArmorSet(classes) == armorSet) {
                    builder.addLore(ChatColor.GREEN + ">>> ACTIVE <<<");
                    builder.enchant(Enchantment.OXYGEN, 1);
                } else {
                    builder.addLore(ChatColor.YELLOW + "> Click to activate! <");
                }
                menu.setItem(
                        xPosition,
                        3,
                        builder.get(),
                        (m, e) -> {
                            player.sendMessage(ChatColor.YELLOW + "Selected: " + ChatColor.GREEN + armorSet.name);
                            playerSettings.setArmor(classes, armorSet);
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
                        (m, e) -> openArmorMenu(player, pageNumber + 1)
                );
            } else if (pageNumber == 2) {
                menu.setItem(
                        8,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Next Page")
                                .lore(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                                .get(),
                        (m, e) -> openArmorMenu(player, pageNumber + 1)
                );
                menu.setItem(
                        0,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Previous Page")
                                .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                                .get(),
                        (m, e) -> openArmorMenu(player, pageNumber - 1)
                );
            } else if (pageNumber == 3) {
                menu.setItem(
                        0,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(ChatColor.GREEN + "Previous Page")
                                .lore(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                                .get(),
                        (m, e) -> openArmorMenu(player, pageNumber - 1)
                );
            }

            menu.setItem(4, 5, MENU_BACK_PVP, (m, e) -> openPvPMenu(player));
            menu.openForPlayer(player);
        }

        public static void openSkillBoostMenu(Player player, Specializations selectedSpec) {
            SkillBoosts selectedBoost = PlayerSettings.getPlayerSettings(player.getUniqueId()).getSkillBoostForClass();
            Menu menu = new Menu("Skill Boost", 9 * 6);
            List<SkillBoosts> values = selectedSpec.skillBoosts;
            for (int i = 0; i < values.size(); i++) {
                SkillBoosts skillBoost = values.get(i);
                ItemBuilder builder = new ItemBuilder(selectedSpec.specType.itemStack)
                        .name(skillBoost == selectedBoost ? ChatColor.GREEN + skillBoost.name + " (" + selectedSpec.name + ")" : ChatColor.RED + skillBoost.name + " (" + selectedSpec.name + ")")
                        .flags(ItemFlag.HIDE_ENCHANTS);
                List<String> lore = new ArrayList<>();
                lore.add(WordWrap.wrapWithNewline(skillBoost == selectedBoost ? skillBoost.selectedDescription
                        .replace("§c", ChatColor.RED.toString())
                        .replace("§a", ChatColor.GREEN.toString()) : skillBoost.description, 130)
                );
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
                            PlayerSettings.getPlayerSettings(player.getUniqueId()).setSkillBoostForSelectedSpec(skillBoost);
                            openSkillBoostMenu(player, selectedSpec);

                            DatabaseManager.updatePlayer(player.getUniqueId(),
                                    databasePlayer -> databasePlayer.getSpec(selectedSpec).setSkillBoost(skillBoost)
                            );
                        }
                );
            }

            //showing change of ability
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
            AbstractPlayerClass apc = selectedSpec.create.get();
            AbstractPlayerClass apc2 = selectedSpec.create.get();
            if (apc2.getWeapon().getClass() == selectedBoost.ability) {
                apc2.getWeapon().boostSkill(selectedBoost, apc2);
                apc.getWeapon().updateDescription(player);
                apc2.getWeapon().updateDescription(player);
                menu.setItem(3,
                        1,
                        apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()),
                        ACTION_DO_NOTHING
                );
                menu.setItem(5,
                        1,
                        apc2.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.FELFLAME_BLADE).getItem()),
                        ACTION_DO_NOTHING
                );
            } else if (apc2.getRed().getClass() == selectedBoost.ability) {
                apc2.getRed().boostSkill(selectedBoost, apc2);
                apc.getRed().updateDescription(player);
                apc2.getRed().updateDescription(player);
                menu.setItem(3, 1, apc.getRed().getItem(RED_ABILITY), ACTION_DO_NOTHING);
                menu.setItem(5, 1, apc2.getRed().getItem(RED_ABILITY), ACTION_DO_NOTHING);
            } else if (apc2.getPurple().getClass() == selectedBoost.ability) {
                apc2.getPurple().boostSkill(selectedBoost, apc2);
                apc.getPurple().updateDescription(player);
                apc2.getPurple().updateDescription(player);
                menu.setItem(3, 1, apc.getPurple().getItem(PURPLE_ABILITY), ACTION_DO_NOTHING);
                menu.setItem(5, 1, apc2.getPurple().getItem(PURPLE_ABILITY), ACTION_DO_NOTHING);
            } else if (apc2.getBlue().getClass() == selectedBoost.ability) {
                apc2.getBlue().boostSkill(selectedBoost, apc2);
                apc.getBlue().updateDescription(player);
                apc2.getBlue().updateDescription(player);
                menu.setItem(3, 1, apc.getBlue().getItem(BLUE_ABILITY), ACTION_DO_NOTHING);
                menu.setItem(5, 1, apc2.getBlue().getItem(BLUE_ABILITY), ACTION_DO_NOTHING);
            } else if (apc2.getOrange().getClass() == selectedBoost.ability) {
                apc2.getOrange().boostSkill(selectedBoost, apc2);
                apc.getOrange().updateDescription(player);
                apc2.getOrange().updateDescription(player);
                menu.setItem(3, 1, apc.getOrange().getItem(ORANGE_ABILITY), ACTION_DO_NOTHING);
                menu.setItem(5, 1, apc2.getOrange().getItem(ORANGE_ABILITY), ACTION_DO_NOTHING);
            }
            menu.setItem(4, 5, MENU_BACK_PVP, (m, e) -> openPvPMenu(player));
            menu.openForPlayer(player);
        }


    }

    public static class PvEMenu {

        public static final ItemStack MENU_BACK_PVE = new ItemBuilder(Material.ARROW)
                .name(ChatColor.GREEN + "Back")
                .lore(ChatColor.GRAY + "To PvE Menu")
                .get();
        public static final ItemStack WEAPONS_MENU = new ItemBuilder(Material.DIAMOND_SWORD)
                .name("§aWeapons")
                .lore(
                        WordWrap.wrapWithNewline(ChatColor.GRAY + "View and modify all your weapons, also accessible through The Weaponsmith.", 160),
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .get();
        public static final ItemStack ITEMS_MENU = new ItemBuilder(Material.ITEM_FRAME)
                .name("§aItems")
                .lore(
                        WordWrap.wrapWithNewline(ChatColor.GRAY + "View and equip all your Items.", 160),
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .get();
        public static final ItemStack REWARD_INVENTORY_MENU = new ItemBuilder(Material.ENDER_CHEST)
                .name("§aReward Inventory")
                .lore(
                        WordWrap.wrapWithNewline(ChatColor.GRAY + "View and claim all your rewards.", 160),
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .get();
        public static final ItemStack ABILITY_TREE_MENU = new ItemBuilder(Material.GOLD_NUGGET)
                .name(ChatColor.GREEN + "Upgrade Talisman")
                .lore(
                        WordWrap.wrapWithNewline(ChatColor.GRAY + "View your ability upgrades.", 160),
                        "",
                        ChatColor.YELLOW + "Click to view!"
                )
                .get();

        public static void openPvEMenu(Player player) {
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                Menu menu = new Menu("PvE Menu", 9 * 4);

                List<AbstractWeapon> weapons = databasePlayer.getPveStats().getWeaponInventory();
                Optional<AbstractWeapon> optionalWeapon = weapons
                        .stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == databasePlayer.getLastSpec())
                        .findFirst();
                ItemStack itemStack = WEAPONS_MENU;
                if (optionalWeapon.isPresent()) {
                    itemStack = optionalWeapon.get().generateItemStack(false);
                }

                menu.setItem(1, 1, itemStack, (m, e) -> {
                    if (e.isRightClick() && optionalWeapon.isPresent()) {
                        WeaponManagerMenu.openWeaponEditor(player, databasePlayer, optionalWeapon.get());
                    } else {
                        WeaponManagerMenu.openWeaponInventoryFromExternal(player, false);
                    }
                });
                menu.setItem(2, 1,
                        new ItemBuilder(Material.SKULL_ITEM, 1, (short) SkullType.ZOMBIE.ordinal())
                                .name("§aMob Drops")
                                .lore(Arrays.stream(MobDrops.VALUES)
                                            .map(drop -> drop.getCostColoredName(databasePlayer.getPveStats()
                                                                                               .getMobDrops()
                                                                                               .getOrDefault(drop, 0L)))
                                            .collect(Collectors.joining("\n")))
                                .get(),
                        (m, e) -> {}
                );
                menu.setItem(3, 1, ITEMS_MENU, (m, e) -> ItemEquipMenu.openItemEquipMenuExternal(player, databasePlayer));
                menu.setItem(4, 1, REWARD_INVENTORY_MENU, (m, e) -> RewardInventory.openRewardInventory(player, 1));
                menu.setItem(5, 1, ABILITY_TREE_MENU, (m, e) -> AbilityTreeCommand.open(player));

                menu.setItem(3, 3, MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(player));
                menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);

                menu.openForPlayer(player);
            });
        }

    }

    @CommandAlias("settings|setting")
    public static class SettingsMenu extends BaseCommand {

        public static final ItemStack MENU_SETTINGS = new ItemBuilder(Material.NETHER_STAR)
                .name(ChatColor.AQUA + "Settings")
                .lore("§7Allows you to toggle different settings\n§7options.", "", "§eClick to edit your settings.")
                .get();
        public static final ItemStack MENU_SETTINGS_PARTICLE_QUALITY = new ItemBuilder(Material.NETHER_STAR)
                .name(ChatColor.GREEN + "Particle Quality")
                .lore("§7Allows you to control, or\n§7disable, particles and the\n§7amount of them.")
                .get();
        public static final ItemStack MENU_SETTINGS_CHAT_SETTINGS = new ItemBuilder(Material.PAPER)
                .name(ChatColor.GREEN + "Chat Settings")
                .lore(WordWrap.wrapWithNewline(ChatColor.GRAY + "Configure which chat messages you see in-game", 150))
                .get();

        @Default
        public static void openSettingsMenu(Player player) {
            PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());

            Menu menu = new Menu("Settings", 9 * 4);
            menu.setItem(
                    1,
                    1,
                    MENU_SETTINGS_PARTICLE_QUALITY,
                    (m, e) -> openParticleQualityMenu(player)
            );
            menu.setItem(
                    3,
                    1,
                    playerSettings.getHotkeyMode().item,
                    (m, e) -> {
                        player.performCommand("hotkeymode");
                        openSettingsMenu(player);
                    }
            );
            menu.setItem(
                    5,
                    1,
                    playerSettings.getFlagMessageMode().item,
                    (m, e) -> {
                        player.performCommand("flagmessagemode");
                        openSettingsMenu(player);
                    }
            );
            menu.setItem(
                    7,
                    1,
                    MENU_SETTINGS_CHAT_SETTINGS,
                    (m, e) -> {
                        Settings.ChatSettings.openChatSettingsMenu(player);
                    }
            );

            menu.setItem(3, 3, MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(player));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openParticleQualityMenu(Player player) {
            Settings.ParticleQuality selectedParticleQuality = PlayerSettings.getPlayerSettings(player.getUniqueId()).getParticleQuality();

            Menu menu = new Menu("Particle Quality", 9 * 4);

            Settings.ParticleQuality[] particleQualities = Settings.ParticleQuality.values();
            for (int i = 0; i < particleQualities.length; i++) {
                Settings.ParticleQuality particleQuality = particleQualities[i];

                menu.setItem(
                        i + 3,
                        1,
                        new ItemBuilder(particleQuality.item)
                                .lore(particleQuality.description,
                                        "",
                                        selectedParticleQuality == particleQuality ? ChatColor.GREEN + "SELECTED" : ChatColor.YELLOW + "Click to select!"
                                )
                                .flags(ItemFlag.HIDE_ENCHANTS)
                                .get(),
                        (m, e) -> {
                            Bukkit.getServer().dispatchCommand(player, "pq " + particleQuality.name());
                            openParticleQualityMenu(player);
                        }
                );
            }
            menu.setItem(4, 3, MENU_BACK, (m, e) -> openSettingsMenu(player));
            menu.openForPlayer(player);
        }
    }

}
