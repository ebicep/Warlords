package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.EarthenSpike;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.ChasingBlockEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsAbilityActivateEvent;
import com.ebicep.warlords.player.general.AbstractPlayerClass;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class AcceleratedSpike implements SpecBoostManager.SpecBoost<AcceleratedSpike> {

    private static final String[] REPEATING_SOUND = new String[]{
            "shaman.earthenspike.animation.a",
            "shaman.earthenspike.animation.b",
            "shaman.earthenspike.animation.c",
            "shaman.earthenspike.animation.d"
    };

    private int maxEnergyIncrease;
    private float travelSpeedIncreasePercent;
    private float castRangeIncrease;
    private float torpedoHitRadius;
    private float passThroughKnockbackHorizontal;
    private float passThroughKnockbackY;
    private float impactKnockbackHorizontal;
    private float impactKnockbackY;

    @Override
    public void init() {
        this.maxEnergyIncrease = getValue("maxEnergyIncrease", int.class);
        this.travelSpeedIncreasePercent = getValue("travelSpeedIncreasePercent", float.class);
        this.castRangeIncrease = getValue("castRangeIncrease", float.class);
        this.torpedoHitRadius = getValue("torpedoHitRadius", float.class);
        this.passThroughKnockbackHorizontal = getValue("passThroughKnockbackHorizontal", float.class);
        this.passThroughKnockbackY = getValue("passThroughKnockbackY", float.class);
        this.impactKnockbackHorizontal = getValue("impactKnockbackHorizontal", float.class);
        this.impactKnockbackY = getValue("impactKnockbackY", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "acceleratedSpike";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                maxEnergyIncrease,
                travelSpeedIncreasePercent,
                castRangeIncrease,
                torpedoHitRadius
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public AcceleratedSpike get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getEnergy().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", maxEnergyIncrease);
            warlordsPlayer.getAbilitiesMatching(EarthenSpike.class).forEach(earthenSpike -> {
                earthenSpike.setSpeed(earthenSpike.getSpeed() * AbstractAbility.convertToMultiplicationDecimal(travelSpeedIncreasePercent));
                earthenSpike.getHitBoxRadius().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", castRangeIncrease);
            });
        }

        @EventHandler
        public void onWarlordsAbilityActivatePreEvent(WarlordsAbilityActivateEvent.Pre event) {
            if (!warlordsEntity.equals(event.getWarlordsEntity())) {
                return;
            }
            if (!(event.getAbility() instanceof EarthenSpike spike)) {
                return;
            }
            event.setCancelled(true);
            Optional<WarlordsEntity> target = spike.findSpikeTarget(warlordsEntity);
            if (target.isEmpty()) {
                return;
            }
            fireTorpedo(warlordsEntity, spike, target.get());
            completeActivation(warlordsEntity, event.getPlayer(), spike, event.getSlot());
        }

        private void fireTorpedo(WarlordsEntity caster, EarthenSpike spike, WarlordsEntity spikeTarget) {
            Location startLocation = caster.getLocation();
            UUID spikeUuid = UUID.randomUUID();
            Set<UUID> hitEntities = new HashSet<>();
            final Vector[] travelDirection = {new Vector(0, 0, 1)};
            Value.RangedValueCritable spikeDamage = spike.getDamageValues().getSpikeDamage();
            float halfMin = spikeDamage.getMinValue() * 0.5f;
            float halfMax = spikeDamage.getMaxValue() * 0.5f;
            new ChasingBlockEffect.Builder()
                    .setGame(caster.getGame())
                    .setSpeed(spike.getSpeed())
                    .setDestination(() -> spikeTarget.isDead() ? null : spikeTarget.getLocation())
                    .setOnTick((ticksElapsed, currentLocation) -> {
                        if (ticksElapsed % 5 == 1) {
                            Utils.playGlobalSound(startLocation, REPEATING_SOUND[(ticksElapsed / 5) % 4], 2, 1);
                        }
                        Vector travelDir = spikeTarget.getLocation().toVector().subtract(currentLocation.toVector());
                        travelDir.setY(0);
                        if (travelDir.lengthSquared() > 0) {
                            travelDir.normalize();
                            travelDirection[0] = travelDir;
                        }
                        for (WarlordsEntity enemy : PlayerFilter
                                .entitiesAround(currentLocation, torpedoHitRadius, torpedoHitRadius, torpedoHitRadius)
                                .aliveEnemiesOf(caster)
                        ) {
                            if (enemy.equals(spikeTarget)) {
                                continue;
                            }
                            if (!hitEntities.add(enemy.getUuid())) {
                                continue;
                            }
                            enemy.addInstance(InstanceBuilder
                                    .damage().ability(spike)
                                    .source(caster)
                                    .value(halfMin, halfMax)
                                    .critChance(spikeDamage.getCritChanceValue())
                                    .critMultiplier(spikeDamage.getCritMultiplierValue())
                                    .uuid(spikeUuid)
                            );
                            applyTorpedoKnockback(caster, enemy, travelDirection[0], currentLocation, passThroughKnockbackHorizontal, passThroughKnockbackY, true);
                        }
                    })
                    .setOnDestinationReached(() -> {
                        spike.applySpikeDamageOnly(caster, spikeTarget, spikeUuid);
                        applyTorpedoKnockback(caster, spikeTarget, travelDirection[0], null, impactKnockbackHorizontal, impactKnockbackY, false);
                        spike.playSpikeImpactEffects(caster, spikeTarget.getLocation());
                    })
                    .setMaxTicks(30)
                    .create()
                    .start(new LocationBuilder(startLocation).y(startLocation.getBlockY()));
        }

        private void applyTorpedoKnockback(WarlordsEntity caster, WarlordsEntity target, Vector travelDir, Location torpedoLocation, float horizontal, float y, boolean perpendicular) {
            Vector flat = travelDir.clone();
            flat.setY(0);
            if (flat.lengthSquared() == 0) {
                return;
            }
            flat.normalize();
            Vector knockbackDir;
            if (perpendicular) {
                Vector toEnemy = target.getLocation().toVector().subtract(torpedoLocation.toVector());
                toEnemy.setY(0);
                double along = toEnemy.dot(flat);
                Vector perp = toEnemy.clone().subtract(flat.clone().multiply(along));
                if (perp.lengthSquared() < 1e-6) {
                    perp = new Vector(-flat.getZ(), 0, flat.getX());
                }
                knockbackDir = perp.normalize();
            } else {
                knockbackDir = flat;
            }
            Vector v = knockbackDir.multiply(horizontal).setY(y);
            new GameRunnable(caster.getGame()) {
                @Override
                public void run() {
                    target.setVelocity(getStringName(), v, false);
                }
            }.runTaskLater(1);
        }

        private void completeActivation(WarlordsEntity wp, Player player, EarthenSpike ability, int slot) {
            WarlordsAbilityActivateEvent.Post post = new WarlordsAbilityActivateEvent.Post(wp, player, ability, slot);
            Bukkit.getPluginManager().callEvent(post);

            wp.subtractEnergy(ability.getName(), ability.getEnergyCostValue(), false);
            ability.getAbilityStats().addTimesUsed();
            if (!wp.isDisableCooldowns() || !ability.getSecondaryAbilities().isEmpty()) {
                ability.useAbility();
            }
            if (player != null) {
                AbstractPlayerClass.sendRightClickPacket(player);
            }
            WarlordsAbilityActivateEvent.PostApply postApply = new WarlordsAbilityActivateEvent.PostApply(wp, player, ability, slot);
            Bukkit.getPluginManager().callEvent(postApply);
        }

    }

}
