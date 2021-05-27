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
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Game implements Runnable {

    private static final int POINT_LIMIT = 1000;
    public static int remaining = 0;

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
                        } else if (time == 20) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in 20 seconds!", false);
                        } else if (time == 10) {
                            sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.GOLD + "10 " + ChatColor.YELLOW + "seconds!", false);
                        } else if (time <= 5 && time != 0) {
                            if (time == 1) {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " second", false);
                            } else {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The game starts in " + ChatColor.RED + time + ChatColor.YELLOW + " seconds!", false);
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
                List<String> blueTeam = new ArrayList<>();
                List<String> redTeam = new ArrayList<>();

                RemoveEntities removeEntities = new RemoveEntities();
                removeEntities.onRemove();

                for (Player p : game.teamRed) {

                    Classes selected = Classes.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selected.create.apply(p), false));

                    redTeam.add(p.getName());
//                    p.setPlayerListName(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Warlords.getPlayer(p).getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] "
//                            + ChatColor.RED + p.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.GOLD + "Lv90" + ChatColor.DARK_GRAY + "]");

                    resetArmor(p, Warlords.getPlayer(p).getSpec(), false);
                    System.out.println("Added " + p.getName());
                }

                for (Player p : game.teamBlue) {

                    Classes selected = Classes.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selected.create.apply(p), false));

                    blueTeam.add(p.getName());
//                    p.setPlayerListName(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + Warlords.getPlayer(p).getSpec().getClassNameShort() + ChatColor.DARK_GRAY + "] "
//                            + ChatColor.BLUE + p.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.GOLD + "Lv90" + ChatColor.DARK_GRAY + "]");

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
                    } else {
                        if (game.timer % 20 == 0) {
                            int time = game.timer / 20;
                            System.out.println(time);
                            if (time == 0) {
                                sendMessageToAllGamePlayer(game, ChatColor.YELLOW + "The gates will fall in " + ChatColor.RED + "10" + ChatColor.YELLOW + " seconds!", false);
                            } else if (time >= 5) {
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
                game.timer = 0;
                boolean teamBlueWins = !game.forceEnd && game.bluePoints > game.redPoints;
                boolean teamRedWins = !game.forceEnd && game.redPoints > game.bluePoints;

                if (game.powerUps != null) {
                    game.powerUps.cancel();
                }

                // Announce winner
                List<WarlordsPlayer> players = new ArrayList<>(Warlords.getPlayers().values());
                sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
                sendMessageToAllGamePlayer(game, "" + ChatColor.WHITE + ChatColor.BOLD + "  Warlords", true);
                sendMessageToAllGamePlayer(game, "", true);
                sendMessageToAllGamePlayer(game, "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "✚ MVP ✚", true);
                sendMessageToAllGamePlayer(game, "" + ChatColor.AQUA + "Joe", true);
                sendMessageToAllGamePlayer(game, "", true);
                sendMessageToAllGamePlayer(game, "" + ChatColor.RED + ChatColor.BOLD + "✚ TOP DAMAGE ✚", true);
                players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getDamage)).collect(Collectors.toList());
                String damageMessage = "";
                for (int i = 0; i < players.size() && i < 3; i++) {
                    damageMessage += ChatColor.AQUA + players.get(i).getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + players.get(i).getDamage() + "k " + ChatColor.GRAY + "- ";
                }
                sendMessageToAllGamePlayer(game, damageMessage, true);
                sendMessageToAllGamePlayer(game, "", true);
                sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "✚ TOP HEALING ✚", true);
                players = players.stream().sorted(Comparator.comparing(WarlordsPlayer::getHealing)).collect(Collectors.toList());
                String healingMessage = "";
                for (int i = 0; i < players.size() && i < 3; i++) {
                    healingMessage += ChatColor.AQUA + players.get(i).getName() + ChatColor.GRAY + ": " + ChatColor.GOLD + players.get(i).getHealing() + "k " + ChatColor.GRAY + "- ";
                }
                sendMessageToAllGamePlayer(game, healingMessage, true);
                sendMessageToAllGamePlayer(game, "", true);
                sendMessageToAllGamePlayer(game, "" + ChatColor.GOLD + ChatColor.BOLD + "✚ YOUR STATISTICS ✚", true);
                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    Utils.sendCenteredMessage(value.getPlayer(),
                            ChatColor.WHITE + "Kills: " + ChatColor.GOLD + value.getKills() + ChatColor.GRAY + " - " +
                                    ChatColor.WHITE + "Assists: " + ChatColor.GOLD + value.getAssists() + ChatColor.GRAY + " - " +
                                    ChatColor.WHITE + "Deaths: " + ChatColor.GOLD + value.getDeaths() + ChatColor.GRAY);

                    TextComponent damage = new TextComponent(ChatColor.WHITE + "Damage " + ChatColor.GOLD + addCommaAndRound(value.getDamage()) + ChatColor.GRAY + " - ");
                    TextComponent heal = new TextComponent(ChatColor.WHITE + "Healing " + ChatColor.GOLD + addCommaAndRound(value.getHealing()) + ChatColor.GRAY + " - ");
                    TextComponent absorb = new TextComponent(ChatColor.WHITE + "Absorbed " + ChatColor.GOLD + addCommaAndRound(value.getAbsorbed()) + ChatColor.GRAY);
                    damage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("312321\ndwa\ndawd").create()));
                    heal.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("80953").create()));
                    absorb.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("645645").create()));
                    Utils.sendCenteredHoverableMessage(value.getPlayer(), Arrays.asList(damage, heal, absorb));

                }

                sendMessageToAllGamePlayer(game, "" + ChatColor.GREEN + ChatColor.BOLD + "▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬", false);
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

        public String addCommaAndRound(float amount) {
            amount = Math.round(amount);
            DecimalFormat formatter = new DecimalFormat("#,###");
            return formatter.format(amount);
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
        sideBar.getScore(ChatColor.YELLOW + "WL 2.0 beta_b-v1.0 ").setScore(1);

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