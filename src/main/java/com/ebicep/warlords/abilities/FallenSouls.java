package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.FallenSoulsBranch;
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

public class FallenSouls extends AbstractPiercingProjectile<FallenSouls, FallenSouls.FallenSoulsStats> implements WeaponAbilityIcon, Damages<FallenSouls.DamageValues> {

    private final FallenSoulsStats stats = new FallenSoulsStats();
    private final DamageValues damageValues = new DamageValues();
    private int cooldownReduction = 2;

    public FallenSouls() {
        this(AbstractAbilityBuilder.create("fallenSouls").pvp());
    }

    public FallenSouls(AbstractAbilityBuilder builder) {
        super(builder);
        this.shotsFiredAtATime = 3;
        this.maxAngleOfShots = 54;
        this.forwardTeleportAmount = 1.6f;
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.cooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("cooldownReduction"), int.class);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation).pitch(0);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
                    itemDisplay.setItemStack(new ItemStack(Material.ACACIA_FENCE_GATE));
                    itemDisplay.setTeleportDuration(1);
                    itemDisplay.setBrightness(new Display.Brightness(15, 15));
                    itemDisplay.setTransformation(new Transformation(new Vector3f(),
                            new AxisAngle4f((float) Math.toRadians(startingLocation.getPitch()), 1, 0, 0),
                            new Vector3f(.75f),
                            new AxisAngle4f()
                    ));
                }
        );
        projectile.addTask(new InternalProjectileTask() {

            @Override
            public void run(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                Location currentLocation = projectile.getCurrentLocation();
                LocationBuilder location = new LocationBuilder(currentLocation).pitch(0);
                display.teleport(location);
                if (projectile.getTicksLived() % 4 == 0) {
                    EffectUtils.displayParticle(Particle.WITCH, projectile.getCurrentLocation(), 1);
                }
            }

            @Override
            public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                display.remove();
                EffectUtils.displayParticle(Particle.WITCH, projectile.getCurrentLocation(), 1, 0, 0, 0, 0.7f);
            }
        });
    }

    @Override
    protected String getActivationSound() {
        return "shaman.lightningbolt.impact";
    }

    @Override
    protected float getSoundVolume() {
        return 2;
    }

    @Override
    protected float getSoundPitch() {
        return 1.5f;
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
        int playersHit = 0;
        for (WarlordsEntity enemy : PlayerFilter.entitiesAround(currentLocation, 3, 3, 3).aliveEnemiesOf(wp).excluding(projectile.getHit())) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(enemy));
            playersHit++;
            if (enemy.onHorse()) {
                stats.addNumberOfDismounts();
            }
            hit(wp, enemy);
            for (SpiritLink spiritLink : wp.getAbilitiesMatching(SpiritLink.class)) {
                spiritLink.subtractCurrentCooldown(2);
            }
        }
        return playersHit;
    }

    private Optional<WarlordsDamageHealingFinalEvent> hit(WarlordsEntity wp, WarlordsEntity enemy) {
        if (pveMasterUpgrade2) {
            if (enemy.getCooldownManager().hasCooldown(FallenSoulsBranch.SoulFeast.class)) {
                new CooldownFilter<>(enemy, PermanentCooldown.class).filterCooldownClassAndMapToObjectsOfClass(FallenSoulsBranch.SoulFeast.class)
                                                                    .forEach(FallenSoulsBranch.SoulFeast::reduce);
            } else {
                FallenSoulsBranch.SoulFeast soulFeast = new FallenSoulsBranch.SoulFeast();
                enemy.getCooldownManager()
                     .addCooldown(new PermanentCooldown<>("Soul Feast", "FEAST", FallenSoulsBranch.SoulFeast.class, soulFeast, wp, CooldownTypes.ABILITY, cooldownManager -> {
                     }, false
                     ) {

                         @Override
                         public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                             return currentDamageValue * soulFeast.getDamageMultiplier();
                         }
                     });
            }
        }
        return enemy.addInstance(InstanceBuilder.damage().ability(this).source(wp).value(damageValues.fallenSoulDamage));
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
            hit(wp, hit);
            for (SpiritLink spiritLink : wp.getAbilitiesMatching(SpiritLink.class)) {
                spiritLink.subtractCurrentCooldown(cooldownReduction);
            }
            reduceCooldowns(wp, hit);
        }
    }

    @Override
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.3).backward(0f);
    }

    private void reduceCooldowns(WarlordsEntity wp, WarlordsEntity enemy) {
        new CooldownFilter<>(wp, PersistentCooldown.class).filterCooldownClassAndMapToObjectsOfClass(Soulbinding.SoulbindingData.class)
                                                          .filter(soulbinding -> soulbinding.hasBoundPlayerSoul(enemy))
                                                          .forEachOrdered(data -> {
                                                              Soulbinding soulbinding = data.getSoulbinding();
                                                              soulbinding.addSoulProcs();
                                                              for (AbstractAbility ability : wp.getAbilities()) {
                                                                  ability.subtractCurrentCooldownForce(soulbinding.getSelfCooldownReduction());
                                                              }
                                                              int radius = soulbinding.getRadius();
                                                              for (WarlordsEntity teammate : PlayerFilter.entitiesAround(wp.getLocation(), radius, radius, radius)
                                                                                                         .aliveTeammatesOfExcludingSelf(wp)
                                                                                                         .filter(warlordsEntity -> warlordsEntity.getSpecClass() != Specializations.SPIRITGUARD)
                                                                                                         .closestWarlordPlayersFirst(wp.getLocation())
                                                                                                         .limit(soulbinding.getMaxAlliesHit())) {
                                                                  soulbinding.addSoulTeammatesCDReductions();
                                                                  for (AbstractAbility ability : teammate.getAbilities()) {
                                                                      ability.subtractCurrentCooldown(soulbinding.getAllyCooldownReduction());
                                                                  }
                                                              }
                                                              if (soulbinding.isPveMasterUpgrade()) {
                                                                  wp.addEnergy(wp, "Soulbinding Weapon", 1);
                                                              }
                                                          });
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Summon a wave of fallen souls, dealing")
                                               .damage(damageValues.fallenSoulDamage)
                                               .text(" damage to all enemies they pass through. Each target hit reduces the cooldown of Spirit Link by ")
                                               .durationSeconds(cooldownReduction)
                                               .text(".")
                                               .maxRange(maxDistance)
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FallenSoulsBranch(abilityTree, this);
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public FallenSoulsStats getAbilityStats() {
        return stats;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable fallenSoulDamage = new Value.RangedValueCritable(140, 181, 20, 180);

        private List<Value> values = List.of(fallenSoulDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.fallenSoulDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("fallenSoulDamage"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(fallenSoulDamage);
        }

        public Value.RangedValueCritable getFallenSoulDamage() {
            return fallenSoulDamage;
        }

    }

    public static class FallenSoulsStats extends AbstractPiercingProjectileStats<FallenSouls, FallenSoulsStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            return statsDisplay;
        }

        @Override
        public FallenSoulsStats merge(FallenSoulsStats other, int multiplier) {
            FallenSoulsStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<FallenSoulsStats> getClazz() {
            return FallenSoulsStats.class;
        }

        @Override
        public FallenSoulsStats create() {
            return new FallenSoulsStats();
        }

    }

}
