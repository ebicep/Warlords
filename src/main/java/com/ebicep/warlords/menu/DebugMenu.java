package com.ebicep.warlords.menu;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.GameMap;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.maps.flags.FlagManager;
import com.ebicep.warlords.maps.flags.GroundFlagLocation;
import com.ebicep.warlords.maps.flags.PlayerFlagLocation;
import com.ebicep.warlords.maps.flags.SpawnFlagLocation;
import com.ebicep.warlords.player.*;
import com.ebicep.warlords.util.ItemBuilder;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.BiConsumer;

import static com.ebicep.warlords.menu.Menu.*;
import static com.ebicep.warlords.player.Classes.setSelectedBoost;
import static com.ebicep.warlords.util.Utils.woolSortedByColor;

public class DebugMenu {

    public static void openDebugMenu(Player player) {
        Menu menu = new Menu("Debug Options", 9 * 4);

        LinkedHashMap<ItemStack, BiConsumer<Menu, InventoryClickEvent>> items = new LinkedHashMap<>();
        items.put(new ItemBuilder(Material.ENDER_PORTAL_FRAME).name(ChatColor.GREEN + "Game Options").get(),
                (n, e) -> openGameMenu(player)
        );
        items.put(new ItemBuilder(CraftItemStack.asBukkitCopy(Warlords.getPlayerHeads().get(player.getUniqueId()))).name(ChatColor.GREEN + "Player Options").get(),
                (n, e) -> openPlayerMenu(player, Warlords.getPlayer(player))
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
                new ItemBuilder(Material.ICE)
                        .name(ChatColor.GREEN + "Freeze Game")
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
                            case 3:
                                Bukkit.getServer().dispatchCommand(player, "wl freeze");
                                break;
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openDebugMenu(player));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openPlayerMenu(Player player, WarlordsPlayer target) {
        String targetName = target != null ? target.getName() : "";
        Menu menu = new Menu("Player Options: " + (target != null ? targetName : player.getName()), 9 * 5);
        ItemStack[] firstRow = {
                new ItemBuilder(Material.EXP_BOTTLE)
                        .name(ChatColor.GREEN + "Energy")
                        .get(),
                new ItemBuilder(Material.INK_SACK, 1, (byte) 8)
                        .name(ChatColor.GREEN + "Cooldown")
                        .get(),
                new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .name(ChatColor.GREEN + "Damage")
                        .get(),
                new ItemBuilder(Material.AIR)
                        .get(),
                new ItemBuilder(Material.AIR)
                        .get(),
                new ItemBuilder(new Potion(PotionType.INSTANT_DAMAGE), 1, true)
                        .name(ChatColor.GREEN + "Kill")
                        .flags(ItemFlag.HIDE_POTION_EFFECTS)
                        .get(),
                new ItemBuilder(Material.WOOL, 1, (short) (Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam() == Team.BLUE ? 14 : 11))
                        .name(ChatColor.GREEN + "Swap to the " + (Warlords.getPlayerSettings(player.getUniqueId()).getWantedTeam() == Team.BLUE ? Team.RED.coloredPrefix() : Team.BLUE.coloredPrefix()) + ChatColor.GREEN + " team")
                        .get(),
        };
        ItemStack[] secondRow = {
                new ItemBuilder(Material.SUGAR)
                        .name(ChatColor.GREEN + "Modify Speed")
                        .get(),
                new ItemBuilder(new Potion(PotionType.INSTANT_HEAL), 1, true)
                        .name(ChatColor.GREEN + "Add Health")
                        .flags(ItemFlag.HIDE_POTION_EFFECTS)
                        .get(),
                new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(ChatColor.GREEN + "Take Damage")
                        .flags(ItemFlag.HIDE_ATTRIBUTES)
                        .get(),
                new ItemBuilder(Material.BREWING_STAND_ITEM)
                        .name(ChatColor.GREEN + "Cooldowns")
                        .get(),
                new ItemBuilder(Material.EYE_OF_ENDER)
                        .name(ChatColor.GREEN + "Teleport To")
                        .get(),
                new ItemBuilder(Material.BANNER)
                        .name(ChatColor.GREEN + "Flag Options")
                        .get(),
                new ItemBuilder(Material.NETHER_STAR)
                        .name(ChatColor.GREEN + "Change Spec")
                        .get(),
        };
        for (int i = 0; i < firstRow.length; i++) {
            int index = i + 1;
            menu.setItem(index, 1, firstRow[i],
                    (n, e) -> {
                        if (target != null) {
                            switch (index) {
                                case 1:
                                    Bukkit.getServer().dispatchCommand(player, "wl energy " + (target.isInfiniteEnergy() ? "enable" : "disable") + " " + targetName);
                                    break;
                                case 2:
                                    Bukkit.getServer().dispatchCommand(player, "wl cooldown " + (target.isDisableCooldowns() ? "enable" : "disable") + " " + targetName);
                                    break;
                                case 3:
                                    Bukkit.getServer().dispatchCommand(player, "wl damage " + (target.isTakeDamage() ? "disable" : "enable") + " " + targetName);
                                    break;
                                case 6:
                                    Bukkit.getServer().dispatchCommand(player, "kill " + targetName);
                                    break;
                                case 7:
                                    Team currentTeam = target.getTeam();
                                    Team otherTeam = target.getTeam() == Team.BLUE ? Team.RED : Team.BLUE;
                                    target.getGame().getPlayers().remove(target.getUuid());
                                    target.getGame().getPlayers().put(target.getUuid(), otherTeam);
                                    //todo something with rejoin point?
                                    target.setTeam(otherTeam);
                                    target.getScoreboard().updatePlayerName();
                                    Warlords.getPlayerSettings(target.getUuid()).setWantedTeam(otherTeam);
                                    target.teleport(otherTeam == Team.RED ? target.getGame().getMap().getRedLobbySpawnPoint() : target.getGame().getMap().getBlueLobbySpawnPoint());
                                    ArmorManager.resetArmor(Bukkit.getPlayer(target.getUuid()), Warlords.getPlayerSettings(target.getUuid()).getSelectedClass(), otherTeam);
                                    player.sendMessage(ChatColor.RED + "DEV: " + currentTeam.teamColor() + target.getName() + "§a was swapped to the " + otherTeam.coloredPrefix() + " §ateam");
                                    openPlayerMenu(player, target);
                                    break;
                            }
                        }
                    }
            );
        }
        for (int i = 0; i < secondRow.length; i++) {
            int index = i + 1;
            menu.setItem(index, 2, secondRow[i],
                    (n, e) -> {
                        if (target != null) {
                            switch (index) {
                                case 1:
                                    //TODO
                                    break;
                                case 2:
                                    openAmountMenu(player, target, "heal");
                                    break;
                                case 3:
                                    openAmountMenu(player, target, "takedamage");
                                    break;
                                case 4:
                                    openCooldownsMenu(player, target);
                                    break;
                                case 5:
                                    openTeleportLocations(player, target);
                                    break;
                                case 6:
                                    openFlagOptionMenu(player, target);
                                    break;
                                case 7:
                                    openSpecMenu(player, target);
                                    break;
                            }
                        }
                    }
            );
        }
        menu.setItem(3, 4, MENU_BACK, (n, e) -> {
            if (target != null && player.getUniqueId() == target.getUuid()) {
                openDebugMenu(player);
            } else {
                openTeamMenu(player);
            }
        });
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
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
                bluePlayers.forEach(wp -> wp.addHealth(wp, "", -69000, -69000, -1, 100, false));
            });
            menu.setItem(5, 0, redInfo, (n, e) -> {
            });
            menu.setItem(8, 0, killTeam, (n, e) -> {
                redPlayers.forEach(wp -> wp.addHealth(wp, "", -69000, -69000, -1, 100, false));
            });

            //players
            addPlayersToMenu(menu, player, bluePlayers, true);
            addPlayersToMenu(menu, player, redPlayers, false);
            menu.setItem(3, 5, MENU_BACK, (n, e) -> openDebugMenu(player));
            menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
            menu.openForPlayer(player);
        }
    }

    private static void addPlayersToMenu(Menu menu, Player player, List<WarlordsPlayer> warlordsPlayers, boolean blueTeam) {
        //flag player first
        warlordsPlayers.sort((wp1, wp2) -> {
            int wp1Flag = wp1.getGameState().flags().hasFlag(wp1) ? 1 : 0;
            int wp2Flag = wp2.getGameState().flags().hasFlag(wp2) ? 1 : 0;
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
                    new ItemBuilder(CraftItemStack.asBukkitCopy(Warlords.getPlayerHeads().get(wp.getUuid())))
                            .name((blueTeam ? ChatColor.BLUE : ChatColor.RED) + wp.getName() + (wp.getGameState().flags().hasFlag(wp) ? ChatColor.WHITE + " ⚑" : ""))
                            .lore(lore)
                            .get(),
                    (n, e) -> {
                        if (e.isRightClick() && player.getUniqueId() != wp.getUuid()) {
                            player.teleport(wp.getLocation());
                        } else {
                            openPlayerMenu(player, wp);
                        }
                    }
            );
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

    public static void openAmountMenu(Player player, WarlordsPlayer target, String commandType) {
        String targetName = target != null ? target.getName() : "";
        String commandName = commandType.equals("heal") ? "Give Health" : "Take Damage";
        Menu menu = new Menu(commandName + ": " + (target != null ? targetName : player.getName()), 9 * 4);
        for (int i = 1; i <= 5; i++) {
            int amount = i * 1000;
            menu.setItem(i + 1, 1,
                    new ItemBuilder(woolSortedByColor[i - 1])
                            .name((commandType.equals("takedamage") ? ChatColor.RED.toString() : ChatColor.GREEN.toString()) + amount)
                            .get(),
                    (n, e) -> Bukkit.getServer().dispatchCommand(player, "wl " + commandType + " " + amount + " " + targetName)
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openPlayerMenu(player, target));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openCooldownsMenu(Player player, WarlordsPlayer target) {
        int menuY = Math.min(5 + StatusEffectCooldowns.values().length / 7, 6);
        Menu menu = new Menu("Cooldowns: " + target.getName(), 9 * menuY);
        //general options
        ItemStack[] generalOptionItems = {
                new ItemBuilder(Material.BEACON)
                        .name(ChatColor.AQUA + "Manage Cooldowns")
                        .get(),
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.AQUA + "Clear All Cooldowns")
                        .get(),
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.AQUA + "Clear All Buffs")
                        .get(),
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.AQUA + "Clear All Debuffs")
                        .get(),
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.AQUA + "Clear All Abilities")
                        .get(),
        };
        for (int i = 0; i < generalOptionItems.length; i++) {
            int finalI = i;
            menu.setItem(i + 1, 1, generalOptionItems[i], (n, e) -> {
                switch (finalI) {
                    case 0:
                        openCooldownManagerMenu(player, target);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (player.getOpenInventory().getTopInventory().getName().equals("Cooldown Manager: " + target.getName())) {
                                    openCooldownManagerMenu(player, target);
                                } else {
                                    this.cancel();
                                }
                            }
                        }.runTaskTimer(Warlords.getInstance(), 20, 20);
                        break;
                    case 1:
                        target.getCooldownManager().clearCooldowns();
                        player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §aCooldowns were cleared");
                        break;
                    case 2:
                        target.getCooldownManager().removeBuffCooldowns();
                        player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §aBuffs were cleared");
                        break;
                    case 3:
                        target.getCooldownManager().removeDebuffCooldowns();
                        player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §aDebuffs were cleared");
                        break;
                    case 4:
                        target.getCooldownManager().removeAbilityCooldowns();
                        player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §aAbility Cooldowns were cleared");
                        break;
                }
            });
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
                            .name(cooldown.color + cooldown.name)
                            .flags(ItemFlag.HIDE_ATTRIBUTES)
                            .get(),
                    (n, e) -> openStatusEffectTimeMenu(player, target, cooldown));
        }
        menu.setItem(3, menuY - 1, MENU_BACK, (n, e) -> openPlayerMenu(player, target));
        menu.setItem(4, menuY - 1, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openCooldownManagerMenu(Player player, WarlordsPlayer target) {
        //int menuY = Math.min(4 + target.getCooldownManager().getCooldowns().size() / 7, 6); Menu shift annoying
        Menu menu = new Menu("Cooldown Manager: " + target.getName(), 9 * 6);
        //general info
        menu.setItem(4, 0,
                new ItemBuilder(CraftItemStack.asBukkitCopy(Warlords.getPlayerHeads().get(player.getUniqueId())))
                        .name(ChatColor.GREEN + "Cooldown Stats")
                        .lore(ChatColor.GREEN + "Total Cooldowns: " + target.getCooldownManager().getTotalCooldowns(),
                                ChatColor.GREEN + "Active Cooldowns: " + target.getCooldownManager().getCooldowns().size()
                        )
                        .get(),
                (n, e) -> {

                }
        );
        //cooldowns
        int yLevel = 0;
        List<Cooldown> cooldowns = new ArrayList<>(target.getCooldownManager().getCooldowns());
        cooldowns.sort(Comparator.comparing(Cooldown::getTimeLeft));
        for (int i = 0; i < cooldowns.size(); i++) {
            if (i % 7 == 0) {
                yLevel++;
                if (yLevel > 4) break;
            }
            Cooldown cooldown = cooldowns.get(i);
            menu.setItem((i % 7) + 1, yLevel,
                    new ItemBuilder(woolSortedByColor[i % woolSortedByColor.length])
                            .name(ChatColor.GOLD + cooldown.getName())
                            .lore(ChatColor.GREEN + "Time Left: " + ChatColor.GOLD + (Math.round(cooldown.getTimeLeft() * 10) / 10.0) + "s",
                                    ChatColor.GREEN + "From: " + cooldown.getFrom().getColoredName()
                            )
                            .get(),
                    (n, e) -> openCooldownMenu(player, target, cooldown)
            );
        }
        menu.setItem(3, 5, MENU_BACK, (n, e) -> openCooldownsMenu(player, target));
        menu.setItem(4, 5, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openCooldownMenu(Player player, WarlordsPlayer target, Cooldown cooldown) {
        Menu menu = new Menu(cooldown.getName() + ": " + target.getName(), 9 * 4);
        ItemStack[] cooldownOptions = {
                new ItemBuilder(Material.MILK_BUCKET)
                        .name(ChatColor.AQUA + "Remove")
                        .get(),
                new ItemBuilder(Material.REDSTONE)
                        .name(ChatColor.AQUA + "Add duration")
                        .get(),
        };
        for (int i = 0; i < cooldownOptions.length; i++) {
            int finalI = i;
            menu.setItem(i + 1, 1, cooldownOptions[i],
                    (n, e) -> {
                        if (target.getCooldownManager().getCooldowns().contains(cooldown)) {
                            switch (finalI + 1) {
                                case 1:
                                    target.getCooldownManager().getCooldowns().remove(cooldown);
                                    player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §a" + cooldown.getName() + " was removed");
                                    openCooldownManagerMenu(player, target);
                                    new BukkitRunnable() {
                                        @Override
                                        public void run() {
                                            if (player.getOpenInventory().getTopInventory().getName().equals("Cooldown Manager: " + target.getName())) {
                                                openCooldownManagerMenu(player, target);
                                            } else {
                                                this.cancel();
                                            }
                                        }
                                    }.runTaskTimer(Warlords.getInstance(), 20, 20);
                                    break;
                                case 2:
                                    openCooldownTimerMenu(player, target, cooldown);
                                    break;
                            }
                        } else {
                            openCooldownsMenu(player, target);
                            player.sendMessage(ChatColor.RED + "DEV: §aThat cooldown no longer exists");
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openCooldownsMenu(player, target));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openCooldownTimerMenu(Player player, WarlordsPlayer target, Cooldown cooldown) {
        Menu menu = new Menu(cooldown.getName() + "Duration: " + target.getName(), 9 * 4);
        int[] durations = {5, 15, 30, 60, 120, 300, 600};
        for (int i = 0; i < durations.length; i++) {
            int finalI = i;
            menu.setItem(i + 1, 1,
                    new ItemBuilder(woolSortedByColor[i + 5])
                            .name(ChatColor.GREEN.toString() + durations[i] + "s")
                            .get(),
                    (n, e) -> {
                        if (target.getCooldownManager().getCooldowns().contains(cooldown)) {
                            cooldown.subtractTime(-durations[finalI]);
                            player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §a" + cooldown.getName() + "'s duration was increased by " + durations[finalI] + " seconds");
                        } else {
                            openCooldownsMenu(player, target);
                            player.sendMessage(ChatColor.RED + "DEV: §aThat cooldown no longer exists");
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openCooldownMenu(player, target, cooldown));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openStatusEffectTimeMenu(Player player, WarlordsPlayer target, StatusEffectCooldowns cooldown) {
        Menu menu = new Menu("Cooldown Time: " + target.getName(), 9 * 4);
        int[] durations = {5, 15, 30, 60, 120, 300, 600};
        for (int i = 0; i < durations.length; i++) {
            int finalI = i;
            menu.setItem(i + 1, 1,
                    new ItemBuilder(woolSortedByColor[i + 5])
                            .name(ChatColor.GREEN.toString() + durations[i] + "s")
                            .get(),
                    (n, e) -> {
                        target.getCooldownManager().addCooldown(cooldown.name, cooldown.cooldownClass, cooldown.cooldownObject, cooldown.actionBarName, durations[finalI], target, cooldown.cooldownType);
                        player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §aRecieved " + durations[finalI] + " seconds of " + cooldown.name);
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openCooldownsMenu(player, target));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openTeleportLocations(Player player, WarlordsPlayer target) {
        Menu menu = new Menu("Teleport To: " + target.getName(), 9 * 5);
        GameMap gameMap = target.getGame().getMap();
        LinkedHashMap<ItemStack, Location> teleportLocationsBlue = new LinkedHashMap<>();
        teleportLocationsBlue.put(new ItemBuilder(Material.BEACON).name(ChatColor.BLUE + "Lobby Spawn Point").get(), gameMap.getBlueLobbySpawnPoint());
        teleportLocationsBlue.put(new ItemBuilder(Material.BED).name(ChatColor.BLUE + "Respawn Point").get(), gameMap.getBlueRespawn());
        teleportLocationsBlue.put(new ItemBuilder(Material.BANNER).name(ChatColor.BLUE + "Flag").get(), gameMap.getBlueFlag());
        teleportLocationsBlue.put(new ItemBuilder(Material.WOOL, 1, (short) 1).name(ChatColor.BLUE + "Energy Powerup").get(), gameMap.getDamagePowerupBlue());
        teleportLocationsBlue.put(new ItemBuilder(Material.WOOL, 1, (short) 5).name(ChatColor.BLUE + "Healing Powerup").get(), gameMap.getHealingPowerupBlue());
        teleportLocationsBlue.put(new ItemBuilder(Material.WOOL, 1, (short) 4).name(ChatColor.BLUE + "Speed Powerup").get(), gameMap.getSpeedPowerupBlue());
        LinkedHashMap<ItemStack, Location> teleportLocationsRed = new LinkedHashMap<>();
        teleportLocationsRed.put(new ItemBuilder(Material.BEACON).name(ChatColor.RED + "Lobby Spawn Point").get(), gameMap.getBlueLobbySpawnPoint());
        teleportLocationsRed.put(new ItemBuilder(Material.BED).name(ChatColor.RED + "Respawn Point").get(), gameMap.getBlueRespawn());
        teleportLocationsRed.put(new ItemBuilder(Material.BANNER).name(ChatColor.RED + "Flag").get(), gameMap.getBlueFlag());
        teleportLocationsRed.put(new ItemBuilder(Material.WOOL, 1, (short) 1).name(ChatColor.RED + "Energy Powerup").get(), gameMap.getDamagePowerupBlue());
        teleportLocationsRed.put(new ItemBuilder(Material.WOOL, 1, (short) 5).name(ChatColor.RED + "Healing Powerup").get(), gameMap.getHealingPowerupBlue());
        teleportLocationsRed.put(new ItemBuilder(Material.WOOL, 1, (short) 4).name(ChatColor.RED + "Speed Powerup").get(), gameMap.getSpeedPowerupBlue());
        for (int i = 0; i < teleportLocationsBlue.entrySet().size(); i++) {
            int finalI = i;
            menu.setItem(i + 1, 1, (ItemStack) teleportLocationsBlue.keySet().toArray()[i], (n, e) -> {
                target.teleport(teleportLocationsBlue.get((ItemStack) teleportLocationsBlue.keySet().toArray()[finalI]));
                player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "§a was teleported to the " + ChatColor.BLUE + "Blue " + ((ItemStack) teleportLocationsBlue.keySet().toArray()[finalI]).getItemMeta().getDisplayName());
            });
            menu.setItem(i + 1, 2, (ItemStack) teleportLocationsRed.keySet().toArray()[i], (n, e) -> {
                target.teleport(teleportLocationsRed.get((ItemStack) teleportLocationsRed.keySet().toArray()[finalI]));
                player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "§a was teleported to the " + ChatColor.RED + "Red " + ((ItemStack) teleportLocationsRed.keySet().toArray()[finalI]).getItemMeta().getDisplayName());
            });
        }
        menu.setItem(3, 4, MENU_BACK, (n, e) -> openPlayerMenu(player, target));
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openFlagOptionMenu(Player player, WarlordsPlayer target) {
        Menu menu = new Menu("Flag Options: " + target.getName(), 9 * 4);
        ItemStack[] flagOptions = {
                new ItemBuilder(Material.BANNER)
                        .name(ChatColor.GREEN + "Pick Up Flag")
                        .get(),
                new ItemBuilder(Material.BED)
                        .name(ChatColor.GREEN + "Return the Flag")
                        .get(),
                new ItemBuilder(Material.GRASS)
                        .name(ChatColor.GREEN + "Drop Flag")
                        .get(),
                new ItemBuilder(Material.REDSTONE_COMPARATOR)
                        .name(ChatColor.GREEN + "Set Multiplier")
                        .get(),
        };
        for (int i = 0; i < flagOptions.length; i++) {
            int finalI = i;
            menu.setItem(i + 1, 1, flagOptions[i],
                    (n, e) -> {
                        FlagManager flagManager = target.getGameState().flags();
                        WarlordsPlayer blueFlagPlayer = flagManager.getPlayerWithBlueFlag();
                        WarlordsPlayer redFlagPlayer = flagManager.getPlayerWithRedFlag();
                        switch (finalI) {
                            case 0:
                                if ((target.getTeam() == Team.RED && blueFlagPlayer == target) || (target.getTeam() == Team.BLUE && redFlagPlayer == target)) {
                                    player.sendMessage(ChatColor.RED + "DEV: §aThat player already has the flag");
                                } else {
                                    if (target.getTeam() == Team.BLUE) {
                                        if (redFlagPlayer != null) {
                                            //dropping flag from teammate
                                            flagManager.dropFlag(redFlagPlayer);
                                            //repicking it
                                            flagManager.getRed().setFlag(new PlayerFlagLocation(target, ((GroundFlagLocation) flagManager.getRed().getFlag()).getDamageTimer()));
                                        } else {
                                            //picking up flag
                                            flagManager.getRed().setFlag(new PlayerFlagLocation(target, 0));
                                        }
                                    } else if (target.getTeam() == Team.RED) {
                                        if (blueFlagPlayer != null) {
                                            //dropping flag from teammate
                                            flagManager.dropFlag(blueFlagPlayer);
                                            //repicking it
                                            flagManager.getBlue().setFlag(new PlayerFlagLocation(target, ((GroundFlagLocation) flagManager.getBlue().getFlag()).getDamageTimer()));
                                        } else {
                                            //picking up flag
                                            flagManager.getBlue().setFlag(new PlayerFlagLocation(target, 0));
                                        }
                                    }
                                }
                                break;
                            case 1:
                                if (flagManager.hasFlag(target)) {
                                    flagManager.dropFlag(target);
                                    if (target.getTeam() == Team.BLUE) {
                                        flagManager.getRed().setFlag(new SpawnFlagLocation(flagManager.getRed().getSpawnLocation(), player.getName()));
                                    } else {
                                        flagManager.getBlue().setFlag(new SpawnFlagLocation(flagManager.getBlue().getSpawnLocation(), player.getName()));
                                    }
                                } else {
                                    player.sendMessage(ChatColor.RED + "DEV: §aThat player does not have the flag");
                                }
                                break;
                            case 2:
                                if (flagManager.hasFlag(target)) {
                                    flagManager.dropFlag(target);
                                } else {
                                    player.sendMessage(ChatColor.RED + "DEV: §aThat player does not have the flag");
                                }
                                break;
                            case 3:
                                if (flagManager.hasFlag(target)) {
                                    openFlagMultiplierMenu(player, target);
                                } else {
                                    player.sendMessage(ChatColor.RED + "DEV: §aThat player does not have the flag");
                                }
                                break;
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openPlayerMenu(player, target));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openFlagMultiplierMenu(Player player, WarlordsPlayer target) {
        Menu menu = new Menu("Flag Multiplier: " + target.getName(), 9 * 4);
        int[] multipliers = {5, 10, 30, 60, 100, 150, 300};
        for (int i = 0; i < 7; i++) {
            int finalI = i;
            menu.setItem(i + 1, 1,
                    new ItemBuilder(woolSortedByColor[i + 5])
                            .name(ChatColor.GREEN.toString() + multipliers[i])
                            .get(),
                    (n, e) -> {
                        FlagManager flagManager = target.getGameState().flags();
                        int amount = e.isLeftClick() ? multipliers[finalI] : -multipliers[finalI];
                        if (target.getTeam() == Team.BLUE) {
                            if (flagManager.getPlayerWithRedFlag() != null) {
                                PlayerFlagLocation redFlag = ((PlayerFlagLocation) flagManager.getRed().getFlag());
                                if (redFlag.getPickUpTicks() + (60 * amount) < 0) {
                                    amount = -redFlag.getPickUpTicks() / 60;
                                }
                                redFlag.addPickUpTicks(60 * amount);
                                player.sendMessage(ChatColor.RED + "DEV: §aThe blue flag carrier gained " + amount + "%");
                            }
                        } else if (target.getTeam() == Team.RED) {
                            if (flagManager.getPlayerWithBlueFlag() != null) {
                                PlayerFlagLocation blueFlag = ((PlayerFlagLocation) flagManager.getBlue().getFlag());
                                if (blueFlag.getPickUpTicks() + (60 * amount) < 0) {
                                    amount = -blueFlag.getPickUpTicks() / 60;
                                }
                                blueFlag.addPickUpTicks(60 * amount);
                                player.sendMessage(ChatColor.RED + "DEV: §aThe red flag carrier gained " + amount + "%");
                            }
                        }
                    }
            );
        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openFlagOptionMenu(player, target));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSpecMenu(Player player, WarlordsPlayer target) {
        Menu menu = new Menu("Spec Menu: " + target.getName(), 9 * 5);
        ClassesGroup[] values = ClassesGroup.values();
        for (int i = 0; i < values.length; i++) {
            ClassesGroup group = values[i];
            menu.setItem(2, i,
                    new ItemBuilder(group.item)
                            .name(ChatColor.GREEN + group.name)
                            .get(),
                    (n, e) -> {
                    });
            List<Classes> classes = group.subclasses;
            for (int j = 0; j < classes.size(); j++) {
                int finalJ = j;
                ItemBuilder spec = new ItemBuilder(classes.get(j).specType.itemStack).name(ChatColor.GREEN + classes.get(j).name);
                if (target.getSpecClass() == classes.get(j)) {
                    spec.enchant(Enchantment.OXYGEN, 1);
                    spec.flags(ItemFlag.HIDE_ENCHANTS);
                }
                menu.setItem(4 + j, i, spec.get(),
                        (n, e) -> openSkillBoostMenu(player, target, classes.get(finalJ))
                );
            }
        }
        menu.setItem(3, 4, MENU_BACK, (n, e) -> openPlayerMenu(player, target));
        menu.setItem(4, 4, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openSkillBoostMenu(Player player, WarlordsPlayer target, Classes selectedClass) {
        Menu menu = new Menu("Skill Boost: " + target.getName(), 9 * 4);
        List<ClassesSkillBoosts> values = selectedClass.skillBoosts;
        for (int i = 0; i < values.size(); i++) {
            ClassesSkillBoosts skillBoost = values.get(i);
            menu.setItem(
                    6 - values.size() + i * 2 - 1,
                    1,
                    new ItemBuilder(selectedClass.specType.itemStack)
                            .name(ChatColor.RED + skillBoost.name + " (" + selectedClass.name + ")")
                            .lore(skillBoost.description,
                                    "",
                                    ChatColor.YELLOW + "Click to select!"
                            ).get(),
                    (n, e) -> {
                        setSelectedBoost(Bukkit.getPlayer(target.getUuid()), skillBoost);
                        target.setSpec(selectedClass.create.get(), skillBoost);
                        target.getScoreboard().updatePlayerName();
                        player.sendMessage(ChatColor.RED + "DEV: " + target.getColoredName() + "'s §aspec was changed to " + selectedClass.name);
                        openSpecMenu(player, target);
                    }
            );

        }
        menu.setItem(3, 3, MENU_BACK, (n, e) -> openSpecMenu(player, target));
        menu.setItem(4, 3, MENU_CLOSE, ACTION_CLOSE_MENU);
        menu.openForPlayer(player);
    }

    public static void openMapsMenu(Player player) {
        Menu menu = new Menu("Map Picker", 9 * 4);
        for (int i = 0; i < GameMap.values().length; i++) {
            String mapName = GameMap.values()[i].getMapName();
            menu.setItem(i + 1, 1,
                    new ItemBuilder(woolSortedByColor[i + 5])
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
