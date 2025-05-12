package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.FlameburstBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlameBurst extends AbstractPiercingProjectile<FlameBurst, FlameBurst.FlameBurstStats> implements RedAbilityIcon, Splash, Damages<FlameBurst.DamageValues> {

    private final FlameBurstStats stats = new FlameBurstStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable splash = new FloatModifiable(5.125f);
    private double acceleration = 1.0275;
    private double projectileWidth = 0.24D;

    public FlameBurst() {
        super(AbstractAbilityBuilder.create("flameBurst").pvp());
    }

    public FlameBurst(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.splash = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("splash"), float.class));
        this.acceleration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("acceleration"), float.class);
        this.projectileWidth = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("projectileWidth"), float.class);
    }

    @Override
    protected String getActivationSound() {
        return "mage.fireball.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
        if (pveMasterUpgrade2) {
            if (ticksLived % 2 == 0) {
                return;
            }
            for (Location location : Arrays.asList(currentLocation,
                    new LocationBuilder(currentLocation).backward(.25f).left(.25f),
                    new LocationBuilder(currentLocation).backward(.5f).left(.5f),
                    new LocationBuilder(currentLocation).backward(.75f).left(.75f),
                    new LocationBuilder(currentLocation).backward(.25f).right(.25f),
                    new LocationBuilder(currentLocation).backward(.5f).right(.5f),
                    new LocationBuilder(currentLocation).backward(.75f).right(.75f)
            )) {
                EffectUtils.displayParticle(Particle.FLAME, location, 5, .05, .05, .05, 0);
            }
            return;
        }
        Matrix4d center = new Matrix4d(currentLocation);
        for (float i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90) + ticksLived * 0.45;
            double width = projectileWidth;
            EffectUtils.displayParticle(Particle.FLAME, center.translateVector(currentLocation.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width), 2);
        }
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (pveMasterUpgrade2) {
            return 0;
        }
        Location currentLocation = projectile.getCurrentLocation();
        Utils.playGlobalSound(currentLocation, "mage.flameburst.impact", 2, 1);
        EffectUtils.displayParticle(Particle.EXPLOSION, currentLocation, 2, 0, 0, 0, 0.5);
        EffectUtils.displayParticle(Particle.LAVA, currentLocation, 10, 0.5F, 0, 0.5F, 2);
        EffectUtils.displayParticle(Particle.CLOUD, currentLocation, 3, 0.3F, 0.3F, 0.3F, 1);
        if (hit != null) {
            hitEntity(projectile, hit);
        }
        return hit(projectile);
    }

    private void hitEntity(@Nonnull InternalProjectile projectile, WarlordsEntity nearEntity) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
        if (nearEntity.onHorse()) {
            stats.addNumberOfDismounts();
        }
        if (pveMasterUpgrade) {
            int damageIncrease = (int) Math.pow(currentLocation.distanceSquared(startingLocation), 0.685);
            nearEntity.addInstance(InstanceBuilder.damage()
                                                  .ability(this)
                                                  .source(shooter)
                                                  .min(damageValues.flameBurstDamage.getMinValue() + damageIncrease)
                                                  .max(damageValues.flameBurstDamage.getMaxValue() + damageIncrease)
                                                  .critChance(damageValues.flameBurstDamage.getCritChanceValue() + damageIncrease)
                                                  .critMultiplier(damageValues.flameBurstDamage.getCritMultiplierValue() + damageIncrease));
        } else {
            float damageBoost = 1;
            if (pveMasterUpgrade2) {
                damageBoost += Math.min(.75f, (projectile.getHit().size() - 1) * .05f);
            }
            nearEntity.addInstance(InstanceBuilder.damage()
                                                  .ability(this)
                                                  .source(shooter)
                                                  .min(damageValues.flameBurstDamage.getMinValue() * damageBoost)
                                                  .max(damageValues.flameBurstDamage.getMaxValue() * damageBoost)
                                                  .critChance(damageValues.flameBurstDamage.getCritChanceValue())
                                                  .critMultiplier(damageValues.flameBurstDamage.getCritMultiplierValue()));
        }
    }

    private int hit(@Nonnull InternalProjectile projectile) {
        WarlordsEntity shooter = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();
        int playersHit = 0;
        float splashRadius = splash.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter.entitiesAround(currentLocation, splashRadius, splashRadius, splashRadius)
                                                     .aliveEnemiesOf(shooter)
                                                     .excluding(projectile.getHit())) {
            playersHit++;
            hitEntity(projectile, nearEntity);
        }
        return playersHit;
    }

    @Override
    protected void updateSpeed(InternalProjectile projectile) {
        int ticksLived = projectile.getTicksLived();
        Vector vector = new Vector(0, 1, 0).normalize();
        if (!pveMasterUpgrade2) {
            projectile.getSpeed().multiply(acceleration);
            return;
        }
        if (ticksLived % 2 == 0) {
            projectile.getSpeed().multiply(acceleration);
        }
        //TODO bezier curve
        if (ticksLived > 30) {
            return;
        }
        if (ticksLived > 26) {
            projectile.getSpeed().rotateAroundAxis(vector, .07);
        } else if (ticksLived > 22) {
            projectile.getSpeed().rotateAroundAxis(vector, .225);
        } else if (ticksLived > 18) {
            projectile.getSpeed().rotateAroundAxis(vector, .22);
        } else if (ticksLived > 15) {
            projectile.getSpeed().rotateAroundAxis(vector, .25);
        } else if (ticksLived > 13) {
            projectile.getSpeed().rotateAroundAxis(vector, .3);
        } else if (ticksLived > 8) {
            projectile.getSpeed().rotateAroundAxis(vector, .17);
        } else if (ticksLived == 8) {
            projectile.getSpeed().rotateAroundAxis(vector, .15);
        }
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp) {
        return !pveMasterUpgrade2;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        if (!pveMasterUpgrade2) {
            return;
        }
        hitEntity(projectile, hit);
        hit(projectile);
    }

    @Override
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        if (pveMasterUpgrade2) {
            Location location = super.modifyProjectileStartingLocation(shooter, startingLocation);
            location.setPitch(0);
            return location;
        }
        return super.modifyProjectileStartingLocation(shooter, startingLocation);
    }

    @Override
    protected Vector getProjectileStartingSpeed(WarlordsEntity shooter, Location startingLocation) {
        if (pveMasterUpgrade2) {
            Vector vector = super.getProjectileStartingSpeed(shooter, startingLocation);
            vector.setY(0);
            return vector.normalize();
        }
        return super.getProjectileStartingSpeed(shooter, startingLocation);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Launch a flame burst that will explode for ")
                                               .damage(damageValues.flameBurstDamage)
                                               .text(" damage. The Crit Chance increases by ")
                                               .percent(1, NamedTextColor.RED)
                                               .text(" for each travelled block. Up to ")
                                               .percent(100, NamedTextColor.RED)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FlameburstBranch(abilityTree, this);
    }

    @Override
    public FloatModifiable getSplashRadius() {
        return splash;
    }

    @Override
    public FlameBurstStats getAbilityStats() {
        return stats;
    }

    public void setProjectileWidth(double projectileWidth) {
        this.projectileWidth = projectileWidth;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable flameBurstDamage = new Value.RangedValueCritable(557, 753, 25, 185);

        private final List<Value> values = List.of(flameBurstDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.flameBurstDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("flameBurstDamage"), Value.RangedValueCritable.class);
        }

        public Value.RangedValueCritable getFlameBurstDamage() {
            return flameBurstDamage;
        }

    }

    public static class FlameBurstStats extends AbstractPiercingProjectileStats<FlameBurst, FlameBurstStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public FlameBurstStats merge(FlameBurstStats other, int multiplier) {
            FlameBurstStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<FlameBurstStats> getClazz() {
            return FlameBurstStats.class;
        }

        @Override
        public FlameBurstStats create() {
            return new FlameBurstStats();
        }

    }

}
