package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.Classes;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.RemoveEntities;
import com.ebicep.warlords.util.Utils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Game implements Runnable {

    private static final int POINT_LIMIT = 1000;
    public static int remaining = 0;
    public static TextComponent spacer = new TextComponent(ChatColor.GRAY + " - ");

    public enum State {
        PRE_GAME {
            @Override
            public void begin(Game game) {
                game.timer = 0;
                game.redPoints = 0;
                game.bluePoints = 0;
                game.forceEnd = false;
                game.teamBlue.clear();
                game.teamRed.clear();
                // Repair gates
                // Repair map damage (remove powerups)
            }

            @Override
            public Game.State run(Game game) {
                int players = game.teamBlue.size() + game.teamRed.size();
                if (players >= game.map.getMinPlayers()) {
                    int total = game.map.getCountdownTimerInTicks();
                    int remaining = total - game.timer;
                    if (remaining % 20 == 0) {
                        int time = remaining / 20;
                        if (time == 30) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + "30 " + ChatColor.YELLOW + "seconds!", false);

                            for (Player player1 : game.teamBlue) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                            for (Player player1 : game.teamRed) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                        } else if (time == 20) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in 20 seconds!", false);

                            for (Player player1 : game.teamBlue) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                            for (Player player1 : game.teamRed) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                        } else if (time == 10) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + "10 " + ChatColor.YELLOW + "seconds!", false);

                            for (Player player1 : game.teamBlue) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                            for (Player player1 : game.teamRed) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                        } else if (time <= 5 && time != 0) {
                            if (time == 1) {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " second", false);

                                for (Player player1 : game.teamBlue) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.teamRed) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                            } else {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " seconds!", false);

                                for (Player player1 : game.teamBlue) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.teamRed) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }
                            }
                        } else if (time == 0) {
                            sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
                            sendMessageToAllGamePlayer(game, "" + ChatColor.WHITE + ChatColor.BOLD + "Warlords", true);
                            sendMessageToAllGamePlayer(game, "", true);
                            sendMessageToAllGamePlayer(game, "" + ChatColor.YELLOW + ChatColor.BOLD + "Steal and capture the enemy team's flag to", true);
                            sendMessageToAllGamePlayer(game, "" + ChatColor.YELLOW + ChatColor.BOLD + "earn " + ChatColor.AQUA + ChatColor.BOLD + "250 " + ChatColor.YELLOW + ChatColor.BOLD + "points! The first team with a", true);
                            sendMessageToAllGamePlayer(game, "" + ChatColor.YELLOW + ChatColor.BOLD + "score of " + ChatColor.AQUA + ChatColor.BOLD + "1000 " + ChatColor.YELLOW + ChatColor.BOLD + "wins!", true);
                            sendMessageToAllGamePlayer(game, "", true);
                            sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);

                            for (Player player1 : game.teamBlue) {
                                player1.playSound(player1.getLocation(), "gamestart", 1, 1);
                            }

                            for (Player player1 : game.teamRed) {
                                player1.playSound(player1.getLocation(), "gamestart", 1, 1);
                            }
                        }
                        for (Player player : game.teamBlue) {
                            updateTimeLeft(player, time, game);
                            updatePlayers(player, players, game);
                        }
                        for (Player player : game.teamRed) {
                            updateTimeLeft(player, time, game);
                            updatePlayers(player, players, game);
                        }

                    }
                    if (game.timer == total) {
                        return GAME;
                    }

                    game.timer++;
                    //TESTING
                    return GAME;

                } else {
                    game.timer = 0;
                }
                return null;
            }

        },
        GAME {
            @Override
            public void begin(Game game) {
                game.flags = new FlagManager(game.map.redFlag, game.map.blueFlag);
                game.timer = 0;
                // Close config screen
                List<WarlordsPlayer> blueTeam = new ArrayList<>();
                List<WarlordsPlayer> redTeam = new ArrayList<>();

                RemoveEntities removeEntities = new RemoveEntities();
                removeEntities.onRemove();

                for (Player p : game.teamRed) {

                    Classes selected = Classes.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selected.create.apply(p), false));

                    redTeam.add(Warlords.getPlayer(p));

                    resetArmor(p, Warlords.getPlayer(p).getSpec(), false);
                    System.out.println("Added " + p.getName());
                }

                for (Player p : game.teamBlue) {

                    Classes selected = Classes.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selected.create.apply(p), false));

                    blueTeam.add(Warlords.getPlayer(p));

                    resetArmor(p, Warlords.getPlayer(p).getSpec(), true);
                    System.out.println("Added " + p.getName());
                }

                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    value.getPlayer().setMaxHealth(40);
                    value.getPlayer().setLevel((int) value.getMaxEnergy());
                    value.getPlayer().getInventory().clear();
                    value.assignItemLore();
                    System.out.println("updated scoreboard for " + value.getName());
                    value.setScoreboard(new CustomScoreboard(value, blueTeam, redTeam, game));
                }

                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    value.getScoreboard().addHealths();
                    System.out.println(value.getScoreboard());
                }

                System.out.println(blueTeam);
                System.out.println(redTeam);

            }

            @Override
            public Game.State run(Game game) {
                if (
                        game.bluePoints >= POINT_LIMIT || game.redPoints >= POINT_LIMIT || game.timer >= game.map.getGameTimerInTicks() * 20 || game.forceEnd
                ) {
                    return END;
                }
                if (game.timer <= 10 * 20) {
                    if (game.timer == 10 * 20) {
                        // Destroy gates
                        // Enable abilities
                        sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Gates opened! " + ChatColor.RED + "FIGHT!", false);

                        for (Player player1 : game.teamBlue) {
                            player1.playSound(player1.getLocation(), Sound.WITHER_SPAWN, 1, 1);
                        }

                        for (Player player1 : game.teamRed) {
                            player1.playSound(player1.getLocation(), Sound.WITHER_SPAWN, 1, 1);
                        }

                    } else {
                        if (game.timer % 20 == 0) {

                            int time = game.timer / 20;
                            if (time == 0) {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + "10" + ChatColor.YELLOW + " seconds!", false);

                                for (Player player1 : game.teamBlue) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.teamRed) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                            } else if (time >= 5) {

                                for (Player player1 : game.teamBlue) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.teamRed) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                if (time == 9) {
                                    sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + (10 - time) + ChatColor.YELLOW + " second!", false);
                                } else {
                                    sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + (10 - time) + ChatColor.YELLOW + " seconds!", false);
                                }
                            }
                        }
                    }
                } else if (game.timer == 30 * 20) {
                    // Enable powerups
                    game.powerUps = new PowerupManager(game.map).runTaskTimer(Warlords.getInstance(), 0, 0);
                }

                if (game.timer % 20 == 0) {
                    remaining = (game.map.getGameTimerInTicks() - game.timer) / 20;
                    for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                        value.getScoreboard().updateTime();
                    }
                }

                game.timer++;
                return null;
            }

        },
        END {
            @Override
            public void begin(Game game) {
                Bukkit.broadcastMessage("The game has ended!");
                // Disable abilities
                game.flags.stop();
                game.flags = null;
                boolean teamBlueWins = !game.forceEnd && game.bluePoints > game.redPoints;
                boolean teamRedWins = !game.forceEnd && game.redPoints > game.bluePoints;

                if (game.powerUps != null) {
                    game.powerUps.cancel();
                }

                // Announce winner
                List<WarlordsPlayer> players = new ArrayList<>(Warlords.getPlayers().values());
                sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
                sendMessageToAllGamePlayer(game, "" + ChatColor.WHITE + ChatColor.BOLD + "  Warlords", true);

                sendMessageToAllGamePlayer(game, "", false);

                TextComponent mvp = new TextComponent("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "✚ MVP ✚");
                //TODO caps/returns MVPPPP
                mvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.LIGHT_PURPLE + "Total Flag Captures (everyone): " + "HERE" + "\n" + //Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::totalcaps).sum())
                                ChatColor.LIGHT_PURPLE + "Total Flag Returns (everyone): " + "HERE").create())); //Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::totalreturns).sum())
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(mvp));
                TextComponent playerMvp = new TextComponent(ChatColor.AQUA + "Joe");
                //players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::totalcaps+returns (make method in warlordsplayer?)).collect(Collectors.toList());
                playerMvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.LIGHT_PURPLE + "Flag Captures: " + ChatColor.GOLD + "HERE" + "\n" + //players.get(0).getcaps...
                                ChatColor.LIGHT_PURPLE + "Flag Returns: " + ChatColor.GOLD + "HERE").create()));
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(playerMvp));

                sendMessageToAllGamePlayer(game, "", false);

                TextComponent totalDamage = new TextComponent("" + ChatColor.RED + ChatColor.BOLD + "✚ TOP DAMAGE ✚");
                totalDamage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.RED + "Total Damage (everyone)" + ChatColor.GRAY + ": " +
                                ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalDamage).sum())).create()));
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalDamage));
                players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalDamage)).collect(Collectors.toList());
                List<TextComponent> leaderboardPlayersDamage = new ArrayList<>();
                for (int i = 0; i < players.size() && i < 3; i++) {
                    WarlordsPlayer warlordsPlayer = players.get(i);
                    TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayer.getTotalHealing() + "k");
                    player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                            ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + "90 " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
                    leaderboardPlayersDamage.add(player);
                    if (i != players.size() - 1) {
                        leaderboardPlayersDamage.add(spacer);
                    }
                }
                sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersDamage);

                sendMessageToAllGamePlayer(game, "", false);

                TextComponent totalHealing = new TextComponent("" + ChatColor.GREEN + ChatColor.BOLD + "✚ TOP HEALING ✚");
                totalHealing.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.GREEN + "Total Healing (everyone)" + ChatColor.GRAY + ": " +
                                ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalHealing).sum())).create()));
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalHealing));
                players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalHealing)).collect(Collectors.toList());
                List<TextComponent> leaderboardPlayersHealing = new ArrayList<>();
                for (int i = 0; i < players.size() && i < 3; i++) {
                    WarlordsPlayer warlordsPlayer = players.get(i);
                    TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + warlordsPlayer.getTotalHealing() + "k");
                    player.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                            ChatColor.DARK_GRAY + "Lv" + ChatColor.GRAY + "90 " + ChatColor.GOLD + warlordsPlayer.getSpec().getClassName() + ChatColor.GREEN + " (" + warlordsPlayer.getSpec().getClass().getSimpleName() + ")").create()));
                    leaderboardPlayersHealing.add(player);
                    if (i != players.size() - 1) {
                        leaderboardPlayersHealing.add(spacer);
                    }
                }
                sendCenteredHoverableMessageToAllGamePlayer(game, leaderboardPlayersHealing);

                sendMessageToAllGamePlayer(game, "", false);

                TextComponent yourStatistics = new TextComponent("" + ChatColor.GOLD + ChatColor.BOLD + "✚ YOUR STATISTICS ✚");
                yourStatistics.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.WHITE + "Total Kills (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalKills).sum()) + "\n" +
                                ChatColor.WHITE + "Total Assists (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalAssists).sum()) + "\n" +
                                ChatColor.WHITE + "Total Deaths (everyone): " + ChatColor.GREEN + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalDeaths).sum())).create()));
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(yourStatistics));
                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    TextComponent kills = new TextComponent(ChatColor.WHITE + "Kills: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalKills()));
                    TextComponent assists = new TextComponent(ChatColor.WHITE + "Assists: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalAssists()));
                    TextComponent deaths = new TextComponent(ChatColor.WHITE + "Deaths: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalDeaths()));
                    String killsJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Kills"));
                    String assistsJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Assists"));
                    String deathsJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Deaths"));
                    kills.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(killsJson).create()));
                    assists.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(assistsJson).create()));
                    deaths.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(deathsJson).create()));
                    Utils.sendCenteredHoverableMessage(value.getPlayer(), Arrays.asList(kills, spacer, assists, spacer, deaths));


                    TextComponent damage = new TextComponent(ChatColor.WHITE + "Damage: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalDamage()));
                    TextComponent heal = new TextComponent(ChatColor.WHITE + "Healing: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalHealing()));
                    TextComponent absorb = new TextComponent(ChatColor.WHITE + "Absorbed: " + ChatColor.GOLD + Utils.addCommaAndRound(value.getTotalAbsorbed()));
                    String damageJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Damage"));
                    String healingJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Healing"));
                    String absorbedJson = Utils.convertItemStackToJsonRegular(value.getStatItemStack("Absorbed"));
                    damage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(damageJson).create()));
                    heal.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(healingJson).create()));
                    absorb.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ITEM, new ComponentBuilder(absorbedJson).create()));
                    Utils.sendCenteredHoverableMessage(value.getPlayer(), Arrays.asList(damage, spacer, heal, spacer, absorb));

                }

                sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);

                game.timer = 0;

            }

            @Override
            public Game.State run(Game game) {
                game.timer++;
                if (game.timer > 10 * 20 || true) {
                    for (Player player : game.teamBlue) {
                        if (player != null) {
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }
                    for (Player player : game.teamRed) {
                        if (player != null) {
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }
                    return PRE_GAME;
                }

                return null;
            }
        },
        ;

        public abstract Game.State run(Game game);

        public abstract void begin(Game game);

        public void updatePlayers(Player player, int players, Game game) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String dateString = format.format(new Date());
            Scoreboard scoreboard = player.getScoreboard();
            for (String entry : scoreboard.getEntries()) {
                String entryUnformatted = ChatColor.stripColor(entry);
                if (entryUnformatted.contains("Players")) {
                    scoreboard.resetScores(entry);
                    scoreboard.getObjective(dateString).getScore(ChatColor.WHITE + "Players: " + ChatColor.GREEN + players + "/" + game.getMap().getMaxPlayers()).setScore(10);
                }
            }
        }

        public void updateTimeLeft(Player player, int time, Game game) {
            SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
            String dateString = format.format(new Date());
            Scoreboard scoreboard = player.getScoreboard();
            for (String entry : scoreboard.getEntries()) {
                String entryUnformatted = ChatColor.stripColor(entry);
                if (entryUnformatted.contains("Starting in")) {
                    scoreboard.resetScores(entry);
                    if (time < 10) {
                        scoreboard.getObjective(dateString).getScore(ChatColor.WHITE + "Starting in: " + ChatColor.GREEN + "00:0" + time + ChatColor.WHITE + " to").setScore(8);
                    } else {
                        scoreboard.getObjective(dateString).getScore(ChatColor.WHITE + "Starting in: " + ChatColor.GREEN + "00:" + time + ChatColor.WHITE + " to").setScore(8);

                    }
                }
            }
        }

        public void sendMessageToAllGamePlayer(Game game, String message, boolean centered) {
            for (Player p : game.teamBlue) {
                if (centered) {
                    Utils.sendCenteredMessage(p, message);
                } else {
                    p.sendMessage(message);
                }
            }
            for (Player p : game.teamRed) {
                if (centered) {
                    Utils.sendCenteredMessage(p, message);
                } else {
                    p.sendMessage(message);
                }
            }
        }

        public void sendCenteredHoverableMessageToAllGamePlayer(Game game, List<TextComponent> message) {
            for (Player p : game.teamBlue) {
                Utils.sendCenteredHoverableMessage(p, message);
            }
            for (Player p : game.teamRed) {
                Utils.sendCenteredHoverableMessage(p, message);
            }
        }

        public void resetArmor(Player p, PlayerClass playerClass, boolean onBlue) {
            ItemStack[] armor = new ItemStack[4];
            if (onBlue) {
                armor[0] = new ItemStack(Material.LEATHER_BOOTS);
                LeatherArmorMeta meta0 = (LeatherArmorMeta) armor[0].getItemMeta();
                meta0.setColor(Color.fromRGB(51, 76, 178));
                armor[0].setItemMeta(meta0);
                armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
                LeatherArmorMeta meta1 = (LeatherArmorMeta) armor[1].getItemMeta();
                meta1.setColor(Color.fromRGB(51, 76, 178));
                armor[1].setItemMeta(meta1);
                armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta meta2 = (LeatherArmorMeta) armor[2].getItemMeta();
                meta2.setColor(Color.fromRGB(51, 76, 178));
                armor[2].setItemMeta(meta2);
                if (playerClass instanceof AbstractPaladin) {
                    armor[3] = new ItemStack(Material.RED_ROSE, 1, (short) 6);
                } else if (playerClass instanceof AbstractWarrior) {
                    armor[3] = new ItemStack(Material.WOOD_PLATE);
                } else if (playerClass instanceof AbstractMage) {
                    armor[3] = new ItemStack(Material.SAPLING, 1, (short) 5);
                } else {
                    armor[3] = new ItemStack(Material.SAPLING, 1, (short) 1);
                }
            } else {
                armor[0] = new ItemStack(Material.LEATHER_BOOTS);
                LeatherArmorMeta meta0 = (LeatherArmorMeta) armor[0].getItemMeta();
                meta0.setColor(Color.fromRGB(153, 51, 51));
                armor[0].setItemMeta(meta0);
                armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
                LeatherArmorMeta meta1 = (LeatherArmorMeta) armor[1].getItemMeta();
                meta1.setColor(Color.fromRGB(153, 51, 51));
                armor[1].setItemMeta(meta1);
                armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta meta2 = (LeatherArmorMeta) armor[2].getItemMeta();
                meta2.setColor(Color.fromRGB(153, 51, 51));
                armor[2].setItemMeta(meta2);
                if (playerClass instanceof AbstractPaladin) {
                    armor[3] = new ItemStack(Material.DEAD_BUSH);
                } else if (playerClass instanceof AbstractWarrior) {
                    armor[3] = new ItemStack(Material.STONE_PLATE);
                } else if (playerClass instanceof AbstractMage) {
                    armor[3] = new ItemStack(Material.RED_ROSE, 1, (short) 5);
                } else {
                    armor[3] = new ItemStack(Material.SAPLING, 1, (short) 0);
                }
            }
            p.getInventory().setArmorContents(armor);
        }
    }


    private Game.State state = Game.State.PRE_GAME;
    private int timer = 0;
    private GameMap map = GameMap.RIFT;
    private final Set<Player> teamRed = new HashSet<>();
    private final Set<Player> teamBlue = new HashSet<>();
    private int redPoints;
    private int bluePoints;
    private boolean forceEnd;
    private FlagManager flags = null;
    private BukkitTask powerUps = null;

    public void forceDraw() {
        this.forceEnd = true;
    }

    public State getState() {
        return state;
    }

    public int getTimer() {
        return timer;
    }

    public GameMap getMap() {
        return map;
    }

    public Set<Player> getTeamRed() {
        return teamRed;
    }

    public Set<Player> getTeamBlue() {
        return teamBlue;
    }

    public int getRedPoints() {
        return redPoints;
    }

    public int getBluePoints() {
        return bluePoints;
    }

    public void addBluePoints(int i) {
        this.bluePoints += i;
    }

    public void addRedPoints(int i) {
        this.redPoints += i;
    }

    public boolean isForceEnd() {
        return forceEnd;
    }

    public boolean isRedTeam(Player player) {
        return teamRed.contains(player);
    }

    public boolean isBlueTeam(Player player) {
        return teamBlue.contains(player);
    }

    public boolean canChangeMap() {
        return teamBlue.isEmpty() && teamRed.isEmpty() && state == Game.State.PRE_GAME;
    }

    public void resetTimer() {
        this.timer = 0;
    }

    public int getMinute() {
        return this.timer / 20 / 60;
    }

    public int getScoreboardMinute() {
        return 15 - (getMinute() + 1);
    }

    public int getSecond() {
        return this.timer / 20;
    }

    public int getScoreboardSecond() {
        return 60 * (getMinute() + 1) - getSecond();
    }

    public void changeMap(GameMap map) {
        if (!canChangeMap()) {
            throw new IllegalStateException("Cannot change map!");
        }
        this.map = map;
    }

    public void addPlayer(Player player, boolean teamBlue) {
        if (teamBlue) {
            this.teamRed.remove(player);
            this.teamBlue.add(player);
            player.teleport(this.map.blueLobbySpawnPoint);
        } else {
            this.teamBlue.remove(player);
            this.teamRed.add(player);
            player.teleport(this.map.redLobbySpawnPoint);
        }
    }

    public void removePlayer(Player player) {
        this.teamRed.remove(player);
        this.teamBlue.remove(player);
    }

    public boolean onSameTeam(Player player1, Player player2) {
        return teamBlue.contains(player1) && teamBlue.contains(player2) || teamRed.contains(player1) && teamRed.contains(player2);
    }

    public boolean onSameTeam(WarlordsPlayer player1, WarlordsPlayer player2) {
        return teamBlue.contains(player1.getPlayer()) && teamBlue.contains(player2.getPlayer()) || teamRed.contains(player1.getPlayer()) && teamRed.contains(player2.getPlayer());
    }

    public FlagManager getFlags() {
        return flags;
    }

    public void setFlags(FlagManager flags) {
        this.flags = flags;
    }

    public void giveLobbyScoreboard(Player player) {
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        Scoreboard board = manager.getNewScoreboard();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Objective sideBar = board.registerNewObjective(dateString, "");
        sideBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideBar.setDisplayName("§e§lWARLORDS");
        sideBar.getScore(ChatColor.GRAY + dateString).setScore(13);
        sideBar.getScore(" ").setScore(12);
        sideBar.getScore(ChatColor.WHITE + "Map: " + ChatColor.GREEN + getMap().getMapName()).setScore(11);
        sideBar.getScore(ChatColor.WHITE + "Players: " + ChatColor.GREEN + "0/" + getMap().getMaxPlayers()).setScore(10);
        sideBar.getScore("  ").setScore(9);
        sideBar.getScore(ChatColor.WHITE + "Starting in: " + ChatColor.GREEN + "00:15 " + ChatColor.WHITE + "to").setScore(8);
        sideBar.getScore(ChatColor.WHITE + "allow time for ").setScore(7);
        sideBar.getScore(ChatColor.WHITE + "additional players").setScore(6);
        sideBar.getScore("   ").setScore(5);
        //sideBar.getScore(ChatColor.GOLD + "Lv90 " + warlordsPlayer.getSpec().getClassName()).setScore(4);
        //sideBar.getScore(ChatColor.WHITE + "Spec: " + ChatColor.GREEN + warlordsPlayer.getSpec().getClass().getSimpleName()).setScore(3);
        sideBar.getScore("    ").setScore(2);
        sideBar.getScore(ChatColor.YELLOW + "WL 2.0 master_b-v0.0.2 ").setScore(1);

        player.setScoreboard(board);
    }


    @Override
    public void run() {
        Game.State newState = state.run(this);
        if (newState != null) {
            this.state = newState;
            newState.begin(this);
        }
    }
}