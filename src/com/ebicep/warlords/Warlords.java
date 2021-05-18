package com.ebicep.warlords;

import com.ebicep.customentities.CustomFallingBlock;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.ActionBarStats;
import com.ebicep.warlords.classes.abilties.Projectile;
import com.ebicep.warlords.classes.abilties.*;
import com.ebicep.warlords.commands.Commands;
import com.ebicep.warlords.events.WarlordsEvents;
import com.ebicep.warlords.maps.FlagManager;
import com.ebicep.warlords.maps.Game;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Warlords extends JavaPlugin {

    private static Warlords instance;

    public static Warlords getInstance() {
        return instance;
    }

    public static List<OrbsOfLife.Orb> orbs = new ArrayList<>();

    public static List<OrbsOfLife.Orb> getOrbs() {
        return orbs;
    }

    private static HashMap<Player, WarlordsPlayer> players = new HashMap<>();

    public static void addPlayer(WarlordsPlayer warlordsPlayer) {
        players.put(warlordsPlayer.getPlayer(), warlordsPlayer);
    }

    public static WarlordsPlayer getPlayer(Player player) {
        return players.get(player);
    }

    public static boolean hasPlayer(Player player) {
        return players.containsKey(player);
    }

    public static HashMap<Player, WarlordsPlayer> getPlayers() {
        return players;
    }

    public static int blueKills;
    public static int redKills;

    private int counter = 0;

    //TODO remove static EVERYWHERE FUCK ME
    public static Game game;

    @Override
    public void onEnable() {
        instance = this;
        getServer().getPluginManager().registerEvents(new WarlordsEvents(), this);
        Commands commands = new Commands();
        getCommand("start").setExecutor(commands);
        getCommand("endgame").setExecutor(commands);
        getCommand("class").setExecutor(commands);

        getCommand("start").setTabCompleter(commands);
        getCommand("class").setTabCompleter(commands);

        game = new Game();
        startTask();

        getServer().getScheduler().runTaskTimer(this, game, 1, 1);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[Warlords]: Plugin is enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "[Warlords]: Plugin is disabled");
    }

    public void startTask() {
        new BukkitRunnable() {

            @Override
            public void run() {

                // EVERY TICK
                // speed every 1 tick updated so it activates instantly
                // MOVEMENT
                for (WarlordsPlayer warlordsPlayer : players.values()) {
                    Player player = warlordsPlayer.getPlayer();
                    warlordsPlayer.getSpeed().updateSpeed();

                    // light infusion
                    if (warlordsPlayer.getInfusion() != 0) {
                        warlordsPlayer.setInfusion((int) (warlordsPlayer.getInfusion() - 0.05));
                    }

                    // presence
                    if (warlordsPlayer.getPresence() != 0) {
                        if (warlordsPlayer.getInfusion() == 0)
                        warlordsPlayer.setPresence((int) (warlordsPlayer.getPresence() - 0.05));
                        List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
                        near = Utils.filterOnlyTeammates(near, player);
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                Player nearPlayer = (Player) entity;
                            }
                        }
                    }

                    //freezingbreath
                    if (warlordsPlayer.getBreathSlowness() != 0) {
                        warlordsPlayer.setBreathSlowness((int) (warlordsPlayer.getBreathSlowness() - 0.05));
                        List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
                        near = Utils.filterOutTeammates(near, player);
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                Player nearPlayer = (Player) entity;
                            }
                        }
                    }

                    // frostbolt
                    if (warlordsPlayer.getFrostbolt() != 0) {
                        warlordsPlayer.setFrostbolt((int) (warlordsPlayer.getFrostbolt() - 0.05));
                        List<Entity> near = player.getNearbyEntities(6.0D, 2.0D, 6.0D);
                        near = Utils.filterOutTeammates(near, player);
                        // TODO: fix shooting all players + shooter instead of just impact location bolt
                        for (Entity entity : near) {
                            if (entity instanceof Player) {
                                Player nearPlayer = (Player) entity;
                            }
                        }
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
                        warlordsPlayer.getSpeed().changeCurrentSpeed("Infusion", -20, 2 * 20);
                    }
                }

                for (WarlordsPlayer warlordsPlayer : players.values()) {
                    Player player = warlordsPlayer.getPlayer();
                    Location location = player.getLocation();

                    if (game.isRedTeam(player)) {
                        if (warlordsPlayer.isTeamFlagCompass()) {
                            player.setCompassTarget(game.getFlags().getRed().getFlag().getLocation());
                        } else {
                            player.setCompassTarget(game.getFlags().getBlue().getFlag().getLocation());
                        }
                    } else if (game.isBlueTeam(player)) {
                        if (warlordsPlayer.isTeamFlagCompass()) {
                            player.setCompassTarget(game.getFlags().getBlue().getFlag().getLocation());
                        } else {
                            player.setCompassTarget(game.getFlags().getRed().getFlag().getLocation());
                        }
                    }

                    //to make it look like the cooldown activates super fast but isnt really fast since it updates next second tick
                    if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() == warlordsPlayer.getSpec().getRed().getCooldown()) {
                        warlordsPlayer.getSpec().getRed().setCurrentCooldown(warlordsPlayer.getSpec().getRed().getCurrentCooldown() - 1);
                        warlordsPlayer.updateRedItem();
                    }
                    if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() == warlordsPlayer.getSpec().getPurple().getCooldown()) {
                        warlordsPlayer.getSpec().getPurple().setCurrentCooldown(warlordsPlayer.getSpec().getPurple().getCurrentCooldown() - 1);
                        warlordsPlayer.updatePurpleItem();
                    }
                    if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() == warlordsPlayer.getSpec().getBlue().getCooldown()) {
                        warlordsPlayer.getSpec().getBlue().setCurrentCooldown(warlordsPlayer.getSpec().getBlue().getCurrentCooldown() - 1);
                        warlordsPlayer.updateBlueItem();
                    }
                    if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() == warlordsPlayer.getSpec().getOrange().getCooldown()) {
                        warlordsPlayer.getSpec().getOrange().setCurrentCooldown(warlordsPlayer.getSpec().getOrange().getCurrentCooldown() - 1);
                        warlordsPlayer.updateOrangeItem();
                    }
                    if (warlordsPlayer.getHorseCooldown() == 15 && !player.isInsideVehicle()) {
                        warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - 1);
                        warlordsPlayer.updateHorseItem();
                    }

                    //respawn
                    if (warlordsPlayer.getRespawnTimer() == 0) {
                        warlordsPlayer.setRespawnTimer(-1);
                        if (game.getTeamBlue().contains(warlordsPlayer.getPlayer())) {
                            warlordsPlayer.getPlayer().teleport(game.getMap().getBlueRespawn());
                        } else if (game.getTeamRed().contains(warlordsPlayer.getPlayer())) {
                            warlordsPlayer.getPlayer().teleport(game.getMap().getRedRespawn());
                        }
                        warlordsPlayer.respawn();
                        player.setGameMode(GameMode.SURVIVAL);
                    }
                    //damage or heal
                    float newHealth = (float) warlordsPlayer.getHealth() / warlordsPlayer.getMaxHealth() * 40;
                    if (warlordsPlayer.getUndyingArmy() != 0 && newHealth <= 0) {
                        warlordsPlayer.getPlayer().sendMessage("YOU DIED TO UNDYING ARMY LOL");
                        warlordsPlayer.respawn();
                        warlordsPlayer.setUndyingArmyDead(true);
                        warlordsPlayer.setUndyingArmy(0);
                        newHealth = 40;
                    }
                    if (newHealth <= 0 && !warlordsPlayer.isUndyingArmyDead()) {
                        warlordsPlayer.getPlayer().sendMessage("DEAD>??>???");
                        warlordsPlayer.respawn();
                        player.setGameMode(GameMode.SPECTATOR);
                        //giving out assists
                        for (WarlordsPlayer assisted : warlordsPlayer.getHitBy()) {
                            assisted.addAssist();
                            assisted.getScoreboard().updateKillsAssists();
                        }
                        //respawn timer
                        Bukkit.broadcastMessage("" + game.getTimer() % 60 % 12);
                        int respawn = game.getTimer() % 60 % 12;
                        if (game.getTimer() % 60 % 12 <= 4) {
                            respawn += 12;
                        }
                        warlordsPlayer.setRespawnTimer(respawn);
                    } else {
                        player.setHealth(newHealth);
                    }

                    if (warlordsPlayer.getIntervene() != 0 && warlordsPlayer.getInterveneDamage() >= 3600 || (warlordsPlayer.getIntervenedBy() != null && warlordsPlayer.getPlayer().getLocation().distanceSquared(warlordsPlayer.getIntervenedBy().getPlayer().getLocation()) > 15 * 15)) {
                        //TODO seperate and add why the vene broke in chat
                        warlordsPlayer.setIntervene(0);
                        warlordsPlayer.getPlayer().sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7has expired!");

                    }
                    //energy
                    if (warlordsPlayer.getEnergy() < warlordsPlayer.getMaxEnergy()) {
                        float newEnergy = warlordsPlayer.getEnergy() + 1;
                        if (warlordsPlayer.getPresence() != 0) {
                            newEnergy += .5;
                        }
                        if (warlordsPlayer.getPowerUpEnergy() != 0) {
                            newEnergy += .35;
                        }
                        warlordsPlayer.setEnergy(newEnergy);
                    }
                    player.setLevel((int) warlordsPlayer.getEnergy());
                    player.setExp(warlordsPlayer.getEnergy() / warlordsPlayer.getMaxEnergy());
                    //melee cooldown
                    if (warlordsPlayer.getHitCooldown() != 0) {
                        warlordsPlayer.setHitCooldown(warlordsPlayer.getHitCooldown() - 1);
                    }

                    if (warlordsPlayer.getCharged() != 0) {
                        List<Entity> playersInside = player.getNearbyEntities(2, 2, 2);
                        playersInside.removeAll(((RecklessCharge) warlordsPlayer.getSpec().getRed()).getPlayersHit());
                        playersInside = Utils.filterOutTeammates(playersInside, player);
                        for (Entity entity : playersInside) {
                            if (entity instanceof Player && ((Player) entity).getGameMode() != GameMode.SPECTATOR) {
                                //TODO add 100 slowness
                                ((RecklessCharge) warlordsPlayer.getSpec().getRed()).getPlayersHit().add((Player) entity);
                                Warlords.getPlayer((Player) entity).addHealth(warlordsPlayer, warlordsPlayer.getSpec().getRed().getName(), warlordsPlayer.getSpec().getRed().getMinDamageHeal(), warlordsPlayer.getSpec().getRed().getMaxDamageHeal(), warlordsPlayer.getSpec().getRed().getCritChance(), warlordsPlayer.getSpec().getRed().getCritMultiplier());
                            }
                        }
                        //cancel charge if hit a block, making the player stand still
                        if (player.getLocation().distanceSquared(warlordsPlayer.getChargeLocation()) > warlordsPlayer.getCharged() || (player.getVelocity().getX() == 0 && player.getVelocity().getZ() == 0)) {
                            player.setVelocity(new Vector(0, 0, 0));
                            warlordsPlayer.setCharged(0);
                        }
                    }
                    //orbs
                    for (int i = 0; i < orbs.size(); i++) {
                        OrbsOfLife.Orb orb = orbs.get(i);
                        Location orbPosition = orb.getBukkitEntity().getLocation();
                        if (game.onSameTeam(orb.getOwner(), warlordsPlayer) && orbPosition.distanceSquared(location) < 1.75 * 1.75) {
                            orb.getArmorStand().remove();
                            orb.getBukkitEntity().remove();
                            orbs.remove(i);
                            i--;
                            warlordsPlayer.addHealth(warlordsPlayer, "Orbs of Life", 502, 502, -1, 100);
                            List<Entity> near = player.getNearbyEntities(3.0D, 3.0D, 3.0D);
                            near = Utils.filterOnlyTeammates(near, player);
                            for (Entity entity : near) {
                                if (entity instanceof Player) {
                                    Player nearPlayer = (Player) entity;
                                    if (nearPlayer.getGameMode() != GameMode.SPECTATOR) {
                                        getPlayer(nearPlayer).addHealth(warlordsPlayer, "Orbs of Life", 420, 420, -1, 100);
                                    }
                                }
                            }
                        }
                        if (orb.getBukkitEntity().getTicksLived() > 160) {
                            orb.getArmorStand().remove();
                            orb.getBukkitEntity().remove();
                            orbs.remove(i);
                            i--;
                        }
                    }
                }

                // PARTICLES - FOUR TICK MODULE
                if (counter % 4 == 0) {
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        Player player = warlordsPlayer.getPlayer();

                        // Arcane Shield
                        if (warlordsPlayer.getArcaneShield() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.CLOUD.display(0.15F, 0.3F, 0.15F, 0.01F, 2, location, 500);
                            ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.3F, 0.3F, 0.0001F, 1, location, 500);
                            ParticleEffect.SPELL_WITCH.display(0.3F, 0.3F, 0.3F, 0.001F, 1, location, 500);
                        }

                        // Blood Lust
                        if (warlordsPlayer.getBloodLust() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.2, 0);
                            //PacketPlayOutWorldParticles p1 = new PacketPlayOutWorldParticles(EnumParticle.REDSTONE, true, 1, 1, 1, 255, 255, 255, 0, 0);
                            // TODO: make it not a rainbow lol
                            ParticleEffect.REDSTONE.display(0.3F, 0.2F, 0.3F, 0.1F, 3, location, 500);
                        }

                        // Earthliving
                        if (warlordsPlayer.getEarthliving() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.2, 0);
                            ParticleEffect.VILLAGER_HAPPY.display(0.3F, 0.3F, 0.3F, 0.1F, 3, location, 500);
                        }
                    }
                }

                // PARTICLES - TWO TICK MODULE
                if (counter % 2 == 0) {
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        //UPDATES SCOREBOARD HEALTHS
                        warlordsPlayer.getScoreboard().updateHealths();

                        Player player = warlordsPlayer.getPlayer();

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
                            Location location = player.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.CLOUD.display(0.2F, 0.2F, 0.2F, 0.001F, 1, location, 500);
                            ParticleEffect.FIREWORKS_SPARK.display(0.3F, 0.2F, 0.3F, 0.0001F, 1, location, 500);
                        }

                        // Berserk
                        if (warlordsPlayer.getBerserk() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 2.1, 0);
                            ParticleEffect.VILLAGER_ANGRY.display(0, 0, 0, 0.1F, 1, location, 500);
                        }

                        // Infusion
                        if (warlordsPlayer.getInfusion() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.5, 0);
                        }

                        // Presence
                        if (warlordsPlayer.getPresence() != 0) {
                            Location location = player.getLocation();
                            location.add(0, 1.5, 0);
                            ParticleEffect.SMOKE_NORMAL.display(0.3F, 0.3F, 0.3F, 0.02F, 1, location, 500);
                        }
                    }
                }

                //EVERY SECOND
                if (counter % 20 == 0) {
                    for (WarlordsPlayer warlordsPlayer : players.values()) {
                        warlordsPlayer.getScoreboard().updateTime();
                        //ACTION BAR
                        if (warlordsPlayer.getPlayer().getInventory().getHeldItemSlot() != 8) {
                            warlordsPlayer.displayActionBar();
                        } else {
                            warlordsPlayer.displayFlagActionBar();
                        }
                        Player player = warlordsPlayer.getPlayer();
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
                        if (warlordsPlayer.getRespawnTimer() != -1) {
                            warlordsPlayer.setRespawnTimer(warlordsPlayer.getRespawnTimer() - 1);
                            warlordsPlayer.getPlayer().sendMessage("RESPAWN: " + warlordsPlayer.getRespawnTimer());
                        }
                        //ABILITY COOLDOWN
                        if (warlordsPlayer.getSpec().getRed().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getRed().getCurrentCooldown() != warlordsPlayer.getSpec().getRed().getCooldown()) {
                            warlordsPlayer.getSpec().getRed().subtractCooldown(1);
                            warlordsPlayer.updateRedItem();
                        }
                        if (warlordsPlayer.getSpec().getPurple().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getPurple().getCurrentCooldown() != warlordsPlayer.getSpec().getPurple().getCooldown()) {
                            warlordsPlayer.getSpec().getPurple().subtractCooldown(1);
                            warlordsPlayer.updatePurpleItem();
                        }
                        if (warlordsPlayer.getSpec().getBlue().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getBlue().getCurrentCooldown() != warlordsPlayer.getSpec().getBlue().getCooldown()) {
                            warlordsPlayer.getSpec().getBlue().subtractCooldown(1);
                            warlordsPlayer.updateBlueItem();
                        }
                        if (warlordsPlayer.getSpec().getOrange().getCurrentCooldown() != 0 && warlordsPlayer.getSpec().getOrange().getCurrentCooldown() != warlordsPlayer.getSpec().getOrange().getCooldown()) {
                            warlordsPlayer.getSpec().getOrange().subtractCooldown(1);
                            warlordsPlayer.updateOrangeItem();
                        }
                        if (warlordsPlayer.getHorseCooldown() != 0 && !player.isInsideVehicle()) {
                            warlordsPlayer.setHorseCooldown(warlordsPlayer.getHorseCooldown() - 1);
                            warlordsPlayer.updateHorseItem();
                        }
                        //COOLDOWNS
                        if (warlordsPlayer.getWrath() != 0) {
                            warlordsPlayer.setWrath(warlordsPlayer.getWrath() - 1);
                        }
                        if (warlordsPlayer.getBloodLust() != 0) {
                            warlordsPlayer.setBloodLust(warlordsPlayer.getBloodLust() - 1);
                        }
                        if (warlordsPlayer.getIntervene() != 0) {
                            if (warlordsPlayer.getIntervene() != 1) {
                                if (warlordsPlayer.getIntervene() == 2)
                                    warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getIntervene() - 1) + "§7 second!");
                                else
                                    warlordsPlayer.getPlayer().sendMessage("§a\u00BB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7will expire in §6" + (warlordsPlayer.getIntervene() - 1) + "§7 seconds!");
                            }
                            warlordsPlayer.setIntervene(warlordsPlayer.getIntervene() - 1);
                            if (warlordsPlayer.getIntervene() == 0) {
                                warlordsPlayer.getPlayer().sendMessage("§c\u00AB§7 " + warlordsPlayer.getIntervenedBy().getName() + "'s §eIntervene §7has expired!");
                                //TODO add intervenedBy player no longer veneing
                            }
                        }
                        if (warlordsPlayer.getLastStand() != 0) {
                            warlordsPlayer.setLastStand(warlordsPlayer.getLastStand() - 1);
                        }
                        if (warlordsPlayer.getOrbOfLife() != 0) {
                            warlordsPlayer.setOrbOfLife(warlordsPlayer.getOrbOfLife() - 1);
                        }
                        if (warlordsPlayer.getUndyingArmy() != 0 && !warlordsPlayer.isUndyingArmyDead()) {
                            warlordsPlayer.setUndyingArmy(warlordsPlayer.getUndyingArmy() - 1);
                            if (warlordsPlayer.getUndyingArmy() == 0) {
                                int healing = (int) ((warlordsPlayer.getMaxHealth() - warlordsPlayer.getHealth()) * .35 + 200);
                                warlordsPlayer.addHealth(warlordsPlayer.getUndyingArmyBy(), "Undying Army", healing, healing, -1, 100);
                            }
                        } else if (warlordsPlayer.isUndyingArmyDead()) {
                            if (warlordsPlayer.getHealth() - 500 < 0) {
                                warlordsPlayer.setHealth(0);
                                warlordsPlayer.setUndyingArmyDead(false);
                            } else {
                                warlordsPlayer.setHealth(warlordsPlayer.getHealth() - 500);
                            }
                        }
                        if (warlordsPlayer.getWindfury() != 0) {
                            warlordsPlayer.setWindfury(warlordsPlayer.getWindfury() - 1);
                        }
                        if (warlordsPlayer.getEarthliving() != 0) {
                            warlordsPlayer.setEarthliving(warlordsPlayer.getEarthliving() - 1);
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
                        if (warlordsPlayer.getRepentance() != 0) {
                            warlordsPlayer.setRepentance(warlordsPlayer.getRepentance() - 1);
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
                            warlordsPlayer.getPlayer().sendMessage("healed for " + heal);
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
