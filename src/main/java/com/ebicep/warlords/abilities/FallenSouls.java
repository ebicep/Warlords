package com.ebicep.warlords.abilities;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.WeaponAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.shaman.spiritguard.FallenSoulsBranch;
import com.ebicep.warlords.util.bukkit.EntitiesUtils;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class FallenSouls extends AbstractPiercingProjectile<FallenSouls, FallenSouls.FallenSoulsStats> implements WeaponAbilityIcon, Damages<FallenSouls.DamageValues> {

    public static final ItemStack ITEM_STACK = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
    private final FallenSoulsStats stats = new FallenSoulsStats();
    private final DamageValues damageValues = new DamageValues();
    private int cooldownReduction = 2;

    public FallenSouls() {
        this(AbstractAbilityBuilder.create("fallenSouls").pvp());
    }

    public FallenSouls(AbstractAbilityBuilder builder) {
        super(builder);
        this.shotsFiredAtATime = 3;
        this.setMaxAngleOfShots(54);
        this.forwardTeleportAmount = 1.6f;
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.cooldownReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("cooldownReduction"), int.class);
    }

    @Override
    protected void updateSpeed(InternalProjectile projectile) {
        if (!pveMasterUpgrade) {
            return;
        }

        WarlordsEntity target = findGraveCompassTarget(projectile);

        if (target == null) {
            keepProjectileAtBaseSpeed(projectile);
            return;
        }

        Vector currentSpeed = projectile.getSpeed();
        double baseSpeed = projectileSpeed.getCalculatedValue();

        if (baseSpeed <= 0 || currentSpeed.lengthSquared() == 0) {
            return;
        }

        Vector desiredSpeed = target.getLocation()
                .clone()
                .add(0, 1, 0)
                .toVector()
                .subtract(projectile.getCurrentLocation().toVector());

        if (desiredSpeed.lengthSquared() == 0) {
            keepProjectileAtBaseSpeed(projectile);
            return;
        }

        desiredSpeed.normalize();

        Vector newDirection = currentSpeed.clone()
                .normalize()
                .multiply(1 - .16f)
                .add(desiredSpeed.multiply(.16f));

        if (newDirection.lengthSquared() == 0) {
            keepProjectileAtBaseSpeed(projectile);
            return;
        }

        currentSpeed.copy(newDirection.normalize().multiply(baseSpeed));
    }

    // for homing master upgrade
    private void keepProjectileAtBaseSpeed(InternalProjectile projectile) {
        Vector currentSpeed = projectile.getSpeed();
        double baseSpeed = projectileSpeed.getCalculatedValue();

        if (baseSpeed <= 0 || currentSpeed.lengthSquared() == 0) {
            return;
        }

        currentSpeed.normalize().multiply(baseSpeed);
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        Location startingLocation = projectile.getStartingLocation();
        LocationBuilder location = new LocationBuilder(startingLocation).pitch(0);
        ItemDisplay display = startingLocation.getWorld().spawn(location, ItemDisplay.class, itemDisplay -> {
            itemDisplay.setItemStack(ITEM_STACK);
                    itemDisplay.setTeleportDuration(1);
            itemDisplay.setBrightness(EntitiesUtils.MAX_BRIGHTNESS);
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

    @Override
    protected Location modifyProjectileStartingLocation(WarlordsEntity shooter, Location startingLocation) {
        return new LocationBuilder(startingLocation.clone()).addY(-.3).backward(0f);
    }

    private Optional<WarlordsDamageHealingFinalEvent> hit(WarlordsEntity wp, WarlordsEntity enemy) {
        if (pveMasterUpgrade) {
            applyGraveCompass(wp, enemy);
        }

        if (pveMasterUpgrade2) {
            if (enemy.getCooldownManager().hasCooldown(FallenSoulsBranch.SoulFeast.class)) {
                new CooldownFilter<>(enemy, PermanentCooldown.class)
                        .filterCooldownClassAndMapToObjectsOfClass(FallenSoulsBranch.SoulFeast.class)
                        .forEach(FallenSoulsBranch.SoulFeast::reduce);
            } else {
                FallenSoulsBranch.SoulFeast soulFeast = new FallenSoulsBranch.SoulFeast();
                enemy.getCooldownManager().addCooldown(new PermanentCooldown<>(
                        "Soul Feast",
                        "FEAST",
                        FallenSoulsBranch.SoulFeast.class,
                        soulFeast,
                        wp,
                        CooldownTypes.LOW_LEVEL_DEBUFF,
                        cooldownManager -> {},
                        false
                ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, name, soulFeast.getDamageMultiplier());
                        }
                ));
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
                                                              for (WarlordsEntity teammate : PlayerFilter
                                                                      .entitiesAround(wp.getLocation(), radius, radius, radius)
                                                                      .aliveTeammatesOfExcludingSelf(wp)
                                                                      .filter(warlordsEntity -> warlordsEntity.getSpecClass() != Specializations.SPIRITGUARD)
                                                                      .closestWarlordPlayersFirst(wp.getLocation())
                                                                      .limit(soulbinding.getMaxAlliesHit())
                                                              ) {
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

    private WarlordsEntity findGraveCompassTarget(InternalProjectile projectile) {
        WarlordsEntity shooter = projectile.getShooter();
        Location currentLocation = projectile.getCurrentLocation();

        List<WarlordsEntity> targets = PlayerFilter.entitiesAround(currentLocation, 20, 20, 20)
                .aliveEnemiesOf(shooter)
                .excluding(projectile.getHit())
                .filter(target -> target instanceof WarlordsNPC warlordsNPC && isTargetingAlly(warlordsNPC, shooter))
                .toList();

        return targets.stream()
                .min(Comparator.comparingDouble(target -> target.getLocation().distanceSquared(currentLocation)))
                .orElse(null);
    }

    private boolean isTargetingAlly(WarlordsNPC warlordsNPC, WarlordsEntity shooter) {
        Entity target = warlordsNPC.getMob().getTarget();

        if (target == null) {
            return false;
        }

        WarlordsEntity targetEntity = Warlords.getPlayer(target);
        if (targetEntity == shooter) {
            return false;
        }

        return shooter.isTeammateAlive(targetEntity);
    }

    private void applyGraveCompass(WarlordsEntity wp, WarlordsEntity enemy) {
        if (enemy instanceof WarlordsNPC warlordsNPC) {
            warlordsNPC.getMob().setTarget(wp);
        }

        EffectUtils.displayParticle(
                Particle.WITCH,
                enemy.getLocation().clone().add(0, 1.2, 0),
                5,
                .25,
                .25,
                .25,
                .05f
        );
    }

    @SuppressWarnings("unchecked")
    private Optional<RegularCooldown<GraveCompassData>> getGraveCompassDebuff(WarlordsEntity wp, WarlordsEntity enemy) {
        return (Optional<RegularCooldown<GraveCompassData>>) (Optional<?>) new CooldownFilter<>(enemy, RegularCooldown.class)
                .filterCooldownClass(GraveCompassData.class)
                .filterCooldownFrom(wp)
                .filterName("Grave Compass")
                .filter(RegularCooldown::hasTicksLeft)
                .findFirst();
    }

    private static class GraveCompassData {
    }

}
