package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

public class Projectile extends AbstractAbility {

    private static final float hitBox = 2;
    private int maxDistance;

    public Projectile(String name, int minDamageHeal, int maxDamageHeal, int cooldown, int energyCost, int critChance, int critMultiplier, String description, int maxDistance) {
        super(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description);
        this.maxDistance = maxDistance;
    }

    @Override
    public void onActivate(Player player) {
        Warlords.getPlayer(player).subtractEnergy(energyCost);

        CustomProjectile customProjectile = new CustomProjectile(player, player.getLocation(), player.getLocation(), player.getLocation().getDirection(), maxDistance,
                new Projectile(name, minDamageHeal, maxDamageHeal, cooldown, energyCost, critChance, critMultiplier, description, maxDistance));

        // SOUNDS
        if (customProjectile.getBall().getName().contains("Fire")) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.fireball.activation", 1, 1);
            }

        } else if (customProjectile.getBall().getName().contains("Frost")) {

            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.frostbolt.activation", 1, 1);
            }
        } else if (customProjectile.getBall().getName().contains("Water")) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.waterbolt.activation", 1, 1);
            }
        } else if (customProjectile.getBall().getName().contains("Flame")) {
            for (Player player1 : Bukkit.getOnlinePlayers()) {
                player1.playSound(player.getLocation(), "mage.fireball.activation", 1, 1);
            }
        }

        new BukkitRunnable() {

            @Override
            public void run() {
                Location location = customProjectile.getCurrentLocation();
                boolean hitPlayer = false;
                //BALLS
                if (customProjectile.getBall().getName().contains("Fire")) {
                    location.add(customProjectile.getDirection().clone().multiply(2.3));
                    location.add(0, 1.5, 0);
                    ParticleEffect.DRIP_LAVA.display(0, 0, 0, 0.35F, 5, location, 500);
                    ParticleEffect.SMOKE_NORMAL.display(0, 0, 0, 0.001F, 7, location, 500);
                    ParticleEffect.FLAME.display(0, 0, 0, 0.06F, 1, location, 500);
                    List<Entity> entities = (List<Entity>) location.getWorld().getNearbyEntities(location, 5, 5, 5);
                    entities = Utils.filterOutTeammates(entities, customProjectile.getShooter());
                    for (Entity entity : entities) {
                        if (entity instanceof Player && entity != customProjectile.getShooter()) {
                            if (entity.getLocation().distanceSquared(location) < hitBox * hitBox) {
                                player.sendMessage("HIT PLAYER");
                                hitPlayer = true;
                                ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.5F, 1, entity.getLocation().add(0, 1, 0), 500);
                                ParticleEffect.LAVA.display(0, 0, 0, 0.5F, 10, entity.getLocation().add(0, 1, 0), 500);
                                Player victim = (Player) entity;

                                if (location.distanceSquared(customProjectile.getStartingLocation()) >= customProjectile.getMaxDistance() * customProjectile.getMaxDistance()) {
                                    double toReduceBy = (1 - ((location.distance(customProjectile.getStartingLocation()) - customProjectile.getMaxDistance()) / 100.0));
                                    if (toReduceBy < 0) toReduceBy = 0;
                                    Warlords.getPlayer(victim).addHealth(
                                            Warlords.getPlayer(customProjectile.getShooter()),
                                            customProjectile.getBall().getName(),
                                            (int) (customProjectile.getBall().getMinDamageHeal() * 1.15 * toReduceBy),
                                            (int) (customProjectile.getBall().getMaxDamageHeal() * 1.15 * toReduceBy),
                                            customProjectile.getBall().getCritChance(),
                                            customProjectile.getBall().getCritMultiplier()

                                    );
                                    List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                    near = Utils.filterOutTeammates(near, customProjectile.getShooter());
                                    for (Entity nearEntity : near) {
                                        if (nearEntity instanceof Player) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    (int) (customProjectile.getBall().getMinDamageHeal() * toReduceBy),
                                                    (int) (customProjectile.getBall().getMaxDamageHeal() * toReduceBy),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    }
                                } else {
                                    Warlords.getPlayer(victim).addHealth(
                                            Warlords.getPlayer(customProjectile.getShooter()),
                                            customProjectile.getBall().getName(),
                                            (int) (customProjectile.getBall().getMinDamageHeal() * 1.15),
                                            (int) (customProjectile.getBall().getMaxDamageHeal() * 1.15),
                                            customProjectile.getBall().getCritChance(),
                                            customProjectile.getBall().getCritMultiplier()

                                    );
                                    List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                    near = Utils.filterOutTeammates(near, customProjectile.getShooter());
                                    for (Entity nearEntity : near) {
                                        if (nearEntity instanceof Player) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    customProjectile.getBall().getMinDamageHeal(),
                                                    customProjectile.getBall().getMaxDamageHeal(),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    }
                                }
                                customProjectile.setRemove(true);
                                break;
                            }
                        }
                    }
                } else if (customProjectile.getBall().getName().contains("Frost")) {
                    location.add(customProjectile.getDirection().clone().multiply(2.1));
                    location.add(0, 1.5, 0);
                    //TODO add slowness
                    ParticleEffect.CLOUD.display(0, 0, 0, 0F, 1, location, 500);
                    //ParticleEffect.FLAME.display(0, 0, 0, 0.1F, 3, location, 500);
                    List<Entity> entities = (List<Entity>) location.getWorld().getNearbyEntities(location, 5, 5, 5);
                    System.out.println(entities);
                    entities = Utils.filterOutTeammates(entities, customProjectile.getShooter());
                    System.out.println(entities);
                    for (Entity entity : entities) {
                        if (entity instanceof Player && entity != customProjectile.getShooter()) {
                            if (entity.getLocation().distanceSquared(location) < hitBox * hitBox) {
                                hitPlayer = true;
                                ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, entity.getLocation().add(0, 1, 0), 500);
                                Player victim = (Player) entity;
                                Warlords.getPlayer(victim).getSpeed().changeCurrentSpeed("Frostbolt", -25, 2 * 20);

                                for (Player player1 : Bukkit.getOnlinePlayers()) {
                                    player1.playSound(entity.getLocation(), "mage.frostbolt.impact", 1, 1);
                                }
                                if (location.distanceSquared(customProjectile.getStartingLocation()) >= customProjectile.getMaxDistance() * customProjectile.getMaxDistance()) {
                                    double toReduceBy = (1 - ((location.distance(customProjectile.getStartingLocation()) - customProjectile.getMaxDistance()) / 100.0));
                                    if (toReduceBy < 0) toReduceBy = 0;
                                    Warlords.getPlayer(victim).addHealth(
                                            Warlords.getPlayer(customProjectile.getShooter()),
                                            customProjectile.getBall().getName(),
                                            (int) (customProjectile.getBall().getMinDamageHeal() * 1.3 * toReduceBy),
                                            (int) (customProjectile.getBall().getMaxDamageHeal() * 1.3 * toReduceBy),
                                            customProjectile.getBall().getCritChance(),
                                            customProjectile.getBall().getCritMultiplier()
                                    );
                                    List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                    near = Utils.filterOutTeammates(near, customProjectile.getShooter());
                                    for (Entity nearEntity : near) {
                                        if (nearEntity instanceof Player) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    (int) (customProjectile.getBall().getMinDamageHeal() * toReduceBy),
                                                    (int) (customProjectile.getBall().getMaxDamageHeal() * toReduceBy),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    }
                                } else {
                                    Warlords.getPlayer(victim).addHealth(
                                            Warlords.getPlayer(customProjectile.getShooter()),
                                            customProjectile.getBall().getName(),
                                            (int) (customProjectile.getBall().getMinDamageHeal() * 1.3),
                                            (int) (customProjectile.getBall().getMaxDamageHeal() * 1.3),
                                            customProjectile.getBall().getCritChance(),
                                            customProjectile.getBall().getCritMultiplier()
                                    );
                                    List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                    near = Utils.filterOutTeammates(near, customProjectile.getShooter());
                                    for (Entity nearEntity : near) {
                                        if (nearEntity instanceof Player) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    customProjectile.getBall().getMinDamageHeal(),
                                                    customProjectile.getBall().getMaxDamageHeal(),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    }
                                }
                                customProjectile.setRemove(true);
                                break;
                            }
                        }
                    }
                } else if (customProjectile.getBall().getName().contains("Water")) {
                    location.add(customProjectile.getDirection().clone().multiply(2));
                    location.add(0, 1.5, 0);
                    //TODO add damage
                    ParticleEffect.DRIP_WATER.display(0.3f, 0.3f, 0.3f, 0.1F, 2, location, 500);
                    ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0.1F, 1, location, 500);
                    ParticleEffect.VILLAGER_HAPPY.display(0, 0, 0, 0.1F, 1, location, 500);
                    ParticleEffect.CLOUD.display(0, 0, 0, 0F, 1, location, 500);
                    //ParticleEffect.FLAME.display(0, 0, 0, 0.1F, 3, location, 500);
                    List<Entity> entities = (List<Entity>) location.getWorld().getNearbyEntities(location, 5, 5, 5);
                    for (Entity entity : entities) {
                        if (entity instanceof Player && entity != customProjectile.getShooter()) {
                            if (entity.getLocation().distanceSquared(location) < hitBox * hitBox) {
                                hitPlayer = true;
                                ParticleEffect.HEART.display(1.5F, 1.5F, 1.5F, 0.2F, 2, entity.getLocation().add(0, 1, 0), 500);
                                ParticleEffect.VILLAGER_HAPPY.display(1.5F, 1.5F, 1.5F, 0.2F, 3, entity.getLocation().add(0, 1, 0), 500);
                                Player victim = (Player) entity;
                                for (Player player1 : Bukkit.getOnlinePlayers()) {
                                    player1.playSound(entity.getLocation(), "mage.waterbolt.impact", 1, 1);
                                }
                                if (location.distanceSquared(customProjectile.getStartingLocation()) >= customProjectile.getMaxDistance() * customProjectile.getMaxDistance()) {
                                    double toReduceBy = (1 - ((location.distance(customProjectile.getStartingLocation()) - customProjectile.getMaxDistance()) / 100.0));
                                    if (toReduceBy < 0) toReduceBy = 0;
                                    if (Warlords.game.onSameTeam((Player) entity, customProjectile.getShooter())) {
                                        Warlords.getPlayer((Player) entity).addHealth(
                                                Warlords.getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                (int) (customProjectile.getBall().getMinDamageHeal() * 1.15 * toReduceBy),
                                                (int) (customProjectile.getBall().getMaxDamageHeal() * 1.15 * toReduceBy),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                    } else {
                                        Warlords.getPlayer((Player) entity).addHealth(
                                                Warlords.getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                (int) (-231 * 1.15 * toReduceBy),
                                                (int) (-299 * 1.15 * toReduceBy),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                    }
                                    List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                    for (Entity nearEntity : near) {
                                        if (nearEntity instanceof Player) {
                                            if (Warlords.game.onSameTeam((Player) nearEntity, customProjectile.getShooter())) {
                                                Warlords.getPlayer((Player) nearEntity).addHealth(
                                                        Warlords.getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        (int) (customProjectile.getBall().getMinDamageHeal() * toReduceBy),
                                                        (int) (customProjectile.getBall().getMaxDamageHeal() * toReduceBy),
                                                        customProjectile.getBall().getCritChance(),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            } else {
                                                Warlords.getPlayer((Player) nearEntity).addHealth(
                                                        Warlords.getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        (int) (-231 * toReduceBy),
                                                        (int) (-299 * toReduceBy),
                                                        customProjectile.getBall().getCritChance(),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            }
                                        }
                                    }
                                } else {
                                    if (Warlords.game.onSameTeam((Player) entity, customProjectile.getShooter())) {
                                        Warlords.getPlayer((Player) entity).addHealth(
                                                Warlords.getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                (int) (customProjectile.getBall().getMinDamageHeal() * 1.15),
                                                (int) (customProjectile.getBall().getMaxDamageHeal() * 1.15),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                    } else {
                                        Warlords.getPlayer((Player) entity).addHealth(
                                                Warlords.getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                (int) (-231 * 1.15),
                                                (int) (-299 * 1.15),
                                                customProjectile.getBall().getCritChance(),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                    }
                                    List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                    for (Entity nearEntity : near) {
                                        if (nearEntity instanceof Player) {
                                            if (Warlords.game.onSameTeam((Player) nearEntity, customProjectile.getShooter())) {
                                                Warlords.getPlayer((Player) nearEntity).addHealth(
                                                        Warlords.getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        customProjectile.getBall().getMinDamageHeal(),
                                                        customProjectile.getBall().getMaxDamageHeal(),
                                                        customProjectile.getBall().getCritChance(),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            } else {
                                                Warlords.getPlayer((Player) nearEntity).addHealth(
                                                        Warlords.getPlayer(customProjectile.getShooter()),
                                                        customProjectile.getBall().getName(),
                                                        -231,
                                                        -299,
                                                        customProjectile.getBall().getCritChance(),
                                                        customProjectile.getBall().getCritMultiplier()
                                                );
                                            }
                                        }
                                    }
                                }
                                customProjectile.setRemove(true);
                                break;
                            }
                        }
                    }


                } else if (customProjectile.getBall().getName().contains("Flame")) {
                    location.add(customProjectile.getDirection().multiply(1.05));
                    location.add(0, 1.5, 0);
                    //TODO add flameburst animation

                    // Equation for spiral animation
                    int radius = 2;
                    for (double x = 0; x <= 50; x += 0.05) { // Set for vertical, need to change
                        double y = radius * Math.cos(x);
                        double z = radius * Math.sin(x);
                    }

                    ParticleEffect.FLAME.display(0.2F, 0, 0.2F, 0F, 4, location, 500);
                    List<Entity> entities = (List<Entity>) location.getWorld().getNearbyEntities(location, 5, 5, 5);
                    entities = Utils.filterOutTeammates(entities, customProjectile.getShooter());
                    for (Entity entity : entities) {
                        if (entity instanceof Player && entity != customProjectile.getShooter()) {
                            if (entity.getLocation().distanceSquared(location) < hitBox * hitBox) {
                                hitPlayer = true;
                                ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, entity.getLocation().add(0, 1, 0), 500);
                                Player victim = (Player) entity;
                                for (Player player1 : Bukkit.getOnlinePlayers()) {
                                    player1.playSound(entity.getLocation(), "mage.flameburst.impact", 1, 1);
                                }

                                Warlords.getPlayer(victim).addHealth(
                                        Warlords.getPlayer(customProjectile.getShooter()),
                                        customProjectile.getBall().getName(),
                                        customProjectile.getBall().getMinDamageHeal(),
                                        customProjectile.getBall().getMaxDamageHeal(),
                                        customProjectile.getBall().getCritChance() + (int) location.distance(customProjectile.getStartingLocation()),
                                        customProjectile.getBall().getCritMultiplier()
                                );
                                List<Entity> near = victim.getNearbyEntities(3.5D, 3.5D, 3.5D);
                                near = Utils.filterOutTeammates(near, customProjectile.getShooter());
                                for (Entity nearEntity : near) {
                                    if (nearEntity instanceof Player) {
                                        Warlords.getPlayer((Player) nearEntity).addHealth(
                                                Warlords.getPlayer(customProjectile.getShooter()),
                                                customProjectile.getBall().getName(),
                                                customProjectile.getBall().getMinDamageHeal(),
                                                customProjectile.getBall().getMaxDamageHeal(),
                                                customProjectile.getBall().getCritChance() + (int) Math.pow(location.distanceSquared(customProjectile.getStartingLocation()), 2),
                                                customProjectile.getBall().getCritMultiplier()
                                        );
                                    }
                                }

                                customProjectile.setRemove(true);
                                break;
                            }
                        }
                    }
                }

                //hit block or out of range
                if ((location.getWorld().getBlockAt(location).getType() != Material.AIR && location.getWorld().getBlockAt(location).getType() != Material.WATER) && !hitPlayer) {
                    player.sendMessage("HIT FLOOR");
                    if (customProjectile.getBall().getName().contains("Water")) {
                        ParticleEffect.HEART.display(1, 1, 1, 0.2F, 5, location, 500);
                        ParticleEffect.VILLAGER_HAPPY.display(1, 1, 1, 0.2F, 5, location, 500);
                    } else {
                        ParticleEffect.EXPLOSION_LARGE.display(0, 0, 0, 0.0F, 1, location, 500);
                    }
                    List<Entity> near = (List<Entity>) location.getWorld().getNearbyEntities(location, 3.5, 3.5, 3.5);
                    for (Entity nearEntity : near) {
                        if (nearEntity instanceof Player) {
                            if (customProjectile.getBall().getName().contains("Flame") && !Warlords.game.onSameTeam((Player) nearEntity, customProjectile.getShooter()) && nearEntity != customProjectile.getShooter()) {
                                Warlords.getPlayer((Player) nearEntity).addHealth(
                                        Warlords.getPlayer(customProjectile.getShooter()),
                                        customProjectile.getBall().getName(),
                                        customProjectile.getBall().getMinDamageHeal(),
                                        customProjectile.getBall().getMaxDamageHeal(),
                                        customProjectile.getBall().getCritChance() + (int) Math.pow(location.distanceSquared(customProjectile.getStartingLocation()), 2),
                                        customProjectile.getBall().getCritMultiplier()
                                );
                            } else {
                                if (location.distanceSquared(customProjectile.getStartingLocation()) >= customProjectile.getMaxDistance() * customProjectile.getMaxDistance()) {
                                    double toReduceBy = (1 - ((location.distance(customProjectile.getStartingLocation()) - customProjectile.getMaxDistance()) / 100.0));
                                    if (toReduceBy < 0) toReduceBy = 0;
                                    if (customProjectile.getBall().getName().contains("Water")) {
                                        if (Warlords.game.onSameTeam((Player) nearEntity, customProjectile.getShooter())) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    (int) (customProjectile.getBall().getMinDamageHeal() * toReduceBy),
                                                    (int) (customProjectile.getBall().getMaxDamageHeal() * toReduceBy),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        } else {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    (int) (-231 * toReduceBy),
                                                    (int) (-299 * toReduceBy),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    } else {
                                        if (!Warlords.game.onSameTeam((Player) nearEntity, customProjectile.getShooter())) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    (int) (customProjectile.getBall().getMinDamageHeal() * toReduceBy),
                                                    (int) (customProjectile.getBall().getMaxDamageHeal() * toReduceBy),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    }
                                } else {

                                    if (customProjectile.getBall().getName().contains("Water")) {
                                        if (Warlords.game.onSameTeam((Player) nearEntity, customProjectile.getShooter())) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    customProjectile.getBall().getMinDamageHeal(),
                                                    customProjectile.getBall().getMaxDamageHeal(),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        } else {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    -231,
                                                    -299,
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    } else {
                                        if (!Warlords.game.onSameTeam((Player) nearEntity, customProjectile.getShooter())) {
                                            Warlords.getPlayer((Player) nearEntity).addHealth(
                                                    Warlords.getPlayer(customProjectile.getShooter()),
                                                    customProjectile.getBall().getName(),
                                                    customProjectile.getBall().getMinDamageHeal(),
                                                    customProjectile.getBall().getMaxDamageHeal(),
                                                    customProjectile.getBall().getCritChance(),
                                                    customProjectile.getBall().getCritMultiplier()
                                            );
                                        }
                                    }
                                }
                            }
                        }
                    }
                    customProjectile.setRemove(true);
                } else if (location.distanceSquared(customProjectile.getStartingLocation()) >= 300 * 300) {
                    customProjectile.setRemove(true);
                }

                location.subtract(0, 1.5, 0);

                if (customProjectile.isRemove()) {
                    this.cancel();
                }

            }

        }.runTaskTimer(Warlords.getInstance(), 0, 0);

    }

    public static class CustomProjectile {

        private Player shooter;
        private Location startingLocation;
        private Location currentLocation;
        private Vector direction;
        private int maxDistance;
        private Projectile projectile;
        private boolean remove;

        public CustomProjectile(Player shooter, Location startingLocation, Location currentLocation, Vector direction, int maxDistance, Projectile projectile) {
            this.shooter = shooter;
            this.startingLocation = startingLocation;
            this.currentLocation = currentLocation;
            this.direction = direction;
            this.maxDistance = maxDistance;
            this.projectile = projectile;
            remove = false;
        }

        public Player getShooter() {
            return shooter;
        }

        public void setShooter(Player shooter) {
            this.shooter = shooter;
        }

        public Location getStartingLocation() {
            return startingLocation;
        }

        public void setStartingLocation(Location startingLocation) {
            this.startingLocation = startingLocation;
        }

        public Location getCurrentLocation() {
            return currentLocation;
        }

        public void setCurrentLocation(Location currentLocation) {
            this.currentLocation = currentLocation;
        }

        public Vector getDirection() {
            return direction;
        }

        public void setDirection(Vector direction) {
            this.direction = direction;
        }

        public int getMaxDistance() {
            return maxDistance;
        }

        public void setMaxDistance(int maxDistance) {
            this.maxDistance = maxDistance;
        }

        public Projectile getBall() {
            return projectile;
        }

        public void setBall(Projectile projectile) {
            this.projectile = projectile;
        }

        public boolean isRemove() {
            return remove;
        }

        public void setRemove(boolean remove) {
            this.remove = remove;
        }
    }
}