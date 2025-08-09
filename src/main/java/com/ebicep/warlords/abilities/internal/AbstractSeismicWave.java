package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class AbstractSeismicWave extends AbstractAbility implements RedAbilityIcon, AbilityStats<AbstractSeismicWave, AbstractSeismicWave.AbstractSeismicWaveStats> {

    protected float velocity = 1.25f;
    protected int waveLength = 8; // foward amount
    protected int waveWidth = 2; // sideways amount (2 => 2 to left and 2 to right)
    private final AbstractSeismicWaveStats stats = new AbstractSeismicWaveStats();

    public AbstractSeismicWave(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.velocity = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("velocity"), float.class);
        this.waveLength = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("waveLength"), int.class);
        this.waveWidth = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("waveWidth"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.seismicwave.activation", 2, 1);

        List<List<Location>> fallingBlockLocations = getWaveLocations(wp.getLocation());

        doWaveDamage(wp, fallingBlockLocations, UUID.randomUUID());

        new GameRunnable(wp.getGame()) {

            final ThreadLocalRandom random = ThreadLocalRandom.current();

            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        if (random.nextDouble() < 0.7) {
                            Utils.addFallingBlock(location);
                        }
                    }
                    fallingBlockLocations.remove(fallingBlockLocation);
                    break;
                }
                if (fallingBlockLocations.isEmpty()) {
                    this.cancel();
                }
            }
        }.runTaskTimer(0, 0);
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Send a wave of incredible force forward that deals ")
                .damage(getWaveDamage())
                .text(" damage to all enemies hit and knocks them back slightly.")
                .build();
    }

    public abstract Value.RangedValueCritable getWaveDamage();

    private List<List<Location>> getWaveLocations(Location location) {
        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        for (int i = 0; i < waveLength; i++) {
            fallingBlockLocations.add(getWaveSideLocations(location, i));
        }
        return fallingBlockLocations;
    }

    protected void doWaveDamage(@Nonnull WarlordsEntity wp, List<List<Location>> fallingBlockLocations, UUID abilityUUID) {
        List<WarlordsEntity> playersHit = new ArrayList<>();
        for (int i = 0; i < fallingBlockLocations.size(); i++) {
            List<Location> fallingBlockLocation = fallingBlockLocations.get(i);
            for (Location loc : fallingBlockLocation) {
                for (WarlordsEntity waveTarget : PlayerFilter
                        .entitiesAroundRectangle(loc, .6, 4, .6)
                        .aliveEnemiesOf(wp)
                        .excluding(playersHit)
                        .closestFirst(wp)
                ) {
                    stats.playersHit++;
                    if (waveTarget.hasFlag()) {
                        stats.carrierHit++;
                    }

                    playersHit.add(waveTarget);

                    onHit(wp, abilityUUID, i, waveTarget);
                }
            }
        }
    }

    private List<Location> getWaveSideLocations(Location center, int distance) {
        List<Location> locations = new ArrayList<>();
        Location location = new Location(center.getWorld(), center.getX(), center.getY(), center.getZ());
        location.setDirection(center.getDirection());
        location.setPitch(0);
        locations.add(location.add(location.getDirection().multiply(distance)));
        for (int i = 1; i <= waveWidth; i++) {
            locations.add(location.clone().add(LocationUtils.getLeftDirection(location).multiply(i)));
            locations.add(location.clone().add(LocationUtils.getRightDirection(location).multiply(i)));
        }
        return locations;
    }

    protected void onHit(@Nonnull WarlordsEntity wp, UUID abilityUUID, int i, WarlordsEntity waveTarget) {
    }

    @Override
    public AbstractSeismicWaveStats getAbilityStats() {
        return stats;
    }

    protected void onHitFinalEvent(@Nonnull WarlordsEntity wp, WarlordsEntity waveTarget) {
        if (waveTarget.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class) && FlagHolder.playerNearFlag(waveTarget)) {
            stats.warpsKnockbacked++;
        }

        final Vector v = wp.getLocation().toVector().subtract(waveTarget.getLocation().toVector()).normalize().multiply(-velocity).setY(0.25);
        waveTarget.setVelocity(name, v, false, false);
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public int getWaveLength() {
        return waveLength;
    }

    public void setWaveLength(int waveLength) {
        this.waveLength = waveLength;
    }

    public int getWaveWidth() {
        return waveWidth;
    }

    public void setWaveWidth(int waveWidth) {
        this.waveWidth = waveWidth;
    }

    public static class AbstractSeismicWaveStats extends AbstractAbilityStats<AbstractSeismicWave, AbstractSeismicWaveStats> {

        @Field("targets_hit")
        private int playersHit = 0;
        @Field("carrier_hit")
        private int carrierHit = 0;
        @Field("warps_knockbacked")
        private int warpsKnockbacked = 0;

        @Override
        public Class<AbstractSeismicWaveStats> getClazz() {
            return AbstractSeismicWaveStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", playersHit));
            statsDisplay.add(new AbilityStatDisplay("Carriers Hit", carrierHit));
            statsDisplay.add(new AbilityStatDisplay("Warps Knockbacked", warpsKnockbacked));
            return statsDisplay;
        }

        @Override
        public AbstractSeismicWaveStats merge(AbstractSeismicWaveStats other, int multiplier) {
            AbstractSeismicWaveStats stats = super.merge(other, multiplier);
            stats.playersHit = this.playersHit + other.playersHit * multiplier;
            stats.carrierHit = this.carrierHit + other.carrierHit * multiplier;
            stats.warpsKnockbacked = this.warpsKnockbacked + other.warpsKnockbacked * multiplier;
            return stats;
        }

        @Override
        public AbstractSeismicWaveStats create() {
            return new AbstractSeismicWaveStats();
        }

    }

}
