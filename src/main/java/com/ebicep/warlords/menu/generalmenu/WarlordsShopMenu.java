package com.ebicep.warlords.menu.generalmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.MapSymmetryMarker;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
import static com.ebicep.warlords.player.general.Settings.ParticleQuality;
import static com.ebicep.warlords.player.general.Specializations.APOTHECARY;
import static com.ebicep.warlords.util.bukkit.ItemBuilder.*;

public class WarlordsShopMenu {

    private static final ItemStack MENU_BACK_PREGAME = new ItemBuilder(Material.ARROW)
            .name(Component.text("Back", NamedTextColor.GREEN))
            .loreLEGACY(ChatColor.GRAY + "To Pre-game Menu")
            .get();
    private static final ItemStack MENU_ARCADE = new ItemBuilder(Material.GOLD_BLOCK)
            .name(Component.text("Mini Games", NamedTextColor.GREEN))
            .loreLEGACY("§7Try your luck in rerolling or\nopening skin shards here!\n")
            .get();

    public static void openMainMenu(Player player) {
        Specializations selectedSpec = PlayerSettings.getPlayerSettings(player.getUniqueId()).getSelectedSpec();

        Menu menu = new Menu("Warlords Shop", 9 * 6);
        Classes[] values = Classes.VALUES;
        for (int i = 0; i < values.length; i++) {
            Classes group = values[i];
            long experience = ExperienceManager.getExperienceForClass(player.getUniqueId(), group);
            int level = (int) ExperienceManager.calculateLevelFromExp(experience);
            ItemBuilder itemBuilder = new ItemBuilder(group.item)
                    .name(Component.text(group.name, NamedTextColor.GOLD)
                                   .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                                   .append(Component.text("Lv" + ExperienceManager.getLevelString(level), NamedTextColor.GRAY))
                                   .append(Component.text("]", NamedTextColor.DARK_GRAY)));
            itemBuilder.addLore(WordWrap.wrapWithNewline(ChatColor.GRAY + group.description, 150));
            itemBuilder.addLore("");
            itemBuilder.addLore(ChatColor.GOLD + "Specializations:");
            for (Specializations subClass : group.subclasses) {
                itemBuilder.addLore((subClass == selectedSpec ? ChatColor.GREEN : ChatColor.GRAY) + subClass.name);
            }
            itemBuilder.addLore("");

            itemBuilder.addLoreC(ExperienceManager.getProgressString(experience, level + 1));
            itemBuilder.addLore("");
            itemBuilder.addLore(ChatColor.YELLOW + "Click here to select a " + group.name + "\n" + ChatColor.YELLOW + "specialization");
            menu.setItem(
                    9 / 2 - values.length / 2 + i * 2 - 2,
                    1,
                    itemBuilder.get(),
                    (m, e) -> {
                        openClassMenu(player, group);
                    }
            );
        }
        menu.setItem(1, 3, WarlordsNewHotbarMenu.PvPMenu.MENU_SKINS, (m, e) -> openWeaponMenu(player, 1));
        menu.setItem(3, 3, WarlordsNewHotbarMenu.PvPMenu.MENU_ARMOR_SETS, (m, e) -> openArmorMenu(player, 1));
        menu.setItem(5, 3, WarlordsNewHotbarMenu.PvPMenu.MENU_BOOSTS, (m, e) -> openSkillBoostMenu(player, selectedSpec));
        menu.setItem(7, 3, WarlordsNewHotbarMenu.SettingsMenu.MENU_SETTINGS, (m, e) -> openSettingsMenu(player));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.setItem(4, 2, WarlordsNewHotbarMenu.PvPMenu.MENU_ABILITY_DESCRIPTION, (m, e) -> openLobbyAbilityMenu(player));
        menu.openForPlayer(player);
    }


    public static void openClassMenu(Player player, Classes selectedGroup) {
        Specializations selectedSpec = PlayerSettings.getPlayerSettings(player.getUniqueId()).getSelectedSpec();
        Menu menu = new Menu(selectedGroup.name, 9 * 4);
        List<Specializations> values = selectedGroup.subclasses;
        for (int i = 0; i < values.size(); i++) {
            Specializations spec = values.get(i);
            ItemBuilder itemBuilder = new ItemBuilder(spec.specType.itemStack)
                    .name(Component.text("Specialization: " + spec.name, NamedTextColor.GREEN)
                                   .append(Component.text(" [", NamedTextColor.DARK_GRAY))
                                   .append(Component.text("Lv" + ExperienceManager.getLevelString(
                                           ExperienceManager.getLevelForSpec(player.getUniqueId(),
                                                   spec
                                           )), NamedTextColor.GRAY))
                                   .append(Component.text("] ", NamedTextColor.DARK_GRAY))
                                   .append(ExperienceManager.getPrestigeLevelString(player.getUniqueId(), spec)))
                    .flags(ItemFlag.HIDE_ENCHANTS);
            itemBuilder.addLore(spec.description);
            itemBuilder.addLore("");
            long experience = ExperienceManager.getExperienceForSpec(player.getUniqueId(), spec);
            int level = (int) ExperienceManager.calculateLevelFromExp(experience);
            itemBuilder.addLoreC(ExperienceManager.getProgressString(experience, level + 1));
            itemBuilder.addLore("");
            if (spec == selectedSpec) {
                itemBuilder.addLore(ChatColor.GREEN + ">>> ACTIVE <<<");
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            } else {
                itemBuilder.addLore(ChatColor.YELLOW + "> Click to activate <");
            }
            menu.setItem(
                    9 / 2 - values.size() / 2 + i * 2 - 1,
                    1,
                    itemBuilder.get(),
                    (m, e) -> {
                        player.sendMessage(ChatColor.GREEN + "You have changed your specialization to: §b" + spec.name);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                        playerSettings.setSelectedSpec(spec);
                        ArmorManager.resetArmor(player);

                        AbstractPlayerClass apc = spec.create.get();
                        player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                                                                                                               .getOrDefault(spec, Weapons.FELFLAME_BLADE)
                                                                                                               .getItem()))
                                .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                                .get());

                        openClassMenu(player, selectedGroup);
                        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> {
                            databasePlayer.setLastSpec(spec);
                        });
                    }
            );
        }
        menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));

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
                    ))
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<Component> lore = new ArrayList<>(WordWrap.wrap(skillBoost == selectedBoost ? skillBoost.selectedDescription : skillBoost.description, 130));
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
                        player.sendMessage(ChatColor.GREEN + "You have changed your weapon boost to: §b" + skillBoost.name + "!");
                        PlayerSettings.getPlayerSettings(player.getUniqueId()).setSkillBoostForSelectedSpec(skillBoost);
                        openSkillBoostMenu(player, selectedSpec);

                        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> databasePlayer.getSpec(selectedSpec).setSkillBoost(skillBoost));
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
        menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
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
                        .name(Component.text(weapon.getName(), NamedTextColor.GREEN))
                        .flags(ItemFlag.HIDE_ENCHANTS);
                List<String> lore = new ArrayList<>();

                if (weapon == selectedWeapon) {
                    lore.add(ChatColor.GREEN + "Currently selected!");
                    builder.enchant(Enchantment.OXYGEN, 1);
                } else {
                    lore.add(ChatColor.YELLOW + "Click to select!");
                }

                builder.loreLEGACY(lore);
            } else {
                builder = new ItemBuilder(Material.BARRIER).name(Component.text("Locked Weapon Skin", NamedTextColor.RED));
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
                            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                                                                                                                   .getOrDefault(selectedSpec,
                                                                                                                           Weapons.FELFLAME_BLADE
                                                                                                                   )
                                                                                                                   .getItem()))
                                    .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                                    .get());

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
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (pageNumber - 1))
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
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openWeaponMenu(player, pageNumber + 1)
            );
        }


        menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }


    public static void openArmorMenu(Player player, int pageNumber) {
        boolean onBlueTeam = Warlords.getGameManager()
                                     .getPlayerGame(player.getUniqueId())
                                     .map(g -> g.getPlayerTeam(player.getUniqueId()))
                                     .orElse(Team.BLUE) == Team.BLUE;
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        List<Helmets> selectedHelmet = playerSettings.getHelmets();

        Menu menu = new Menu("Armor Sets & Helmets", 9 * 6);

        Helmets[] helmets = Helmets.VALUES;
        for (int i = (pageNumber - 1) * 8; i < pageNumber * 8 && i < helmets.length; i++) {
            Helmets helmet = helmets[i];
            ItemBuilder builder = new ItemBuilder(onBlueTeam ? helmet.itemBlue : helmet.itemRed)
                    .name(Component.text(helmet.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(HELMET_DESCRIPTION, "")
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
            ArmorSets armorSet = ArmorSets.VALUES[(i % 3) * 3];
            Classes classes = Classes.VALUES[i / 3];
            ItemBuilder builder = new ItemBuilder(i % 3 == 0 ? ArmorSets.applyColor(armorSet.itemBlue, onBlueTeam) : armorSet.itemBlue)
                    .name(Component.text(armorSet.name, onBlueTeam ? NamedTextColor.BLUE : NamedTextColor.RED))
                    .loreLEGACY(ARMOR_DESCRIPTION, "")
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
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openArmorMenu(player, pageNumber + 1)
            );
        } else if (pageNumber == 2) {
            menu.setItem(
                    8,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (pageNumber + 1))
                            .get(),
                    (m, e) -> openArmorMenu(player, pageNumber + 1)
            );
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openArmorMenu(player, pageNumber - 1)
            );
        } else if (pageNumber == 3) {
            menu.setItem(
                    0,
                    5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .loreLEGACY(ChatColor.YELLOW + "Page " + (pageNumber - 1))
                            .get(),
                    (m, e) -> openArmorMenu(player, pageNumber - 1)
            );
        }

        menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }


    public static void openSettingsMenu(Player player) {
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());

        Menu menu = new Menu("Settings", 9 * 4);
        menu.setItem(
                1,
                1,
                WarlordsNewHotbarMenu.SettingsMenu.MENU_SETTINGS_PARTICLE_QUALITY,
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

        menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }


    public static void openParticleQualityMenu(Player player) {
        ParticleQuality selectedParticleQuality = PlayerSettings.getPlayerSettings(player.getUniqueId()).getParticleQuality();

        Menu menu = new Menu("Particle Quality", 9 * 4);

        ParticleQuality[] particleQualities = ParticleQuality.values();
        for (int i = 0; i < particleQualities.length; i++) {
            ParticleQuality particleQuality = particleQualities[i];

            menu.setItem(
                    i + 3,
                    1,
                    new ItemBuilder(particleQuality.item)
                            .loreLEGACY(particleQuality.description,
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
        menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openTeamMenu(Player player) {
        Team selectedTeam = PlayerSettings.getPlayerSettings(player.getUniqueId()).getWantedTeam();
        Menu menu = new Menu("Team Selector", 9 * 4);
        List<Team> values = new ArrayList<>(Arrays.asList(Team.values()));
        for (int i = 0; i < values.size(); i++) {
            Team team = values.get(i);
            ItemBuilder builder = new ItemBuilder(team.getItem())
                    .name(Component.text(team.getName(), team.teamColor))
                    .flags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            if (team == selectedTeam) {
                lore.add(ChatColor.GREEN + "Currently selected!");
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(ChatColor.YELLOW + "Click to select!");
            }
            builder.loreLEGACY(lore);
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
                                                                                    .getOppositeLocation(game,
                                                                                            oldTeam,
                                                                                            team,
                                                                                            player.getLocation(),
                                                                                            randomLobbyLocation.getLocation()
                                                                                    );
                                    player.teleport(teleportDestination);
                                    Warlords.setRejoinPoint(player.getUniqueId(), teleportDestination);
                                }
                            }
                            PlayerSettings.getPlayerSettings(player.getUniqueId()).setWantedTeam(team);
                            ArmorManager.resetArmor(player);
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
        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
        Specializations selectedSpec = playerSettings.getSelectedSpec();
        AbstractPlayerClass apc = selectedSpec.create.get();

        ItemBuilder icon = new ItemBuilder(selectedSpec.specType.itemStack);
        icon.name(Component.text(selectedSpec.name, NamedTextColor.GREEN));
        icon.addLore(
                Component.empty(),
                Component.text("Specialization Stats:", NamedTextColor.GOLD),
                Component.empty(),
                Component.text("Health: ", NamedTextColor.GRAY)
                         .append(Component.text(NumberFormat.formatOptionalHundredths(apc.getMaxHealth()), NamedTextColor.GREEN)),
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
        menu.setItem(8, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));

        menu.openForPlayer(player);
    }
}