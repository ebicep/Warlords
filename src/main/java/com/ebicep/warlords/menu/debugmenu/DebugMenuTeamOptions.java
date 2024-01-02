package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.*;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.menu.debugmenu.DebugMenuTeamOptions.TeamOptionsUtil.getPlayerStatLore;
import static com.ebicep.warlords.util.chat.ChatChannels.sendDebugMessage;

public class DebugMenuTeamOptions {

    public static void openTeamSelectorMenu(Player player, Game game) {
        Menu menu = new Menu("Team Options", 9 * 4);

        HashMap<Team, List<WarlordsEntity>> teamPlayers = new HashMap<>();
        PlayerFilter.playingGame(game).forEach((wp) -> teamPlayers.computeIfAbsent(wp.getTeam(), v -> new ArrayList<>()).add(wp));

        int i = 0;
        for (Map.Entry<Team, List<WarlordsEntity>> teamListEntry : teamPlayers.entrySet()) {
            Team team = teamListEntry.getKey();
            List<WarlordsEntity> warlordsEntities = teamListEntry.getValue();
            menu.setItem(i % 7 + 1, i / 7 + 1,
                    new ItemBuilder(team.woolItem)
                            .name(team.chatTagColored)
                            .lore(TeamOptionsUtil.getTeamStatLore(warlordsEntities))
                            .get(),
                    (m, e) -> openTeamMenu(player, game, team, warlordsEntities, 1)
            );
            i++;
        }

        menu.setItem(3, 3, MENU_BACK, (m, e) -> DebugMenu.openDebugMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openTeamMenu(Player player, Game game, Team team, List<WarlordsEntity> warlordsEntities, int page) {
        Menu menu = new Menu("Team " + team.name, 9 * 6);

        //flag player first
        warlordsEntities.sort((wp1, wp2) -> Boolean.compare(wp2.hasFlag(), wp1.hasFlag()));
        int playerPerPage = 45;
        for (int i = 0; i < playerPerPage; i++) {
            int index = ((page - 1) * playerPerPage) + i;
            if (index < warlordsEntities.size()) {
                WarlordsEntity warlordsEntity = warlordsEntities.get(i);
                List<Component> lore = new ArrayList<>(Arrays.asList(getPlayerStatLore(warlordsEntity)));
                lore.add(Component.empty());
                if (player.getUniqueId() != warlordsEntity.getUuid()) {
                    lore.add(Component.textOfChildren(
                            Component.text("LEFT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                            Component.text(" to ", NamedTextColor.GREEN),
                            Component.text("Open Player Options.", NamedTextColor.YELLOW)
                    ));
                    lore.add(Component.textOfChildren(
                            Component.text("LEFT-CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                            Component.text(" to ", NamedTextColor.GREEN),
                            Component.text("Teleport.", NamedTextColor.YELLOW)
                    ));
                } else {
                    lore.add(Component.textOfChildren(
                            Component.text("CLICK", NamedTextColor.YELLOW, TextDecoration.BOLD),
                            Component.text(" to ", NamedTextColor.GREEN),
                            Component.text("Open Player Options.", NamedTextColor.YELLOW)
                    ));
                }
                ItemStack itemStack;
                if (warlordsEntity instanceof WarlordsNPC) {
                    AbstractMob mob = ((WarlordsNPC) warlordsEntity).getMob();
                    if (mob != null) {
                        EntityEquipment ee = mob.getEquipment();
                        if (ee != null && ee.getHelmet() != null) {
                            itemStack = ee.getHelmet();
                        } else {
                            itemStack = new ItemStack(Material.PLAYER_HEAD);
                        }
                    } else {
                        itemStack = new ItemStack(Material.PLAYER_HEAD);
                    }
                } else {
                    itemStack = HeadUtils.getHead(warlordsEntity.getUuid());
                }
                menu.setItem(i % 9, i / 9,
                        new ItemBuilder(itemStack)
                                .name(Component.text(warlordsEntity.getName(), team.teamColor)
                                               .append(Component.text(warlordsEntity.hasFlag() ? " ⚑" : "", NamedTextColor.WHITE)))
                                .lore(lore)
                                .get(),
                        (m, e) -> {
                            if (e.isRightClick() && player.getUniqueId() != warlordsEntity.getUuid()) {
                                player.teleport(warlordsEntity.getLocation());
                            } else {
                                DebugMenuPlayerOptions.openPlayerMenu(player, warlordsEntity);
                            }
                        }
                );
            } else {
                break;
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Previous Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page - 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openTeamMenu(player, game, team, warlordsEntities, page - 1)
            );
        }
        if (warlordsEntities.size() > (page * playerPerPage)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(Component.text("Next Page", NamedTextColor.GREEN))
                            .lore(Component.text("Page " + (page + 1), NamedTextColor.YELLOW))
                            .get(),
                    (m, e) -> openTeamMenu(player, game, team, warlordsEntities, page + 1)
            );
        }

        menu.setItem(3, 5, MENU_BACK, (m, e) -> openTeamSelectorMenu(player, game));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.setItem(5, 5,
                new ItemBuilder(team.woolItem)
                        .name(team.chatTagColored)
                        .lore(TeamOptionsUtil.getTeamStatLore(warlordsEntities))
                        .get(),
                ACTION_DO_NOTHING
        );
        menu.setItem(5, 5,
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(Component.text("Kill All", NamedTextColor.RED))
                        .lore(Component.text("Kills all the players on the team", NamedTextColor.GRAY))
                        .get(), (m, e) -> {
                    warlordsEntities.forEach(wp -> wp.addDamageInstance(wp, "", 69000, 69000, 0, 100));
                    sendDebugMessage(player, Component.text("Killed all " + team.name + " players", NamedTextColor.GREEN));
                }
        );
        menu.openForPlayer(player);
    }

    static class TeamOptionsUtil {


        static Component[] getTeamStatLore(List<WarlordsEntity> warlordsPlayers) {
            return new Component[]{
                    Component.text("Kills", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getKills()).sum(), NamedTextColor.GOLD)),
                    Component.text("Assists", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getAssists()).sum(), NamedTextColor.GOLD)),
                    Component.text("Deaths", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getDeaths()).sum(), NamedTextColor.GOLD)),
                    Component.text("Damage", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getDamage()).sum()),
                            NamedTextColor.RED
                    )),
                    Component.text("Healing", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getHealing()).sum()),
                            NamedTextColor.DARK_GREEN
                    )),
                    Component.text("Absorbed", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getAbsorbed()).sum()),
                            NamedTextColor.GOLD
                    ))
            };
        }

        static Component[] getPlayerStatLore(WarlordsEntity wp) {
            return new Component[]{
                    Component.text("Spec", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(wp.getSpec().getClass().getSimpleName(), NamedTextColor.GOLD)),
                    Component.text("Health", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(Math.round(wp.getCurrentHealth()), NamedTextColor.RED)),
                    Component.text("Energy", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text((int) wp.getEnergy(), NamedTextColor.YELLOW)),
                    Component.text("Kills", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(wp.getMinuteStats().total().getKills(), NamedTextColor.GOLD)),
                    Component.text("Assists", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(wp.getMinuteStats().total().getAssists(), NamedTextColor.GOLD)),
                    Component.text("Deaths", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(wp.getMinuteStats().total().getDeaths(), NamedTextColor.GOLD)),
                    Component.text("Damage", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getDamage()), NamedTextColor.RED)),
                    Component.text("Healing", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getHealing()), NamedTextColor.DARK_GREEN)),
                    Component.text("Absorbed", NamedTextColor.GREEN).append(Component.text(": ", NamedTextColor.GRAY))
                            .append(Component.text(NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getAbsorbed()), NamedTextColor.GOLD))

            };
        }
    }

}
