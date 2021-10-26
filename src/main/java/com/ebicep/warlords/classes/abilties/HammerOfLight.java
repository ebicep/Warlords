package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.CooldownTypes;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HammerOfLight extends AbstractAbility {

    private final static int radius = 6;
    private final int duration = 10;
    private boolean isCrownOfLight = false;

    public boolean isCrownOfLight() {
        return isCrownOfLight;
    }

    public void setCrownOfLight(boolean crownOfLight) {
        isCrownOfLight = crownOfLight;
    }

    public HammerOfLight() {
        super("Hammer of Light", 178, 244, 62.64f, 50, 20, 175
        );
    }

    @Override
    public void updateDescription(Player player) {
        description = "§7Throw down a Hammer of Light on\n" +
                "§7the ground, dealing §c" + format(minDamageHeal) + " §7- §c" + format(maxDamageHeal) + " §7damage\n" +
                "§7damage every second to nearby enemies and\n" +
                "§7healing nearby allies for §a" + format(minDamageHeal) + " §7- §a" + format(maxDamageHeal) + " §7every second\n" +
                "§7in a §e" + radius + " §7block radius. Your Protector Strike\n" +
                "§7pierces shields and defenses of enemies\n" +
                "§7standing on top of the Hammer of Light.\n" +
                "§7Lasts §6" + duration + " §7seconds." +
                "\n\n" +
                "§7You may SNEAK to turn your hammer into Crown of Light.\n" +
                "§7Removing the damage and piercing BUT increasing\n" +
                "§7the healing §7by §a50% §7and reducing the\n" +
                "§7energy cost of your Protector Strike by\n" +
                "§e10 §7energy. You cannot put the Hammer of Light\n" +
                "§7back down after you converted it.";
    }

    @Override
    public void onActivate(WarlordsPlayer wp, Player player) {

        if (player.getTargetBlock((HashSet<Byte>) null, 25).getType() == Material.AIR) return;
        DamageHealCircle damageHealCircle = new DamageHealCircle(wp, player.getTargetBlock((HashSet<Byte>) null, 25).getLocation().add(1, 0, 1), radius, duration, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        damageHealCircle.spawnHammer();
        damageHealCircle.getLocation().add(0, 1, 0);
        wp.subtractEnergy(energyCost);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
        HammerOfLight tempHammerOfLight = new HammerOfLight();
        wp.getCooldownManager().addCooldown(name, this.getClass(), tempHammerOfLight, "HAMMER", duration, wp, CooldownTypes.ABILITY);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);
        }

        BukkitTask task = Bukkit.getScheduler().runTaskTimer(Warlords.getInstance(), damageHealCircle::spawn, 0, 1);
        wp.getGame().getGameTasks().put(task, System.currentTimeMillis());
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    int counter = 0;

                    @Override
                    public void run() {
                        if (counter % 20 == 0) {
                            damageHealCircle.setDuration(damageHealCircle.getDuration() - 1);
                            for (WarlordsPlayer warlordsPlayer : PlayerFilter
                                    .entitiesAround(damageHealCircle.getLocation(), radius, radius, radius)
                                    .isAlive()
                            ) {
                                if (damageHealCircle.getWarlordsPlayer().isTeammateAlive(warlordsPlayer)) {
                                    warlordsPlayer.addHealth(
                                            damageHealCircle.getWarlordsPlayer(),
                                            damageHealCircle.getName(),
                                            damageHealCircle.getMinDamage(),
                                            damageHealCircle.getMaxDamage(),
                                            damageHealCircle.getCritChance(),
                                            damageHealCircle.getCritMultiplier(),
                                            false);
                                } else {
                                    warlordsPlayer.addHealth(
                                            damageHealCircle.getWarlordsPlayer(),
                                            damageHealCircle.getName(),
                                            -damageHealCircle.getMinDamage(),
                                            -damageHealCircle.getMaxDamage(),
                                            damageHealCircle.getCritChance(),
                                            damageHealCircle.getCritMultiplier(),
                                            false);
                                }
                            }
                        }
                        if (damageHealCircle.getDuration() <= 0) {
                            damageHealCircle.removeHammer();
                            this.cancel();
                            task.cancel();
                        }
                        counter++;
                    }

                }.runTaskTimer(Warlords.getInstance(), 0, 0),
                System.currentTimeMillis()
        );
        wp.getGame().getGameTasks().put(

                new BukkitRunnable() {
                    boolean wasSneaking = false;

                    @Override
                    public void run() {

                        if (wp.isAlive() && player.isSneaking() && !wasSneaking) {
                            tempHammerOfLight.setCrownOfLight(true);
                            wp.getCooldownManager().getCooldown(tempHammerOfLight).ifPresent(cd -> {
                                cd.setActionBarName("CROWN");
                            });

                            for (Player player1 : player.getWorld().getPlayers()) {
                                player1.playSound(player.getLocation(), "warrior.revenant.orbsoflife", 2, 0.15f);
                                player1.playSound(player.getLocation(), "mage.firebreath.activation", 2, 0.25f);
                            }

                            BukkitTask particles = new BukkitRunnable() {
                                @Override
                                public void run() {
                                    double angle = 0;
                                    for (int i = 0; i < 9; i++) {
                                        double x = .4 * Math.cos(angle);
                                        double z = .4 * Math.sin(angle);
                                        angle += 40;
                                        Vector v = new Vector(x, 2, z);
                                        Location loc = wp.getLocation().clone().add(v);
                                        ParticleEffect.SPELL.display(0, 0, 0, 0f, 1, loc, 500);
                                    }

                                    CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), player.getLocation().add(0, 0.75f, 0), radius / 2f);
                                    circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(0.5f));
                                    circle.playEffects();
                                }
                            }.runTaskTimer(Warlords.getInstance(), 0, 6);
                            wp.getGame().getGameTasks().put(particles, System.currentTimeMillis());
                            wp.getGame().getGameTasks().put(
                                    new BukkitRunnable() {
                                        int timeLeft = damageHealCircle.getDuration();

                                        @Override
                                        public void run() {
                                            PlayerFilter.entitiesAround(wp.getLocation(), radius, radius, radius)
                                                    .aliveTeammatesOf(wp)
                                                    .forEach(teammate -> {
                                                        teammate.addHealth(
                                                                damageHealCircle.getWarlordsPlayer(),
                                                                "Crown of Light",
                                                                damageHealCircle.getMinDamage() * 1.5f,
                                                                damageHealCircle.getMaxDamage() * 1.5f,
                                                                damageHealCircle.getCritChance(),
                                                                damageHealCircle.getCritMultiplier(),
                                                                false);
                                                    });
                                            timeLeft--;

                                            if (timeLeft <= 0 || wp.isDead()) {
                                                this.cancel();
                                                particles.cancel();
                                            }
                                        }
                                    }.runTaskTimer(Warlords.getInstance(), 2, 20),
                                    System.currentTimeMillis()
                            );
                            this.cancel();
                            damageHealCircle.setDuration(0);
                        }

                        wasSneaking = player.isSneaking();

                        if (wp.isDead()) {
                            this.cancel();
                        }
                    }
                }.runTaskTimer(Warlords.getInstance(), 0, 0),
                System.currentTimeMillis()
        );
    }

    public static boolean standingInHammer(WarlordsPlayer owner, Entity standing) {
        if (!(owner.getSpec() instanceof Protector)) return false;
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Hammer of Light - " + owner.getName())) {
                if (entity.getLocation().clone().add(0, 2, 0).distanceSquared(standing.getLocation()) < 5 * 5.25) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    // in case we use it again
    public static List<WarlordsPlayer> getStandingInHammer(WarlordsPlayer owner) {
        List<WarlordsPlayer> playersInHammer = new ArrayList<>();
        for (Entity entity : owner.getWorld().getEntities()) {
            if (entity instanceof ArmorStand && entity.hasMetadata("Hammer of Light - " + owner.getName())) {
                for (WarlordsPlayer enemy : PlayerFilter
                        .entitiesAround(entity, radius, 4, radius)
                        .enemiesOf(owner)
                        .isAlive()) {
                    playersInHammer.add(enemy);
                }
                break;
            }
        }
        return playersInHammer;
    }
}