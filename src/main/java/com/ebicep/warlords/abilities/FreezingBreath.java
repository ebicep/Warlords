package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.cryomancer.FreezingBreathBranch;
import com.ebicep.warlords.util.bukkit.LocationBuilder;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FreezingBreath extends AbstractProjectile<FreezingBreath, FreezingBreath.FreezingBreathStats> implements RedAbilityIcon, Damages<FreezingBreath.DamageValues> {

    private final FreezingBreathStats stats = new FreezingBreathStats();
    private final DamageValues damageValues = new DamageValues();
    private int slowDuration = 4;
    private int slowness = 40;
    private float hitbox = 10;
    private int maxAnimationTime = 12;

    public FreezingBreath() {
        super(AbstractAbilityBuilder.create("freezingBreath").pvp());
    }

    public FreezingBreath(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.slowDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slowDuration"), int.class);
        this.slowness = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("slowness"), int.class);
        this.hitbox = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitbox"), float.class);
        this.maxAnimationTime = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("maxAnimationTime"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        if (pveMasterUpgrade2) {
            return super.onActivateInternal(wp);
        }
        Utils.playGlobalSound(wp.getLocation(), "mage.freezingbreath.activation", 2, 1);
        Location playerLoc = new LocationBuilder(wp.getLocation()).pitch(0).add(0, 1.7, 0);
        EffectUtils.playSpiralAnimation(
                wp,
                playerLoc,
                4,
                maxAnimationTime,
                (center, animationTimer) -> {
                    EffectUtils.displayParticle(
                            Particle.CLOUD,
                            center.translateVector(wp.getWorld(), animationTimer / 2D, 0, 0),
                            5,
                            ThreadLocalRandom.current().nextDouble(.5),
                            ThreadLocalRandom.current().nextDouble(.5),
                            ThreadLocalRandom.current().nextDouble(.5),
                            0.4f
                    );
                },
                Particle.FIREWORK
        );
        Location playerEyeLoc = new LocationBuilder(wp.getLocation()).pitch(0).backward(1);
        Vector viewDirection = playerLoc.getDirection();
        int counter = 0;
        UUID uuid = UUID.randomUUID();
        for (WarlordsEntity breathTarget : PlayerFilter.entitiesAroundRectangle(wp, hitbox - 2.5, hitbox, hitbox - 2.5).aliveEnemiesOf(wp)) {
            Vector direction = breathTarget.getLocation().subtract(playerEyeLoc).toVector().normalize();
            if (!(viewDirection.dot(direction) > .68)) {
                continue;
            }
            stats.addPlayersHit();
            counter++;
            breathTarget.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .value(damageValues.freezingBreathDamage)
                    .uuid(uuid));
            breathTarget.addSpeedModifier(wp, "Freezing Breath", -slowness, slowDuration * 20);
        }
        if (pveMasterUpgrade) {
            if (counter > 6) {
                counter = 6;
            }
            damageReductionOnHit(wp, counter);
        }
        return true;
    }

    @Override
    protected void onSpawn(@Nonnull InternalProjectile projectile) {
        super.onSpawn(projectile);
        if (!pveMasterUpgrade2) {
            return;
        }
        Utils.playGlobalSound(projectile.getShooter().getLocation(), "shaman.boulder.activation", 2, .8f);
        List<ArmorStand> ball = new ArrayList<>();
        Location startingLocation = projectile.getStartingLocation();
        List<Location> sphereLocations = LocationUtils.getSphereLocations(startingLocation.clone().add(0, -1, 0), .2, 3);
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (Location sphereLocation : sphereLocations) {
            Material material = switch (random.nextInt(5)) {
                case 0 -> Material.ICE;
                case 1 -> Material.BLUE_ICE;
                case 2 -> Material.PACKED_ICE;
                case 3 -> Material.SNOW_BLOCK;
                default -> Material.PODZOL;
            };
            ball.add(Utils.spawnArmorStand(sphereLocation, armorStand -> {
                        armorStand.setMarker(true);
                        armorStand.setSmall(true);
                        armorStand.getEquipment().setHelmet(new ItemStack(material));
                        armorStand.setHeadPose(new EulerAngle(Math.toRadians(random.nextInt(90)), Math.toRadians(random.nextInt(90)), Math.toRadians(random.nextInt(90))));
                    }
            ));
        }
        projectile.addTask(new InternalProjectileTask() {

            @Override
            public void run(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                Vector vector = new LocationBuilder(startingLocation).getVectorTowards(projectile.getCurrentLocation());
                for (ArmorStand armorStand : ball) {
                    Location currentLoc = armorStand.getLocation();
                    armorStand.teleport(currentLoc.add(vector), PlayerTeleportEvent.TeleportCause.PLUGIN);
                }
            }

            @Override
            public void onDestroy(AbstractPiercingProjectile<?, ?>.InternalProjectile projectile) {
                ball.forEach(Entity::remove);
                LocationBuilder impactLocation = new LocationBuilder(projectile.getCurrentLocation()).backward(1.5f);
                Utils.spawnFallingBlocks(impactLocation, 1, 12, Material.ICE, Material.BLUE_ICE, Material.PACKED_ICE, Material.SNOW_BLOCK, Material.PODZOL);
                EffectUtils.displayParticle(Particle.CLOUD, impactLocation, 15, 0.5, 0.5, 0.5, .5);
            }
        });
    }

    @Nullable
    @Override
    protected String getActivationSound() {
        return null;
    }

    @Override
    protected float getSoundVolume() {
        return 0;
    }

    @Override
    protected float getSoundPitch() {
        return 0;
    }

    @Override
    protected void playEffect(@Nonnull Location currentLocation, int ticksLived) {
    }

    @Override
    protected int onHit(@Nonnull InternalProjectile projectile, @Nullable WarlordsEntity hit) {
        if (!pveMasterUpgrade2) {
            return 0;
        }
        WarlordsEntity shooter = projectile.getShooter();
        Location startingLocation = projectile.getStartingLocation();
        Location currentLocation = projectile.getCurrentLocation();
        Utils.playGlobalSound(currentLocation, "shaman.boulder.impact", 2, .5f);
        int playersHit = 0;
        for (WarlordsEntity nearEntity : PlayerFilter.entitiesAround(currentLocation, 5, 5, 5).aliveEnemiesOf(shooter).excluding(projectile.getHit())) {
            getProjectiles(projectile).forEach(p -> p.getHit().add(nearEntity));
            playersHit++;
            if (nearEntity.onHorse()) {
                stats.addNumberOfDismounts();
            }
            nearEntity.addSpeedModifier(shooter, name, -50, 5 * 20);
            float damageIncrease = (float) Math.min(1 + projectile.getBlocksTravelled() * .08, 2);
            nearEntity.addInstance(InstanceBuilder.damage()
                                                  .ability(this)
                                                  .source(shooter)
                                                  .min(damageValues.freezingBreathDamage.getMinValue() * damageIncrease)
                                                  .max(damageValues.freezingBreathDamage.getMaxValue() * damageIncrease)
                                                  .crit(damageValues.freezingBreathDamage));
            nearEntity.getCooldownManager()
                      .addCooldown(new RegularCooldown<>("Chilled",
                              "CHILLED",
                              FreezingBreath.class,
                              new FreezingBreath(),
                              shooter,
                              CooldownTypes.LOW_LEVEL_DEBUFF,
                              cooldownManager -> {
                      }, 5 * 20
                      ) {

                          @Override
                          public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                              return currentDamageValue * .6f;
                          }
                      });
        }
        return playersHit;
    }

    private void damageReductionOnHit(WarlordsEntity we, int counter) {
        we.getCooldownManager().removeCooldown(FreezingBreath.class, false);
        we.getCooldownManager().addCooldown(new RegularCooldown<>(name, "FRZ RES", FreezingBreath.class, new FreezingBreath(), we, CooldownTypes.BUFF, cooldownManager -> {
        }, 4 * 20
        ) {

            @Override
            public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 - (0.05f * counter));
            }
        });
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Breathe cold air in a cone in front of you, dealing ")
                                               .damage(damageValues.freezingBreathDamage)
                                               .text(" damage to all enemies hit and slowing them by ")
                                               .percent(slowness, NamedTextColor.WHITE)
                                               .text(" for ")
                                               .durationSeconds(slowDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new FreezingBreathBranch(abilityTree, this);
    }

    @Override
    public FreezingBreathStats getAbilityStats() {
        return stats;
    }

    public float getHitbox() {
        return hitbox;
    }

    public void setHitbox(float hitbox) {
        this.hitbox = hitbox;
    }

    public int getMaxAnimationTime() {
        return maxAnimationTime;
    }

    public void setMaxAnimationTime(int maxAnimationTime) {
        this.maxAnimationTime = maxAnimationTime;
    }

    public int getSlowness() {
        return slowness;
    }

    public void setSlowness(int slowness) {
        this.slowness = slowness;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable freezingBreathDamage = new Value.RangedValueCritable(443, 614, 20, 175);

        private List<Value> values = List.of(freezingBreathDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.freezingBreathDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(),
                    builder.getAppendedFieldNameDamage("freezingBreathDamage"),
                    Value.RangedValueCritable.class
            );
            this.values = List.of(freezingBreathDamage);
        }

        public Value.RangedValueCritable getFreezingBreathDamage() {
            return freezingBreathDamage;
        }

    }

    public static class FreezingBreathStats extends AbstractPiercingProjectileStats<FreezingBreath, FreezingBreathStats> {

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>();
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", targetsHit));
            return statsDisplay;
        }

        @Override
        public FreezingBreathStats merge(FreezingBreathStats other, int multiplier) {
            FreezingBreathStats stats = super.merge(other, multiplier);
            return stats;
        }

        @Override
        public Class<FreezingBreathStats> getClazz() {
            return FreezingBreathStats.class;
        }

        @Override
        public FreezingBreathStats create() {
            return new FreezingBreathStats();
        }

    }

}
