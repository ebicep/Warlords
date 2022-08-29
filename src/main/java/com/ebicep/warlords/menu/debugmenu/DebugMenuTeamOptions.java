package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.wavedefense.mobs.AbstractMob;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.util.bukkit.HeadUtils;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemFlag;
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
                    new ItemBuilder(team.item)
                            .name(team.chatTagColored)
                            .lore(TeamOptionsUtil.getTeamStatLore(warlordsEntities))
                            .get(),
                    (m, e) -> openTeamMenu(player, game, team, warlordsEntities, 1));
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
                List<String> lore = new ArrayList<>(Arrays.asList(getPlayerStatLore(warlordsEntity)));
                lore.add("");
                if (player.getUniqueId() != warlordsEntity.getUuid()) {
                    lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GREEN + " to " + ChatColor.YELLOW + "Teleport");
                    lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" + ChatColor.GREEN + " to " + ChatColor.YELLOW + "Open Player Options");
                } else {
                    lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to " + ChatColor.YELLOW + "Open Player Options");
                }
                ItemStack itemStack;
                if (warlordsEntity instanceof WarlordsNPC) {
                    AbstractMob<?> mob = ((WarlordsNPC) warlordsEntity).getMob();
                    EntityEquipment ee = mob.getEe();
                    if (ee != null && ee.getHelmet() != null) {
                        itemStack = ee.getHelmet();
                    } else {
                        itemStack = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
                    }
                } else {
                    itemStack = HeadUtils.getHead(warlordsEntity.getUuid());
                }
                menu.setItem(i % 9, i / 9,
                        new ItemBuilder(itemStack)
                                .name(team.teamColor + warlordsEntity.getName() + (warlordsEntity.hasFlag() ? ChatColor.WHITE + " ⚑" : ""))
                                .lore(lore)
                                .get(),
                        (m, e) -> {
                            if (e.isRightClick() && player.getUniqueId() != warlordsEntity.getUuid()) {
                                player.teleport(warlordsEntity.getLocation());
                            } else {
                                DebugMenuPlayerOptions.openPlayerMenu(player, warlordsEntity);
                            }
                        });
            } else {
                break;
            }
        }

        if (page - 1 > 0) {
            menu.setItem(0, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Previous Page")
                            .lore(ChatColor.YELLOW + "Page " + (page - 1))
                            .get(),
                    (m, e) -> openTeamMenu(player, game, team, warlordsEntities, page - 1)
            );
        }
        if (warlordsEntities.size() > (page * playerPerPage)) {
            menu.setItem(8, 5,
                    new ItemBuilder(Material.ARROW)
                            .name(ChatColor.GREEN + "Next Page")
                            .lore(ChatColor.YELLOW + "Page " + (page + 1))
                            .get(),
                    (m, e) -> openTeamMenu(player, game, team, warlordsEntities, page + 1)
            );
        }

        menu.setItem(3, 5, MENU_BACK, (m, e) -> openTeamSelectorMenu(player, game));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.setItem(5, 5,
                new ItemBuilder(team.item)
                        .name(team.chatTagColored)
                        .lore(TeamOptionsUtil.getTeamStatLore(warlordsEntities))
                        .get(),
                ACTION_DO_NOTHING);
        menu.setItem(5, 5,
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(ChatColor.RED + "Kill All")
                        .lore(ChatColor.GRAY + "Kills all the players on the team")
                        .flags(ItemFlag.HIDE_ATTRIBUTES)
                        .get(), (m, e) -> {
                    warlordsEntities.forEach(wp -> wp.addDamageInstance(wp, "", 69000, 69000, -1, 100, false));
                    sendDebugMessage(player, ChatColor.GREEN + "Killed all blue players", true);
                });
        menu.openForPlayer(player);
    }

    static class TeamOptionsUtil {

        static String[] getTeamStatLore(List<WarlordsEntity> warlordsPlayers) {
            return new String[]{
                    ChatColor.GREEN + "Kills" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getKills()).sum(),
                    ChatColor.GREEN + "Assists" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getAssists()).sum(),
                    ChatColor.GREEN + "Deaths" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getDeaths()).sum(),
                    ChatColor.GREEN + "Damage" + ChatColor.GRAY + ": " + ChatColor.RED + NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getDamage()).sum()),
                    ChatColor.GREEN + "Healing" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getHealing()).sum()),
                    ChatColor.GREEN + "Absorbed" + ChatColor.GRAY + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getAbsorbed()).sum())
            };
        }

        static String[] getPlayerStatLore(WarlordsEntity wp) {
            return new String[]{
                    ChatColor.GREEN + "Spec" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getSpec().getClass().getSimpleName(),
                    ChatColor.GREEN + "Health" + ChatColor.GRAY + ": " + ChatColor.RED + Math.round(wp.getHealth()),
                    ChatColor.GREEN + "Energy" + ChatColor.GRAY + ": " + ChatColor.YELLOW + (int) wp.getEnergy(),
                    ChatColor.GREEN + "Kills" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getMinuteStats().total().getKills(),
                    ChatColor.GREEN + "Assists" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getMinuteStats().total().getAssists(),
                    ChatColor.GREEN + "Deaths" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getMinuteStats().total().getDeaths(),
                    ChatColor.GREEN + "Damage" + ChatColor.GRAY + ": " + ChatColor.RED + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getDamage()),
                    ChatColor.GREEN + "Healing" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getHealing()),
                    ChatColor.GREEN + "Absorbed" + ChatColor.GRAY + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound(wp.getMinuteStats().total().getAbsorbed())
            };
        }
    }

}
