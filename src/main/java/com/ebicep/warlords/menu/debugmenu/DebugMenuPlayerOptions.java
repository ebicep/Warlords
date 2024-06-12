package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.flags.GroundFlagLocation;
import com.ebicep.warlords.game.flags.PlayerFlagLocation;
import com.ebicep.warlords.game.flags.SpawnFlagLocation;
import com.ebicep.warlords.game.option.marker.DebugLocationMarker;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.game.option.marker.LobbyLocationMarker;
import com.ebicep.warlords.game.option.marker.MapSymmetryMarker;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.menu.MenuItemPairList;
import com.ebicep.warlords.permissions.Permissions;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.SkillBoosts;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.warlords.Utils;
import io.github.rapha149.signgui.SignGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;


public class DebugMenuPlayerOptions {

    public static void openPlayerMenu(Player player, WarlordsEntity target) {
        if (target == null) {
            return;
        }
        String targetName = target.getName();
        Component coloredName = target.getColoredName();

        Menu menu = new Menu("Player Options: " + targetName, 9 * 5);

        MenuItemPairList firstRow = new MenuItemPairList();

        firstRow.add(new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                        .name(Component.text(target.isNoEnergyConsumption() ? "Enable Energy Consumption" : "Disable Energy Consumption",
                                target.isNoEnergyConsumption() ? NamedTextColor.GREEN : NamedTextColor.RED
                        ))
                        .get(),
                (m, e) -> {
                    player.performCommand("wl energy " + (target.isNoEnergyConsumption() ? "disable " : "enable ") + targetName);
                    openPlayerMenu(player, target);
                }
        );
        firstRow.add(new ItemBuilder(Material.GRAY_DYE)
                        .name(Component.text(target.isDisableCooldowns() ? "Enable Cooldowns Timers" : "Disable Cooldown Timers",
                                target.isDisableCooldowns() ? NamedTextColor.GREEN : NamedTextColor.RED
                        ))
                        .get(),
                (m, e) -> {
                    player.performCommand("wl cooldown " + (target.isDisableCooldowns() ? "enable " : "disable ") + targetName);
                    openPlayerMenu(player, target);
                }
        );
        firstRow.add(new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .name(Component.text(target.isTakeDamage() ? "Disable Taking Damage" : "Enable Taking Damage",
                                target.isTakeDamage() ? NamedTextColor.GREEN : NamedTextColor.RED
                        ))
                        .get(),
                (m, e) -> {
                    player.performCommand("wl takedamage " + (target.isTakeDamage() ? "disable " : "enable ") + targetName);
                    openPlayerMenu(player, target);
                }
        );
        firstRow.add(new ItemBuilder(Material.RABBIT_FOOT)
                        .name(Component.text(target.isCanCrit() ? "Disable Crits" : "Enable Crits",
                                target.isCanCrit() ? NamedTextColor.RED : NamedTextColor.GREEN
                        ))
                        .get(),
                (m, e) -> {
                    player.performCommand("wl crits " + (target.isCanCrit() ? "disable " : "enable ") + targetName);
                    openPlayerMenu(player, target);
                }
        );
        firstRow.add(new ItemBuilder(Material.AIR)
                        .get(),
                (m, e) -> {
                }
        );
        firstRow.add(new ItemBuilder(Material.SPLASH_POTION, PotionType.INSTANT_DAMAGE)
                        .name(Component.text("Kill", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    target.addDamageInstance(target, "God", 100000, 100000, 0, 100);
                    sendDebugMessage(player, Component.text("Killed " + targetName, NamedTextColor.GREEN));
                }
        );
        firstRow.add(new ItemBuilder((PlayerSettings.getPlayerSettings(player.getUniqueId()).getWantedTeam().enemy().woolItem))
                        .name(Component.text("Swap to the ", NamedTextColor.GREEN)
                                       .append(PlayerSettings.getPlayerSettings(player.getUniqueId())
                                                             .getWantedTeam() == Team.BLUE ? Team.RED.coloredPrefix() : Team.BLUE.coloredPrefix())
                                       .append(Component.text(" team")))
                        .get(),
                (m, e) -> {
                    Game game = target.getGame();
                    Team currentTeam = target.getTeam();
                    Team otherTeam = target.getTeam().enemy();
                    game.setPlayerTeam(target.getUuid(), otherTeam);
                    target.setTeam(otherTeam);
                    PlayerSettings.getPlayerSettings(target.getUuid()).setWantedTeam(otherTeam);
                    LobbyLocationMarker randomLobbyLocation = LobbyLocationMarker.getRandomLobbyLocation(game, otherTeam);
                    if (randomLobbyLocation != null) {
                        Location teleportDestination = MapSymmetryMarker.getSymmetry(game)
                                                                        .getOppositeLocation(game,
                                                                                currentTeam,
                                                                                otherTeam,
                                                                                target.getLocation(),
                                                                                randomLobbyLocation.getLocation()
                                                                        );
                        target.teleport(teleportDestination);
                    }
                    target.updateArmor();
                    openPlayerMenu(player, target);
                    sendDebugMessage(player, Component.text("Swapped ", NamedTextColor.GREEN)
                                                      .append(coloredName)
                                                      .append(Component.text(" to the "))
                                                      .append(otherTeam.coloredPrefix())
                                                      .append(Component.text(" team"))
                    );
                }
        );

        for (int i = 0; i < firstRow.size(); i++) {
            menu.setItem(i + 1, 1, firstRow.get(i).getA(), firstRow.get(i).getB());
        }

        MenuItemPairList secondRow = new MenuItemPairList();
        secondRow.add(new ItemBuilder(Material.SUGAR)
                        .name(Component.text("Modify Speed", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                }
        );
        secondRow.add(new ItemBuilder(Material.SPLASH_POTION, PotionType.INSTANT_HEAL)
                        .name(Component.text("Add Health", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    SignGUI.builder()
                           .setLines("", "^^^^^^^", "Enter heal amount", "greater than 0")
                           .setHandler((p, lines) -> {
                               String amount = lines.getLine(0);
                               try {
                                   int amountNumber = Integer.parseInt(amount);
                                   if (amountNumber < 0) {
                                       throw new NumberFormatException();
                                   }
                                   new BukkitRunnable() {
                                       @Override
                                       public void run() {
                                           target.addHealingInstance(target, "God", amountNumber, amountNumber, 0, 100);
                                           sendDebugMessage(player, Component.text("Healed ", NamedTextColor.GREEN)
                                                                             .append(coloredName)
                                                                             .append(Component.text(" for " + amountNumber))
                                           );
                                       }
                                   }.runTaskLater(Warlords.getInstance(), 1);
                               } catch (NumberFormatException exception) {
                                   p.sendMessage(Component.text("Invalid number", NamedTextColor.RED));
                               }
                               openPlayerMenuAfterTick(player, target);
                               return null;
                           }).build().open(player);
                }
        );
        secondRow.add(new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(Component.text("Take Damage", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    SignGUI.builder()
                           .setLines("", "^^^^^^^", "Enter damage amount", "greater than 0")
                           .setHandler((p, lines) -> {
                               String amount = lines.getLine(0);
                               try {
                                   int amountNumber = Integer.parseInt(amount);
                                   if (amountNumber < 0) {
                                       throw new NumberFormatException();
                                   }
                                   new BukkitRunnable() {
                                       @Override
                                       public void run() {
                                           target.addDamageInstance(target, "God", amountNumber, amountNumber, 0, 100);
                                           sendDebugMessage(player, Component.text("Damaged ", NamedTextColor.GREEN)
                                                                             .append(coloredName)
                                                                             .append(Component.text(" for " + amountNumber))
                                           );
                                       }
                                   }.runTaskLater(Warlords.getInstance(), 1);
                               } catch (NumberFormatException exception) {
                                   p.sendMessage(Component.text("Invalid number", NamedTextColor.RED));
                               }
                               openPlayerMenuAfterTick(player, target);
                               return null;
                           }).build().open(player);
                }
        );
        secondRow.add(new ItemBuilder(Material.BREWING_STAND)
                        .name(Component.text("Cooldowns", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> PlayerOptionMenus.openCooldownsMenu(player, target)
        );
        secondRow.add(new ItemBuilder(Material.ENDER_EYE)
                        .name(Component.text("Teleport", NamedTextColor.GREEN))
                        .lore(
                                Component.textOfChildren(
                                        Component.text("LEFT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" - ", NamedTextColor.GRAY),
                                        Component.text("Teleport to", NamedTextColor.YELLOW)
                                ),
                                Component.textOfChildren(
                                        Component.text("RIGHT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                                        Component.text(" - ", NamedTextColor.GRAY),
                                        Component.text("Teleport here", NamedTextColor.YELLOW)
                                )
                        )
                        .get(),
                (m, e) -> {
                    if (e.isLeftClick()) {
                        PlayerOptionMenus.openTeleportLocations(player, target);
                    } else {
                        target.teleport(player.getLocation());
                        sendDebugMessage(player, Component.text("Teleported ", NamedTextColor.GREEN)
                                                          .append(coloredName)
                                                          .append(Component.text(" to you"))
                        );
                    }
                }
        );
        secondRow.add(new ItemBuilder(Material.BLACK_BANNER)
                        .name(Component.text("Flag Options", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> PlayerOptionMenus.openFlagOptionMenu(player, target)
        );
        secondRow.add(new ItemBuilder(Material.NETHER_STAR)
                        .name(Component.text("Change Spec", NamedTextColor.GREEN))
                        .get(),
                (m, e) -> {
                    if (GameMode.isPvE(target.getGame().getGameMode())) {
                        if (Permissions.isAdmin(player)) {
                            sendDebugMessage(player, Component.text("Changing spec is not advised.", NamedTextColor.RED));
                        } else {
                            sendDebugMessage(player, Component.text("Cannot change spec in wave defense.", NamedTextColor.RED));
                            return;
                        }
                    }
                    PlayerOptionMenus.openSpecMenu(player, target);
                }
        );

        for (int i = 0; i < secondRow.size(); i++) {
            menu.setItem(i + 1, 2, secondRow.get(i).getA(), secondRow.get(i).getB());
        }

        menu.setItem(3, 4, MENU_BACK, (m, e) -> {
            if (player.getUniqueId() == target.getUuid()) {
                DebugMenu.openDebugMenu(player);
            } else {
                DebugMenuTeamOptions.openTeamSelectorMenu(player, target.getGame());
            }
        });
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    private static void openPlayerMenuAfterTick(Player player, WarlordsEntity target) {
        new BukkitRunnable() {
            @Override
            public void run() {
                openPlayerMenu(player, target);
            }
        }.runTaskLater(Warlords.getInstance(), 1);
    }

    public static void setSpec(Player player, WarlordsEntity target, Specializations selectedSpec, SkillBoosts skillBoost) {
        target.setSpec(selectedSpec, skillBoost);
        sendDebugMessage(player, Component.text("Changed ", NamedTextColor.GREEN)
                                          .append(target.getColoredName())
                                          .append(Component.text("'s spec to " + selectedSpec.name))
        );
    }

    static class PlayerOptionMenus {

        public static void openCooldownsMenu(Player player, WarlordsEntity target) {
            String name = target.getName();
            Component coloredName = target.getColoredName();

            int menuY = Math.min(5 + StatusEffectCooldowns.values().length / 7, 6);
            Menu menu = new Menu("Cooldowns: " + name, 9 * menuY);

            MenuItemPairList firstRow = new MenuItemPairList();
            firstRow.add(new ItemBuilder(Material.BEACON)
                            .name(Component.text("Manage Cooldowns", NamedTextColor.AQUA))
                            .get(),
                    (m, e) -> {
                        CooldownOptionMenus.openCooldownManagerMenu(player, target);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (PlainTextComponentSerializer.plainText().serialize(player.getOpenInventory().title()).equals("CD Manager: " + name)) {
                                    CooldownOptionMenus.openCooldownManagerMenu(player, target);
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 20, 20);
                    }
            );
            firstRow.add(new ItemBuilder(Material.MILK_BUCKET)
                            .name(Component.text("Clear All Cooldowns", NamedTextColor.AQUA))
                            .get(),
                    (m, e) -> {
                        target.getCooldownManager().clearAllCooldowns();
                        sendDebugMessage(player, Component.text("Cleared ", NamedTextColor.GREEN)
                                                          .append(coloredName)
                                                          .append(Component.text("'s Cooldowns"))
                        );
                    }
            );

            firstRow.add(new ItemBuilder(Material.MILK_BUCKET)
                            .name(Component.text("Clear All Buffs", NamedTextColor.AQUA))
                            .get(),
                    (m, e) -> {
                        target.getCooldownManager().removeBuffCooldowns();
                        sendDebugMessage(player, Component.text("Cleared ", NamedTextColor.GREEN)
                                                          .append(coloredName)
                                                          .append(Component.text("'s Buffs"))
                        );
                    }
            );
            firstRow.add(new ItemBuilder(Material.MILK_BUCKET)
                            .name(Component.text("Clear All Debuffs", NamedTextColor.AQUA))
                            .get(),
                    (m, e) -> {
                        target.getCooldownManager().removeDebuffCooldowns();
                        sendDebugMessage(player, Component.text("Cleared ", NamedTextColor.GREEN)
                                                          .append(coloredName)
                                                          .append(Component.text("'s Debuffs"))
                        );
                    }
            );
            firstRow.add(new ItemBuilder(Material.MILK_BUCKET)
                            .name(Component.text("Clear All Abilities", NamedTextColor.AQUA))
                            .get(),
                    (m, e) -> {
                        target.getCooldownManager().removeAbilityCooldowns();
                        sendDebugMessage(player, Component.text("Cleared ", NamedTextColor.GREEN)
                                                          .append(coloredName)
                                                          .append(Component.text("'s Ability Cooldowns"))
                        );
                    }
            );

            for (int i = 0; i < firstRow.size(); i++) {
                menu.setItem(i + 1, 1, firstRow.get(i).getA(), firstRow.get(i).getB());
            }

            //effects
            int yLevel = 1;
            for (int i = 0; i < StatusEffectCooldowns.values().length; i++) {
                if (i % 7 == 0) {
                    yLevel++;
                }
                StatusEffectCooldowns cooldown = StatusEffectCooldowns.values()[i];
                menu.setItem((i % 7) + 1, yLevel,
                        new ItemBuilder(cooldown.itemStack)
                                .name(Component.text(cooldown.name, cooldown.color))
                                .get(),
                        (m, e) -> {
                            SignGUI.builder()
                                   .setLines("", "^^^^^^^", "Enter time of", "cooldown in seconds")
                                   .setHandler((p, lines) -> {
                                       new BukkitRunnable() {
                                           @Override
                                           public void run() {
                                               String amount = lines.getLine(0);
                                               try {
                                                   int amountNumber = Integer.parseInt(amount);
                                                   target.getCooldownManager().addRegularCooldown(cooldown.name,
                                                           cooldown.actionBarName,
                                                           cooldown.cooldownClass,
                                                           cooldown.cooldownObject,
                                                            target,
                                                            cooldown.cooldownType,
                                                            cooldownManager -> {
                                                            },
                                                            amountNumber * 20
                                                    );
                                                    if (cooldown == StatusEffectCooldowns.SPEED) {
                                                        target.addSpeedModifier(target, "Speed Powerup", 40, amountNumber * 20, "BASE");
                                                    }
                                                    sendDebugMessage(player, Component.text("Gave ", NamedTextColor.GREEN)
                                                                                      .append(coloredName)
                                                                                      .append(Component.text(" " + amountNumber + " seconds of " + cooldown.name))
                                                    );
                                               } catch (NumberFormatException exception) {
                                                   p.sendMessage(Component.text("Invalid number", NamedTextColor.RED));
                                               }
                                               openCooldownsMenu(player, target);
                                           }
                                       }.runTaskLater(Warlords.getInstance(), 1);
                                       return null;
                                   }).build().open(player);
                        }
                );
            }
            menu.setItem(3, menuY - 1, MENU_BACK, (m, e) -> openPlayerMenu(player, target));
            menu.setItem(4, menuY - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openTeleportLocations(Player player, WarlordsEntity target) {
            Menu menu = new Menu("Teleport To: " + target.getName(), 9 * 5);

            Game game = target.getGame();
            int x = 0;
            int y = 0;
            for (DebugLocationMarker marker : game.getMarkers(DebugLocationMarker.class)) {
                menu.setItem(x, y, marker.getAsItem(), (m, e) -> {
                    target.teleport(marker.getLocation());
                    sendDebugMessage(player, Component.text("Teleported ", NamedTextColor.GREEN)
                                                      .append(target.getColoredName())
                                                      .append(Component.text(" to "))
                                                      .append(marker.getName())
                    );
                });

                x++;

                if (x > 8) {
                    x = 0;
                    y++;
                }
            }
            menu.setItem(3, 4, MENU_BACK, (m, e) -> openPlayerMenu(player, target));
            menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openFlagOptionMenu(Player player, WarlordsEntity target) {
            Menu menu = new Menu("Flag Options: " + target.getName(), 9 * 4);
            int row = 0;
            for (FlagHolder holder : target.getGame().getMarkers(FlagHolder.class)) {
                if (holder.getTeam() == target.getTeam()) {
                    continue;
                }
                row++;
                MenuItemPairList menuItemPairList = new MenuItemPairList();
                Component targetColoredName = target.getColoredName();
                menuItemPairList.add(new ItemBuilder(Material.BLACK_BANNER)
                                .name(Component.text("Pick Up Flag", NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                            if (target.getCarriedFlag() == holder.getInfo()) {
                                sendDebugMessage(player, Component.text("That player already has the flag", NamedTextColor.RED));
                            } else {
                                FlagHolder.update(
                                        target.getGame(),
                                        info -> info.getFlag() instanceof PlayerFlagLocation && ((PlayerFlagLocation) info.getFlag()).getPlayer() == target ?
                                                GroundFlagLocation.of(info.getFlag()) :
                                                info == holder.getInfo() ?
                                                PlayerFlagLocation.of(info.getFlag(), target) :
                                                null
                                );
                                sendDebugMessage(player, Component.text("Picked up the flag for ", NamedTextColor.GREEN)
                                                                  .append(targetColoredName));
                            }
                        }
                );
                menuItemPairList.add(new ItemBuilder(Material.BLACK_BED)
                                .name(Component.text("Return the Flag", NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                            if (target.getCarriedFlag() == holder.getInfo()) {
                                holder.getInfo().setFlag(new SpawnFlagLocation(holder.getInfo().getSpawnLocation(), null));
                                sendDebugMessage(player, Component.text("Returned the flag for ", NamedTextColor.GREEN)
                                                                  .append(targetColoredName));
                            } else {
                                sendDebugMessage(player, Component.text("That player does not have the flag", NamedTextColor.RED));
                            }
                        }
                );
                menuItemPairList.add(new ItemBuilder(Material.GRASS)
                                .name(Component.text("Drop Flag", NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                            if (target.getCarriedFlag() == holder.getInfo()) {
                                FlagHolder.dropFlagForPlayer(target);
                                sendDebugMessage(player, Component.text("Dropped the flag for ", NamedTextColor.GREEN)
                                                                  .append(targetColoredName));

                            } else {
                                sendDebugMessage(player, Component.text("That player does not have the flag", NamedTextColor.RED));
                            }
                        }
                );
                menuItemPairList.add(new ItemBuilder(Material.COMPARATOR)
                                .name(Component.text("Set Multiplier", NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                            if (target.getCarriedFlag() == holder.getInfo()) {
                                SignGUI.builder()
                                       .setLines("", "^^^^^^^", "Enter flag %", "0 < % < 10,000")
                                       .setHandler((p, lines) -> {

                                           new BukkitRunnable() {
                                               @Override
                                               public void run() {
                                                   String amount = lines.getLine(0);
                                                   try {
                                                       int amountNumber = Integer.parseInt(amount);
                                                       if (amountNumber < 0 || amountNumber > 10000) {
                                                           throw new NumberFormatException();
                                                       }
                                                        if (target.getCarriedFlag() != null) {
                                                            PlayerFlagLocation flag = ((PlayerFlagLocation) target.getCarriedFlag().getFlag());
                                                            flag.setPickUpTicks(amountNumber * 60);
                                                            sendDebugMessage(player, Component.text("Set the ", NamedTextColor.RED)
                                                                                              .append(target.getTeam().chatTagColored)
                                                                                              .append(Component.text(" flag carrier multiplier to " + amount + "%"))
                                                            );

                                                        }
                                                   } catch (NumberFormatException exception) {
                                                       p.sendMessage(Component.text("Invalid number", NamedTextColor.RED));
                                                   }
                                                   openFlagOptionMenu(player, target);
                                               }
                                           }.runTaskLater(Warlords.getInstance(), 1);
                                           return null;
                                       }).build().open(player);
                            } else {
                                sendDebugMessage(player, Component.text("That player does not have the flag", NamedTextColor.RED));
                            }
                        }
                );

                for (int i = 0; i < menuItemPairList.size(); i++) {
                    menu.setItem(i + 1, row, menuItemPairList.get(i).getA(), menuItemPairList.get(i).getB());
                }
            }
            menu.setItem(3, 3, MENU_BACK, (m, e) -> openPlayerMenu(player, target));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openSpecMenu(Player player, WarlordsEntity target) {
            Menu menu = new Menu("Spec Menu: " + target.getName(), 9 * 6);
            Classes[] values = Classes.VALUES;
            for (int i = 0; i < values.length; i++) {
                Classes group = values[i];
                menu.setItem(2, i,
                        new ItemBuilder(group.item)
                                .name(Component.text(group.name, NamedTextColor.GREEN))
                                .get(),
                        (m, e) -> {
                        }
                );
                List<Specializations> aClasses = group.subclasses;
                for (int j = 0; j < aClasses.size(); j++) {
                    int finalJ = j;
                    ItemBuilder spec = new ItemBuilder(aClasses.get(j).specType.itemStack).name(Component.text(aClasses.get(j).name, NamedTextColor.GREEN));
                    if (target.getSpecClass() == aClasses.get(j)) {
                        spec.enchant(Enchantment.OXYGEN, 1);
                    }
                    menu.setItem(4 + j, i,
                            spec.get(),
                            (m, e) -> openSkillBoostMenu(player, target, aClasses.get(finalJ))
                    );
                }
            }
            menu.setItem(8, 4, MENU_BACK, (m, e) -> openPlayerMenu(player, target));
            menu.setItem(8, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        public static void openSkillBoostMenu(Player player, WarlordsEntity target, Specializations selectedSpec) {
            Menu menu = new Menu("Skill Boost: " + target.getName(), 9 * 4);
            List<SkillBoosts> values = selectedSpec.skillBoosts;
            for (int i = 0; i < values.size(); i++) {
                SkillBoosts skillBoost = values.get(i);
                menu.setItem(
                        i + 2,
                        1,
                        new ItemBuilder(selectedSpec.specType.itemStack)
                                .name(Component.text(skillBoost.name + " (" + selectedSpec.name + ")", NamedTextColor.RED))
                                .lore(WordWrap.wrap(skillBoost.description, 150))
                                .addLore(
                                        Component.empty(),
                                        Component.text("Click to select!", NamedTextColor.YELLOW)
                                ).get(),
                        (m, e) -> {
                            setSpec(player, target, selectedSpec, skillBoost);
                            openSpecMenu(player, target);
                        }
                );

            }
            menu.setItem(3, 3, MENU_BACK, (m, e) -> openSpecMenu(player, target));
            menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }

        static class CooldownOptionMenus {

            public static void openCooldownManagerMenu(Player player, WarlordsEntity target) {
                //int menuY = Math.min(4 + target.getCooldownManager().getCooldowns().size() / 7, 6); Menu shift annoying
                Menu menu = new Menu("CD Manager: " + target.getName(), 9 * 6);
                //general info
                menu.setItem(4, 0,
                        new ItemBuilder(HeadUtils.getHead(player))
                                .name(Component.text("Cooldown Stats", NamedTextColor.GREEN))
                                .lore(
                                        Component.text("Total Cooldowns: " + target.getCooldownManager().getTotalCooldowns(), NamedTextColor.GREEN),
                                        Component.text("Active Cooldowns: " + target.getCooldownManager().getCooldowns().size(), NamedTextColor.GREEN)
                                )
                                .get(),
                        (m, e) -> {

                        }
                );
                //cooldowns
                int yLevel = 0;
                List<AbstractCooldown<?>> abstractCooldowns = new ArrayList<>(target.getCooldownManager().getCooldowns());
                abstractCooldowns.sort(Comparator.comparing(abstractCooldown -> abstractCooldown instanceof RegularCooldown ? ((RegularCooldown<?>) abstractCooldown).getTicksLeft() : 0));
                for (int i = 0; i < abstractCooldowns.size(); i++) {
                    if (i % 7 == 0) {
                        yLevel++;
                        if (yLevel > 4) {
                            break;
                        }
                    }
                    AbstractCooldown<?> abstractCooldown = abstractCooldowns.get(i);
                    menu.setItem((i % 7) + 1, yLevel,
                            new ItemBuilder(Utils.getWoolFromIndex(i))
                                    .name(Component.text(abstractCooldown.getName(), NamedTextColor.GOLD))
                                    .lore(Component.empty()
                                                   .append(Component.text("Time Left: ", NamedTextColor.GREEN))
                                                   .append(Component.text(abstractCooldown instanceof RegularCooldown ? Math.round(((RegularCooldown<?>) abstractCooldown).getTicksLeft() / 20f * 10) / 10.0 + "s" : "N/A",
                                                           NamedTextColor.GOLD
                                                   )),
                                            Component.text("From: " + abstractCooldown.getFrom().getColoredName(), NamedTextColor.GREEN)
                                    )
                                    .get(),
                            (m, e) -> openCooldownEditorMenu(player, target, abstractCooldown)
                    );
                }
                menu.setItem(3, 5, MENU_BACK, (m, e) -> openCooldownsMenu(player, target));
                menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
                menu.openForPlayer(player);
            }

            public static void openCooldownEditorMenu(Player player, WarlordsEntity target, AbstractCooldown<?> abstractCooldown) {
                Menu menu = new Menu(abstractCooldown.getName() + ": " + target.getName(), 9 * 4);

                MenuItemPairList menuItemPairList = new MenuItemPairList();
                menuItemPairList.add(new ItemBuilder(Material.MILK_BUCKET)
                                .name(Component.text("Remove", NamedTextColor.AQUA))
                                .get(),
                        (m, e) -> {
                            target.getCooldownManager().removeCooldown(abstractCooldown);
                            openCooldownManagerMenu(player, target);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (PlainTextComponentSerializer.plainText()
                                                                    .serialize(player.getOpenInventory().title())
                                                                    .equals("CD Manager: " + target.getName())) {
                                        openCooldownManagerMenu(player, target);
                                    } else {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(Warlords.getInstance(), 20, 20);
                            sendDebugMessage(player, Component.text("Removed ", NamedTextColor.GREEN)
                                                              .append(target.getColoredName())
                                                              .append(Component.text("'s " + abstractCooldown.getName() + " cooldown"))
                            );
                        }
                );
                menuItemPairList.add(new ItemBuilder(Material.REDSTONE)
                                .name(Component.text("Add duration", NamedTextColor.AQUA))
                                .get(),
                        (m, e) -> {
                            if (!target.getCooldownManager().getCooldowns().contains(abstractCooldown)) {
                                openCooldownsMenu(player, target);
                                sendDebugMessage(player, Component.text("That cooldown no longer exists", NamedTextColor.RED));
                                return;
                            }
                            if (!(abstractCooldown instanceof RegularCooldown)) {
                                return;
                            }

                            SignGUI.builder()
                                   .setLines("", "^^^^^^^", "Enter seconds", "to add")
                                   .setHandler((p, lines) -> {
                                       new BukkitRunnable() {
                                           @Override
                                           public void run() {
                                               String amount = lines.getLine(0);
                                               try {
                                                   int amountNumber = Integer.parseInt(amount);
                                                   ((RegularCooldown<?>) abstractCooldown).subtractTime(-amountNumber * 20);
                                                   sendDebugMessage(player, Component.text("Added " + amountNumber + " seconds to ", NamedTextColor.GREEN)
                                                                                     .append(target.getColoredName())
                                                                                     .append(Component.text("'s " + abstractCooldown.getName()))
                                                   );
                                               } catch (NumberFormatException exception) {
                                                   p.sendMessage(Component.text("Invalid number", NamedTextColor.RED));
                                               }
                                               openCooldownEditorMenu(player, target, abstractCooldown);
                                           }
                                       }.runTaskLater(Warlords.getInstance(), 1);
                                       return null;
                                   }).build().open(player);
                        }
                );

                for (int i = 0; i < menuItemPairList.size(); i++) {
                    menu.setItem(i + 1, 1, menuItemPairList.get(i).getA(), menuItemPairList.get(i).getB());
                }

                menu.setItem(3, 3, MENU_BACK, (m, e) -> {
                    openCooldownManagerMenu(player, target);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (PlainTextComponentSerializer.plainText()
                                                            .serialize(player.getOpenInventory().title())
                                                            .equals("CD Manager: " + target.getName())) {
                                openCooldownManagerMenu(player, target);
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 20, 20);
                });
                menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
                menu.openForPlayer(player);
            }
        }
    }

}
