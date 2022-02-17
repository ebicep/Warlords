package com.ebicep.warlords.classes.abilties;

import com.ebicep.warlords.classes.AbstractAbility;
import com.ebicep.warlords.classes.paladin.specs.protector.Protector;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.player.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.GameRunnable;
import com.ebicep.warlords.util.ParticleEffect;
import com.ebicep.warlords.util.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class HammerOfLight extends AbstractAbility {

    private final static int radius = 6;
    private final int duration = 10;
    private boolean isCrownOfLight = false;

    public HammerOfLight() {
        super("Hammer of Light", 178, 244, 62.64f, 50, 20, 175);
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

    public boolean isCrownOfLight() {
        return isCrownOfLight;
    }

    public void setCrownOfLight(boolean crownOfLight) {
        isCrownOfLight = crownOfLight;
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
                "§7energy cost of your Protector's Strike by\n" +
                "§e10 §7energy. You cannot put the Hammer of Light\n" +
                "§7back down after you converted it.";
    }

    @Override
    public boolean onActivate(WarlordsPlayer wp, Player player) {
        if (player.getTargetBlock((HashSet<Byte>) null, 25).getType() == Material.AIR) return false;

        DamageHealCircle hol = new DamageHealCircle(wp, player.getTargetBlock((HashSet<Byte>) null, 25).getLocation().add(1, 0, 1), radius, duration, minDamageHeal, maxDamageHeal, critChance, critMultiplier, name);
        hol.spawnHammer();
        hol.getLocation().add(0, 1, 0);
        wp.subtractEnergy(energyCost);
        wp.getSpec().getOrange().setCurrentCooldown((float) (cooldown * wp.getCooldownModifier()));
        HammerOfLight tempHammerOfLight = new HammerOfLight();
        wp.getCooldownManager().addRegularCooldown(name, "HAMMER", HammerOfLight.class, tempHammerOfLight, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, duration * 20);

        for (Player player1 : player.getWorld().getPlayers()) {
            player1.playSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);
        }

        BukkitTask task = wp.getGame().registerGameTask(hol::spawn, 0, 1);
        new GameRunnable(wp.getGame()) {
            int counter = 0;

            @Override
            public void run() {
                if (!wp.getGame().isFrozen()) {

                    if (counter % 20 == 0) {
                        hol.setDuration(hol.getDuration() - 1);
                        for (WarlordsPlayer warlordsPlayer : PlayerFilter
                                .entitiesAround(hol.getLocation(), radius, radius, radius)
                                .isAlive()
                        ) {
                            if (hol.getWarlordsPlayer().isTeammateAlive(warlordsPlayer)) {
                                warlordsPlayer.addHealingInstance(
                                        hol.getWarlordsPlayer(),
                                        hol.getName(),
                                        hol.getMinDamage(),
                                        hol.getMaxDamage(),
                                        hol.getCritChance(),
                                        hol.getCritMultiplier(),
                                        false, false);
                            } else {
                                warlordsPlayer.addDamageInstance(
                                        hol.getWarlordsPlayer(),
                                        hol.getName(),
                                        hol.getMinDamage(),
                                        hol.getMaxDamage(),
                                        hol.getCritChance(),
                                        hol.getCritMultiplier(),
                                        false);
                            }
                        }
                    }
                    if (hol.getDuration() <= 0) {
                        hol.removeHammer();
                        this.cancel();
                        task.cancel();
                    }
                    counter++;
                }
            }

        }.runTaskTimer(0, 0);
        new GameRunnable(wp.getGame()) {
            boolean wasSneaking = false;

            @Override
            public void run() {
                if (!wp.getGame().isFrozen()) {

                    if (wp.isAlive() && wp.isSneaking() && !wasSneaking) {
                        tempHammerOfLight.setCrownOfLight(true);
                        new CooldownFilter<>(wp, RegularCooldown.class)
                                .filterCooldownObject(tempHammerOfLight)
                                .findAny()
                                .ifPresent(regularCooldown -> {
                                    regularCooldown.setNameAbbreviation("CROWN");
                                });

                        for (Player player1 : wp.getWorld().getPlayers()) {
                            player1.playSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.15f);
                            player1.playSound(wp.getLocation(), "mage.firebreath.activation", 2, 0.25f);
                        }

                        BukkitTask particles = new GameRunnable(wp.getGame()) {
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

                                CircleEffect circle = new CircleEffect(wp.getGame(), wp.getTeam(), wp.getLocation().add(0, 0.75f, 0), radius / 2f);
                                circle.addEffect(new CircumferenceEffect(ParticleEffect.SPELL).particlesPerCircumference(0.5f));
                                circle.playEffects();
                            }
                        }.runTaskTimer(0, 6);
                        new GameRunnable(wp.getGame()) {
                            int timeLeft = hol.getDuration();

                            @Override
                            public void run() {
                                PlayerFilter.entitiesAround(wp.getLocation(), radius, radius, radius)
                                        .aliveTeammatesOf(wp)
                                        .forEach(teammate -> teammate.addHealingInstance(
                                                hol.getWarlordsPlayer(),
                                                "Crown of Light",
                                                hol.getMinDamage() * 1.5f,
                                                hol.getMaxDamage() * 1.5f,
                                                hol.getCritChance(),
                                                hol.getCritMultiplier(),
                                                false, false));
                                timeLeft--;

                                if (timeLeft <= 0 || wp.isDead()) {
                                    this.cancel();
                                    particles.cancel();
                                }
                            }
                        }.runTaskTimer(2, 20);
                        this.cancel();
                        hol.setDuration(0);
                    }


                    wasSneaking = wp.isSneaking();


                    if (wp.isDead() || hol.getDuration() <= 0) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(0, 0);

        return true;
    }
}