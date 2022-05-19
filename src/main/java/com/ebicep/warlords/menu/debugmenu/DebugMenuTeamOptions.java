package com.ebicep.warlords.menu.debugmenu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.menu.Menu;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.java.NumberFormat;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.ebicep.warlords.menu.Menu.*;

public class DebugMenuTeamOptions {

    public static void openTeamMenu(Player player, Game game) {
        Menu menu = new Menu("Team Options", 9 * 6);
        //divider
        for (int i = 0; i < 5; i++) {
            menu.setItem(4, i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).name(" ").get(), (m, e) -> {
            });
        }
        //team info = color - other shit
        List<WarlordsPlayer> bluePlayers = new ArrayList<>();
        List<WarlordsPlayer> redPlayers = new ArrayList<>();
        PlayerFilter.playingGame(game).forEach((wp) -> {
            if (wp.getTeam() == Team.BLUE) {
                bluePlayers.add(wp);
            } else if (wp.getTeam() == Team.RED) {
                redPlayers.add(wp);
            }
        });
        ItemStack blueInfo = new ItemBuilder(Material.WOOL, 1, (byte) 11)
                .name(ChatColor.BLUE + "BLU")
                .lore(TeamOptionsUtil.getTeamStatLore(bluePlayers))
                .get();
        ItemStack redInfo = new ItemBuilder(Material.WOOL, 1, (byte) 14)
                .name(ChatColor.RED + "RED")
                .lore(TeamOptionsUtil.getTeamStatLore(redPlayers))
                .get();
        ItemStack killTeam = new ItemBuilder(Material.DIAMOND_SWORD)
                .name(ChatColor.RED + "Kill All")
                .lore(ChatColor.GRAY + "Kills all the players on the team")
                .flags(ItemFlag.HIDE_ATTRIBUTES)
                .get();
        menu.setItem(0, 0, blueInfo, (m, e) -> {
        });
        menu.setItem(3, 0, killTeam, (m, e) -> {
            bluePlayers.forEach(wp -> wp.addDamageInstance(wp, "", 69000, 69000, -1, 100, false));
        });
        menu.setItem(5, 0, redInfo, (m, e) -> {
        });
        menu.setItem(8, 0, killTeam, (m, e) -> {
            redPlayers.forEach(wp -> wp.addDamageInstance(wp, "", 69000, 69000, -1, 100, false));
        });

        //players
        TeamOptionsUtil.addPlayersToMenu(menu, player, bluePlayers, true);
        TeamOptionsUtil.addPlayersToMenu(menu, player, redPlayers, false);
        menu.setItem(3, 5, MENU_BACK, (m, e) -> DebugMenu.openDebugMenu(player));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    static class TeamOptionsUtil {

        public static void addPlayersToMenu(Menu menu, Player player, List<WarlordsPlayer> warlordsPlayers, boolean blueTeam) {
            //flag player first
            warlordsPlayers.sort((wp1, wp2) -> {
                int wp1Flag = wp1.getCarriedFlag() != null ? 1 : 0;
                int wp2Flag = wp2.getCarriedFlag() != null ? 1 : 0;
                return wp2Flag - wp1Flag;
            });
            int y = 0;
            for (int i = 0; i < warlordsPlayers.size(); i++) {
                if (i % 4 == 0) {
                    y++;
                }
                WarlordsPlayer wp = warlordsPlayers.get(i);
                List<String> lore = new ArrayList<>(Arrays.asList(getPlayerStatLore(wp)));
                lore.add("");
                if (player.getUniqueId() != wp.getUuid()) {
                    lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK" + ChatColor.GREEN + " to " + ChatColor.YELLOW + "Teleport");
                    lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "LEFT-CLICK" + ChatColor.GREEN + " to " + ChatColor.YELLOW + "Open Player Options");
                } else {
                    lore.add(ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK" + ChatColor.GREEN + " to " + ChatColor.YELLOW + "Open Player Options");
                }
                menu.setItem(i % 4 + (blueTeam ? 0 : 5), y,
                        new ItemBuilder(Warlords.getHead(wp.getUuid()))
                                .name((blueTeam ? ChatColor.BLUE : ChatColor.RED) + wp.getName() + (wp.getCarriedFlag() != null ? ChatColor.WHITE + " ⚑" : ""))
                                .lore(lore)
                                .get(),
                        (m, e) -> {
                            if (e.isRightClick() && player.getUniqueId() != wp.getUuid()) {
                                player.teleport(wp.getLocation());
                            } else {
                                DebugMenuPlayerOptions.openPlayerMenu(player, wp);
                            }
                        }
                );
            }
        }

        public static String[] getTeamStatLore(List<WarlordsPlayer> warlordsPlayers) {
            return new String[]{
                    ChatColor.GREEN + "Kills" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getKills()).sum(),
                    ChatColor.GREEN + "Assists" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getAssists()).sum(),
                    ChatColor.GREEN + "Deaths" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(e -> e.getMinuteStats().total().getDeaths()).sum(),
                    ChatColor.GREEN + "Damage" + ChatColor.GRAY + ": " + ChatColor.RED + NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getDamage()).sum()),
                    ChatColor.GREEN + "Healing" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getHealing()).sum()),
                    ChatColor.GREEN + "Absorbed" + ChatColor.GRAY + ": " + ChatColor.GOLD + NumberFormat.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(e -> e.getMinuteStats().total().getAbsorbed()).sum())
            };
        }

        private static String[] getPlayerStatLore(WarlordsPlayer wp) {
            return new String[]{
                    ChatColor.GREEN + "Spec" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getSpec().getClass().getSimpleName(),
                    ChatColor.GREEN + "Health" + ChatColor.GRAY + ": " + ChatColor.RED + wp.getHealth(),
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
