package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.*;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
                game.cachedTeamBlue.clear();
                game.cachedTeamRed.clear();
                Gates.changeGates(game.map, false);
                // Repair map damage (remove powerups)
            }

            @Override
            public Game.State run(Game game) {
                int players = game.cachedTeamBlue.size() + game.cachedTeamRed.size();

                if (players >= game.map.getMinPlayers()) {
                    int total = game.map.getCountdownTimerInTicks();
                    int remaining = total - game.timer;
                    for (Player player : game.cachedTeamBlue) {
                        game.updateTimeLeft(player, remaining / 20);
                        game.updatePlayers(player, players, game);
                    }
                    for (Player player : game.cachedTeamRed) {
                        game.updateTimeLeft(player, remaining / 20);
                        game.updatePlayers(player, players, game);
                    }
                    if (remaining % 20 == 0) {
                        int time = remaining / 20;
                        if (time == 30) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.GREEN + "30 " + ChatColor.YELLOW + "seconds!", false);

                            for (Player player1 : game.cachedTeamBlue) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                            for (Player player1 : game.cachedTeamRed) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                        } else if (time == 20) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in 20 seconds!", false);

                            for (Player player1 : game.cachedTeamBlue) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                            for (Player player1 : game.cachedTeamRed) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                        } else if (time == 10) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + "10 " + ChatColor.YELLOW + "seconds!", false);

                            for (Player player1 : game.cachedTeamBlue) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                            for (Player player1 : game.cachedTeamRed) {
                                player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                            }

                        } else if (time <= 5 && time != 0) {
                            if (time == 1) {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " second", false);

                                for (Player player1 : game.cachedTeamBlue) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.cachedTeamRed) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                            } else {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " seconds!", false);

                                for (Player player1 : game.cachedTeamBlue) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.cachedTeamRed) {
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

                            for (Player player1 : game.cachedTeamBlue) {
                                player1.playSound(player1.getLocation(), "gamestart", 1, 1);
                            }

                            for (Player player1 : game.cachedTeamRed) {
                                player1.playSound(player1.getLocation(), "gamestart", 1, 1);
                            }
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
                RemoveEntities removeEntities = new RemoveEntities();
                removeEntities.onRemove();

                List<WarlordsPlayer> blueTeam = new ArrayList<>();
                List<WarlordsPlayer> redTeam = new ArrayList<>();

                //temp garbage bc of concurrent modification + teams final + see who is a retard and didnt pick teams (very intentional)
                Set<Player> tempCachedTeamBlue = new HashSet<>();
                Set<Player> tempCachedTeamRed = new HashSet<>();
                Set<Player> tempPlayersNullTeam = new HashSet<>();
                for (Player player : game.cachedTeamBlue) {
                    Team team = Team.getSelected(player);
                    if (team == Team.RED) {
                        tempCachedTeamRed.add(player);
                    } else if (team == Team.BLUE) {
                        tempCachedTeamBlue.add(player);
                    } else {
                        tempPlayersNullTeam.add(player);
                    }
                }
                for (Player player : game.cachedTeamRed) {
                    Team team = Team.getSelected(player);
                    if (team == Team.RED) {
                        tempCachedTeamRed.add(player);
                    } else if (team == Team.BLUE) {
                        tempCachedTeamBlue.add(player);
                    } else {
                        tempPlayersNullTeam.add(player);
                    }
                }

                game.cachedTeamBlue.clear();
                for (Player player : tempCachedTeamBlue) {
                    game.addPlayer(player, true);
                }
                game.cachedTeamRed.clear();
                for (Player player : tempCachedTeamRed) {
                    game.addPlayer(player, false);
                }
                for (Player player : tempPlayersNullTeam) {
                    Bukkit.broadcastMessage(player.getName() + " did not choose a team!");
                    game.addPlayer(player, game.cachedTeamBlue.size() < game.cachedTeamRed.size());
                }

                for (Player p : game.cachedTeamRed) {
                    Classes selectedClass = Classes.getSelected(p);
                    Weapons selectedWeapon = Weapons.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selectedClass.create.apply(p), selectedWeapon, false));
                    redTeam.add(Warlords.getPlayer(p));
                }
                for (Player p : game.cachedTeamBlue) {
                    Classes selectedClass = Classes.getSelected(p);
                    Weapons selectedWeapon = Weapons.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selectedClass.create.apply(p), selectedWeapon, false));
                    blueTeam.add(Warlords.getPlayer(p));
                }

                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    Player player = value.getPlayer();
                    player.setGameMode(GameMode.ADVENTURE);
                    ((EntityLiving) ((CraftPlayer) player).getHandle()).setAbsorptionHearts(0);
                    player.setMaxHealth(40);
                    player.getInventory().clear();
                    player.closeInventory();
                    value.assignItemLore();
                    value.setScoreboard(new CustomScoreboard(value, blueTeam, redTeam, game));
                }

                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    value.getScoreboard().addHealths();
                    value.applySkillBoost();
                }
            }

            @Override
            public Game.State run(Game game) {
                if (
                        game.bluePoints >= POINT_LIMIT || game.redPoints >= POINT_LIMIT || game.timer >= game.map.getGameTimerInTicks() || game.forceEnd
                ) {
                    return END;
                }
                if (game.timer <= 10 * 20) {
                    if (game.timer == 10 * 20) {
                        Gates.changeGates(game.map, true);
                        sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Gates opened! " + ChatColor.RED + "FIGHT!", false);

                        for (Player player1 : game.cachedTeamBlue) {
                            PacketUtils.sendTitle(player1, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);
                            player1.playSound(player1.getLocation(), Sound.WITHER_SPAWN, 1, 1);
                        }

                        for (Player player1 : game.cachedTeamRed) {
                            PacketUtils.sendTitle(player1, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);
                            player1.playSound(player1.getLocation(), Sound.WITHER_SPAWN, 1, 1);
                        }

                    } else {
                        if (game.timer % 20 == 0) {
                            int time = game.timer / 20;
                            if (time == 0) {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + "10" + ChatColor.YELLOW + " seconds!", false);

                                for (Player player1 : game.cachedTeamBlue) {
                                    PacketUtils.sendTitle(player1, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.cachedTeamRed) {
                                    PacketUtils.sendTitle(player1, ChatColor.GREEN + "GO!", ChatColor.YELLOW + "Steal and capture the enemy flag!", 0, 40, 20);
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                            } else if (time >= 5) {

                                for (Player player1 : game.cachedTeamBlue) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                for (Player player1 : game.cachedTeamRed) {
                                    player1.playSound(player1.getLocation(), Sound.NOTE_STICKS, 1, 1);
                                }

                                if (time == 9) {
                                    sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + (10 - time) + ChatColor.YELLOW + " second!", false);
                                } else {
                                    sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + (10 - time) + ChatColor.YELLOW + " seconds!", false);
                                }
                            }
                            String number = "";
                            if (10 - time >= 8) {
                                number += ChatColor.GREEN;
                            } else if (10 - time >= 4) {
                                number += ChatColor.YELLOW;
                            } else {
                                number += ChatColor.RED;
                            }
                            number += 10 - time;
                            for (Player p : game.cachedTeamBlue) {
                                PacketUtils.sendTitle(p, number, "", 0, 40, 0);
                            }
                            for (Player p : game.cachedTeamRed) {
                                PacketUtils.sendTitle(p, number, "", 0, 40, 0);
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
                game.flags.stop();
                game.flags = null;
                boolean teamBlueWins = !game.forceEnd && game.bluePoints > game.redPoints;
                boolean teamRedWins = !game.forceEnd && game.redPoints > game.bluePoints;

                if (game.powerUps != null) {
                    game.powerUps.cancel();
                }

                List<WarlordsPlayer> players = new ArrayList<>(Warlords.getPlayers().values());

                sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
                sendMessageToAllGamePlayer(game, "" + ChatColor.WHITE + ChatColor.BOLD + "  Warlords", true);

                sendMessageToAllGamePlayer(game, "", false);

                if (teamBlueWins) {
                    sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.BLUE + "BLU", true);
                } else if (teamRedWins) {
                    sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.RED + "RED", true);
                } else {
                    sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "Winner" + ChatColor.GRAY + " - " + ChatColor.LIGHT_PURPLE + "DRAW", true);
                }

                sendMessageToAllGamePlayer(game, "", false);

                TextComponent mvp = new TextComponent("" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "✚ MVP ✚");
                mvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.LIGHT_PURPLE + "Total Flag Captures (everyone): " + ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getFlagsCaptured).sum()) + "\n" +
                                ChatColor.LIGHT_PURPLE + "Total Flag Returns (everyone): " + ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getFlagsCaptured).sum())).create()));
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(mvp));
                players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalCapsAndReturns)).collect(Collectors.toList());
                Collections.reverse(players);
                TextComponent playerMvp = new TextComponent(ChatColor.AQUA + players.get(0).getName());
                playerMvp.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.LIGHT_PURPLE + "Flag Captures: " + ChatColor.GOLD + players.get(0).getFlagsCaptured() + "\n" +
                                ChatColor.LIGHT_PURPLE + "Flag Returns: " + ChatColor.GOLD + players.get(0).getFlagsReturned()).create()));
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(playerMvp));

                sendMessageToAllGamePlayer(game, "", false);

                TextComponent totalDamage = new TextComponent("" + ChatColor.RED + ChatColor.BOLD + "✚ TOP DAMAGE ✚");
                totalDamage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(
                        ChatColor.RED + "Total Damage (everyone)" + ChatColor.GRAY + ": " +
                                ChatColor.GOLD + Utils.addCommaAndRound((float) players.stream().mapToDouble(WarlordsPlayer::getTotalDamage).sum())).create()));
                sendCenteredHoverableMessageToAllGamePlayer(game, Collections.singletonList(totalDamage));
                players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getTotalDamage)).collect(Collectors.toList());
                Collections.reverse(players);
                List<TextComponent> leaderboardPlayersDamage = new ArrayList<>();
                for (int i = 0; i < players.size() && i < 3; i++) {
                    WarlordsPlayer warlordsPlayer = players.get(i);
                    TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + getSimplifiedNumber((long) warlordsPlayer.getTotalDamage()));
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
                Collections.reverse(players);
                List<TextComponent> leaderboardPlayersHealing = new ArrayList<>();
                for (int i = 0; i < players.size() && i < 3; i++) {
                    WarlordsPlayer warlordsPlayer = players.get(i);
                    TextComponent player = new TextComponent(ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + getSimplifiedNumber((long) warlordsPlayer.getTotalHealing()));
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

                    value.getPlayer().setGameMode(GameMode.ADVENTURE);
                }

                sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);

                game.timer = 0;

            }

            @Override
            public Game.State run(Game game) {
                game.timer++;
                if (game.timer > 10 * 20 || true) {
                    for (Player player : game.clearAllPlayers()) {
                        if (player != null) {
                            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                        }
                    }
                    Gates.changeGates(game.map, false);
                    return PRE_GAME;
                }

                return null;
            }
        },
        ;

        public abstract Game.State run(Game game);

        public abstract void begin(Game game);

        public static WarlordsPlayer updateTempPlayer(Player player) {
            WarlordsPlayer temp = new WarlordsPlayer(player, player.getName(), player.getUniqueId(), Classes.getSelected(player).create.apply(player), Weapons.getSelected(player), false);
            temp.applySkillBoost();
            temp.assignItemLore();
            return temp;
        }

        public void sendMessageToAllGamePlayer(Game game, String message, boolean centered) {
            for (Player p : game.players.keySet()) {
                if (centered) {
                    Utils.sendCenteredMessage(p, message);
                } else {
                    p.sendMessage(message);
                }
            }
        }

        public void sendCenteredHoverableMessageToAllGamePlayer(Game game, List<TextComponent> message) {
            for (Player p : game.players.keySet()) {
                Utils.sendCenteredHoverableMessage(p, message);
            }
        }

        private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

        static {
            suffixes.put(1_000L, "k");
            suffixes.put(1_000_000L, "m");
            suffixes.put(1_000_000_000L, "b");
            suffixes.put(1_000_000_000_000L, "t");
            suffixes.put(1_000_000_000_000_000L, "p");
            suffixes.put(1_000_000_000_000_000_000L, "e");
        }

        public static String getSimplifiedNumber(long value) {
            //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
            if (value == Long.MIN_VALUE) return getSimplifiedNumber(Long.MIN_VALUE + 1);
            if (value < 0) return "-" + getSimplifiedNumber(-value);
            if (value < 1000) return Long.toString(value); //deal with easy case

            Map.Entry<Long, String> e = suffixes.floorEntry(value);
            Long divideBy = e.getKey();
            String suffix = e.getValue();

            long truncated = value / (divideBy / 10); //the number part of the output times 10
            boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
            return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
        }
    }


    private Game.State state = Game.State.PRE_GAME;
    private int timer = 0;
    private GameMap map = GameMap.RIFT;
    private final Map<Player, Team> players = new HashMap<>();
    private final Map<Player, Team> playersProtected = Collections.unmodifiableMap(players);
    private final Set<Player> cachedTeamRed = new HashSet<>();
    private final Set<Player> cachedTeamRedProtected = Collections.unmodifiableSet(cachedTeamRed);
    private final Set<Player> cachedTeamBlue = new HashSet<>();
    private final Set<Player> cachedTeamBlueProtected = Collections.unmodifiableSet(cachedTeamBlue);
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

    public Map<Player, Team> getPlayersProtected() {
        return playersProtected;
    }

    public Collection<Player> getTeamRedProtected() {
        return cachedTeamRedProtected;
    }

    public Collection<Player> getTeamBlueProtected() {
        return cachedTeamBlueProtected;
    }

    public Set<Player> getCachedTeamRed() {
        return cachedTeamRed;
    }

    public Set<Player> getCachedTeamBlue() {
        return cachedTeamBlue;
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

    public boolean isRedTeam(@Nonnull Player player) {
        return players.get(player) == Team.RED;
    }

    public boolean isBlueTeam(@Nonnull Player player) {
        return players.get(player) == Team.BLUE;
    }

    @Nullable
    public Team getPlayerTeamOrNull(@Nonnull Player player) {
        return this.players.get(player);
    }

    @Nonnull
    public Team getPlayerTeam(@Nonnull Player player) {
        Team team = getPlayerTeamOrNull(player);
        if (team == null) {
            throw new IllegalArgumentException("Player provided is not playing a game at the moment");
        }
        return team;
    }

    public boolean canChangeMap() {
        return players.isEmpty() && state == Game.State.PRE_GAME;
    }

    public void resetTimer() {
        this.timer = 0;
    }

    public int getMinute() {
        return this.timer / 20 / 60;
    }

    public int getScoreboardMinute() {
        return remaining / 60;
    }

    public int getSecond() {
        return this.timer / 20;
    }

    public int getScoreboardSecond() {
        return remaining % 60;
    }

    public void changeMap(@Nonnull GameMap map) {
        if (!canChangeMap()) {
            throw new IllegalStateException("Cannot change map!");
        }
        this.map = map;
    }

    public void addPlayer(@Nonnull Player player, @Nonnull Team team) {
        Validate.notNull(player, "player");
        Validate.notNull(team, "team");

        Team oldTeam = this.players.put(player, team);
        if (oldTeam != team) {
            if (oldTeam == Team.RED) {
                this.cachedTeamRed.remove(player);
            } else if (oldTeam == Team.BLUE) {
                this.cachedTeamBlue.remove(player);
            }
        }
        switch (team) {
            case BLUE:
                this.cachedTeamBlue.add(player);
                player.teleport(this.map.blueLobbySpawnPoint);
                break;
            case RED:
                this.cachedTeamRed.add(player);
                player.teleport(this.map.redLobbySpawnPoint);
                break;
        }
    }

    /**
     * Adds a player to the game
     *
     * @param player
     * @param teamBlue
     * @deprecated use {@link #addPlayer(Player, Team) addPlayer(Player, Team)} instead
     */
    @Deprecated
    public void addPlayer(Player player, boolean teamBlue) {
        if (teamBlue) {
            this.addPlayer(player, Team.BLUE);
        } else {
            this.addPlayer(player, Team.RED);
        }
        ArmorManager.resetArmor(player, Classes.getSelected(player));
    }

    public void removePlayer(Player player) {
        Team oldTeam = this.players.remove(player);
        if (oldTeam == Team.RED) {
            this.cachedTeamRed.remove(player);
        } else if (oldTeam == Team.BLUE) {
            this.cachedTeamBlue.remove(player);
        }
    }

    public List<Player> clearAllPlayers() {
        List<Player> toRemove = new ArrayList<>(this.players.keySet());
        for (Player p : toRemove) {
            this.removePlayer(p);
        }
        assert this.players.isEmpty();
        assert this.cachedTeamBlue.isEmpty();
        assert this.cachedTeamRed.isEmpty();
        return toRemove;
    }

    public boolean onSameTeam(Player player1, Player player2) {
        return players.get(player1) == players.get(player2);
    }

    public boolean onSameTeam(@Nonnull WarlordsPlayer player1, @Nonnull WarlordsPlayer player2) {
        return onSameTeam(player1.getPlayer(), player2.getPlayer());
    }

    @Nullable
    public FlagManager getFlags() {
        return flags;
    }

    public void setFlags(@Nullable FlagManager flags) {
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
        sideBar.getScore(ChatColor.GOLD + "Lv90 " + Classes.getClassesGroup(Classes.getSelected(player)).name).setScore(4);
        sideBar.getScore(ChatColor.WHITE + "Spec: " + ChatColor.GREEN + Classes.getSelected(player).name).setScore(3);
        sideBar.getScore("    ").setScore(2);
        sideBar.getScore(ChatColor.YELLOW + "WL 2.0 master_b-v0.0.4 ").setScore(1);

        player.setScoreboard(board);
    }

    public void updateClass(Player player) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        String dateString = format.format(new Date());
        Scoreboard scoreboard = player.getScoreboard();
        for (String entry : scoreboard.getEntries()) {
            String entryUnformatted = ChatColor.stripColor(entry);
            if (entryUnformatted.contains("Lv90")) {
                scoreboard.resetScores(entry);
                scoreboard.getObjective(dateString).getScore(ChatColor.GOLD + "Lv90 " + Classes.getClassesGroup(Classes.getSelected(player)).name).setScore(4);
            } else if (entryUnformatted.contains("Spec:")) {
                scoreboard.resetScores(entry);
                scoreboard.getObjective(dateString).getScore(ChatColor.WHITE + "Spec: " + ChatColor.GREEN + Classes.getSelected(player).name).setScore(3);
            }
        }
    }

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

    public void updateTimeLeft(Player player, int time) {
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

    @Override
    public void run() {
        Game.State newState = state.run(this);
        if (newState != null) {
            this.state = newState;
            newState.begin(this);
        }
    }
}