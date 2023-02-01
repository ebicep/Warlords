package com.ebicep.warlords.game.option.onslaught;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.events.game.pve.WarlordsMobSpawnEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDeathEvent;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.WeaponOption;
import com.ebicep.warlords.game.option.cuboid.BoundingBoxOption;
import com.ebicep.warlords.game.option.marker.SpawnLocationMarker;
import com.ebicep.warlords.game.option.marker.scoreboard.ScoreboardHandler;
import com.ebicep.warlords.game.option.marker.scoreboard.SimpleScoreboardHandler;
import com.ebicep.warlords.game.option.wavedefense.commands.MobCommand;
import com.ebicep.warlords.game.state.EndState;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.zombie.BasicZombie;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import com.ebicep.warlords.pve.weapons.weapontypes.legendaries.AbstractLegendaryWeapon;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OnslaughtOption implements Option {

    private Game game;
    private SimpleScoreboardHandler scoreboard;
    private final Team team;
    private final AtomicInteger ticksElapsed = new AtomicInteger(0);
    private final ConcurrentHashMap<AbstractMob<?>, Integer> mobs = new ConcurrentHashMap<>();
    private int spawnCount = 0;
    private Location lastLocation;
    private final AtomicInteger integrityCounter = new AtomicInteger(100);

    public OnslaughtOption(Team team) {
        this.team = team;
    }

    @Override
    public void register(@Nonnull Game game) {
        this.game = game;
        for (Option o : game.getOptions()) {
            if (o instanceof BoundingBoxOption) {
                BoundingBoxOption boundingBoxOption = (BoundingBoxOption) o;
                lastLocation = boundingBoxOption.getCenter();
            }
        }

        game.registerEvents(new Listener() {

            @EventHandler
            public void onEvent(WarlordsDamageHealingEvent event) {
                WarlordsEntity attacker = event.getAttacker();
                WarlordsEntity receiver = event.getPlayer();

                if (event.isDamageInstance()) {
                    if (attacker instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) attacker).getMob();
                        if (mobs.containsKey(mob)) {
                            mob.onAttack(attacker, receiver, event);
                        }
                    }

                    if (receiver instanceof WarlordsNPC) {
                        AbstractMob<?> mob = ((WarlordsNPC) receiver).getMob();
                        if (mobs.containsKey(mob)) {
                            mob.onDamageTaken(receiver, attacker, event);
                        }
                    }
                }
            }

            @EventHandler
            public void onEvent(WarlordsDeathEvent event) {
                WarlordsEntity we = event.getPlayer();
                WarlordsEntity killer = event.getKiller();

                if (we instanceof WarlordsNPC) {
                    AbstractMob<?> mobToRemove = ((WarlordsNPC) we).getMob();
                    if (mobs.containsKey(mobToRemove)) {
                        //mobToRemove.onDeath(killer, we.getDeathLocation(), OnslaughtOption.this);
                        new GameRunnable(game) {
                            @Override
                            public void run() {
                                integrityCounter.getAndAdd(1);
                                if (integrityCounter.get() >= 100) {
                                    integrityCounter.set(100);
                                }
                                spawnCount--;
                                mobs.remove(mobToRemove);
                                game.getPlayers().remove(we.getUuid());
                                Warlords.removePlayer(we.getUuid());
                                //game.removePlayer(we.getUuid());
                            }
                        }.runTaskLater(1);

                        if (killer instanceof WarlordsPlayer) {
                            killer.getMinuteStats().addMobKill(mobToRemove.getName());
                            we.getHitBy().forEach((assisted, value) -> assisted.getMinuteStats().addMobAssist(mobToRemove.getName()));
                        }
                    }
                    MobCommand.SPAWNED_MOBS.remove(mobToRemove);
                } else if (we instanceof WarlordsPlayer && killer instanceof WarlordsNPC) {
                    if (mobs.containsKey(((WarlordsNPC) killer).getMob())) {
                        we.getMinuteStats().addMobDeath(((WarlordsNPC) killer).getMob().getName());
                    }
                }
            }
        });

        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(5, "percentage") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {

                return Collections.singletonList(difficultyScoreboard());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(5, "percentage") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {

                return Collections.singletonList(integrityScoreboard());
            }
        });
        game.registerGameMarker(ScoreboardHandler.class, scoreboard = new SimpleScoreboardHandler(6, "kills") {
            @Nonnull
            @Override
            public List<String> computeLines(@Nullable WarlordsPlayer player) {
                return healthScoreboard(game);
            }
        });
    }

    @Override
    public void onWarlordsEntityCreated(@Nonnull WarlordsEntity player) {
        if (player instanceof WarlordsPlayer) {
            player.setInPve(true);
            if (player.getEntity() instanceof Player) {
                game.setPlayerTeam((OfflinePlayer) player.getEntity(), Team.BLUE);
                player.setTeam(Team.BLUE);
                player.updateArmor();
            }

            DatabaseManager.getPlayer(player.getUuid(), databasePlayer -> {
                Optional<AbstractWeapon> optionalWeapon = databasePlayer
                        .getPveStats()
                        .getWeaponInventory()
                        .stream()
                        .filter(AbstractWeapon::isBound)
                        .filter(abstractWeapon -> abstractWeapon.getSpecializations() == player.getSpecClass())
                        .findFirst();
                optionalWeapon.ifPresent(abstractWeapon -> {
                    WarlordsPlayer warlordsPlayer = (WarlordsPlayer) player;

                    ((WarlordsPlayer) player).getCosmeticSettings().setWeaponSkin(abstractWeapon.getSelectedWeaponSkin());
                    warlordsPlayer.setWeapon(abstractWeapon);
                    abstractWeapon.applyToWarlordsPlayer(warlordsPlayer);
                    player.updateEntity();
                    player.getSpec().updateCustomStats();
                });
            });
        }
    }

    @Override
    public void start(@Nonnull Game game) {
        new GameRunnable(game) {
            int counter = 0;
            @Override
            public void run() {
                ticksElapsed.getAndIncrement();
                counter++;
                if (counter % 20 == 0) {
                    integrityCounter.getAndDecrement();
                }

                if (integrityCounter.get() <= 0) {
                    getGame().setNextState(new EndState(game, null));
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);

        new GameRunnable(game) {
            WarlordsEntity lastSpawn = null;

            @Override
            public void run() {
                if (game.getState() instanceof EndState) {
                    this.cancel();
                    return;
                }

                if (spawnCount >= 20) {
                    return;
                }

                if (lastSpawn == null) {
                    lastSpawn = spawn(getSpawnLocation(null));
                } else {
                    lastSpawn = spawn(getSpawnLocation(lastSpawn));
                }
                lastSpawn.getLocation(lastLocation);

                spawnCount++;
            }

            public WarlordsEntity spawn(Location loc) {
                AbstractMob<?> abstractMob = new BasicZombie(loc);
                mobs.put(abstractMob, ticksElapsed.get());
                WarlordsNPC warlordsNPC = abstractMob.toNPC(game, team, UUID.randomUUID());
                Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, abstractMob));
                return warlordsNPC;
            }

            private Location getSpawnLocation(WarlordsEntity entity) {
                List<Location> candidates = new ArrayList<>();
                double priority = Double.NEGATIVE_INFINITY;
                for (SpawnLocationMarker marker : getGame().getMarkers(SpawnLocationMarker.class)) {
                    if (entity == null) {
                        return marker.getLocation();
                    }
                    if (candidates.isEmpty()) {
                        candidates.add(marker.getLocation());
                        priority = marker.getPriority(entity);
                    } else {
                        double newPriority = marker.getPriority(entity);
                        if (newPriority >= priority) {
                            if (newPriority > priority) {
                                candidates.clear();
                                priority = newPriority;
                            }
                            candidates.add(marker.getLocation());
                        }
                    }
                }
                if (!candidates.isEmpty()) {
                    return candidates.get((int) (Math.random() * candidates.size()));
                }
                return lastLocation;
            }

        }.runTaskTimer(200, 5);
    }

    @Override
    public void updateInventory(@Nonnull WarlordsPlayer warlordsPlayer, Player player) {
        AbstractWeapon weapon = warlordsPlayer.getWeapon();
        if (weapon == null) {
            WeaponOption.showWeaponStats(warlordsPlayer, player);
        } else {
            WeaponOption.showPvEWeapon(warlordsPlayer, player);
        }

        player.getInventory().setItem(7, new ItemBuilder(Material.GOLD_NUGGET).name(ChatColor.GREEN + "Upgrade Talisman").get());
        if (warlordsPlayer.getWeapon() instanceof AbstractLegendaryWeapon) {
            ((AbstractLegendaryWeapon) warlordsPlayer.getWeapon()).updateAbilityItem(warlordsPlayer, player);
        }
    }

    private List<String> healthScoreboard(Game game) {
        List<String> list = new ArrayList<>();
        for (WarlordsEntity we : PlayerFilter.playingGame(game).filter(e -> e instanceof WarlordsPlayer)) {
            float healthRatio = we.getHealth() / we.getMaxHealth();
            ChatColor healthColor;
            String name = we.getName();
            String newName;

            if (healthRatio >= .5) {
                healthColor = ChatColor.GREEN;
            } else if (healthRatio >= .25) {
                healthColor = ChatColor.YELLOW;
            } else {
                healthColor = ChatColor.RED;
            }

            if (name.length() >= 8) {
                newName = name.substring(0, 8);
            } else {
                newName = name;
            }

            list.add(newName + ": " + (we.isDead() ? ChatColor.DARK_RED + "DEAD" : healthColor + "❤ " + (int) we.getHealth()) + ChatColor.RESET + " / " + ChatColor.RED + "⚔ " + we.getMinuteStats()
                    .total()
                    .getKills());
        }

        return list;
    }

    private String integrityScoreboard() {
        ChatColor color;
        if (integrityCounter.get() >= 50) {
            color = ChatColor.GREEN;
        } else if (integrityCounter.get() >= 25) {
            color = ChatColor.GOLD;
        } else {
            color = ChatColor.RED;
        }

        return "Integrity: " + color + (integrityCounter.get() + "%");
    }

    private String difficultyScoreboard() {
        String difficulty = ChatColor.GREEN + "EASY";
        if (ticksElapsed.get() >= 60 * 2) {
            difficulty = ChatColor.YELLOW + "MEDIUM";
        }
        if (ticksElapsed.get() >= 60 * 3) {
            difficulty = ChatColor.GOLD + "HARD";
        }
        if (ticksElapsed.get() >= 60 * 4) {
            difficulty = ChatColor.RED + "INSANE";
        }
        if (ticksElapsed.get() >= 60 * 5) {
            difficulty = ChatColor.DARK_RED + "EXTREME";
        }
        if (ticksElapsed.get() >= 60 * 6) {
            difficulty = ChatColor.LIGHT_PURPLE + "NIGHTMARE";
        }
        if (ticksElapsed.get() >= 60 * 7) {
            difficulty = ChatColor.DARK_PURPLE + "INSOMNIA";
        }
        if (ticksElapsed.get() >= 60 * 8) {
            difficulty = ChatColor.DARK_GRAY + "VANGUARD";
        }
        if (ticksElapsed.get() >= 60 * 10) {
            difficulty = ChatColor.BLACK.toString() + ChatColor.MAGIC + "???????";
        }

        return "Difficulty: " + difficulty;
    }

    public void spawnNewMob(AbstractMob<?> abstractMob) {
        abstractMob.toNPC(game, Team.RED, UUID.randomUUID());
        game.addNPC(abstractMob.getWarlordsNPC());
        mobs.put(abstractMob, ticksElapsed.get());
        Bukkit.getPluginManager().callEvent(new WarlordsMobSpawnEvent(game, abstractMob));
    }
}
