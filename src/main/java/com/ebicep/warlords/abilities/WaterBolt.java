package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.aquamancer.WaterBoltBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class WaterBolt extends AbstractProjectile<WaterBolt, WaterBolt.WaterBoltStats> implements WeaponAbilityIcon, Splash, Damages<WaterBolt.DamageValues>, Heals<WaterBolt.HealingValues> {

    private final WaterBoltStats stats = new WaterBoltStats();
    private final DamageValues damageValues = new DamageValues();
    private final HealingValues healingValues = new HealingValues();
    private int maxFullDistance = 40;
    private FloatModifiable directHitMultiplier = new FloatModifiable(15);
    private FloatModifiable splashRadius = new FloatModifiable(4.125f);

    public WaterBolt() {
        super(AbstractAbilityBuilder.create("waterBolt").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.maxFullDistance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxFullDistance"), int.class);
        this.directHitMultiplier = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                builder.getAppendedFieldName("directHitMultiplier"),
                float.class
        ));
        this.splashRadius = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("splashRadius"), float.class));
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
    }

    @Override
    protected String getActivationSound() {
        return "mage.waterbolt.activation";
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
        World world = currentLocation.getWorld();
        world.spawnParticle(Particle.DRIPPING_WATER, currentLocation, 2, 0.3, 0.3, 0.3, 0.1, null, true);
        world.spawnParticle(Particle.ENCHANT, currentLocation, 1, 0, 0, 0, 0.1, null, true);
        world.spawnParticle(Particle.HAPPY_VILLAGER, currentLocation, 1, 0, 0, 0, 0.1, null, true);
        world.spawnParticle(Particle.CLOUD, currentLocation, 1, 0, 0, 0, 0, null, true);
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        World world = currentLocation.getWorld();
        Location effectLocation = hit != null ? hit.getEyeLocation() : currentLocation;
        world.spawnParticle(Particle.HEART, effectLocation, 3, 1, 1, 1, 0.2, null, true);
        world.spawnParticle(Particle.HAPPY_VILLAGER, effectLocation, 5, 1, 1, 1, 0.2, null, true);
        Utils.playGlobalSound(effectLocation, "mage.waterbolt.impact", 2, 1);
        double distanceSquared = startingLocation.distanceSquared(effectLocation);
        float toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 : (float) (1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75);
        if (toReduceBy < .2) {
            toReduceBy = .2f;
        }
        if (hit != null && !projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            float cc = pveMasterUpgrade2 ? 100 : healingValues.boltHealing.getCritChanceValue();
            stats.addPlayersHit();
            float directHitMultiplierCalculatedValue = directHitMultiplier.getCalculatedValue();
            if (hit.isTeammate(shooter)) {
                stats.teammatesHit++;
                hit.addInstance(InstanceBuilder.healing()
                                               .ability(this)
                                               .source(shooter)
                                               .min(healingValues.boltHealing.getMinValue() * convertToMultiplicationDecimal(directHitMultiplierCalculatedValue) * toReduceBy)
                                               .max(healingValues.boltHealing.getMaxValue() * convertToMultiplicationDecimal(directHitMultiplierCalculatedValue) * toReduceBy)
                                               .critChance(cc)
                                               .critMultiplier(healingValues.boltHealing.getCritMultiplierValue())
                                               .flags(InstanceFlags.CAN_OVERHEAL_OTHERS));
                if (hit != shooter) {
                    Overheal.giveOverHeal(shooter, hit);
                }
                if (pveMasterUpgrade) {
                    increaseDamageOnHit(shooter, hit);
                }
            } else {
                stats.enemiesHit++;
                if (hit.onHorse()) {
                    stats.addNumberOfDismounts();
                }
                hit.addInstance(InstanceBuilder.damage()
                                               .ability(this)
                                               .source(shooter)
                                               .min(damageValues.boltDamage.getMinValue() * convertToMultiplicationDecimal(directHitMultiplierCalculatedValue) * toReduceBy)
                                               .max(damageValues.boltDamage.getMaxValue() * convertToMultiplicationDecimal(directHitMultiplierCalculatedValue) * toReduceBy)
                                               .critChance(cc)
                                               .critMultiplier(damageValues.boltDamage.getCritMultiplierValue()));
            }
        }
        int playersHit = 0;
        float radius = splashRadius.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter.entitiesAround(hit != null ? hit.getLocation() : currentLocation, radius, radius, radius)
                                                     .isAlive()
                                                     .excluding(projectile.getHit())) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
            playersHit++;
            stats.addPlayersHit();
            if (nearEntity.isTeammate(shooter)) {
                stats.teammatesHit++;
                nearEntity.addInstance(InstanceBuilder.healing()
                                                      .ability(this)
                                                      .source(shooter)
                                                      .min(healingValues.boltHealing.getMinValue() * toReduceBy)
                                                      .max(healingValues.boltHealing.getMaxValue() * toReduceBy)
                                                      .crit(healingValues.boltHealing)
                                                      .flags(InstanceFlags.CAN_OVERHEAL_OTHERS));
                if (nearEntity != shooter) {
                    Overheal.giveOverHeal(shooter, nearEntity);
                }
                if (pveMasterUpgrade) {
                    increaseDamageOnHit(shooter, nearEntity);
                }
            } else {
                stats.enemiesHit++;
                if (nearEntity.onHorse()) {
                    stats.addNumberOfDismounts();
                }
                nearEntity.addInstance(InstanceBuilder.damage()
                                                      .ability(this)
                                                      .source(shooter)
                                                      .min(damageValues.boltDamage.getMinValue() * toReduceBy)
                                                      .max(damageValues.boltDamage.getMaxValue() * toReduceBy)
                                                      .crit(damageValues.boltDamage));
            }
        }
        return playersHit;
    }

    private void increaseDamageOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        hit.getCooldownManager().removeCooldown(WaterBolt.class, false);
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(name, "BOLT DMG", WaterBolt.class, new WaterBolt(), giver, CooldownTypes.ABILITY, cooldownManager -> {
        }, 10 * 20
        ) {

            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * 1.1f;
            }
        });
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Shoot a bolt of water that will burst for")
                                               .damage(damageValues.boltDamage)
                                               .text(" damage and restore")
                                               .heal(healingValues.boltHealing)
                                               .text(" health to allies. A direct hit will cause ")
                                               .percent(directHitMultiplier, NamedTextColor.GREEN)
                                               .text(" increased damage or healing for the target hit.")
                                               .optimalRange(maxFullDistance)
                                               .emptyLine()
                                               .text("Water Bolt can overheal allies for up to ")
                                               .percent(10, NamedTextColor.GREEN)
                                               .text(" of their max health as bonus health for ")
                                               .durationSeconds(Overheal.OVERHEAL_DURATION)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new WaterBoltBranch(abilityTree, this);
    }

    @Override
    public FloatModifiable getSplashRadius() {
        return splashRadius;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public HealingValues getHealValues() {
        return healingValues;
    }

    @Override
    public WaterBoltStats getAbilityStats() {
        return stats;
    }

    public FloatModifiable getDirectHitMultiplier() {
        return directHitMultiplier;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable boltDamage = new Value.RangedValueCritable(231, 299, 20, 175);

        private List<Value> values = List.of(boltDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.boltDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("boltDamage"), Value.RangedValueCritable.class);
            this.values = List.of(boltDamage);
        }

        public Value.RangedValueCritable getBoltDamage() {
            return boltDamage;
        }

    }

    public static class HealingValues implements Value.ValueHolder {

        private Value.RangedValueCritable boltHealing = new Value.RangedValueCritable(315, 434, 20, 175);

        private List<Value> values = List.of(boltHealing);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.boltHealing = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameHealing("boltHealing"), Value.RangedValueCritable.class);
            this.values = List.of(boltHealing);
        }

        public Value.RangedValueCritable getBoltHealing() {
            return boltHealing;
        }

    }

    public static class WaterBoltStats extends AbstractPiercingProjectileStats<WaterBolt, WaterBoltStats> {

        @Field("teammates_hit")
        private int teammatesHit = 0;

        @Field("enemies_hit")
        private int enemiesHit = 0;

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Teammates Hit", teammatesHit));
            statsDisplay.add(new AbilityStatDisplay("Enemies Hit", enemiesHit));
            return statsDisplay;
        }

        @Override
        public WaterBoltStats merge(WaterBoltStats other, int multiplier) {
            WaterBoltStats stats = super.merge(other, multiplier);
            stats.teammatesHit = this.teammatesHit + other.teammatesHit * multiplier;
            stats.enemiesHit = this.enemiesHit + other.enemiesHit * multiplier;
            return stats;
        }

        @Override
        public Class<WaterBoltStats> getClazz() {
            return WaterBoltStats.class;
        }

        @Override
        public WaterBoltStats create() {
            return new WaterBoltStats();
        }

    }

}
