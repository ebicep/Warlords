package com.ebicep.warlords.maps;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.WarlordsPlayer;
import com.ebicep.warlords.classes.PlayerClass;
import com.ebicep.warlords.classes.mage.AbstractMage;
import com.ebicep.warlords.classes.mage.specs.aquamancer.Aquamancer;
import com.ebicep.warlords.classes.mage.specs.cryomancer.Cryomancer;
import com.ebicep.warlords.classes.mage.specs.pyromancer.Pyromancer;
import com.ebicep.warlords.classes.paladin.AbstractPaladin;
import com.ebicep.warlords.classes.paladin.specs.avenger.Avenger;
import com.ebicep.warlords.classes.paladin.specs.crusader.Crusader;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.classes.shaman.specs.earthwarden.Earthwarden;
import com.ebicep.warlords.classes.shaman.specs.spiritguard.Spiritguard;
import com.ebicep.warlords.classes.shaman.specs.thunderlord.ThunderLord;
import com.ebicep.warlords.classes.warrior.AbstractWarrior;
import com.ebicep.warlords.classes.warrior.specs.berserker.Berserker;
import com.ebicep.warlords.classes.warrior.specs.defender.Defender;
import com.ebicep.warlords.classes.warrior.specs.revenant.Revenant;
import com.ebicep.warlords.powerups.PowerupManager;
import com.ebicep.warlords.util.Classes;
import com.ebicep.warlords.util.CustomScoreboard;
import com.ebicep.warlords.util.RemoveEntities;
import com.sun.jndi.ldap.Ber;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

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
                    game.timer++;
                    int total = game.map.getCountdownTimerInTicks();
                    int remaining = total - game.timer;
                    if (remaining % 20 == 1) {
                        Bukkit.broadcastMessage(ChatColor.GOLD + "DEBUG - Gamestate: PRE_GAME | Remaining time: " + remaining / 20);
                    }
                    if (game.timer == total) {
                        return GAME;
                    }
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

                for(Player p : game.teamRed) {

                    Classes selected = Classes.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selected.create.apply(p), false));

                    redTeam.add(p.getName());
                    p.setPlayerListName(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "SPEC" + ChatColor.DARK_GRAY + "] "
                            // do we even need levels anymore?
                            + ChatColor.RED + p.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.DARK_RED + "Lv999" + ChatColor.DARK_GRAY + "]");

                    resetArmor(p, Warlords.getPlayer(p).getSpec(), false);
                    System.out.println("Added " + p.getName());
                }

                for(Player p : game.teamBlue) {

                    Classes selected = Classes.getSelected(p);
                    Warlords.addPlayer(new WarlordsPlayer(p, p.getName(), p.getUniqueId(), selected.create.apply(p), false));

                    blueTeam.add(p.getName());
                    p.setPlayerListName(ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "SPEC" + ChatColor.DARK_GRAY + "] "
                            + ChatColor.BLUE + p.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.DARK_RED + "Lv999" + ChatColor.DARK_GRAY + "]");

                    resetArmor(p, Warlords.getPlayer(p).getSpec(), true);
                    System.out.println("Added " + p.getName());
                }

                for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                    value.getPlayer().setMaxHealth(40);
                    value.getPlayer().setLevel((int) value.getMaxEnergy());
                    value.getPlayer().getInventory().clear();
                    value.assignItemLore();
                    System.out.println("updated scoreboard for " + value.getName());
                    value.setScoreboard(new CustomScoreboard(value.getPlayer(), blueTeam, redTeam));
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
                game.timer++;
                if (
                        game.bluePoints >= POINT_LIMIT || game.redPoints >= POINT_LIMIT || game.timer >= game.map.getGameTimerInTicks() * 20 || game.forceEnd
                ) {
                    return END;
                }
                if (game.timer == 10 * 20) {
                    // Destroy gates
                    // Enable abilities
                } else if (game.timer == 30 * 20) {
                    // Enable powerups
                    new PowerupManager(game.map).runTaskTimer(Warlords.getInstance(), 0, 0);
                }

                if (game.timer % 20 == 0) {
                    remaining = (game.map.getGameTimerInTicks() - game.timer) / 20;
                    for (WarlordsPlayer value : Warlords.getPlayers().values()) {
                        value.getScoreboard().updateTime();
                    }
                }

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
                // Announce winner
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


    @Override
    public void run() {
        Game.State newState = state.run(this);
        if (newState != null) {
            this.state = newState;
            newState.begin(this);
        }
    }
}