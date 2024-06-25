package com.ebicep.warlords.menu.generalmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.classes.AbstractPlayerClass;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.MapSymmetryMarker;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.general.*;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.java.NumberFormat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.player.general.ArmorManager.*;
import static com.ebicep.warlords.player.general.Settings.ParticleQuality;

public class WarlordsShopMenu {

    private static final ItemStack MENU_BACK_PREGAME = new ItemBuilder(Material.ARROW)
            .name(Component.text("Back", NamedTextColor.GREEN))
            .lore(Component.text("To Pre-game Menu", NamedTextColor.GRAY))
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
            itemBuilder.addLore(WordWrap.wrap(Component.text(group.description, NamedTextColor.GRAY), 150));
            itemBuilder.addLore(Component.empty());
            itemBuilder.addLore(Component.text("Specializations:", NamedTextColor.GOLD));
            for (Specializations subClass : group.subclasses) {
                itemBuilder.addLore(Component.text(subClass.name, subClass == selectedSpec ? NamedTextColor.GREEN : NamedTextColor.GRAY));
            }
            itemBuilder.addLore(Component.empty());

            itemBuilder.addLore(ExperienceManager.getProgressString(experience, level + 1));
            itemBuilder.addLore(Component.empty());
            itemBuilder.addLore(WordWrap.wrap(Component.text("Click here to select a " + group.name + " specialization", NamedTextColor.YELLOW), 150));
            menu.setItem(
                    i + 1 + (i >= 3 ? 1 : 0),
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
                                   .append(ExperienceManager.getPrestigeLevelString(player.getUniqueId(), spec)));
            itemBuilder.addLore(WordWrap.wrap(spec.getDescription(), 150));
            itemBuilder.addLore(Component.empty());
            long experience = ExperienceManager.getExperienceForSpec(player.getUniqueId(), spec);
            int level = (int) ExperienceManager.calculateLevelFromExp(experience);
            itemBuilder.addLore(ExperienceManager.getProgressString(experience, level + 1));
            itemBuilder.addLore(Component.empty());
            if (spec == selectedSpec) {
                itemBuilder.addLore(Component.text(">>> ACTIVE <<<", NamedTextColor.GREEN));
                itemBuilder.enchant(Enchantment.OXYGEN, 1);
            } else {
                itemBuilder.addLore(Component.text("> Click to activate <", NamedTextColor.YELLOW));
            }
            menu.setItem(
                    9 / 2 - values.size() / 2 + i * 2 - 1,
                    1,
                    itemBuilder.get(),
                    (m, e) -> {
                        player.sendMessage(Component.text("You have changed your specialization to: ", NamedTextColor.GREEN)
                                                    .append(Component.text(spec.name, NamedTextColor.AQUA)));
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
                        PlayerSettings playerSettings = PlayerSettings.getPlayerSettings(player.getUniqueId());
                        playerSettings.setSelectedSpec(spec);
                        ArmorManager.resetArmor(player);

                        AbstractPlayerClass apc = spec.create.get();
                        ItemStack weaponSkin = playerSettings.getWeaponSkins()
                                                             .getOrDefault(spec, Weapons.STEEL_SWORD)
                                                             .getItem();
                        player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(weaponSkin))
                                .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                                .noLore()
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

    public static void openSkillBoostMenu(Player player, Specializations selectedSpec, Consumer<Menu> menuSupplier) {
        SkillBoosts selectedBoost = PlayerSettings.getPlayerSettings(player.getUniqueId()).getSkillBoostForSpec(selectedSpec);
        Menu menu = new Menu("Skill Boost", 9 * 6);
        List<SkillBoosts> values = selectedSpec.skillBoosts;
        for (int i = 0; i < values.size(); i++) {
            SkillBoosts skillBoost = values.get(i);
            ItemBuilder builder = new ItemBuilder(selectedSpec.specType.itemStack)
                    .name(Component.text(skillBoost.name + " (" + selectedSpec.name + ")",
                            skillBoost == selectedBoost ? NamedTextColor.GREEN : NamedTextColor.RED
                    ));
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
                        player.sendMessage(Component.text("You have changed your weapon boost to: ", NamedTextColor.GREEN)
                                                    .append(Component.text(skillBoost.name, NamedTextColor.AQUA)));

                        PlayerSettings.getPlayerSettings(player.getUniqueId()).setSkillBoostForSpec(selectedSpec, skillBoost);
                        openSkillBoostMenu(player, selectedSpec, menuSupplier);

                        DatabaseManager.updatePlayer(player.getUniqueId(), databasePlayer -> databasePlayer.getSpec(selectedSpec).setSkillBoost(skillBoost));
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
            if (!selectedBoost.ability.isAssignableFrom(ability.getClass())) {
                continue;
            }
            ItemStack icon;
            if (ability == apc.getWeapon()) {
                icon = apc.getWeapon().getItem(playerSettings.getWeaponSkins().getOrDefault(selectedSpec, Weapons.STEEL_SWORD).getItem());
            } else {
                icon = ability.getAbilityIcon();
            }
            ability2.boostSkill(selectedBoost, new WarlordsPlayer(player, selectedSpec));
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
        menuSupplier.accept(menu);
        menu.openForPlayer(player);
    }

    public static void openSkillBoostMenu(Player player, Specializations selectedSpec) {
        openSkillBoostMenu(player, selectedSpec, menu -> menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player)));
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
                                                        .append(Component.text("'s weapon skin to: "))
                                                        .append(Component.text(weapon.getName() + "!", NamedTextColor.AQUA)));
                            playerSettings.getWeaponSkins().put(selectedSpec, weapon);
                            openWeaponMenu(player, pageNumber);
                            AbstractPlayerClass apc = selectedSpec.create.get();
                            player.getInventory().setItem(1, new ItemBuilder(apc.getWeapon().getItem(playerSettings.getWeaponSkins()
                                                                                                                   .getOrDefault(selectedSpec,
                                                                                                                           Weapons.FELFLAME_BLADE
                                                                                                                   )
                                                                                                                   .getItem()))
                                    .name(Component.text("Weapon Skin Preview", NamedTextColor.GREEN))
                                    .noLore()
                                    .get());

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
            if (pageNumber == 3 && i == 15) {
                break;
            }
            ArmorSets armorSet = ArmorSets.VALUES[(i % 3) * 3];
            Classes classes = Classes.VALUES[i / 3];
            ItemBuilder builder = new ItemBuilder(i % 3 == 0 ? ArmorSets.applyColor(armorSet.itemBlue, onBlueTeam) : armorSet.itemBlue)
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

        menu.setItem(4, 5, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
        menu.openForPlayer(player);
    }

    public static void openSettingsMenu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
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
                    databasePlayer.getHotkeyMode().item,
                    (m, e) -> {
                        player.performCommand("hotkeymode");
                        openSettingsMenu(player);
                    }
            );
            menu.setItem(
                    5,
                    1,
                    databasePlayer.getFlagMessageMode().item,
                    (m, e) -> {
                        player.performCommand("flagmessagemode");
                        openSettingsMenu(player);
                    }
            );

            menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
            menu.openForPlayer(player);
        });
    }

    public static void openParticleQualityMenu(Player player) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
            ParticleQuality selectedParticleQuality = databasePlayer.getParticleQuality();

            Menu menu = new Menu("Particle Quality", 9 * 4);

            ParticleQuality[] particleQualities = ParticleQuality.values();
            for (int i = 0; i < particleQualities.length; i++) {
                ParticleQuality particleQuality = particleQualities[i];

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
            menu.setItem(4, 3, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));
            menu.openForPlayer(player);
        });
    }

    public static void openTeamMenu(Player player) {
        Team selectedTeam = PlayerSettings.getPlayerSettings(player.getUniqueId()).getWantedTeam();
        Menu menu = new Menu("Team Selector", 9 * 4);
        List<Team> values = new ArrayList<>(Arrays.asList(Team.RED, Team.BLUE));
        for (int i = 0; i < values.size(); i++) {
            Team team = values.get(i);
            ItemBuilder builder = new ItemBuilder(team.getWool())
                    .name(Component.text(team.getName(), team.getTeamColor()));
            List<Component> lore = new ArrayList<>();
            if (team == selectedTeam) {
                lore.add(Component.text("Currently selected!", NamedTextColor.GREEN));
                builder.enchant(Enchantment.OXYGEN, 1);
            } else {
                lore.add(Component.text("Click to select", NamedTextColor.YELLOW));
            }
            builder.lore(lore);
            menu.setItem(
                    9 / 2 - values.size() % 2 + i * 2 - 1,
                    1,
                    builder.get(),
                    (m, e) -> {
                        if (selectedTeam != team) {
                            player.sendMessage(Component.text("You have joined the ", NamedTextColor.GREEN)
                                                        .append(Component.text(team.getName(), team.getTeamColor()))
                                                        .append(Component.text(" team!"))
                            );
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
        boolean noDamageResistance = apc.getDamageResistance() == 0;
        icon.addLore(Component.text("Damage Reduction: ", NamedTextColor.GRAY)
                              .append(Component.text(noDamageResistance ? "None" : NumberFormat.formatOptionalTenths(apc.getDamageResistance()) + "%",
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
        menu.setItem(8, MENU_BACK_PREGAME, (m, e) -> openMainMenu(player));

        menu.openForPlayer(player);
    }
}