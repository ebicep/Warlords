package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.LineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownManager;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.paladin.protector.HammerOfLightBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HammerOfLight extends AbstractAbility implements OrangeAbilityIcon, Duration, Damages<HammerOfLight.DamageValues>, Heals<HammerOfLight.HealingValues> {

    public int playersHealed = 0;
    public int playersDamaged = 0;
    protected float amountHealed = 0;
    private final FloatModifiable radius = new FloatModifiable(6);
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private boolean isCrownOfLight = false;
    private Location location;
    private int tickDuration = 200;
    private float minDamage = 178;
    private float maxDamage = 244;
    private float crownBonusHealing = 35;
    public HammerOfLight() {
        super("Hammer of Light", 178, 244, 65.25f, 50, 20, 175);
    }
    public HammerOfLight(Location location) {
        super("Hammer of Light", 178, 244, 65.25f, 50, 20, 175);
        this.location = location;
    }

    public DamageValues getDamageValues() {
        return damageValues;
    }

    public HealingValues getHealValues() {
        return healingValues;
    }

    public boolean isHammer() {
        return !isCrownOfLight;
    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Throw down a Hammer of Light on the ground, dealing ")
                               .append(formatRangeDamage(minDamage, maxDamage))
                               .append(Component.text(" damage every second to nearby enemies and healing nearby allies for "))
                               .append(formatRangeHealing(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" every second in a "))
                               .append(Component.text(format(radius.getCalculatedValue()), NamedTextColor.YELLOW))
                               .append(Component.text(" block radius. Your attacks pierces shields and defenses of enemies standing on top of the Hammer of Light. Lasts "))
                               .append(Component.text(format(tickDuration / 20f), NamedTextColor.GOLD))
                               .append(Component.text(" seconds."))
                               .append(Component.newline())
                               .append(Component.newline())
                               .append(Component.text("Recast to turn your hammer into Crown of Light. Removing the damage and piercing BUT increasing the healing by "))
                               .append(Component.text(crownBonusHealing + "%", NamedTextColor.GREEN))
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
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Block targetBlock = !(wp.getEntity() instanceof Player) ? LocationUtils.getGroundLocation(wp.getLocation()).getBlock() : Utils.getTargetBlock(wp, 25);
        if (targetBlock.getType() == Material.AIR) {
            return false;
        }

        Utils.playGlobalSound(wp.getLocation(), "paladin.hammeroflight.impact", 2, 0.85f);

        Location location = targetBlock.getLocation().clone().add(.6, 0, .6).clone();
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

        float rad = radius.getCalculatedValue();
        CircleEffect circleEffect = new CircleEffect(
                wp.getGame(),
                wp.getTeam(),
                location,
                rad,
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

                    for (ProtectorsStrike protectorsStrike : wp.getAbilitiesMatching(ProtectorsStrike.class)) {
                        protectorsStrike.getEnergyCost().removeModifier("Hammer of Light");
                    }
                },
                false,
                tickDuration,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (pveMasterUpgrade2 && ticksElapsed % 5 == 0) {
                        for (WarlordsEntity allyTarget : PlayerFilter
                                .entitiesAround(wp.getLocation(), rad, rad, rad)
                                .aliveTeammatesOfExcludingSelf(wp)
                        ) {
                            allyTarget.getSpeed().removeSlownessModifiers();
                            CooldownManager allyTargetCooldownManager = allyTarget.getCooldownManager();
                            allyTargetCooldownManager.removeDebuffCooldowns();
                            allyTargetCooldownManager.removeCooldownByObject(tempHammerOfLight);
                            allyTargetCooldownManager.addCooldown(new RegularCooldown<>(
                                    "Debuff Immunity",
                                    null,
                                    HammerOfLight.class,
                                    tempHammerOfLight,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    5
                            ));
                        }

                    }
                    if (ticksElapsed % 20 != 0) {
                        return;
                    }
                    if (tempHammerOfLight.isCrownOfLight()) {
                        if (!wp.isAlive()) {
                            return;
                        }
                        for (WarlordsEntity crownTarget : PlayerFilter
                                .entitiesAround(wp.getLocation(), rad, rad, rad)
                                .isAlive()
                        ) {
                            if (wp.isTeammate(crownTarget)) {
                                playersHealed++;
                                crownTarget.addInstance(InstanceBuilder
                                        .healing()
                                        .cause("Crown of Light")
                                        .source(wp)
                                        .min(healingValues.hammerHealing.getMinValue() * convertToMultiplicationDecimal(crownBonusHealing))
                                        .max(healingValues.hammerHealing.getMaxValue() * convertToMultiplicationDecimal(crownBonusHealing))
                                        .crit(healingValues.hammerHealing)
                                ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                    tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                });
                            } else {
                                if (pveMasterUpgrade2) {
                                    giveHammerOfDisillusionEffect(crownTarget, wp);
                                }
                            }
                        }
                    } else {
                        for (WarlordsEntity hammerTarget : PlayerFilter
                                .entitiesAround(location, rad, rad, rad)
                                .isAlive()
                        ) {
                            if (wp.isTeammate(hammerTarget)) {
                                playersHealed++;
                                hammerTarget.addInstance(InstanceBuilder
                                        .healing()
                                        .ability(this)
                                        .source(wp)
                                        .value(healingValues.hammerHealing)
                                ).ifPresent(warlordsDamageHealingFinalEvent -> {
                                    tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                                });
                            } else {
                                playersDamaged++;
                                hammerTarget.addInstance(InstanceBuilder
                                        .damage()
                                        .ability(this)
                                        .source(wp)
                                        .value(damageValues.hammerDamage)
                                );
                                if (pveMasterUpgrade2) {
                                    giveHammerOfDisillusionEffect(hammerTarget, wp);
                                }
                            }
                        }
                    }
                })
        ) {
            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler
                    public void onDamageHeal(WarlordsDamageHealingEvent event) {
                        if (!event.isDamageInstance() || !event.getSource().equals(wp)) {
                            return;
                        }
                        if (tempHammerOfLight.isCrownOfLight) {
                            return;
                        }
                        event.getFlags().add(InstanceFlags.PIERCE);
                    }
                };
            }
        };

        wp.getCooldownManager().addCooldown(hammerOfLightCooldown);

        location.add(0, 1, 0);

        addSecondaryAbility(
                3,
                () -> {
                    if (!wp.isAlive() || !wp.getCooldownManager().hasCooldown(hammerOfLightCooldown)) {
                        return;
                    }
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
                                    rad / 2f,
                                    new CircumferenceEffect(Particle.SPELL).particlesPerCircumference(0.5f)
                            ).playEffects();
                        }
                    });

                    tempHammerOfLight.setCrownOfLight(true);
                    hammerOfLightCooldown.setNameAbbreviation("CROWN");

                    // prot strike energy reduction
                    for (ProtectorsStrike protectorsStrike : wp.getAbilitiesMatching(ProtectorsStrike.class)) {
                        protectorsStrike.getEnergyCost().addAdditiveModifier("Hammer of Light", -10);
                    }

                    if (pveMasterUpgrade) {
                        pulseHeal(wp, 20, 1.5, tempHammerOfLight);
                        pulseHeal(wp, 40, 2.5, tempHammerOfLight);
                        pulseHeal(wp, 60, 3.5, tempHammerOfLight);
                        pulseHeal(wp, 80, 4.5, tempHammerOfLight);
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

    private static void giveHammerOfDisillusionEffect(WarlordsEntity hammerTarget, @Nonnull WarlordsEntity wp) {
        hammerTarget.getCooldownManager().removeCooldownByName("Hammer of Disillusion");
        hammerTarget.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Hammer of Disillusion",
                null,
                HammerOfLight.class,
                null,
                wp,
                CooldownTypes.DEBUFF,
                cooldownManager -> {
                },
                20
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.1f;
            }
        });
    }

    private void pulseHeal(WarlordsEntity wp, int delay, double radiusMultiplier, HammerOfLight tempHammerOfLight) {
        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                Utils.playGlobalSound(wp.getLocation(), "warrior.revenant.orbsoflife", 2, 0.4f);
                EffectUtils.strikeLightning(wp.getLocation(), false, delay / 10);
                float rad = radius.getCalculatedValue();
                EffectUtils.playHelixAnimation(wp.getLocation(), rad * radiusMultiplier, Particle.SPELL_WITCH, 1, 20);
                new CircleEffect(
                        wp.getGame(),
                        wp.getTeam(),
                        wp.getLocation().add(0, 0.75f, 0),
                        rad * radiusMultiplier,
                        new CircumferenceEffect(Particle.SPELL).particlesPerCircumference(1)
                ).playEffects();

                for (WarlordsEntity allyTarget : PlayerFilter
                        .entitiesAround(wp.getLocation(), rad * radiusMultiplier, rad * radiusMultiplier, rad * radiusMultiplier)
                        .aliveTeammatesOf(wp)
                ) {
                    playersHealed++;
                    allyTarget.addInstance(InstanceBuilder
                            .healing()
                            .cause("Hammer of Illusion")
                            .source(wp)
                            .min(healingValues.hammerHealing.getMinValue() * 5)
                            .max(healingValues.hammerHealing.getMaxValue() * 5)
                            .critChance(20)
                            .critMultiplier(150)
                    ).ifPresent(warlordsDamageHealingFinalEvent -> {
                        tempHammerOfLight.addAmountHealed(warlordsDamageHealingFinalEvent.getValue());
                    });
                }

                for (WarlordsEntity enemyTarget : PlayerFilter
                        .entitiesAround(wp.getLocation(), rad * radiusMultiplier, rad * radiusMultiplier, rad * radiusMultiplier)
                        .aliveEnemiesOf(wp)
                ) {
                    enemyTarget.addInstance(InstanceBuilder
                            .damage()
                            .cause("Hammer of Illusion")
                            .source(wp)
                            .min(damageValues.hammerDamage.getMinValue() * 5)
                            .max(damageValues.hammerDamage.getMaxValue() * 5)
                            .critChance(20)
                            .critMultiplier(150)
                    );
                }
            }
        }.runTaskLater(delay);
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new HammerOfLightBranch(abilityTree, this);
    }

    public FloatModifiable getRadius() {
        return radius;
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

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable hammerDamage = new Value.RangedValueCritable(178, 244, 20, 175);
        private final List<Value> values = List.of(hammerDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private final Value.RangedValueCritable hammerHealing = new Value.RangedValueCritable(178, 244, 20, 175);
        private final List<Value> values = List.of(hammerHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

}