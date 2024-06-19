package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.FlameburstBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.Matrix4d;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
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

public class FlameBurst extends AbstractPiercingProjectile implements RedAbilityIcon, Splash, Damages<FlameBurst.DamageValues> {

    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable splash = new FloatModifiable(5);
    private double acceleration = 1.0275;
    private double projectileWidth = 0.24D;
    public FlameBurst() {
        super("Flame Burst", 557, 753, 9.4f, 60, 25, 185, 1.65, 200, false);
    }

    public FlameBurst(float minDamageHeal, int maxDamageHeal, float filler) {
        super("Flame Burst", minDamageHeal, maxDamageHeal, 9.4f, 60, 25, 185, 1.65, 200, false);
    }

    public FlameBurst(float cooldown) {
        super("Flame Burst", 557, 753, cooldown, 60, 25, 185, 1.65, 200, false);
    }

    public FlameBurst(float cooldown, int critChance) {
        super("Flame Burst", 557, 753, cooldown, 60, critChance, 185, 1.65, 200, false);
    }

    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = Component.text("Launch a flame burst that will explode for ")
                               .append(formatRangeDamage(minDamageHeal, maxDamageHeal))
                               .append(Component.text(" damage. The Crit Chance increases by "))
                               .append(Component.text("1%", NamedTextColor.RED))
                               .append(Component.text(" for each travelled block. Up to "))
                               .append(Component.text("100%", NamedTextColor.RED))
                               .append(Component.text("."));
    }

    @Override
    public List<Pair<String, String>> getAbilityInfo() {
        List<Pair<String, String>> info = new ArrayList<>();
        info.add(new Pair<>("Times Used", "" + timesUsed));
        info.add(new Pair<>("Players Hit", "" + playersHit));
        info.add(new Pair<>("Dismounts", "" + numberOfDismounts));

        return info;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FlameburstBranch(abilityTree, this);
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
        if (pveMasterUpgrade2) {
            if (ticksLived % 2 == 0) {
                return;
            }
            for (Location location : Arrays.asList(
                    currentLocation,
                    new LocationBuilder(currentLocation).backward(.25f).left(.25f),
                    new LocationBuilder(currentLocation).backward(.5f).left(.5f),
                    new LocationBuilder(currentLocation).backward(.75f).left(.75f),
                    new LocationBuilder(currentLocation).backward(.25f).right(.25f),
                    new LocationBuilder(currentLocation).backward(.5f).right(.5f),
                    new LocationBuilder(currentLocation).backward(.75f).right(.75f)
            )) {
                EffectUtils.displayParticle(
                        Particle.FLAME,
                        location,
                        5,
                        .05,
                        .05,
                        .05,
                        0
                );
            }
            return;
        }
        Matrix4d center = new Matrix4d(currentLocation);

        for (float i = 0; i < 4; i++) {
            double angle = Math.toRadians(i * 90) + ticksLived * 0.45;
            double width = projectileWidth;
            EffectUtils.displayParticle(
                    Particle.FLAME,
                    center.translateVector(currentLocation.getWorld(), 0, Math.sin(angle) * width, Math.cos(angle) * width),
                    2
            );
        }
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (pveMasterUpgrade2) {
            return 0;
        }
        Location currentLocation = projectile.getCurrentLocation();

        Utils.playGlobalSound(currentLocation, "mage.flameburst.impact", 2, 1);

        EffectUtils.displayParticle(Particle.EXPLOSION_LARGE, currentLocation, 2, 0, 0, 0, 0.5);
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
            numberOfDismounts++;
        }

        if (pveMasterUpgrade) {
            int damageIncrease = (int) Math.pow(currentLocation.distanceSquared(startingLocation), 0.685);
            nearEntity.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(shooter)
                    .min(damageValues.flameBurstDamage.getMinValue() + damageIncrease)
                    .max(damageValues.flameBurstDamage.getMaxValue() + damageIncrease)
                    .critChance(damageValues.flameBurstDamage.getCritChanceValue() + damageIncrease)
                    .critMultiplier(damageValues.flameBurstDamage.getCritMultiplierValue() + damageIncrease)
            );
        } else {
            float damageBoost = 0;
            float blocksTravelled = (float) projectile.getBlocksTravelled();
            if (pveMasterUpgrade2) {
                blocksTravelled = Math.min(30, blocksTravelled);
                damageBoost = DamageCheck.clamp(nearEntity.getMaxBaseHealth() * .01f);
            }
            nearEntity.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(shooter)
                    .min(damageValues.flameBurstDamage.getMinValue() + damageBoost)
                    .max(damageValues.flameBurstDamage.getMaxValue() + damageBoost)
                    .critChance(damageValues.flameBurstDamage.getCritChanceValue() + blocksTravelled)
                    .critMultiplier(damageValues.flameBurstDamage.getCritMultiplierValue())
            );
        }
    }

    private int hit(@Nonnull InternalProjectile projectile) {
        WarlordsEntity shooter = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();

        int playersHit = 0;
        float splashRadius = splash.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(currentLocation, splashRadius, splashRadius, splashRadius)
                .aliveEnemiesOf(shooter)
                .excluding(projectile.getHit())
        ) {
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

    public void setProjectileWidth(double projectileWidth) {
        this.projectileWidth = projectileWidth;
    }

    @Override
    public FloatModifiable getSplashRadius() {
        return splash;
    }

    public static class DamageValues implements Value.ValueHolder {

        private final Value.RangedValueCritable flameBurstDamage = new Value.RangedValueCritable(557, 753, 25, 185);
        private final List<Value> values = List.of(flameBurstDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

    }


}
