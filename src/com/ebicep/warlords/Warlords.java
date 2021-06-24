package com.ebicep.warlords;

import com.ebicep.warlords.classes.abilties.OrbsOfLife;
import com.ebicep.warlords.classes.abilties.Soulbinding;
import com.ebicep.warlords.classes.abilties.UndyingArmy;
import com.ebicep.warlords.commands.Commands;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.menu.MenuEventListener;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.metadata.MetadataValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

public class Warlords extends JavaPlugin {

    private static Warlords instance;

    public static Warlords getInstance() {
        return instance;
    }

    public static List<OrbsOfLife.Orb> orbs = new ArrayList<>();

    public static List<OrbsOfLife.Orb> getOrbs() {
        return orbs;
    }

    private static final HashMap<UUID, WarlordsPlayer> players = new HashMap<>();

    public static void addPlayer(@Nonnull WarlordsPlayer warlordsPlayer) {
        players.put(warlordsPlayer.getUuid(), warlordsPlayer);
    }

    @Deprecated // This method is useless, but handles the parts of the code that are slow with updating
    @Nullable
    public static WarlordsPlayer getPlayer(@Nullable WarlordsPlayer player) {
        return player;
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nullable Entity entity) {
        if (entity != null) {
            Optional<MetadataValue> metadata = entity.getMetadata("WARLORDS_PLAYER").stream().findAny();
            if (metadata.isPresent()) {
                return (WarlordsPlayer)metadata.get().value();
            }
        }
        return null;
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nonnull OfflinePlayer player) {
        return getPlayer(player.getUniqueId());
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nonnull Player player) {
        return getPlayer((OfflinePlayer)player);
    }

    @Nullable
    public static WarlordsPlayer getPlayer(@Nonnull UUID player) {
        return players.get(player);
    }


    public static boolean hasPlayer(@Nonnull OfflinePlayer player) {
        return hasPlayer(player.getUniqueId());
    }

    public static boolean hasPlayer(@Nonnull UUID player) {
        return players.containsKey(player);
    }

    public static void removePlayer(@Nonnull UUID player) {
        WarlordsPlayer wp = players.remove(player);
        if (wp != null) {
            if (!(wp.getEntity() instanceof Player)) {
                wp.getEntity().remove();
            }
        }
        Location loc = spawnPoints.remove(player);
        Player p = Bukkit.getPlayer(player);
        if (p != null) {
            p.removeMetadata("WARLORDS_PLAYER", Warlords.getInstance());
            if (loc != null) {
                p.teleport(getRejoinPoint(player));
            }
        }
    }

    public static HashMap<UUID, WarlordsPlayer> getPlayers() {
        return players;
    }

    private final static HashMap<UUID, Location> spawnPoints = new HashMap<>();

    @Nonnull
    public static Location getRejoinPoint(@Nonnull UUID key) {
        return spawnPoints.getOrDefault(key, Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    public static void setRejoinPoint(@Nonnull UUID key, @Nonnull Location value) {
        spawnPoints.put(key, value);
        Player player = Bukkit.getPlayer(key);
        if (player != null) {
            player.teleport(value);
        }
    }

    private final static HashMap<UUID, PlayerSettings> playerSettings = new HashMap<>();

    @Nonnull
    public static PlayerSettings getPlayerSettings(@Nonnull UUID key) {
        PlayerSettings settings = playerSettings.computeIfAbsent(key, (k) -> new PlayerSettings());
        // TODO update last accessed field on settings
        return settings;
    }

    public static int blueKills;
    public static int redKills;

    private int counter = 0;

    //TODO remove static EVERYWHERE FUCK ME
    public static Game game;

    @Override
    public void onEnable() {
        ConfigurationSerialization.registerClass(PlayerSettings.class);
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        getServer().getPluginManager().registerEvents(new MenuEventListener(this), this);
        Commands commands = new Commands();
        getCommand("start").setExecutor(commands);
        getCommand("endgame").setExecutor(commands);
        getCommand("class").setExecutor(commands);
        getCommand("menu").setExecutor(commands);
        getCommand("shout").setExecutor(commands);
        getCommand("hotkeymode").setExecutor(commands);

        getCommand("start").setTabCompleter(commands);
        getCommand("class").setTabCompleter(commands);

        game = new Game();
        startTask();

        getServer().getScheduler().runTaskTimer(this, game, 1, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : getServer().getOnlinePlayers()) {
                    player.setFoodLevel(20);
                    player.setSaturation(1);
                }
            }

        }.runTaskTimer(this, 50, 50);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords]: Plugin is enabled");
    }


    @Override
    public void onDisable() {
        game.clearAllPlayers();
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords]: Plugin is disabled");
        // TODO persist this.playerSettings to a database
    }

    public void startTask() {
        new BukkitRunnable() {

            @Override
            public void run() {
                // EVERY TICK
                {
                    // MOVEMENT
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        warlordsPlayer.getSpeed().updateSpeed();

                        // light infusion
                        if (warlordsPlayer.getInfusion() != 0) {
                            warlordsPlayer.setInfusion((int) (warlordsPlayer.getInfusion() - 0.05));
                        }

                        // presence
                        if (warlordsPlayer.getPresence() != 0) {
                            if (warlordsPlayer.getInfusion() == 0) {
                                warlordsPlayer.setPresence((int) (warlordsPlayer.getPresence() - 0.05));
                            }
                        }

                        //freezingbreath
                        if (warlordsPlayer.getBreathSlowness() != 0) {
                            warlordsPlayer.setBreathSlowness((int) (warlordsPlayer.getBreathSlowness() - 0.05));
                        }

                        // frostbolt
                        if (warlordsPlayer.getFrostbolt() != 0) {
                            warlordsPlayer.setFrostbolt((int) (warlordsPlayer.getFrostbolt() - 0.05));
                        }

                        // berserk
                        if (warlordsPlayer.getBerserk() != 0) {
                            //berserk same speed as presence 30%
                            warlordsPlayer.setBerserk((int) (warlordsPlayer.getBerserk() - 0.05));
                        }

                        // spiritlink
                        if (warlordsPlayer.getSpiritLink() != 0) {
                            warlordsPlayer.setSpiritLink((int) (warlordsPlayer.getSpiritLink() - 0.05));
                        }

                        // ice barrier
                        if (warlordsPlayer.getIceBarrier() != 0) {
                            warlordsPlayer.setIceBarrier((int) (warlordsPlayer.getIceBarrier() - 0.05));
                        }

                        // ice barrier slowness duration
                        if (warlordsPlayer.getIceBarrierSlowness() != 0) {
                            warlordsPlayer.setIceBarrierSlowness((int) (warlordsPlayer.getIceBarrierSlowness() - 0.05));
                            warlordsPlayer.getSpeed().addSpeedModifier("Ice Barrier", -20, 2 * 20);
                        }
                    }

                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        Player player = warlordsPlayer.getEntity() instanceof Player ? (Player)warlordsPlayer.getEntity() : null;

                        if(player != null) {
                            Location location = player.getLocation();
                            player.setCompassTarget(warlordsPlayer
                                    .getGameState()
                                    .flags()
                                    .get(warlordsPlayer.isTeamFlagCompass() ? warlordsPlayer.getTeam() : warlordsPlayer.getTeam().enemy())
                                    .getFlag()
                                    .getLocation()
                            );
                            //dismount directly downwards
                            if (player.isSneaking() && player.getVehicle() != null) {
                                player.getVehicle().remove();
                            }

                        }


                        //ABILITY COOLDOWN
                        if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getRed().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updateRedItem(player);
                            }
                        }
                        if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getPurple().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updatePurpleItem(player);
                            }
                        }
                        if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getBlue().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updateBlueItem(player);
                            }
                        }
                        if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() != 0) {
                            warlordsPlayer.getSpec().getOrange().subtractCooldown(.05f);
                            if (player != null) {
                                warlordsPlayer.updateOrangeItem(player);
                            }
                        }
                        if (warlordsPlayer.getHorseCooldown() != 0 && !warlordsPlayer.getEntity().isInsideVehicle()) {
                            warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - 1);
                            if (player != null) {
                                warlordsPlayer.updateHorseItem(player);
                            }
                        }

                        //respawn
                        if (warlordsPlayer.getRespawnTimer() == 0) {
                            warlordsPlayer.setRespawnTimer(-1);
                            warlordsPlayer.setSpawnProtection(10);
                            warlordsPlayer.setSpawnDamage(5);
                            warlordsPlayer.setDead(false);
                            Location respawnPoint = game.getMap().getRespawn(warlordsPlayer.getTeam());
                            warlordsPlayer.teleport(respawnPoint);
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (player.getLocation().distanceSquared(game.getMap().getBlueRespawn()) > 5 * 5) {
                                        warlordsPlayer.setSpawnProtection(0);
                                    }
                                    if (warlordsPlayer.getSpawnProtection() == 0) {
                                        this.cancel();
                                    }
                                }
                            }.runTaskTimer(instance, 0, 5);
                            warlordsPlayer.respawn();

                        }
                        //damage or heal
                        float newHealth = (float) warlordsPlayer.getHealth() / warlordsPlayer.getMaxHealth() * 40;
                        if (warlordsPlayer.getUndyingArmyDuration() != 0 && newHealth <= 0) {
                            if (warlordsPlayer.getUndyingArmyBy() == warlordsPlayer) {
                                warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + "Your Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                            } else {
                                warlordsPlayer.sendMessage("§a\u00BB§7 " + ChatColor.LIGHT_PURPLE + warlordsPlayer.getUndyingArmyBy().getName() + "'s Undying Army revived you with temporary health. Fight until your death! Your health will decay by " + ChatColor.RED + "500 " + ChatColor.LIGHT_PURPLE + "every second.");
                            }
                            warlordsPlayer.respawn();
                            warlordsPlayer.setUndyingArmyDead(true);
                            warlordsPlayer.setUndyingArmyDuration(0);
                            if (player != null) {
                                player.getInventory().setItem(5, UndyingArmy.BONE);
                            }
                            newHealth = 40;
                        }
                        if (newHealth <= 0) {
                            if (warlordsPlayer.isUndyingArmyDead()) {
                                warlordsPlayer.setUndyingArmyDead(false);
                                if (player != null) {
                                    player.getInventory().remove(UndyingArmy.BONE);
                                }
                            }
                            warlordsPlayer.respawn();
                            if (player != null) {
                                player.setGameMode(GameMode.SPECTATOR);
                            }
                            //giving out assists
                            for (int i = 1; i < warlordsPlayer.getHitBy().size(); i++) {
                                WarlordsPlayer assisted = warlordsPlayer.getHitBy().get(i);
                                if (warlordsPlayer.getHitBy().get(0) == warlordsPlayer) {
                                    assisted.sendMessage(
                                        ChatColor.GRAY +
                                        "You assisted in killing " +
                                        warlordsPlayer.getColoredName()
                                    );
                                } else {
                                    assisted.sendMessage(
                                        ChatColor.GRAY +
                                        "You assisted " +
                                        ChatColor.BLUE +
                                        warlordsPlayer.getHitBy().get(0).getColoredName() +
                                        ChatColor.GRAY + " in killing " +
                                        ChatColor.RED + warlordsPlayer.getName()
                                    );
                                }
                                assisted.addAssist();
                                assisted.getScoreboard().updateKillsAssists();
                            }
                            //respawn timer
                            int respawn = warlordsPlayer.getGameState().getTimerInSeconds() % 12;
                            if (respawn <= 4) {
                                respawn += 12;
                            }
                            warlordsPlayer.setRespawnTimer(respawn);
                        } else {
                            if(player != null) {
                                player.setHealth(newHealth);
                            }
                        }

                        if (warlordsPlayer.getInterveneDuration() != 0 && (warlordsPlayer.getInterveneDamage() >= 3600 || warlordsPlayer.getIntervenedBy().isDead() || (warlordsPlayer.getIntervenedBy() != null && warlordsPlayer.getLocation().distanceSquared(warlordsPlayer.getIntervenedBy().getLocation()) > 15 * 15))) {
                            //TODO seperate and add why the vene broke in chat
                            warlordsPlayer.setInterveneDuration(0);
                            warlordsPlayer.sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s " + ChatColor.YELLOW + "Intervene " + ChatColor.GRAY + "has expired!");

                        }
                        //energy
                        if (player == null || player.getGameMode() == GameMode.CREATIVE) {
                            if (warlordsPlayer.getEnergy() < warlordsPlayer.getMaxEnergy()) {
                                float newEnergy = warlordsPlayer.getEnergy() + warlordsPlayer.getSpec().getEnergyPerSec() / 20f;
                                if (warlordsPlayer.getWrathDuration() != 0) {
                                    newEnergy += 1;
                                }
                                if (warlordsPlayer.getPresence() != 0) {
                                    newEnergy += .5;
                                }
                                if (warlordsPlayer.getPowerUpEnergy() != 0) {
                                    newEnergy += .35;
                                }
                                warlordsPlayer.setEnergy(newEnergy);
                            }
                            if (player != null) {
                                player.setLevel((int) warlordsPlayer.getEnergy());
                                player.setExp(warlordsPlayer.getEnergy() / warlordsPlayer.getMaxEnergy());
                            }
                        }
                        //melee cooldown
                        if (warlordsPlayer.getHitCooldown() != 0) {
                            warlordsPlayer.setHitCooldown(warlordsPlayer.getHitCooldown() - 1);
                        }
                        //orbs
                        Location playerPosition = warlordsPlayer.getLocation();
                        Iterator<OrbsOfLife.Orb> itr = orbs.iterator();
                        while(itr.hasNext()) {
                            OrbsOfLife.Orb orb = itr.next();
                            Location orbPosition = orb.getBukkitEntity().getLocation();
                            if (orb.getOwner().isTeammate(warlordsPlayer) && orbPosition.distanceSquared(playerPosition) < 1.75 * 1.75) {
                                orb.getArmorStand().remove();
                                orb.getBukkitEntity().remove();
                                itr.remove();
                                warlordsPlayer.addHealth(warlordsPlayer, "Orbs of Life", 502, 502, -1, 100);
                                Utils.filterOnlyTeammates(player, 3, 3, 3, player).forEach((nearPlayer) -> {
                                    nearPlayer.addHealth(warlordsPlayer, "Orbs of Life", 420, 420, -1, 100);
                                });
                            }
                            if (orb.getBukkitEntity().getTicksLived() > 160) {
                                orb.getArmorStand().remove();
                                orb.getBukkitEntity().remove();
                                itr.remove();
                            }
                        }
                    }
                }
                // PARTICLES - FOUR TICK MODULE
                if (counter % 4 == 0) {
                    for (WarlordsPlayer warlordsPlayer : players.values()) {

                        // Arcane Shield
                        if (warlordsPlayer.getArcaneShield() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.CLOUD.display(0.15F, 0.3F, 0.15F, 0.01F, 2, location, 500);
                            ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.3F, 0.3F, 0.0001F, 1, location, 500);
                            ParticleEffect.SPELL_WITCH.display(0.3F, 0.3F, 0.3F, 0.001F, 1, location, 500);
                        }

                        // Blood Lust
                        if (warlordsPlayer.getBloodLustDuration() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add((Math.random() - 0.5) * 1, 1.2, (Math.random() - 0.5) * 1);
                            ParticleEffect.REDSTONE.display(new ParticleEffect.OrdinaryColor(255, 0, 0), location, 500);
                        }
                        // Earthliving
                        if (warlordsPlayer.getEarthlivingDuration() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.VILLAGER_HAPPY.display(0.3F, 0.3F, 0.3F, 0.1F, 3, location, 500);
                        }

                        // Wrath
                        if (warlordsPlayer.getWrathDuration() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.SPELL.display(0.3F, 0.1F, 0.3F, 0.2F, 6, location, 500);
                        }

                        // Windfury
                        if (warlordsPlayer.getWindfuryDuration() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.CRIT.display(0.2F, 0F, 0.2F, 0.1F, 3, location, 500);
                        }

                        // Soulbinding Weapon
                        if (warlordsPlayer.getSoulBindCooldown() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.SPELL_WITCH.display(0.2F, 0F, 0.2F, 0.1F, 1, location, 500);
                        }
                    }
                }

                // PARTICLES - TWO TICK MODULE
                if (counter % 2 == 0) {
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        //UPDATES SCOREBOARD HEALTHS
                        Entity player = warlordsPlayer.getEntity();

                        // Inferno
                        if (warlordsPlayer.getInferno() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.DRIP_LAVA.display(0.5F, 0.3F, 0.5F, 0.4F, 1, location, 500);
                            ParticleEffect.FLAME.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                            ParticleEffect.CRIT.display(0.5F, 0.3F, 0.5F, 0.0001F, 1, location, 500);
                        }

                        // Ice Barrier
                        if (warlordsPlayer.getIceBarrier() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.CLOUD.display(0.2F, 0.2F, 0.2F, 0.001F, 1, location, 500);
                            ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.2F, 0.3F, 0.0001F, 1, location, 500);
                        }

                        // Berserk
                        if (warlordsPlayer.getBerserk() != 0) {
                            Location location = warlordsPlayer.getLocation();
                            location.add(0, 2.1, 0);
                            ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0.1F, 1, location, 500);
                        }

                        // Infusion
                        if (warlordsPlayer.getInfusion() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.SPELL.display(0.3F, 0.1F, 0.3F, 0.2F, 2, location, 500);
                        }

                        // Presence
                        if (warlordsPlayer.getPresence() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                            ParticleEffect.SPELL.display(0.3F, 0.3F, 0.3F, 0.5F, 2, location, 500);
                        }
                    }
                }

                //EVERY SECOND
                if (counter % 20 == 0) {
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        Player player = warlordsPlayer.getEntity() instanceof Player ? (Player)warlordsPlayer.getEntity() : null;
                        //ACTION BAR
                        if(player != null) {
                            if (player.getInventory().getHeldItemSlot() != 8) {
                                warlordsPlayer.displayActionBar(player);
                            } else {
                                warlordsPlayer.displayFlagActionBar(player);
                            }
                        }
                        //REGEN
                        if (warlordsPlayer.getRegenTimer() != -1) {
                            warlordsPlayer.setRegenTimer(warlordsPlayer.getRegenTimer() - 1);
                            if (warlordsPlayer.getRegenTimer() == 0) {
                                warlordsPlayer.getHitBy().clear();
                            }
                        } else {
                            int healthToAdd = (int) (warlordsPlayer.getMaxHealth() / 55.3);
                            if (warlordsPlayer.getHealth() + healthToAdd >= warlordsPlayer.getMaxHealth()) {
                                warlordsPlayer.setHealth(warlordsPlayer.getMaxHealth());
                            } else {
                                warlordsPlayer.setHealth(warlordsPlayer.getHealth() + (int) (warlordsPlayer.getMaxHealth() / 55.3));
                            }
                        }

                        //RESPAWN
                        int respawn = warlordsPlayer.getRespawnTimer();
                        if (respawn != -1) {
                            if (respawn <= 6) {
                                if (respawn == 1) {
                                    PacketUtils.sendTitle(player, "", "", 0, 0, 0);
                                } else {
                                    PacketUtils.sendTitle(player, "", warlordsPlayer.getTeam().teamColor() + "Respawning in... " + ChatColor.YELLOW + (respawn - 1), 0, 40, 0);
                                }
                            }
                            warlordsPlayer.setRespawnTimer(respawn - 1);
                        }
                        //COOLDOWNS
                        if (warlordsPlayer.getSpawnProtection() != 0) {
                            warlordsPlayer.setSpawnProtection(warlordsPlayer.getSpawnProtection() - 1);
                        }
                        if (warlordsPlayer.getSpawnDamage() != 0) {
                            warlordsPlayer.setSpawnDamage(warlordsPlayer.getSpawnDamage() - 1);
                        }
                        if (warlordsPlayer.getWrathDuration() != 0) {
                            warlordsPlayer.setWrathDuration(warlordsPlayer.getWrathDuration() - 1);
                        }
                        if (warlordsPlayer.getFlagCooldown() != 0) {
                            warlordsPlayer.setFlagCooldown(warlordsPlayer.getFlagCooldown() - 1);
                        }
                        if (warlordsPlayer.getBloodLustDuration() != 0) {
                            warlordsPlayer.setBloodLustDuration(warlordsPlayer.getBloodLustDuration() - 1);
                        }
                        if (warlordsPlayer.getInterveneDuration() != 0) {
                            if (warlordsPlayer.getInterveneDuration() != 1) {
                                if (warlordsPlayer.getInterveneDuration() == 2)
                                    warlordsPlayer.sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getInterveneDuration() - 1) + "§7 second!");
                                else
                                    warlordsPlayer.sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getInterveneDuration() - 1) + "§7 seconds!");
                            }
                            warlordsPlayer.setInterveneDuration(warlordsPlayer.getInterveneDuration() - 1);
                            if (warlordsPlayer.getInterveneDuration() == 0) {
                                warlordsPlayer.sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7has expired!");
                            }
                        }
                        if (warlordsPlayer.getLastStandDuration() != 0) {
                            warlordsPlayer.setLastStandDuration(warlordsPlayer.getLastStandDuration() - 1);
                        }
                        if (warlordsPlayer.getOrbsOfLifeDuration() != 0) {
                            warlordsPlayer.setOrbsOfLifeDuration(warlordsPlayer.getOrbsOfLifeDuration() - 1);
                        }
                        if (warlordsPlayer.getUndyingArmyDuration() != 0 && !warlordsPlayer.isUndyingArmyDead()) {
                            warlordsPlayer.setUndyingArmyDuration(warlordsPlayer.getUndyingArmyDuration() - 1);
                            if (warlordsPlayer.getUndyingArmyDuration() == 0) {
                                int healing = (int) ((warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth()) * .35 + 200);
                                warlordsPlayer.addHealth(warlordsPlayer.getUndyingArmyBy(), "Undying Army", healing, healing, -1, 100);
                            }
                        } else if (warlordsPlayer.isUndyingArmyDead()) {
                            warlordsPlayer.addHealth(warlordsPlayer, "", -500, -500, -1, 100);
                        }
                        if (warlordsPlayer.getWindfuryDuration() != 0) {
                            warlordsPlayer.setWindfuryDuration(warlordsPlayer.getWindfuryDuration() - 1);
                        }
                        if (warlordsPlayer.getEarthlivingDuration() != 0) {
                            warlordsPlayer.setEarthlivingDuration(warlordsPlayer.getEarthlivingDuration() - 1);
                        }

                        if (warlordsPlayer.getBerserkerWounded() != 0) {
                            warlordsPlayer.setBerserkerWounded(warlordsPlayer.getBerserkerWounded() - 1);
                        }
                        if (warlordsPlayer.getDefenderWounded() != 0) {
                            warlordsPlayer.setDefenderWounded(warlordsPlayer.getDefenderWounded() - 1);
                        }
                        if (warlordsPlayer.getCrippled() != 0) {
                            warlordsPlayer.setCrippled(warlordsPlayer.getCrippled() - 1);
                        }
                        if (warlordsPlayer.getRepentanceDuration() != 0) {
                            warlordsPlayer.setRepentanceDuration(warlordsPlayer.getRepentanceDuration() - 1);
                        }
                        if (warlordsPlayer.getRepentanceCounter() != 0) {
                            int newRepentanceCounter = (int) (warlordsPlayer.getRepentanceCounter() * .8 - 60);
                            warlordsPlayer.setRepentanceCounter(Math.max(newRepentanceCounter, 0));
                        }
                        if (warlordsPlayer.getArcaneShield() != 0) {
                            warlordsPlayer.setArcaneShield(warlordsPlayer.getArcaneShield() - 1);
                        }
                        if (warlordsPlayer.getInferno() != 0) {
                            warlordsPlayer.setInferno(warlordsPlayer.getInferno() - 1);
                        }
                        if (warlordsPlayer.getChainLightningCooldown() != 0) {
                            warlordsPlayer.setChainLightningCooldown(warlordsPlayer.getChainLightningCooldown() - 1);
                        }
                        if (warlordsPlayer.getSoulBindCooldown() != 0) {
                            warlordsPlayer.setSoulBindCooldown(warlordsPlayer.getSoulBindCooldown() - 1);
                        }
                        for (int i = 0; i < warlordsPlayer.getSoulBindedPlayers().size(); i++) {
                            Soulbinding.SoulBoundPlayer soulBoundPlayer = warlordsPlayer.getSoulBindedPlayers().get(0);
                            soulBoundPlayer.setTimeLeft(soulBoundPlayer.getTimeLeft() - 1);
                            if (soulBoundPlayer.getTimeLeft() == 0) {
                                warlordsPlayer.getSoulBindedPlayers().remove(i);
                                i--;
                            }
                        }
                        if (warlordsPlayer.getPowerUpDamage() != 0) {
                            warlordsPlayer.setPowerUpDamage(warlordsPlayer.getPowerUpDamage() - 1);
                        }
                        if (warlordsPlayer.getPowerUpEnergy() != 0) {
                            warlordsPlayer.setPowerUpEnergy(warlordsPlayer.getPowerUpEnergy() - 1);
                        }
                        if (warlordsPlayer.isPowerUpHeal()) {
                            int heal = (int) (warlordsPlayer.getMaxHealth() * .1);
                            if (warlordsPlayer.getHealth() + heal > warlordsPlayer.getMaxHealth()) {
                                heal = warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth();
                            }
                            warlordsPlayer.setHealth(warlordsPlayer.getHealth() + heal);
                            warlordsPlayer.sendMessage("§a\u00BB §7Healed §a" + heal + " §7health.");

                            if (warlordsPlayer.getHealth() == warlordsPlayer.getMaxHealth()) {
                                warlordsPlayer.setPowerUpHeal(false);
                            }
                        }
                        if (warlordsPlayer.getPowerUpSpeed() != 0) {
                            warlordsPlayer.setPowerUpSpeed(warlordsPlayer.getPowerUpSpeed() - 1);
                        }

                    }
                }
                counter++;
            }

        }.runTaskTimer(this, 0, 0);
    }
}