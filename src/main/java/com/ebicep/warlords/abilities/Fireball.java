package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsApplyBurnEffectEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.FireballBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Fireball extends AbstractProjectile<Fireball, Fireball.FireballStats> implements WeaponAbilityIcon, Splash, Damages<Fireball.DamageValues> {

    private final FireballStats stats = new FireballStats();
    private final DamageValues damageValues = new DamageValues();
    private int maxFullDistance = 50;
    private float directHitMultiplier = 15;

    private FloatModifiable splashRadius = new FloatModifiable(4.125f);

    public Fireball() {
        this(AbstractAbilityBuilder.create("fireball").pvp());
    }

    public Fireball(AbstractAbilityBuilder builder) {
        super(builder);
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .2f);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.maxFullDistance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxFullDistance"), int.class);
        this.directHitMultiplier = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("directHitMultiplier"), float.class);
        this.splashRadius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("splashRadius"), float.class));
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
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
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        EffectUtils.displayParticle(Particle.DRIPPING_LAVA, currentLocation, 5, 0, 0, 0, 0.35);
        EffectUtils.displayParticle(Particle.SMOKE, currentLocation, 7, 0, 0, 0, 0.001);
        EffectUtils.displayParticle(Particle.FLAME, currentLocation, 1, 0, 0, 0, 0.06);
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        Location effectLocation = hit != null ? hit.getEyeLocation() : currentLocation;
        Utils.playGlobalSound(effectLocation, "mage.fireball.impact", 2, 1);
        EffectUtils.displayParticle(Particle.EXPLOSION, effectLocation, 1, 0, 0, 0, 0.35);
        EffectUtils.displayParticle(Particle.LAVA, effectLocation, 10, 0.5F, 0, 0.5F, 1.5);
        EffectUtils.displayParticle(Particle.CLOUD, effectLocation, 3, 0.3F, 0.3F, 0.3F, 1);

        double distanceSquared = startingLocation.distanceSquared(effectLocation);
        float toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 : (float) (1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75);
        if (toReduceBy < .2) {
            toReduceBy = .2f;
        }

        if (hit != null && !projectile.getHit().contains(hit)) {

            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            if (hit.onHorse()) {
                stats.addNumberOfDismounts();
            }

            hit.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(shooter)
                    .min(damageValues.fireballDamage.getMinValue() * convertToMultiplicationDecimal(directHitMultiplier))
                    .max(damageValues.fireballDamage.getMaxValue() * convertToMultiplicationDecimal(directHitMultiplier))
                    .crit(damageValues.fireballDamage)
                    .flags(InstanceFlags.DIRECT_HIT)
            );

            if (pveMasterUpgrade) {
                applyBurnEffect(hit, shooter);
            } else if (pveMasterUpgrade2) {
                applyIgniteEffect(shooter, hit);
            }
        }

        int playersHit = 0;
        float radius = splashRadius.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter
                .entitiesAround(hit != null ? hit.getLocation() : currentLocation, radius, radius, radius)
                .aliveEnemiesOf(shooter)
                .excluding(projectile.getHit())
        ) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
            playersHit++;

            if (nearEntity.onHorse()) {
                stats.addNumberOfDismounts();
            }

            nearEntity.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(shooter)
                    .min(damageValues.fireballDamage.getMinValue() * toReduceBy)
                    .max(damageValues.fireballDamage.getMaxValue() * toReduceBy)
                    .crit(damageValues.fireballDamage)
            );
        }
        return playersHit;
    }

    private void applyBurnEffect(@Nonnull WarlordsEntity hit, WarlordsEntity shooter) {
        WarlordsApplyBurnEffectEvent applyBurnEffectEvent = new WarlordsApplyBurnEffectEvent(hit, shooter, 20);
        Bukkit.getPluginManager().callEvent(applyBurnEffectEvent);
        hit.getCooldownManager().removeCooldownByName("Burn");
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Burn",
                "BRN",
                Fireball.class,
                new Fireball(),
                shooter,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {},
                5 * 20,
                Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                    if (ticksLeft % applyBurnEffectEvent.getTickPeriod() == 0) {
                        float healthDamage = hit.getMaxHealth() * 0.005f;
                        healthDamage = DamageCheck.clamp(healthDamage);
                        hit.addInstance(InstanceBuilder
                                .damage()
                                .cause("Burn")
                                .source(shooter)
                                .value(healthDamage)
                                .flags(InstanceFlags.DOT, InstanceFlags.IGNORE_SOURCE_DAMAGE_BOOST)
                        );
                    }
                })
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.15f;
            }
        });
    }

    private void applyIgniteEffect(WarlordsEntity giver, WarlordsEntity hit) {
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Ignite",
                "IGN",
                Fireball.class,
                new Fireball(),
                giver,
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                        PlayerFilter.entitiesAround(hit, 3, 3, 3)
                                .aliveTeammatesOf(hit)
                                .forEach(warlordsEntity -> {
                                        warlordsEntity.addInstance(InstanceBuilder
                                                .damage().cause("Ignite")
                                                .source(giver)
                                                .value(damageValues.igniteDamage)
                                                .flags(InstanceFlags.TRUE_DAMAGE)
                                        );
                        });
                },
                20
        ));
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Shoot a fireball that will explode for ")
                                               .damage(damageValues.fireballDamage)
                                               .text(" damage. A direct hit will cause the enemy to take an additional ")
                                               .percent(directHitMultiplier, NamedTextColor.RED)
                                               .text(" extra damage.")
                                               .optimalRange(maxFullDistance)
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FireballBranch(abilityTree, this);
    }

    @Override
    public FloatModifiable getSplashRadius() {
        return splashRadius;
    }

    @Override
    public FireballStats getAbilityStats() {
        return stats;
    }

    public int getMaxFullDistance() {
        return maxFullDistance;
    }

    public void setMaxFullDistance(int maxFullDistance) {
        this.maxFullDistance = maxFullDistance;
    }

    public float getDirectHitMultiplier() {
        return directHitMultiplier;
    }

    public void setDirectHitMultiplier(float directHitMultiplier) {
        this.directHitMultiplier = directHitMultiplier;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable fireballDamage = new Value.RangedValueCritable(334, 433, 20, 175);

        private Value.RangedValue igniteDamage = new Value.RangedValue(450, 650);

        private List<Value> values = List.of(fireballDamage, igniteDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.fireballDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("fireballDamage"),
                    Value.RangedValueCritable.class
            );
            this.igniteDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("igniteDamage"), Value.RangedValue.class);
            this.values = List.of(fireballDamage, igniteDamage);
        }

        public Value.RangedValueCritable getFireballDamage() {
            return fireballDamage;
        }

    }

    public static class FireballStats extends AbstractPiercingProjectileStats<Fireball, FireballStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public FireballStats merge(FireballStats other, int multiplier) {
            FireballStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<FireballStats> getClazz() {
            return FireballStats.class;
        }

        @Override
        public FireballStats create() {
            return new FireballStats();
        }

    }

}
