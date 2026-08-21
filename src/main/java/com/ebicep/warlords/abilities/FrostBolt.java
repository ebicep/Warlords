package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.FrostboltBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class FrostBolt extends AbstractPiercingProjectile<FrostBolt, FrostBolt.FrostBoltStats> implements WeaponAbilityIcon, Splash, Damages<FrostBolt.DamageValues> {

    private final FrostBoltStats stats = new FrostBoltStats();
    private final DamageValues damageValues = new DamageValues();
    private int maxFullDistance = 30;
    private float directHitMultiplier = 15;
    private FloatModifiable splash = new FloatModifiable(4.125f);
    private int slowDuration = 2;
    private int slowness = 30;
    private int directHitAdditionalSlowness = 0;

    public FrostBolt() {
        super(AbstractAbilityBuilder.create("frostBolt").pvp());
        this.hitboxInflation.setBaseValue(hitboxInflation.getBaseValue() + .2f);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.maxFullDistance = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxFullDistance"), int.class);
        this.directHitMultiplier = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("directHitMultiplier"), float.class);
        this.splash = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("splash"), float.class));
        this.slowDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slowDuration"), int.class);
        this.slowness = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slowness"), int.class);
        this.directHitAdditionalSlowness = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("directHitAdditionalSlowness"), int.class);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        this.playEffect(projectile);
        if (!pveMasterUpgrade2) {
            return;
        }
        List<ArmorStand> icicles = new ArrayList<>();
        LocationBuilder startLocation = new LocationBuilder(projectile.getStartingLocation().clone().add(0, -1.1, 0));
        for (int i = 0; i < 4; i++) {
            icicles.add(Utils.spawnArmorStand(startLocation, armorStand -> {
                        armorStand.setMarker(true);
                        armorStand.setSmall(true);
                        armorStand.getEquipment().setHelmet(new ItemStack(Material.ICE));
                        armorStand.setHeadPose(new EulerAngle(-Math.atan2(projectile.getSpeed().getY(),
                                Math.sqrt(Math.pow(projectile.getSpeed().getX(), 2) + Math.pow(projectile.getSpeed().getZ(), 2))
                        ), 0, Math.toRadians(45)
                        ));
                    }
            ));
            startLocation.forward(.75);
        }
        projectile.addTask(new InternalProjectileTask() {

            @Override
            public void run(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                for (int i = 0; i < icicles.size(); i++) {
                    ArmorStand armorStand = icicles.get(i);
                    LocationBuilder location = new LocationBuilder(projectile.getCurrentLocation().clone().add(0, -1.1, 0));
                    location.forward(.75 * i);
                    armorStand.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }

            @Override
            public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                icicles.forEach(Entity::remove);
                EffectUtils.displayParticle(Particle.CLOUD, icicles.get(3).getLocation(), 10, 0.2, 0.2, 0.2, 0);
            }
        });
    }

    @Override
    protected String getActivationSound() {
        return "mage.frostbolt.activation";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return pveMasterUpgrade2 ? 2f : 1;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int animationTimer) {
        if (pveMasterUpgrade2) {
            return;
        }
        EffectUtils.displayParticle(Particle.CLOUD, currentLocation, 1);
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (pveMasterUpgrade2) {
            return 0;
        }
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        Location effectLocation = hit != null ? hit.getEyeLocation() : currentLocation;
        Utils.playGlobalSound(effectLocation, "mage.frostbolt.impact", 2, 1);
        EffectUtils.displayParticle(Particle.EXPLOSION, effectLocation, 1);
        EffectUtils.displayParticle(Particle.CLOUD, effectLocation, 3, 0.3, 0.3, 0.3, 1);
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
            hit.addSpeedModifier(shooter, "Frostbolt", -(slowness + directHitAdditionalSlowness), slowDuration * 20);
            hit.addInstance(InstanceBuilder.damage()
                    .ability(this)
                    .source(shooter)
                    .min(damageValues.boltDamage.getMinValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy)
                    .max(damageValues.boltDamage.getMaxValue() * convertToMultiplicationDecimal(directHitMultiplier) * toReduceBy)
                    .crit(damageValues.boltDamage)
                    .flags(InstanceFlags.DIRECT_HIT));
            if (pveMasterUpgrade) {
                freezeExplodeOnHit(shooter, hit);
            }
        }
        int playersHit = 0;
        float splashRadius = splash.getCalculatedValue();
        for (WarlordsEntity nearEntity : PlayerFilter.entitiesAround(hit != null ? hit.getLocation() : currentLocation, splashRadius, splashRadius, splashRadius)
                .aliveEnemiesOf(shooter)
                .excluding(projectile.getHit())) {
            playersHit = hit(projectile, shooter, toReduceBy, playersHit, nearEntity);
        }
        return playersHit;
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
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        double distanceSquared = currentLocation.distanceSquared(startingLocation);
        float toReduceBy = maxFullDistance * maxFullDistance > distanceSquared ? 1 : (float) (1 - (Math.sqrt(distanceSquared) - maxFullDistance) / 75);
        if (toReduceBy < .2) {
            toReduceBy = .2f;
        }
        if (projectile.getHit().isEmpty()) {
            toReduceBy += .15f;
        }
        hit.getCooldownManager().limitCooldowns(RegularCooldown.class, SplinteredIce.class, 3);
        SplinteredIce data = new SplinteredIce();
        hit.getCooldownManager().addCooldown(new RegularCooldown<>(
                "Splintered Ice",
                "SPLINT",
                SplinteredIce.class,
                data,
                projectile.getShooter(),
                CooldownTypes.LOW_LEVEL_DEBUFF,
                cooldownManager -> {
                },
                3 * 20
        ).addModifier(Modifier.INCOMING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
            int stacks = (int) new CooldownFilter<>(hit, RegularCooldown.class)
                    .filterCooldownClass(SplinteredIce.class)
                    .stream()
                    .count();
            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, "Splintered Ice", 1 + 0.06f * stacks);
        }));
        hit(projectile, shooter, toReduceBy, stats.getTargetsHit(), hit);
        hit.addSpeedModifier(shooter, "Splintered Ice", -35, 40);
        EffectUtils.displayParticle(Particle.ITEM_SNOWBALL, hit.getLocation().add(0, 1, 0), 10, .2, .2, .2, 0);
    }

    private void freezeExplodeOnHit(WarlordsEntity giver, WarlordsEntity hit) {
        new GameRunnable(giver.getGame()) {

            @Override
            public void run() {
                FallingBlockWaveEffect.create(hit.getLocation(), 3, 7, Material.PACKED_ICE);
                for (WarlordsEntity freezeTarget : PlayerFilter.entitiesAround(hit, 3, 3, 3).aliveEnemiesOf(giver)) {
                    Utils.playGlobalSound(freezeTarget.getLocation(), Sound.BLOCK_FIRE_EXTINGUISH, 2, 0.7f);
                    Utils.playGlobalSound(freezeTarget.getLocation(), Sound.BLOCK_GLASS_BREAK, 2, 0.1f);
                    freezeTarget.addInstance(InstanceBuilder.damage().ability(FrostBolt.this).source(giver).value(damageValues.shatterBoltDamage));
                }
            }
        }.runTaskLater(30);
    }

    private int hit(@Nonnull InternalProjectile projectile, WarlordsEntity shooter, float damageModifier, int playersHit, WarlordsEntity nearEntity) {
        getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
        playersHit++;
        if (nearEntity.onHorse()) {
            stats.addNumberOfDismounts();
        }
        nearEntity.addSpeedModifier(shooter, "Frostbolt", -slowness, slowDuration * 20);
        nearEntity.addInstance(InstanceBuilder.damage()
                .ability(this)
                .source(shooter)
                .min(damageValues.boltDamage.getMinValue() * damageModifier)
                .max(damageValues.boltDamage.getMaxValue() * damageModifier)
                .crit(damageValues.boltDamage));
        return playersHit;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Shoot a frostbolt that will shatter for ")
                .damage(damageValues.boltDamage)
                .text(" damage and slow by ")
                .percent(slowness, NamedTextColor.WHITE)
                .text(" for ")
                .durationSeconds(slowDuration)
                .text(". A direct hit will cause the enemy to take an additional ")
                .percent(directHitMultiplier, NamedTextColor.RED)
                .text(" extra damage.")
                .optimalRange(maxFullDistance)
                .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FrostboltBranch(abilityTree, this);
    }

    @Override
    public FloatModifiable getSplashRadius() {
        return splash;
    }

    @Override
    public FrostBoltStats getAbilityStats() {
        return stats;
    }

    public int getSlowness() {
        return slowness;
    }

    public void setSlowness(int slowness) {
        this.slowness = slowness;
    }

    public int getDirectHitAdditionalSlowness() {
        return directHitAdditionalSlowness;
    }

    public void setDirectHitAdditionalSlowness(int directHitAdditionalSlowness) {
        this.directHitAdditionalSlowness = directHitAdditionalSlowness;
    }

    public float getDirectHitMultiplier() {
        return directHitMultiplier;
    }

    public void setDirectHitMultiplier(float directHitMultiplier) {
        this.directHitMultiplier = directHitMultiplier;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable boltDamage = new Value.RangedValueCritable(242, 311, 20, 175);

        private Value.RangedValue shatterBoltDamage = new Value.RangedValue(409, 554);

        private List<Value> values = List.of(boltDamage, shatterBoltDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.boltDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("boltDamage"), Value.RangedValueCritable.class);
            this.shatterBoltDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("shatterBoltDamage"), Value.RangedValue.class);
            this.values = List.of(boltDamage, shatterBoltDamage);
        }

        public Value.RangedValueCritable getBoltDamage() {
            return boltDamage;
        }

    }

    static class SplinteredIce {

        private int stacks = 1;

        public int getStacks() {
            return stacks;
        }

        public void setStacks(int stacks) {
            this.stacks = stacks;
        }

    }

    public static class FrostBoltStats extends AbstractPiercingProjectileStats<FrostBolt, FrostBoltStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public FrostBoltStats merge(FrostBoltStats other, int multiplier) {
            FrostBoltStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<FrostBoltStats> getClazz() {
            return FrostBoltStats.class;
        }

        @Override
        public FrostBoltStats create() {
            return new FrostBoltStats();
        }

    }

}