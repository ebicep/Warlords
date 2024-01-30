package com.ebicep.warlords.menu.generalmenu;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
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
import com.ebicep.warlords.pve.mobs.MobDrop;
import com.ebicep.warlords.pve.rewards.RewardInventory;
import com.ebicep.warlords.pve.rewards.types.LevelUpReward;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.menu.WeaponManagerMenu;
import com.ebicep.warlords.util.bukkit.ComponentUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.player.general.ArmorManager.ARMOR_DESCRIPTION;
import static com.ebicep.warlords.player.general.ArmorManager.HELMET_DESCRIPTION;
import static com.ebicep.warlords.player.general.ExperienceManager.getLevelString;
import static com.ebicep.warlords.player.general.Specializations.APOTHECARY;

public class WarlordsNewHotbarMenu {

    private static void claimLevelReward(
            Player player,
            DatabasePlayer databasePlayer,
            DatabaseSpecialization databasePlayerSpec,
            LinkedHashMap<Spendable, Long> rewardForLevel,
            int prestigeCheck,
            int levelCheck,
            Specializations spec
    ) {
        // precaution
        if (databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
            return;
        }
        rewardForLevel.forEach((spendable, amount) -> spendable.addToPlayer(databasePlayer, amount));
        databasePlayerSpec.addLevelUpReward(new LevelUpReward(rewardForLevel, levelCheck, prestigeCheck));
        player.sendMessage(Component.text("You claimed the reward for level " + levelCheck + "!", NamedTextColor.GREEN)
                                    .hoverEvent(HoverEvent.showText(Component.textOfChildren(
                                            Component.text("Specialization: ", NamedTextColor.GRAY),
                                            Component.text(spec.name, NamedTextColor.GOLD),
                                            Component.newline(),
                                            Component.text("Prestige: ", NamedTextColor.GRAY),
                                            Component.text(prestigeCheck, NamedTextColor.GOLD)
                                    ))));
        DatabaseManager.queueUpdatePlayerAsync(databasePlayer);
    }

    public static class SelectionMenu {

        public static final int LEVELS_PER_PAGE = 25;

        public static void openWarlordsMenu(Player player) {
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                Menu menu = new Menu("Warlords Menu", 9 * 6);

                boolean hasRewardsForAny = false;
                Classes[] classes = Classes.VALUES;
                for (int i = 0, classesLength = classes.length; i < classesLength; i++) {
                    Classes value = classes[i];

                    long classExperience = ExperienceManager.getExperienceForClass(player.getUniqueId(), value);
                    int classLevel = (int) ExperienceManager.calculateLevelFromExp(classExperience);

                    ItemBuilder itemBuilder = new ItemBuilder(value.item)
                            .name(Component.text(value.name, NamedTextColor.GOLD)
                                           .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                                           .append(Component.text("Lv" + classLevel, NamedTextColor.GRAY))
                                           .append(Component.text("]", NamedTextColor.DARK_GRAY)))
                            .lore(WordWrap.wrap(Component.text(value.description, NamedTextColor.GRAY), 150))
                            .addLore(
                                    Component.empty(),
                                    Component.text("Class Stats:", NamedTextColor.GOLD)
                            )
                            .addLore(
                                    ExperienceManager.getProgressString(classExperience, classLevel + 1))
                            .addLore(
                                    Component.empty(),
                                    Component.text("Spec Stats:", NamedTextColor.GOLD)
                            );


                    boolean hasRewards = false;
                    for (Specializations spec : value.subclasses) {
                        DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                        int prestige = databasePlayerSpec.getPrestige();
                        int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                        long experience = databasePlayerSpec.getExperience();

                        itemBuilder.addLore(Component.text(spec.name, (databasePlayer.getLastSpec() == spec ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                                                     .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                                                     .append(Component.text("Lv" + getLevelString(level), NamedTextColor.GRAY))
                                                     .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                                                     .append(ExperienceManager.getPrestigeLevelString(prestige)));
                        itemBuilder.addLore(ExperienceManager.getProgressStringWithPrestige(experience, level + 1, prestige));
                        itemBuilder.addLore(Component.empty());

                        for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                            int maxLevel = prestigeCheck == prestige ? level : 100;
                            for (int levelCheck = 1; levelCheck <= maxLevel; levelCheck++) {
                                if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                    hasRewards = true;
                                    break;
                                }
                            }
                            if (hasRewards) {
                                break;
                            }
                        }
                    }

                    itemBuilder.addLore(WordWrap.wrap(Component.text("Click here to select a " + value.name + " specialization or claim rewards", NamedTextColor.YELLOW), 170));
                    if (hasRewards) {
                        itemBuilder.addLore(Component.empty(), Component.text("You have unclaimed rewards!", NamedTextColor.GREEN));
                        itemBuilder.enchant(Enchantment.OXYGEN, 1);
                        hasRewardsForAny = true;
                    }
                    menu.setItem(
                            i + 1 + (i >= 3 ? 1 : 0),
                            1,
                            itemBuilder.get(),
                            (m, e) -> openLevelingRewardsMenuForClass(player, databasePlayer, value)
                    );
                }

                menu.setItem(2, 3, PlayerHotBarItemListener.PVP_MENU, (m, e) -> PvPMenu.openPvPMenu(player));
                menu.setItem(4, 3, PlayerHotBarItemListener.SETTINGS_MENU, (m, e) -> SettingsMenu.openSettingsMenu(player));
                menu.setItem(6, 3, PlayerHotBarItemListener.PVE_MENU, (m, e) -> PvEMenu.openPvEMenu(player));
                menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
                if (hasRewardsForAny) {
                    menu.setItem(5, 5, CLAIM_ALL, (m, e) -> {
                        for (Classes value : classes) {
                            for (Specializations spec : value.subclasses) {
                                DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                                int prestige = databasePlayerSpec.getPrestige();
                                int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                                for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                                    int maxLevel = prestigeCheck == prestige ? level : 100;
                                    for (int levelCheck = 1; levelCheck <= maxLevel; levelCheck++) {
                                        if (databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                            continue;
                                        }
                                        claimLevelReward(player,
                                                databasePlayer,
                                                databasePlayerSpec,
                                                LevelUpReward.getRewardForLevel(levelCheck),
                                                prestigeCheck,
                                                levelCheck,
                                                spec
                                        );
                                    }
                                }
                            }
                        }
                        openWarlordsMenu(player);
                    });
                }
                menu.openForPlayer(player);
            });
        }

        public static void openLevelingRewardsMenuForClass(Player player, DatabasePlayer databasePlayer, Classes classes) {
            Menu menu = new Menu(classes.name, 9 * 4);

            boolean hasRewardsForAny = false;
            List<Specializations> specs = classes.subclasses;
            for (int i = 0; i < specs.size(); i++) {
                Specializations spec = specs.get(i);
                DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                int prestige = databasePlayerSpec.getPrestige();
                int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                long experience = databasePlayerSpec.getExperience();

                boolean hasRewards = false;
                for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                    int maxLevel = prestigeCheck == prestige ? level : 100;
                    for (int levelCheck = 1; levelCheck <= maxLevel; levelCheck++) {
                        if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                            hasRewards = true;
                            break;
                        }
                    }
                    if (hasRewards) {
                        break;
                    }
                }

                ItemBuilder itemBuilder = new ItemBuilder(spec.specType.itemStack)
                        .name(Component.text(spec.name, NamedTextColor.GOLD)
                                       .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                                       .append(Component.text("Lv" + getLevelString(level), NamedTextColor.GRAY))
                                       .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                                       .append(ExperienceManager.getPrestigeLevelString(prestige))
                        )
                        .lore(ExperienceManager.getProgressStringWithPrestige(experience, level + 1, prestige))
                        .addLore(
                                Component.empty(),
                                Component.textOfChildren(
                                        Component.text("LEFT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to select this specialization.", NamedTextColor.GREEN)
                                ),
                                Component.textOfChildren(
                                        Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" to claim rewards.", NamedTextColor.GREEN)
                                )
                        );
                if (hasRewards) {
                    itemBuilder.addLore(Component.empty(), Component.text("You have unclaimed rewards!", NamedTextColor.GREEN));
                    itemBuilder.enchant(Enchantment.OXYGEN, 1);
                    hasRewardsForAny = true;
                }
                menu.setItem(
                        9 / 2 - specs.size() / 2 + i * 2 - 1,
                        1,
                        itemBuilder
                                .get(),
                        (m, e) -> {
                            if (e.isLeftClick()) {
                                player.sendMessage(Component.text("You have changed your specialization to: ", NamedTextColor.GREEN)
                                                            .append(Component.text(spec.name, NamedTextColor.AQUA)));
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                                PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                                playerSettings.setSelectedSpec(spec);
                                if (!player.getWorld().getName().equals("MainLobby")) {
                                    ArmorManager.resetArmor(player);
                                }

                                AbstractPlayerClass apc = spec.create.get();
                                ItemStack weaponSkin = playerSettings
                                        .getWeaponSkins()
                                        .getOrDefault(spec, Weapons.STEEL_SWORD)
                                        .getItem();
                                player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(weaponSkin))
                                        .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                                        .noLore()
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
            if (hasRewardsForAny) {
                menu.setItem(5, 3, CLAIM_ALL, (m, e) -> {
                    for (Specializations spec : specs) {
                        DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
                        int prestige = databasePlayerSpec.getPrestige();
                        int level = ExperienceManager.getLevelFromExp(databasePlayerSpec.getExperience());
                        for (int prestigeCheck = 0; prestigeCheck < prestige + 1; prestigeCheck++) {
                            int maxLevel = prestigeCheck == prestige ? level : 100;
                            for (int levelCheck = 1; levelCheck <= maxLevel; levelCheck++) {
                                if (databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                    continue;
                                }
                                claimLevelReward(player,
                                        databasePlayer,
                                        databasePlayerSpec,
                                        LevelUpReward.getRewardForLevel(levelCheck),
                                        prestigeCheck,
                                        levelCheck,
                                        spec
                                );
                            }
                        }
                    }
                    openLevelingRewardsMenuForClass(player, databasePlayer, classes);
                });
            }
            menu.setItem(8, 3,
                    new ItemBuilder(Material.NETHERITE_SCRAP)
                            .name(Component.text("Level Rewards", NamedTextColor.GREEN))
                            .lore(Currencies.ASCENDANT_SHARD.getCostColoredName(Currencies.ASCENDANT_SHARD.getFromPlayer(databasePlayer)))
                            .get(),
                    (m, e) -> {}
            );
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

            DatabaseSpecialization databasePlayerSpec = databasePlayer.getSpec(spec);
            int currentPrestige = databasePlayer.getSpec(spec).getPrestige();
            int level = ExperienceManager.getLevelFromExp(databasePlayer.getSpec(spec).getExperience());
            long experience = databasePlayer.getSpec(spec).getExperience();

            menu.setItem(
                    4,
                    0,
                    new ItemBuilder(spec.specType.itemStack)
                            .name(Component.text(spec.name, NamedTextColor.GOLD)
                                           .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                                           .append(Component.text("Lv" + getLevelString(level), NamedTextColor.GRAY))
                                           .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                                           .append(ExperienceManager.getPrestigeLevelString(currentPrestige))
                            )
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
                List<Component> lore = rewardForLevel.entrySet()
                                                     .stream()
                                                     .map(currenciesLongEntry -> {
                                                         Spendable spendable = currenciesLongEntry.getKey();
                                                         Long value = currenciesLongEntry.getValue();
                                                         return spendable.getCostColoredName(value);
                                                     }).collect(Collectors.toList());
                lore.add(0, Component.empty());
                lore.add(Component.empty());
                AtomicBoolean claimed = new AtomicBoolean(false);
                boolean currentPrestigeSelected = selectedPrestige != currentPrestige;
                if (menuLevel <= level || currentPrestigeSelected) {
                    claimed.set(databasePlayerSpec.hasLevelUpReward(menuLevel, selectedPrestige));
                    if (claimed.get()) {
                        lore.add(Component.text("Claimed!", NamedTextColor.GREEN));
                    } else {
                        lore.add(Component.text("Click to claim!", NamedTextColor.YELLOW));
                    }
                } else {
                    lore.add(Component.text("You can't claim this yet!", NamedTextColor.RED));
                }
                menu.setItem(
                        column,
                        row,
                        new ItemBuilder(menuLevel <= level || currentPrestigeSelected ?
                                        claimed.get() ?
                                        Material.LIME_STAINED_GLASS_PANE :
                                        Material.YELLOW_STAINED_GLASS_PANE :
                                        Material.BLACK_STAINED_GLASS_PANE)
                                .name(Component.text("Level Reward " + menuLevel, menuLevel <= level ? NamedTextColor.GREEN : NamedTextColor.RED))
                                .lore(lore)
                                .get(),
                        (m, e) -> {
                            if (menuLevel <= level || currentPrestigeSelected) {
                                if (claimed.get()) {
                                    player.sendMessage(Component.text("You already claimed this reward!", NamedTextColor.RED));
                                } else {
                                    claimLevelReward(player, databasePlayer, databasePlayerSpec, rewardForLevel, selectedPrestige, menuLevel, spec);
                                    openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page, selectedPrestige);
                                }
                            } else {
                                player.sendMessage(Component.text("You can't claim this reward yet!", NamedTextColor.RED));
                            }
                        }
                );
            }


            if (page - 1 > 0) {
                menu.setItem(
                        0,
                        3,
                        new ItemBuilder(Material.ARROW)
                                .name(Component.text("Previous Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                                .get(),
                        (m, e) -> openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page - 1, selectedPrestige)
                );
            }
            if (page + 1 < 5) {
                menu.setItem(
                        8,
                        3,
                        new ItemBuilder(Material.ARROW)
                                .name(Component.text("Next Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                                .get(),
                        (m, e) -> openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page + 1, selectedPrestige)
                );

            }


            menu.setItem(3, 5, MENU_BACK, (m, e) -> openLevelingRewardsMenuForClass(player, databasePlayer, Specializations.getClass(spec)));
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            if (currentPrestige != 0) {
                ItemBuilder itemBuilder = new ItemBuilder(Material.HOPPER)
                        .name(Component.text("Click to Cycle Between Prestige Rewards", NamedTextColor.GREEN));
                List<Component> lore = new ArrayList<>();
                for (int i = 0; i <= currentPrestige; i++) {
                    lore.add(Component.text("Prestige " + i, i == selectedPrestige ? NamedTextColor.AQUA : NamedTextColor.GRAY));
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
            boolean hasRewards = false;
            for (int prestigeCheck = 0; prestigeCheck < currentPrestige + 1; prestigeCheck++) {
                int maxLevel = prestigeCheck == currentPrestige ? level : 100;
                for (int levelCheck = 1; levelCheck <= maxLevel; levelCheck++) {
                    if (!databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                        hasRewards = true;
                        break;
                    }
                }
                if (hasRewards) {
                    break;
                }
            }
            if (hasRewards) {
                menu.setItem(6, 5, CLAIM_ALL, (m, e) -> {
                    for (int prestigeCheck = 0; prestigeCheck < currentPrestige + 1; prestigeCheck++) {
                        int maxLevel = prestigeCheck == currentPrestige ? level : 100;
                        for (int levelCheck = 1; levelCheck <= maxLevel; levelCheck++) {
                            if (databasePlayerSpec.hasLevelUpReward(levelCheck, prestigeCheck)) {
                                continue;
                            }
                            claimLevelReward(player,
                                    databasePlayer,
                                    databasePlayerSpec,
                                    LevelUpReward.getRewardForLevel(levelCheck),
                                    prestigeCheck,
                                    levelCheck,
                                    spec
                            );
                        }
                    }
                    openLevelingRewardsMenuForSpec(player, databasePlayer, spec, page, selectedPrestige);
                });
            }
            menu.setItem(8, 5,
                    new ItemBuilder(Material.NETHERITE_SCRAP)
                            .name(Component.text("Level Rewards", NamedTextColor.GREEN))
                            .lore(Currencies.ASCENDANT_SHARD.getCostColoredName(Currencies.ASCENDANT_SHARD.getFromPlayer(databasePlayer)))
                            .get(),
                    (m, e) -> {}
            );
            menu.openForPlayer(player);
        }
    }

    public static class PvPMenu {

        public static final ItemStack MENU_SKINS = new ItemBuilder(Material.PAINTING)
                .name(Component.text("Weapon Skin Selector", NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text("Change the cosmetic appearance of your weapon to better suit your tastes.", NamedTextColor.GRAY), 160))
                .addLore(
                        Component.empty(),
                        Component.text("Click to change weapon skin!", NamedTextColor.YELLOW)
                )
                .get();
        public static final ItemStack MENU_ARMOR_SETS = new ItemBuilder(Material.DIAMOND_HELMET)
                .name(Component.text("Armor Sets ", NamedTextColor.AQUA)
                               .append(Component.text("& ", NamedTextColor.GRAY))
                               .append(Component.text("Helmets "))
                               .append(Component.text("(Cosmetic)", NamedTextColor.GOLD))
                )
                .lore(
                        Component.text("Equip your favorite armor", NamedTextColor.GRAY),
                        Component.text("sets or class helmets", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("Click to equip!", NamedTextColor.YELLOW)
                )
                .get();
        public static final ItemStack MENU_BOOSTS = new ItemBuilder(Material.BOOKSHELF)
                .name(Component.text("Weapon Skill Boost", NamedTextColor.AQUA))
                .lore(
                        Component.text("Choose which of your skills you", NamedTextColor.GRAY),
                        Component.text("want your equipped weapon to boost.", NamedTextColor.GRAY),
                        Component.empty(),
                        Component.text("WARNING:", NamedTextColor.RED).append(Component.text(" This does not apply to PvE.", NamedTextColor.GRAY)),
                        Component.empty(),
                        Component.text("Click to change skill boost!", NamedTextColor.YELLOW)
                )
                .get();
        public static final ItemStack MENU_BACK_PVP = new ItemBuilder(Material.ARROW)
                .name(Component.text("Back", NamedTextColor.GREEN))
                .lore(Component.text("To PvP Menu", NamedTextColor.GRAY))
                .get();
        public static final ItemStack MENU_ABILITY_DESCRIPTION = new ItemBuilder(Material.BOOK)
                .name(Component.text("Class Information", NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text("Preview of your ability descriptions and specialization stats.", NamedTextColor.GRAY), 160))
                .addLore(
                        Component.empty(),
                        Component.text("Click to preview!", NamedTextColor.YELLOW)
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
                            .name(Component.text(weapon.getName(), NamedTextColor.GREEN));
                    List<Component> lore = new ArrayList<>();

                    if (weapon == selectedWeapon) {
                        lore.add(Component.text("Currently selected!", NamedTextColor.GREEN));
                        builder.enchant(Enchantment.OXYGEN, 1);
                    } else {
                        lore.add(Component.text("Click to select", NamedTextColor.YELLOW));
                    }

                    builder.lore(lore);
                } else {
                    builder = new ItemBuilder(Material.BARRIER).name(Component.text("Locked Weapon Skin", NamedTextColor.RED));
                }

                menu.setItem(
                        (i - (pageNumber - 1) * 21) % 7 + 1,
                        (i - (pageNumber - 1) * 21) / 7 + 1,
                        builder.get(),
                        (m, e) -> {
                            if (weapon.isUnlocked) {
                                player.sendMessage(Component.text("You have changed your ", NamedTextColor.GREEN)
                                                            .append(Component.text(selectedSpec.name, NamedTextColor.AQUA))
                                                            .append(Component.text("'s weapon skin to: §b" + weapon.getName() + "!")));
                                playerSettings.getWeaponSkins().put(selectedSpec, weapon);
                                openWeaponMenu(player, pageNumber);
                                AbstractPlayerClass apc = selectedSpec.create.get();
                                ItemStack weaponSkin = playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.STEEL_SWORD).getItem();
                                player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(weaponSkin))
                                        .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                                        .noLore()
                                        .get()
                                );
                                DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> databasePlayer.getSpec(selectedSpec).setWeapon(weapon));
                            } else {
                                player.sendMessage(Component.text("This weapon skin has not been unlocked yet!", NamedTextColor.RED));
                            }
                        }
                );
            }

            if (pageNumber > 1) {
                menu.setItem(
                        0,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(Component.text("Previous Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (pageNumber - 1), NamedTextColor.YELLOW))
                                .get(),
                        (m, e) -> openWeaponMenu(player, pageNumber - 1)
                );
            }
            if (values.size() > pageNumber * 21) {
                menu.setItem(
                        8,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(Component.text("Next Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (pageNumber + 1), NamedTextColor.YELLOW))
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

            ItemBuilder icon = new ItemBuilder(selectedSpec.specType.itemStack)
                    .name(Component.text(selectedSpec.name, NamedTextColor.GREEN))
                    .lore(WordWrap.wrap(selectedSpec.getDescription(), 200));
            icon.addLore(
                    Component.empty(),
                    Component.text("Specialization Stats:", NamedTextColor.GOLD),
                    Component.empty(),
                    Component.text("Health: ", NamedTextColor.GRAY).append(Component.text(NumberFormat.formatOptionalHundredths(apc.getMaxHealth()), NamedTextColor.GREEN)),
                    Component.empty(),
                    Component.text("Energy: ", NamedTextColor.GRAY)
                             .append(Component.text(NumberFormat.formatOptionalHundredths(apc.getMaxEnergy()), NamedTextColor.GREEN))
                             .append(Component.text(" / "))
                             .append(Component.text("+" + NumberFormat.formatOptionalHundredths(apc.getEnergyPerSec()), NamedTextColor.GREEN))
                             .append(Component.text(" per sec / "))
                             .append(Component.text("+" + NumberFormat.formatOptionalHundredths(apc.getEnergyPerHit()), NamedTextColor.GREEN))
                             .append(Component.text(" per hit"))
            );
            if (selectedSpec == APOTHECARY) {
                icon.addLore(Component.text("Speed: ", NamedTextColor.GRAY).append(Component.text("10%", NamedTextColor.YELLOW)));
            }
            boolean noDamageResistance = apc.getDamageResistance() == 0;
            icon.addLore(Component.text("Damage Reduction: ", NamedTextColor.GRAY)
                                  .append(Component.text(noDamageResistance ? "None" : apc.getDamageResistance() + "%",
                                          noDamageResistance ? NamedTextColor.RED : NamedTextColor.YELLOW
                                  ))
            );


            // not including skill boost - these display base stats
            List<AbstractAbility> abilities = apc.getAbilities();

            abilities.forEach(ability -> ability.updateDescription(player));

            menu.setItem(0, icon.get(), ACTION_DO_NOTHING);
            ItemStack weaponSkin = playerSettings.getWeaponSkins()
                                                 .getOrDefault(selectedSpec, Weapons.STEEL_SWORD)
                                                 .getItem();
            for (int i = 0; i < abilities.size() && i < 5; i++) {
                AbstractAbility ability = abilities.get(i);
                menu.setItem(i + 2, ability.getItem(i == 0 ? weaponSkin : ability.getAbilityIcon()), ACTION_DO_NOTHING);
            }
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
                        .name(Component.text(helmet.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                        .lore(HELMET_DESCRIPTION)
                        .addLore(Component.empty());
                if (selectedHelmet.contains(helmet)) {
                    builder.addLore(Component.text(">>> ACTIVE <<<", NamedTextColor.GREEN));
                    builder.enchant(Enchantment.OXYGEN, 1);
                } else {
                    builder.addLore(Component.text("> Click to activate! <", NamedTextColor.YELLOW));
                }
                menu.setItem(
                        (i - (pageNumber - 1) * 8) + 1,
                        2,
                        builder.get(),
                        (m, e) -> {
                            player.sendMessage(Component.text("Selected: ", NamedTextColor.YELLOW).append(Component.text(helmet.name, NamedTextColor.GREEN)));
                            playerSettings.setHelmet(helmet.classes, helmet);
                            ArmorManager.resetArmor(player);
                            openArmorMenu(player, pageNumber);
                        }
                );
            }
            int xPosition = 1;
            for (int i = (pageNumber - 1) * 6; i < pageNumber * 6; i++) {
                if (pageNumber == 3 && i == Specializations.VALUES.length) {
                    break;
                }
                ArmorManager.ArmorSets armorSet = ArmorManager.ArmorSets.VALUES[(i % 3) * 3];
                Classes classes = Classes.VALUES[i / 3];
                ItemBuilder builder = new ItemBuilder(i % 3 == 0 ? ArmorManager.ArmorSets.applyColor(armorSet.itemBlue, onBlueTeam) : armorSet.itemBlue)
                        .name(Component.text(armorSet.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                        .lore(ARMOR_DESCRIPTION)
                        .addLore(Component.empty());
                if (playerSettings.getArmorSet(classes) == armorSet) {
                    builder.addLore(Component.text(">>> ACTIVE <<<", NamedTextColor.GREEN));
                    builder.enchant(Enchantment.OXYGEN, 1);
                } else {
                    builder.addLore(Component.text("> Click to activate! <", NamedTextColor.YELLOW));
                }
                menu.setItem(
                        xPosition,
                        3,
                        builder.get(),
                        (m, e) -> {
                            player.sendMessage(Component.text("Selected: ", NamedTextColor.YELLOW).append(Component.text(armorSet.name, NamedTextColor.GREEN)));
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
                                .name(Component.text("Next Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (pageNumber + 1), NamedTextColor.YELLOW))
                                .get(),
                        (m, e) -> openArmorMenu(player, pageNumber + 1)
                );
            } else if (pageNumber == 2) {
                menu.setItem(
                        8,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(Component.text("Next Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (pageNumber + 1), NamedTextColor.YELLOW))
                                .get(),
                        (m, e) -> openArmorMenu(player, pageNumber + 1)
                );
                menu.setItem(
                        0,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(Component.text("Previous Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (pageNumber - 1), NamedTextColor.YELLOW))
                                .get(),
                        (m, e) -> openArmorMenu(player, pageNumber - 1)
                );
            } else if (pageNumber == 3) {
                menu.setItem(
                        0,
                        5,
                        new ItemBuilder(Material.ARROW)
                                .name(Component.text("Previous Page", NamedTextColor.GREEN))
                                .lore(Component.text("Page " + (pageNumber - 1), NamedTextColor.YELLOW))
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
                        .name(Component.text(skillBoost.name + " (" + selectedSpec.name + ")",
                                skillBoost == selectedBoost ? NamedTextColor.GREEN : NamedTextColor.RED
                        ));
                List<Component> lore = new ArrayList<>(WordWrap.wrap(skillBoost == selectedBoost ? skillBoost.selectedDescription : skillBoost.description,
                        130
                ));
                lore.add(Component.empty());
                if (skillBoost == selectedBoost) {
                    lore.add(Component.text("Currently selected!", NamedTextColor.GREEN));
                    builder.enchant(Enchantment.OXYGEN, 1);
                } else {
                    lore.add(Component.text("Click to select!", NamedTextColor.YELLOW));
                }
                builder.lore(lore);
                menu.setItem(
                        i + 2,
                        3,
                        builder.get(),
                        (m, e) -> {
                            player.sendMessage(Component.text("You have changed your weapon boost to: ", NamedTextColor.GREEN).append(Component.text(skillBoost.name + "!")));
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
            List<AbstractAbility> abilities = apc.getAbilities();
            List<AbstractAbility> abilities2 = apc2.getAbilities();
            for (int i = 0; i < abilities.size(); i++) {
                AbstractAbility ability = abilities.get(i);
                AbstractAbility ability2 = abilities2.get(i);
                if (ability.getClass() != selectedBoost.ability) {
                    continue;
                }
                ItemStack icon;
                if (ability == apc.getWeapon()) {
                    icon = new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.STEEL_SWORD).getItem()))
                            .noLore()
                            .get();
                } else {
                    icon = ability.getAbilityIcon();
                }
                ability2.boostSkill(selectedBoost, apc2);
                ability.updateDescription(player);
                ability2.updateDescription(player);
                menu.setItem(3,
                        1,
                        ability.getItem(icon),
                        ACTION_DO_NOTHING
                );
                menu.setItem(5,
                        1,
                        ability2.getItem(icon),
                        ACTION_DO_NOTHING
                );
                break;
            }
            menu.setItem(4, 5, MENU_BACK_PVP, (m, e) -> openPvPMenu(player));
            menu.openForPlayer(player);
        }


    }

    public static class PvEMenu {

        public static final ItemStack MENU_BACK_PVE = new ItemBuilder(Material.ARROW)
                .name(Component.text("Back", NamedTextColor.GREEN))
                .lore(Component.text("To PvE Menu", NamedTextColor.GRAY))
                .get();
        public static final ItemStack WEAPONS_MENU = new ItemBuilder(Material.DIAMOND_SWORD)
                .name(Component.text("Weapons", NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text("View and modify all your weapons, also accessible through The Weaponsmith.", NamedTextColor.GRAY), 160))
                .addLore(
                        Component.empty(),
                        ComponentUtils.CLICK_TO_VIEW
                )
                .get();
        public static final ItemStack ITEMS_MENU = new ItemBuilder(Material.ITEM_FRAME)
                .name(Component.text("Items", NamedTextColor.GREEN))
                .lore(
                        Component.text("View and equip all your Items.", NamedTextColor.GRAY),
                        Component.empty(),
                        ComponentUtils.CLICK_TO_VIEW
                )
                .get();
        public static final ItemStack REWARD_INVENTORY_MENU = new ItemBuilder(Material.ENDER_CHEST)
                .name(Component.text("Reward Inventory", NamedTextColor.GREEN))
                .lore(
                        Component.text("View and claim all your rewards.", NamedTextColor.GRAY),
                        Component.empty(),
                        ComponentUtils.CLICK_TO_VIEW
                )
                .get();
        public static final ItemStack ABILITY_TREE_MENU = new ItemBuilder(Material.GOLD_NUGGET)
                .name(Component.text("Upgrade Talisman", NamedTextColor.GREEN))
                .lore(
                        Component.text("View your ability upgrades.", NamedTextColor.GRAY),
                        Component.empty(),
                        ComponentUtils.CLICK_TO_VIEW
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
                        new ItemBuilder(Material.ZOMBIE_HEAD)
                                .name(Component.text("Mob Drops", NamedTextColor.GREEN))
                                .lore(Arrays.stream(MobDrop.VALUES)
                                            .map(drop -> drop.getCostColoredName(databasePlayer.getPveStats()
                                                                                               .getMobDrops()
                                                                                               .getOrDefault(drop, 0L)))
                                            .collect(Collectors.toList()))
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
                .name(Component.text("Settings", NamedTextColor.AQUA))
                .lore(WordWrap.wrap(Component.text("Allows you to toggle different settings options.", NamedTextColor.GRAY), 150))
                .addLore(
                        Component.empty(),
                        Component.text("Click to edit your settings.", NamedTextColor.GRAY)
                )
                .get();
        public static final ItemStack MENU_SETTINGS_PARTICLE_QUALITY = new ItemBuilder(Material.NETHER_STAR)
                .name(Component.text("Particle Quality", NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text("Allows you to control, or disable, particles and the amount of them.", NamedTextColor.GRAY), 150))
                .get();
        public static final ItemStack MENU_SETTINGS_CHAT_SETTINGS = new ItemBuilder(Material.PAPER)
                .name(Component.text("Chat Settings", NamedTextColor.GREEN))
                .lore(WordWrap.wrap(Component.text("Configure which chat messages you see in-game", NamedTextColor.GRAY), 150))
                .get();

        @Default
        public static void openSettingsMenu(Player player) {
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                Menu menu = new Menu("Settings", 9 * 4);
                menu.setItem(
                        1,
                        1,
                        MENU_SETTINGS_PARTICLE_QUALITY,
                        (m, e) -> openParticleQualityMenu(player)
                );
                menu.setItem(
                        2,
                        1,
                        databasePlayer.getHotkeyMode().item,
                        (m, e) -> {
                            player.performCommand("hotkeymode");
                            openSettingsMenu(player);
                        }
                );
                menu.setItem(
                        3,
                        1,
                        databasePlayer.getFlagMessageMode().item,
                        (m, e) -> {
                            player.performCommand("flagmessagemode");
                            openSettingsMenu(player);
                        }
                );
                menu.setItem(
                        4,
                        1,
                        databasePlayer.getGlowingMode().item,
                        (m, e) -> {
                            player.performCommand("glowingmode");
                            openSettingsMenu(player);
                        }
                );
                menu.setItem(
                        5,
                        1,
                        MENU_SETTINGS_CHAT_SETTINGS,
                        (m, e) -> {
                            Settings.ChatSettings.openChatSettingsMenu(player);
                        }
                );

                menu.setItem(3, 3, MENU_BACK, (m, e) -> WarlordsNewHotbarMenu.SelectionMenu.openWarlordsMenu(player));
                menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
                menu.openForPlayer(player);
            });
        }

        public static void openParticleQualityMenu(Player player) {
            DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                Settings.ParticleQuality selectedParticleQuality = databasePlayer.getParticleQuality();

                Menu menu = new Menu("Particle Quality", 9 * 4);

                Settings.ParticleQuality[] particleQualities = Settings.ParticleQuality.values();
                for (int i = 0; i < particleQualities.length; i++) {
                    Settings.ParticleQuality particleQuality = particleQualities[i];

                    menu.setItem(
                            i + 3,
                            1,
                            new ItemBuilder(particleQuality.item)
                                    .lore(WordWrap.wrap(particleQuality.description, 160))
                                    .addLore(
                                            Component.empty(),
                                            selectedParticleQuality == particleQuality ? Component.text("SELECTED", NamedTextColor.GREEN) : Component.text("Click to select",
                                                    NamedTextColor.YELLOW
                                            )
                                    )
                                    .get(),
                            (m, e) -> {
                                Bukkit.getServer().dispatchCommand(player, "pq " + particleQuality.name());
                                openParticleQualityMenu(player);
                            }
                    );
                }
                menu.setItem(4, 3, MENU_BACK, (m, e) -> openSettingsMenu(player));
                menu.openForPlayer(player);
            });
        }
    }

}
