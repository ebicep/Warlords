package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.effects.ChasingBlockEffect;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.earthwarden.EarthenSpikeBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class EarthenSpike extends AbstractAbility implements WeaponAbilityIcon, HitBox, Damages<EarthenSpike.DamageValues>, AbilityStats<EarthenSpike, EarthenSpike.EarthenSpikeStats> {

    public static final Map<UUID, Long> PLAYER_SPIKE_COOLDOWN = new HashMap<>();
    private static final String[] REPEATING_SOUND = new String[]{
            "shaman.earthenspike.animation.a",
            "shaman.earthenspike.animation.b",
            "shaman.earthenspike.animation.c",
            "shaman.earthenspike.animation.d",
    };


    private final DamageValues damageValues = new DamageValues();
    private final EarthenSpikeStats stats = new EarthenSpikeStats();
    private FloatModifiable radius = new FloatModifiable(10);
    private float speed = 1;
    private double spikeHitbox = 2.5;
    private double verticalVelocity = .625;

    public EarthenSpike() {
        this(0, 0);
    }

    public EarthenSpike(float cooldown, float startCooldown) {
        super("Earthen Spike", cooldown, 100, startCooldown);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Send forth an underground earth spike that locks onto a targeted enemy player. When the spike reaches its target it emerges from the ground, dealing ")
                .damage(damageValues.spikeDamage)
                .text(" damage to any nearby enemies and launches them up into the air.")
                .initialRange(radius)
                .build();

    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        List<WarlordsEntity> spiked = new ArrayList<>();
        float rad = radius.getCalculatedValue();
        for (WarlordsEntity spikeTarget : PlayerFilter
                .entitiesAround(wp, rad, rad, rad)
                .aliveEnemiesOf(wp)
                .lookingAtFirst(wp)
        ) {
            if (!LocationUtils.isLookingAt(wp, spikeTarget) || !LocationUtils.hasLineOfSight(wp, spikeTarget)) {
                continue;
            }

            spiked.add(spikeTarget);
            spikeTarget(wp, spikeTarget);

            break;
        }
        return !spiked.isEmpty();
    }

    protected void spikeTarget(@Nonnull WarlordsEntity wp, WarlordsEntity spikeTarget) {
        Location location = wp.getLocation();

        new ChasingBlockEffect.Builder()
                .setGame(wp.getGame())
                .setSpeed(speed)
                .setDestination(() -> spikeTarget.isDead() ? null : spikeTarget.getLocation())
                .setOnTick(ticksElapsed -> {
                    if (ticksElapsed % 5 == 1) {
                        Utils.playGlobalSound(location, REPEATING_SOUND[(ticksElapsed / 5) % 4], 2, 1);
                    }
                })
                .setOnDestinationReached(() -> {
                    Location targetLocation = spikeTarget.getLocation();
                    if (pveMasterUpgrade2) {
                        onSpikeTarget(wp, spikeTarget);
                    } else {
                        for (WarlordsEntity nearSpikeTarget : PlayerFilter
                                .entitiesAround(targetLocation, spikeHitbox, spikeHitbox, spikeHitbox)
                                .aliveEnemiesOf(wp)
                        ) {
                            onSpikeTarget(wp, nearSpikeTarget);
                        }
                    }

                    if (pveMasterUpgrade) {
                        new GameRunnable(wp.getGame()) {
                            @Override
                            public void run() {
                                new FallingBlockWaveEffect(targetLocation.add(0, 1, 0), 4, 0.9, Material.DIRT).play();
                                for (WarlordsEntity wave : PlayerFilter
                                        .entitiesAround(targetLocation, 6, 6, 6)
                                        .aliveEnemiesOf(wp)
                                ) {
                                    wave.addInstance(InstanceBuilder
                                            .damage()
                                            .cause("Earthen Rupture")
                                            .source(wp)
                                            .value(damageValues.earthenRuptureDamage)
                                    );
                                    wave.addSpeedModifier(wp, "Spike Slow", -35, 20);
                                }
                                Utils.playGlobalSound(targetLocation, Sound.BLOCK_GRAVEL_BREAK, 2, 0.5f);
                                targetLocation.getWorld().spawnParticle(
                                        Particle.EXPLOSION_LARGE,
                                        targetLocation,
                                        2,
                                        1,
                                        1,
                                        1,
                                        0.01F,
                                        null,
                                        true
                                );
                            }
                        }.runTaskLater(15);
                    }

                    Utils.playGlobalSound(wp.getLocation(), "shaman.earthenspike.impact", 2, 1);

                    targetLocation.setYaw(0);
                    for (int i = 0; i < 100; i++) {
                        if (targetLocation.clone().add(0, -1, 0).getBlock().getType() == Material.AIR) {
                            targetLocation.add(0, -1, 0);
                        } else {
                            break;
                        }
                    }

                    ArmorStand stand = Utils.spawnArmorStand(targetLocation.add(0, -.6, 0), armorStand -> {
                        armorStand.getEquipment().setHelmet(new ItemStack(Material.BROWN_MUSHROOM));
                        armorStand.setMarker(true);
                    });

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            stand.remove();
                            this.cancel();
                        }

                    }.runTaskTimer(Warlords.getInstance(), 10, 0);
                })
                .setMaxTicks(30)
                .create()
                .start(new LocationBuilder(location).y(location.getBlockY()));
    }

    protected void onSpikeTarget(WarlordsEntity caster, WarlordsEntity spikeTarget) {
        stats.targetsSpiked++;
        if (spikeTarget.hasFlag()) {
            stats.carrierSpiked++;
        }
        spikeTarget.addInstance(InstanceBuilder
                .damage()
                .ability(this)
                .source(caster)
                .value(damageValues.spikeDamage)
        ).ifPresent(finalEvent -> {
            if (!pveMasterUpgrade2) {
                return;
            }
            if (!finalEvent.isDead()) {
                return;
            }
            float healing = finalEvent.getValue() * .35f;
            caster.addInstance(InstanceBuilder
                    .healing()
                    .cause("Earthen Verdancy")
                    .source(caster)
                    .value(healing)
                    .showAsCrit(finalEvent.isCrit())
                    .flags(InstanceFlags.IGNORE_CRIT_MODIFIERS)
            );
            if (finalEvent.isCrit()) {
                caster.addEnergy(caster, "Earthen Verdancy", 10);
            }
        });
        if (LocationUtils.getDistance(spikeTarget.getEntity(),
                .1
        ) < 1.82 && (PLAYER_SPIKE_COOLDOWN.get(spikeTarget.getUuid()) == null || PLAYER_SPIKE_COOLDOWN.get(spikeTarget.getUuid()) + 750 < System.currentTimeMillis())) {
            PLAYER_SPIKE_COOLDOWN.put(spikeTarget.getUuid(), System.currentTimeMillis());
            spikeTarget.setVelocity(name, new Vector(0, verticalVelocity, 0), false);
        }
        if (pveMasterUpgrade2) {
            spikeTarget.getCooldownManager().removeCooldownByName("Earthen Verdancy");
            CripplingStrike.cripple(caster, spikeTarget, "Earthen Verdancy", 5 * 20);
        }
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new EarthenSpikeBranch(abilityTree, this);
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public double getSpikeHitbox() {
        return spikeHitbox;
    }

    public void setSpikeHitbox(double spikeHitbox) {
        this.spikeHitbox = spikeHitbox;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return radius;
    }

    @Override
    public EarthenSpikeStats getAbilityStats() {
        return stats;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable spikeDamage = new Value.RangedValueCritable(404, 562, 15, 175);
        private final Value.RangedValue earthenRuptureDamage = new Value.RangedValue(548, 695);
        private final List<Value> values = List.of(spikeDamage, earthenRuptureDamage);

        public Value.RangedValueCritable getSpikeDamage() {
            return spikeDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

    }

    public static class EarthenSpikeStats extends AbstractAbilityStats<EarthenSpike, EarthenSpikeStats> {

        @Field("targets_spiked")
        private int targetsSpiked = 0;
        @Field("carrier_spiked")
        private int carrierSpiked = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Spiked", targetsSpiked));
            statsDisplay.add(new AbilityStatDisplay("Times Carrier Spiked", carrierSpiked));
            return statsDisplay;
        }

        @Override
        public EarthenSpikeStats merge(EarthenSpikeStats other, int multiplier) {
            EarthenSpikeStats stats = super.merge(other, multiplier);
            stats.targetsSpiked = this.targetsSpiked + other.targetsSpiked * multiplier;
            stats.carrierSpiked = this.carrierSpiked + other.carrierSpiked * multiplier;
            return stats;
        }

        @Override
        public Class<EarthenSpikeStats> getClazz() {
            return EarthenSpikeStats.class;
        }

        @Override
        public EarthenSpikeStats create() {
            return new EarthenSpikeStats();
        }
    }
}