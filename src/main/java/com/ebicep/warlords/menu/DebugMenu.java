package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.BiConsumer;

import static com.ebicep.warlords.menu.Menu.*;

public class DebugMenu {

    public static void openDebugMenu(Player player) {
        Menu menu = new Menu("Debug Options", 9 * 4);

        LinkedHashMap<ItemStack, BiConsumer<Menu, InventoryClickEvent>> items = new LinkedHashMap<>();
        items.put(new ItemBuilder(Material.ENDER_PORTAL_FRAME).name(ChatColor.GREEN + "Game Options").get(),
                (n, e) -> openGameMenu(player)
        );
        items.put(new ItemBuilder(CraftItemStack.asBukkitCopy(Warlords.getPlayerHeads().get(player.getUniqueId()))).name(ChatColor.GREEN + "Player Options").get(),
                (n, e) -> openPlayerMenu(player)
        );
        items.put(new ItemBuilder(Material.NOTE_BLOCK).name(ChatColor.GREEN + "Team Options").get(),
                (n, e) -> {
                    openTeamMenu(player);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (player.getOpenInventory().getTopInventory().getName().equals("Team Options")) {
                                openTeamMenu(player);
                            } else {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 20, 20);
                }
        );

        List<ItemStack> itemsArray = new ArrayList<>(items.keySet());
        for (int i = 0; i < items.size(); i++) {
            menu.setItem(i + 1, 1, itemsArray.get(i), items.get(itemsArray.get(i)));
        }

        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openGameMenu(Player player) {
        Menu menu = new Menu("Game Options", 9 * 4);
        ItemStack[] itemStack = {
                new ItemBuilder(Material.DARK_OAK_DOOR_ITEM)
                        .name(ChatColor.GREEN + "Start")
                        .get(),
                new ItemBuilder(Material.DIODE)
                        .name(ChatColor.GREEN + "Timer")
                        .get(),
        };
        for (int i = 0; i < itemStack.length; i++) {
            int index = i + 1;
            menu.setItem(index, 1, itemStack[i],
                    (n, e) -> {
                        switch (index) {
                            case 1:
                                openMapsMenu(player);
                                break;
                            case 2:
                                openTimerMenu(player);
                                break;
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openDebugMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openPlayerMenu(Player player) {
        Menu menu = new Menu("Player Options", 9 * 4);
        ItemStack[] itemStack = {
                new ItemBuilder(Material.EXP_BOTTLE)
                        .name(ChatColor.GREEN + "Energy")
                        .get(),
                new ItemBuilder(Material.INK_SACK, 1, (byte) 8)
                        .name(ChatColor.GREEN + "Cooldown")
                        .get(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .name(ChatColor.GREEN + "Damage")
                        .get(),
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(ChatColor.GREEN + "Take Damage")
                        .flags(ItemFlag.HIDE_ATTRIBUTES)
                        .get(),
        };
        for (int i = 0; i < itemStack.length; i++) {
            int index = i + 1;
            menu.setItem(index, 1, itemStack[i],
                    (n, e) -> {
                        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
                        if (warlordsPlayer != null) {
                            switch (index) {
                                case 1:
                                    Bukkit.getServer().dispatchCommand(player, "wl energy " + (warlordsPlayer.isInfiniteEnergy() ? "enable" : "disable"));
                                    break;
                                case 2:
                                    Bukkit.getServer().dispatchCommand(player, "wl cooldown " + (warlordsPlayer.isDisableCooldowns() ? "enable" : "disable"));
                                    break;
                                case 3:
                                    Bukkit.getServer().dispatchCommand(player, "wl damage " + (warlordsPlayer.isTakeDamage() ? "disable" : "enable"));
                                    break;
                                case 4:
                                    openTakeDamageMenu(player);
                                    break;
                            }
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openDebugMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openTeamMenu(Player player) {
        WarlordsPlayer warlordsPlayer = Warlords.getPlayer(player);
        if (warlordsPlayer != null) {
            Menu menu = new Menu("Team Options", 9 * 6);
            //divider
            for (int i = 0; i < 5; i++) {
                menu.setItem(4, i, new ItemBuilder(Material.STAINED_GLASS_PANE, 1, (byte) 7).name(" ").get(), (n, e) -> {
                });
            }
            //team info = color - other shit
            List<WarlordsPlayer> bluePlayers = new ArrayList<>();
            List<WarlordsPlayer> redPlayers = new ArrayList<>();
            Warlords.getPlayers().forEach((key, value) -> {
                if (value.getGame().isBlueTeam(key)) {
                    bluePlayers.add(value);
                } else {
                    redPlayers.add(value);
                }
            });
            ItemStack blueInfo = new ItemBuilder(Material.WOOL, 1, (byte) 11)
                    .name(ChatColor.BLUE + "BLU")
                    .lore(getTeamStatLore(bluePlayers))
                    .get();
            ItemStack redInfo = new ItemBuilder(Material.WOOL, 1, (byte) 14)
                    .name(ChatColor.RED + "RED")
                    .lore(getTeamStatLore(redPlayers))
                    .get();
            ItemStack killTeam = new ItemBuilder(Material.DIAMOND_SWORD)
                    .name(ChatColor.RED + "Kill All")
                    .lore(ChatColor.GRAY + "Kills all the players on the team")
                    .flags(ItemFlag.HIDE_ATTRIBUTES)
                    .get();
            menu.setItem(0, 0, blueInfo, (n, e) -> {
            });
            menu.setItem(3, 0, killTeam, (n, e) -> {
                bluePlayers.forEach(wp -> wp.addHealth(wp, "", -69000, -69000, -1, 100));
            });
            menu.setItem(5, 0, redInfo, (n, e) -> {
            });
            menu.setItem(8, 0, killTeam, (n, e) -> {
                redPlayers.forEach(wp -> wp.addHealth(wp, "", -69000, -69000, -1, 100));
            });

            //players
            int y = 0;
            for (int i = 0; i < bluePlayers.size(); i++) {
                if (i % 4 == 0) {
                    y++;
                }
                WarlordsPlayer wp = bluePlayers.get(i);
                menu.setItem(i % 4, y,
                        new ItemBuilder(CraftItemStack.asBukkitCopy(Warlords.getPlayerHeads().get(wp.getUuid())))
                                .name(ChatColor.BLUE + wp.getName())
                                .lore(getPlayerStatLore(wp))
                                .get(),
                        (n, e) -> {
                        }
                );
            }
            y = 0;
            for (int i = 0; i < redPlayers.size(); i++) {
                if (i % 4 == 0) {
                    y++;
                }
                WarlordsPlayer wp = redPlayers.get(i);
                menu.setItem(i % 4 + 5, y,
                        new ItemBuilder(CraftItemStack.asBukkitCopy(Warlords.getPlayerHeads().get(wp.getUuid())))
                                .name(ChatColor.RED + wp.getName())
                                .lore(getPlayerStatLore(wp))
                                .get(),
                        (n, e) -> {
                        }
                );
            }
            menu.setItem(3, 5, MENU_BACK, (n, e) -> openDebugMenu(player));
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }
    }

    private static String[] getTeamStatLore(List<WarlordsPlayer> warlordsPlayers) {
        return new String[]{
                ChatColor.GREEN + "Kills" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(WarlordsPlayer::getTotalKills).sum(),
                ChatColor.GREEN + "Assists" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(WarlordsPlayer::getTotalAssists).sum(),
                ChatColor.GREEN + "Deaths" + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayers.stream().mapToInt(WarlordsPlayer::getTotalDeaths).sum(),
                ChatColor.GREEN + "Damage" + ChatColor.GRAY + ": " + ChatColor.RED + Utils.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(WarlordsPlayer::getTotalDamage).sum()),
                ChatColor.GREEN + "Healing" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + Utils.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(WarlordsPlayer::getTotalHealing).sum()),
                ChatColor.GREEN + "Absorbed" + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.addCommaAndRound((float) warlordsPlayers.stream().mapToDouble(WarlordsPlayer::getTotalAbsorbed).sum())
        };
    }

    private static String[] getPlayerStatLore(WarlordsPlayer wp) {
        return new String[]{
                ChatColor.GREEN + "Spec" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getSpec().getClass().getSimpleName(),
                ChatColor.GREEN + "Health" + ChatColor.GRAY + ": " + ChatColor.RED + wp.getHealth(),
                ChatColor.GREEN + "Energy" + ChatColor.GRAY + ": " + ChatColor.YELLOW + (int) wp.getEnergy(),
                ChatColor.GREEN + "Kills" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getTotalKills(),
                ChatColor.GREEN + "Assists" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getTotalAssists(),
                ChatColor.GREEN + "Deaths" + ChatColor.GRAY + ": " + ChatColor.GOLD + wp.getTotalDeaths(),
                ChatColor.GREEN + "Damage" + ChatColor.GRAY + ": " + ChatColor.RED + Utils.addCommaAndRound(wp.getTotalDamage()),
                ChatColor.GREEN + "Healing" + ChatColor.GRAY + ": " + ChatColor.DARK_GREEN + Utils.addCommaAndRound(wp.getTotalHealing()),
                ChatColor.GREEN + "Absorbed" + ChatColor.GRAY + ": " + ChatColor.GOLD + Utils.addCommaAndRound(wp.getTotalAbsorbed())
        };
    }

    public static void openTakeDamageMenu(Player player) {
        Menu menu = new Menu("Take Damage", 9 * 4);
        for (int i = 1; i <= 5; i++) {
            int damage = i * 1000;
            menu.setItem(i + 1, 1,
                    new ItemBuilder(Utils.woolSortedByColor[i - 1])
                            .name(ChatColor.RED.toString() + damage)
                            .get(),
                    (n, e) -> Bukkit.getServer().dispatchCommand(player, "wl takedamage " + damage)
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openPlayerMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }


    public static void openMapsMenu(Player player) {
        Menu menu = new Menu("Map Picker", 9 * 4);
        for (int i = 0; i < GameMap.values().length; i++) {
            String mapName = GameMap.values()[i].getMapName();
            menu.setItem(i + 1, 1,
                    new ItemBuilder(Utils.woolSortedByColor[i + 5])
                            .name(ChatColor.GREEN + mapName)
                            .get(),
                    (n, e) -> Bukkit.getServer().dispatchCommand(player, "start " + mapName)
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openGameMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openTimerMenu(Player player) {
        Menu menu = new Menu("Timer", 9 * 4);
        menu.setItem(3, 1,
                new ItemBuilder(Material.WOOD_BUTTON)
                        .name(ChatColor.GREEN + "Reset")
                        .get(),
                (n, e) -> Bukkit.getServer().dispatchCommand(player, "wl timer reset")
        );
        menu.setItem(5, 1,
                new ItemBuilder(Material.STONE_BUTTON)
                        .name(ChatColor.GREEN + "Skip")
                        .get(),
                (n, e) -> Bukkit.getServer().dispatchCommand(player, "wl timer skip")
        );
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openGameMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }


}
