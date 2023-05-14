package com.ebicep.warlords.abilties;

import com.ebicep.warlords.abilties.internal.AbstractAbility;
import com.ebicep.warlords.abilties.internal.Duration;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.*;

public class HammerOfLight extends AbstractAbility implements Duration {

    private static final int RADIUS = 6;

    public static boolean notStandingInHammer(WarlordsEntity owner, WarlordsEntity standing) {
        return new CooldownFilter<>(owner, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                .filter(HammerOfLight::isHammer)
                .noneMatch(hammerOfLight -> hammerOfLight.getLocation().distanceSquared(standing.getLocation()) < RADIUS * RADIUS);
    }

    public boolean isHammer() {
        return !isCrownOfLight;
    }

    public Location getLocation() {
        return location;
    }

    public static List<WarlordsEntity> getStandingInHammer(WarlordsEntity owner) {
        Set<WarlordsEntity> playersInHammer = new HashSet<>();
        new CooldownFilter<>(owner, RegularCooldown.class)
                .filterCooldownClassAndMapToObjectsOfClass(HammerOfLight.class)
                .filter(HammerOfLight::isHammer)
                .map(HammerOfLight::getLocation)
                .forEach(loc -> {
                    for (WarlordsEntity enemy : PlayerFilter
                            .entitiesAround(loc, RADIUS, 4, RADIUS)
                            .enemiesOf(owner)
                            .isAlive()) {
                        playersInHammer.add(enemy);
                    }
                });
        return new ArrayList<>(playersInHammer);
    }

    public int playersHealed = 0;
    public int playersDamaged = 0;
    protected float amountHealed = 0;
    private boolean isCrownOfLight = false;
    private Location location;
    private int tickDuration = 200;
    private float minDamage = 178;
    private float maxDamage = 244;

    public HammerOfLight() {
        super("Hammer of Light", 178, 244, 62.64f, 50, 20, 175);
    }

    public HammerOfLight(Location location) {
        super("Hammer of Light", 178, 244, 62.64f, 50, 20, 175);
        this.location = location;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Throw down a Hammer of Light on the ground, dealing ")
                               .append(formatRangeDamage(minDamage, maxDamage))
                               .append(Component.text(" damage every second to nearby enemies and healing nearby allies for "))
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" every second in a "))
                               .append(Component.text(RADIUS, NamedTextColor.YELLOW))
                               .append(Component.text(" block radius. Your Protector Strike pierces shields and defenses of enemies standing on top of the Hammer of Light. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.newline())
                               .append(Component.newline())
                               .append(Component.text("Recast to turn your hammer into Crown of Light. Removing the damage and piercing BUT increasing the healing by "))
                               .append(Component.text("50%", NamedTextColor.GREEN))
                               .append(Component.text(" and reducing the energy cost of your Protector's Strike by "))
                               .append(Component.text("10", NamedTextColor.YELLOW))
                               .append(Component.text(" energy. You cannot put the Hammer of Light back down after you converted it."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Healed", "" + playersHealed));
        info.add(new Pair<>("Players Damaged", "" + playersDamaged));

        return info;
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp, @Nonnull Player player) {
        if (player.getTargetBlock(null, 25).getType() == Material.AIR) {
            return false;
        }
        wp.subtractEnergy(energyCost, false);

        Utils.playGlobalSound(player.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);

        Location location = player.getTargetBlock(null, 25).getLocation().clone().add(.6, 0, .6).clone();
        if (location.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
            if (location.clone().add(1, 0, 0).getBlock().getType() == Material.AIR) {
                location.add(.6, 0, 0);
            } else if (location.clone().add(-1, 0, 0).getBlock().getType() == Material.AIR) {
                location.add(-.6, 0, 0);
            } else if (location.clone().add(0, 0, 1).getBlock().getType() == Material.AIR) {
                location.add(0, 0, .6);
            } else if (location.clone().add(0, 0, -1).getBlock().getType() == Material.AIR) {
                location.add(0, 0, -.6);
            }
        }

        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                RADIUS,
                new CircumferenceEffect(Particle.VILLAGER_HAPPY, Particle.REDSTONE),
                new LineEffect(location.clone().add(0, 2.3, 0), Particle.SPELL)
        );
        BukkitTask particleTask = wp.getGame().registerGameTask(circleEffect::playEffects, 0, 1);
        ArmorStand hammer = spawnHammer(location);

        HammerOfLight tempHammerOfLight = new HammerOfLight(location);
        RegularCooldown<HammerOfLight> hammerOfLightCooldown = new RegularCooldown<>(
                name,
                "HAMMER",
                HammerOfLight.class,
                tempHammerOfLight,
                wp,
                CooldownTypes.ABILITY,
                cooldownManager -> {
                },
                cooldownManager -> {
                    hammer.remove();
                    particleTask.cancel();
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksElapsed % 20 == 0) {
                        if (tempHammerOfLight.isCrownOfLight()) {
                            if (wp.isAlive()) {
                                for (WarlordsEntity allyTarget : PlayerFilter
                                        .entitiesAround(wp.getLocation(), RADIUS, RADIUS, RADIUS)
                                        .aliveTeammatesOf(wp)
                                ) {
                                    playersHealed++;
                                    allyTarget.addHealingInstance(
                                            wp,
                                            "Crown of Light",
                                            minDamageHeal * 1.5f,
                                            maxDamageHeal * 1.5f,
                                            critChance,
                                            critMultiplier,
                                            false,
                                            false
                                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                        tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                    });
                                }
                            }
                        } else {
                            for (WarlordsEntity hammerTarget : PlayerFilter
                                    .entitiesAround(location, RADIUS, RADIUS, RADIUS)
                                    .isAlive()
                            ) {
                                if (wp.isTeammate(hammerTarget)) {
                                    playersHealed++;
                                    hammerTarget.addHealingInstance(
                                            wp,
                                            name,
                                            minDamageHeal,
                                            maxDamageHeal,
                                            critChance,
                                            critMultiplier,
                                            false,
                                            false
                                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                        tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                    });
                                } else {
                                    playersDamaged++;
                                    hammerTarget.addDamageInstance(
                                            wp,
                                            name,
                                            minDamage,
                                            maxDamage,
                                            critChance,
                                            critMultiplier,
                                            false
                                    );
                                }
                            }
                        }
                    }
                })
        );

        wp.getCooldownManager().addCooldown(hammerOfLightCooldown);

        location.add(0, 1, 0);

        addSecondaryAbility(() -> {
                    if (wp.isAlive() && wp.getCooldownManager().hasCooldown(hammerOfLightCooldown)) {
                        hammer.remove();
                        particleTask.cancel();

                        Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.15f);
                        Utils.playGlobalSound(wp.getLocation(), "mage.firebreath.activation", 2, 0.25f);

                        hammerOfLightCooldown.setRemoveOnDeath(true);
                        hammerOfLightCooldown.addTriConsumer((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 6 == 0) {
                                double angle = 0;
                                for (int i = 0; i < 9; i++) {
                                    double x = .4 * Math.cos(angle);
                                    double z = .4 * Math.sin(angle);
                                    angle += 40;
                                    Vector v = new Vector(x, 2, z);
                                    Location loc = wp.getLocation().clone().add(v);
                                    loc.getWorld().spawnParticle(
                                            Particle.SPELL,
                                            loc,
                                            1,
                                            0,
                                            0,
                                            0,
                                            0,
                                            null,
                                            true
                                    );
                                }

                                new CircleEffect(
                                        wp.getGame(),
                                        wp.getTeam(),
                                        wp.getLocation().add(0, 0.75f, 0),
                                        RADIUS / 2f,
                                        new CircumferenceEffect(Particle.SPELL).particlesPerCircumference(0.5f)
                                ).playEffects();
                            }
                        });


                        tempHammerOfLight.setCrownOfLight(true);
                        hammerOfLightCooldown.setNameAbbreviation("CROWN");

                        if (pveUpgrade) {
                            pulseHeal(wp, 20, 1.5, tempHammerOfLight);
                            pulseHeal(wp, 40, 2.5, tempHammerOfLight);
                            pulseHeal(wp, 60, 3.5, tempHammerOfLight);
                            pulseHeal(wp, 80, 4.5, tempHammerOfLight);
                        }
                    }
                },
                false,
                secondaryAbility -> !wp.getCooldownManager().hasCooldown(hammerOfLightCooldown) || wp.isDead()
        );

        return true;
    }

    public ArmorStand spawnHammer(Location location) {
        Location newLocation = location.clone();
        for (int i = 0; i < 10; i++) {
            if (newLocation.getWorld().getBlockAt(newLocation.clone().add(0, -1, 0)).getType() == Material.AIR) {
                newLocation.add(0, -1, 0);
            }
        }
        newLocation.add(0, -1, 0);

        return Utils.spawnArmorStand(newLocation.clone().add(.25, 1.9, -.25), armorStand -> {
            armorStand.setRightArmPose(new EulerAngle(20.25, 0, 0));
            armorStand.getEquipment().setItemInMainHand(new ItemStack(Material.STRING));
            armorStand.setMarker(true);
        });
    }

    public boolean isCrownOfLight() {
        return isCrownOfLight;
    }

    public void setCrownOfLight(boolean crownOfLight) {
        isCrownOfLight = crownOfLight;
    }

    public void addAmountHealed(float amount) {
        this.amountHealed += amount;
    }

    private void pulseHeal(WarlordsEntity wp, int delay, double radiusMultiplier, HammerOfLight tempHammerOfLight) {
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.4f);
                EffectUtils.strikeLightning(wp.getLocation(), false, delay / 10);
                EffectUtils.playHelixAnimation(wp.getLocation(), RADIUS * radiusMultiplier, Particle.SPELL_WITCH, 1, 20);
                new CircleEffect(
                        wp.getGame(),
                        wp.getTeam(),
                        wp.getLocation().add(0, 0.75f, 0),
                        RADIUS * radiusMultiplier,
                        new CircumferenceEffect(Particle.SPELL).particlesPerCircumference(1)
                ).playEffects();

                for (WarlordsEntity allyTarget : PlayerFilter
                        .entitiesAround(wp.getLocation(), RADIUS * radiusMultiplier, RADIUS * radiusMultiplier, RADIUS * radiusMultiplier)
                        .aliveTeammatesOf(wp)
                ) {
                    playersHealed++;
                    allyTarget.addHealingInstance(
                            wp,
                            "Hammer of Illusion",
                            minDamageHeal * 5,
                            maxDamageHeal * 5,
                            -1,
                            100,
                            false,
                            false
                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                        tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                    });
                }

                for (WarlordsEntity enemyTarget : PlayerFilter
                        .entitiesAround(wp.getLocation(), RADIUS * radiusMultiplier, RADIUS * radiusMultiplier, RADIUS * radiusMultiplier)
                        .aliveEnemiesOf(wp)
                ) {
                    enemyTarget.addDamageInstance(
                            wp,
                            "Hammer of Illusion",
                            minDamage * 5,
                            maxDamage * 5,
                            -1,
                            100,
                            false
                    );
                }
            }
        }.runTaskLater(delay);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public float getMinDamage() {
        return minDamage;
    }

    public void setMinDamage(float minDamage) {
        this.minDamage = minDamage;
    }

    public float getMaxDamage() {
        return maxDamage;
    }

    public void setMaxDamage(float maxDamage) {
        this.maxDamage = maxDamage;
    }


    public float getAmountHealed() {
        return amountHealed;
    }
}