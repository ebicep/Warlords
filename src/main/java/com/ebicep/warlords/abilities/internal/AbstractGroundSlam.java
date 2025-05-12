package com.ebicep.warlords.abilities.internal;

import com.ebicep.warlords.abilities.internal.icon.PurpleAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class AbstractGroundSlam extends AbstractAbility implements PurpleAbilityIcon, HitBox, AbilityStats<AbstractGroundSlam, AbstractGroundSlam.AbstractGroundSlamStats> {

    protected boolean trueDamage = false;
    private final AbstractGroundSlamStats stats = new AbstractGroundSlamStats();
    private final FloatModifiable slamSize = new FloatModifiable(6);
    private float velocity = 1.25f;

    public AbstractGroundSlam(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.velocity = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("velocity"), float.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder
                .create("Slam the ground, creating a shockwave around you that deals ")
                .damage(getSlamDamage())
                .text(" damage and knocks enemies back slightly.")
                .build();

    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.groundslam.activation", 2, 1);

        UUID abilityUUID = UUID.randomUUID();
        activateAbility(wp, 1, abilityUUID, false);

        if (pveMasterUpgrade || pveMasterUpgrade2) {
            wp.setVelocity(name, new Vector(0, 1.2, 0), true);
            new GameRunnable(wp.getGame()) {
                boolean wasOnGround = true;
                int counter = 0;

                @Override
                public void run() {
                    counter++;
                    // if player never lands in the span of 10 seconds, remove damage.
                    if (counter == 200 || wp.isDead()) {
                        this.cancel();
                    }

                    boolean hitGround = wp.getEntity().isOnGround();

                    if (wasOnGround && !hitGround) {
                        wasOnGround = false;
                    }

                    if (!wasOnGround && hitGround) {
                        wasOnGround = true;

                        Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_IRON_GOLEM_DEATH, 2, 0.2f);
                        Utils.playGlobalSound(wp.getLocation(), "warrior.groundslam.activation", 2, 0.8f);
                        activateAbility(wp, pveMasterUpgrade ? 1.5f : 1f, abilityUUID, true);
                        this.cancel();
                    }
                }
            }.runTaskTimer(0, 0);
        }
        return true;
    }

    protected void activateAbility(@Nonnull WarlordsEntity wp, float damageMultiplier, UUID abilityUUID, boolean second) {
        List<List<Location>> fallingBlockLocations = new ArrayList<>();
        Set<WarlordsEntity> currentPlayersHit = new HashSet<>();
        Location location = wp.getLocation();

        float radius = slamSize.getCalculatedValue();
        for (int i = 0; i < radius; i++) {
            fallingBlockLocations.add(LocationUtils.getCircle(location, i, (i * ((int) (Math.PI * 2)))));
        }

        fallingBlockLocations.get(0).add(wp.getLocation());

        new GameRunnable(wp.getGame()) {
            @Override
            public void run() {
                for (List<Location> fallingBlockLocation : fallingBlockLocations) {
                    for (Location location : fallingBlockLocation) {
                        Utils.addFallingBlock(location);
                        // Damage
                        for (WarlordsEntity slamTarget : PlayerFilter
                                .entitiesAroundRectangle(location.clone().add(0, -.75, 0), 0.75, 4.5, 0.75)
                                .aliveEnemiesOf(wp)
                                .excluding(currentPlayersHit)
                        ) {
                            stats.playersHit++;
                            if (slamTarget.hasFlag()) {
                                stats.carrierHit++;
                            }

                            if (slamTarget.getCooldownManager().hasCooldownExtends(AbstractTimeWarp.class) && FlagHolder.playerNearFlag(slamTarget)) {
                                stats.warpsKnockbacked++;
                            }

                            currentPlayersHit.add(slamTarget);
                            final Location loc = slamTarget.getLocation();
                            final Vector v = wp.getLocation().toVector().subtract(loc.toVector()).normalize().multiply(-velocity).setY(0.25);
                            slamTarget.setVelocity(name, v, false, false);
                            Value.RangedValueCritable slamDamage = getSlamDamage();
                            slamTarget.addInstance(InstanceBuilder
                                    .damage()
                                    .ability(AbstractGroundSlam.this)
                                    .source(wp)
                                    .min(slamDamage.getMinValue() * damageMultiplier)
                                    .max(slamDamage.getMaxValue() * damageMultiplier)
                                    .crit(slamDamage)
                                    .flag(InstanceFlags.TRUE_DAMAGE, trueDamage)
                                    .uuid(abilityUUID)
                            );
                        }
                    }

                    fallingBlockLocations.remove(fallingBlockLocation);
                    break;
                }

                if (fallingBlockLocations.isEmpty()) {
                    if (second) {
                        onSecondSlamHit(wp, currentPlayersHit);
                    }
                    this.cancel();
                }
            }

        }.runTaskTimer(0, 2);
    }

    protected void onSecondSlamHit(WarlordsEntity wp, Set<WarlordsEntity> playersHit) {

    }

    @Override
    public void runEveryTick(@Nullable WarlordsEntity warlordsEntity) {
        slamSize.tick();
        super.runEveryTick(warlordsEntity);
    }

    public abstract Value.RangedValueCritable getSlamDamage();

    @Override
    public FloatModifiable getHitBoxRadius() {
        return slamSize;
    }

    @Override
    public AbstractGroundSlamStats getAbilityStats() {
        return stats;
    }

    public float getVelocity() {
        return velocity;
    }

    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }

    public void setTrueDamage(boolean trueDamage) {
        this.trueDamage = trueDamage;
    }

    public static class AbstractGroundSlamStats extends AbstractAbilityStats<AbstractGroundSlam, AbstractGroundSlamStats> {

        @Field("targets_hit")
        private int playersHit = 0;
        @Field("carrier_hit")
        private int carrierHit = 0;
        @Field("warps_knockbacked")
        private int warpsKnockbacked = 0;

        @Override
        public Class<AbstractGroundSlamStats> getClazz() {
            return AbstractGroundSlamStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", playersHit));
            statsDisplay.add(new AbilityStatDisplay("Carrier Hit", carrierHit));
            statsDisplay.add(new AbilityStatDisplay("Warps Knockbacked", warpsKnockbacked));
            return statsDisplay;
        }

        @Override
        public AbstractGroundSlamStats merge(AbstractGroundSlamStats other, int multiplier) {
            AbstractGroundSlamStats stats = super.merge(other, multiplier);
            stats.playersHit = this.playersHit + other.playersHit * multiplier;
            stats.carrierHit = this.carrierHit + other.carrierHit * multiplier;
            stats.warpsKnockbacked = this.warpsKnockbacked + other.warpsKnockbacked * multiplier;
            return stats;
        }

        @Override
        public AbstractGroundSlamStats create() {
            return new AbstractGroundSlamStats();
        }

    }

}
