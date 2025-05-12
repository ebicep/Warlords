package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.thunderlord.LightningBoltBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LightningBolt extends AbstractPiercingProjectile<LightningBolt, LightningBolt.LightningBoltStats> implements WeaponAbilityIcon, Damages<LightningBolt.DamageValues> {

    private final LightningBoltStats stats = new LightningBoltStats();
    private final DamageValues damageValues = new DamageValues();
    private double hitbox = 3;
    private int cooldownReduction = 2;

    public LightningBolt() {
        super(AbstractAbilityBuilder.create("lightningBolt").pvp());
    }

    public LightningBolt(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Hurl a fast, piercing bolt of lightning that deals ")
                                               .damage(damageValues.boltDamage)
                                               .text(" to all enemies it passes through. Each target hit reduces the cooldown of Chain Lightning by ")
                                               .durationSeconds(cooldownReduction)
                                               .text(".")
                                               .maxRange(maxDistance)
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new LightningBoltBranch(abilityTree, this);
    }

    public double getHitbox() {
        return hitbox;
    }

    public void setHitbox(double hitbox) {
        this.hitbox = hitbox;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public LightningBoltStats getAbilityStats() {
        return stats;
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hitbox = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitbox"), float.class);
        this.cooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("cooldownReduction"), int.class);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation).pitch(0).yaw(startingLocation.getYaw() - 90);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
                    itemDisplay.setItemStack(new ItemStack(Material.JUNGLE_SAPLING));
                    itemDisplay.setTeleportDuration(1);
                    itemDisplay.setBrightness(new Display.Brightness(15, 15));
                    itemDisplay.setTransformation(new Transformation(new Vector3f(),
                            new AxisAngle4f((float) Math.toRadians(startingLocation.getPitch()), 0, 0, 1),
                            new Vector3f(2f),
                            new AxisAngle4f()
                    ));
                }
        );
        projectile.addTask(new InternalProjectileTask() {

            @Override
            public void run(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                Location currentLocation = projectile.getCurrentLocation();
                LocationBuilder location = new LocationBuilder(currentLocation).pitch(0).yaw(currentLocation.getYaw() - 90);
                display.teleport(location);
            }

            @Override
            public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                display.remove();
            }
        });
    }

    @Override
    protected String getActivationSound() {
        return "shaman.lightningbolt.activation";
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
    protected void playEffect(@Nonnull InternalProjectile projectile) {
        super.playEffect(projectile);
    }

    @Override
    @Deprecated
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, WarlordsEntity hit) {
        WarlordsEntity wp = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();
        Utils.playGlobalSound(currentLocation, "shaman.lightningbolt.impact", 2, 1);
        currentLocation.getWorld().spawnParticle(Particle.EXPLOSION, currentLocation, 1, 0, 0, 0, 0, null, true);
        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter.entitiesAround(currentLocation, hitbox, hitbox, hitbox).aliveEnemiesOf(wp).excluding(projectile.getHit())) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(enemy));
            playersHit++;
            if (enemy.onHorse()) {
                stats.addNumberOfDismounts();
            }
            Utils.playGlobalSound(enemy.getLocation(), "shaman.lightningbolt.impact", 2, 1);
            //hitting player
            hit(enemy, wp, projectile);
            //reducing chain cooldown
            if (!(wp.isInPve() && projectile.getHit().size() > 2)) {
                for (ChainLightning chainLightning : wp.getAbilitiesMatching(ChainLightning.class)) {
                    chainLightning.subtractCurrentCooldown(cooldownReduction);
                }
            }
        }
        return playersHit;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, WarlordsEntity wp) {
        return false;
    }

    @Override
    protected boolean shouldEndProjectileOnHit(@Nonnull InternalProjectile projectile, Block block) {
        return true;
    }

    @Override
    protected void onNonCancellingHit(@Nonnull InternalProjectile projectile, @Nonnull WarlordsEntity hit, @Nonnull Location impactLocation) {
        WarlordsEntity wp = projectile.getShooter();
        if (!projectile.getHit().contains(hit)) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(hit));
            stats.addPlayersHit();
            if (hit.onHorse()) {
                stats.addNumberOfDismounts();
            }
            Utils.playGlobalSound(impactLocation, "shaman.lightningbolt.impact", 2, 1);
            hit(hit, wp, projectile);
            //reducing chain cooldown
            if (!(wp.isInPve() && projectile.getHit().size() > 2)) {
                for (ChainLightning chainLightning : wp.getAbilitiesMatching(ChainLightning.class)) {
                    chainLightning.subtractCurrentCooldown(2);
                }
            }
        }
    }

    @Override
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.1);
    }

    private Optional<WarlordsDamageHealingFinalEvent> hit(@Nonnull WarlordsEntity hit, WarlordsEntity wp, InternalProjectile projectile) {
        int playersHit = projectile.getHit().size();
        float damageMultiplier = 1;
        if (pveMasterUpgrade2) {
            if (playersHit == 1) {
                damageMultiplier = 1.35f;
            } else {
                damageMultiplier = 1.1f;
            }
            EffectUtils.displayParticle(Particle.ENCHANTED_HIT, hit.getLocation().add(0, 1.2, 0), 5, .25, .25, .25, 0);
        }
        return hit.addInstance(InstanceBuilder.damage()
                                              .ability(this)
                                              .source(wp)
                                              .min(damageValues.boltDamage.getMinValue() * damageMultiplier)
                                              .max(damageValues.boltDamage.getMaxValue() * damageMultiplier)
                                              .crit(damageValues.boltDamage));
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable boltDamage = new Value.RangedValueCritable(252, 340, 25, 180);

        private final List<Value> values = List.of(boltDamage);

        public Value.RangedValueCritable getBoltDamage() {
            return boltDamage;
        }

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.boltDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("boltDamage"), Value.RangedValueCritable.class);
        }

    }

    public static class LightningBoltStats extends AbstractPiercingProjectileStats<LightningBolt, LightningBoltStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.removeIf(abilityStatDisplay -> abilityStatDisplay.name().equals("Direct Hits"));
            return statsDisplay;
        }

        @Override
        public LightningBoltStats merge(LightningBoltStats other, int multiplier) {
            LightningBoltStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<LightningBoltStats> getClazz() {
            return LightningBoltStats.class;
        }

        @Override
        public LightningBoltStats create() {
            return new LightningBoltStats();
        }

    }

}
