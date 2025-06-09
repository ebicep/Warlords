package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsThrowableProjectileImpactEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.rogue.assassin.IncendiaryCurseBranch;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class IncendiaryCurse extends AbstractAbility implements RedAbilityIcon, HitBox, Damages<IncendiaryCurse.DamageValues>, AbilityStats<IncendiaryCurse, IncendiaryCurse.IncendiaryCurseStats> {

    private static final double SPEED = 0.250;
    private static final double GRAVITY = -0.008;
    private final IncendiaryCurseStats stats = new IncendiaryCurseStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable hitbox = new FloatModifiable(5);

    private int blindDurationInTicks = 30;
    private int damageIncrease;
    private int damageIncreaseHealthThreshold;

    public IncendiaryCurse() {
        this(AbstractAbilityBuilder.create("incendiaryCurse").pvp());
    }

    public IncendiaryCurse(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hitbox = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitbox"), float.class));
        this.blindDurationInTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("blindDurationInTicks"), int.class);
        this.damageIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageIncrease"), int.class);
        this.damageIncreaseHealthThreshold = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("damageIncreaseHealthThreshold"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.frostbolt.activation", 2, 0.7f);
        Utils.spawnThrowableProjectile(
                wp.getGame(),
                Utils.spawnArmorStand(wp.getLocation(),
                        armorStand -> {
                            armorStand.getEquipment().setHelmet(new ItemStack(Material.FIRE_CHARGE));
                        }
                ),
                calculateSpeed(wp),
                GRAVITY,
                SPEED,
                (newLoc, integer) -> {
                },
                newLoc -> PlayerFilter.entitiesAroundRectangle(newLoc, 1, 2, 1).aliveEnemiesOf(wp).findFirstOrNull(),
                (newLoc, directHit) -> {
                    WarlordsThrowableProjectileImpactEvent projectileImpactEvent = new WarlordsThrowableProjectileImpactEvent(wp, this, newLoc, directHit);
                    Bukkit.getPluginManager().callEvent(projectileImpactEvent);
                    onImpact(wp, newLoc);
                }
        );
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Ignite the targeted area with a cross flame, dealing")
                                               .damage(damageValues.curseDamage)
                                               .text("damage. Deals ")
                                               .percent(damageIncrease, NamedTextColor.RED)
                                               .text(" more damage to enemies above ")
                                               .percent(damageIncreaseHealthThreshold, NamedTextColor.RED)
                                               .text("health. Enemies hit are " + (inPve ? "stunned" : "blinded") + " for ")
                                               .durationTicks(blindDurationInTicks)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new IncendiaryCurseBranch(abilityTree, this);
    }

    protected Vector calculateSpeed(WarlordsEntity we) {
        return we.getLocation().getDirection().multiply(SPEED);
    }

    public void onImpact(@Nonnull WarlordsEntity wp, Location newLoc) {
        Utils.playGlobalSound(newLoc, Sound.ITEM_FLINTANDSTEEL_USE, 2, 0.1f);
        EffectUtils.playFirework(newLoc, FireworkEffect.builder().withColor(Color.ORANGE).withColor(Color.RED).with(FireworkEffect.Type.BURST).build(), 1);
        EffectUtils.displayParticle(Particle.SMOKE, newLoc, 100, 0.4, 0.05, 0.4, 0.2);
        float hitboxValue = hitbox.getCalculatedValue();
        List<WarlordsEntity> enemies = PlayerFilter.entitiesAround(newLoc, hitboxValue, hitboxValue, hitboxValue).aliveEnemiesOf(wp).toList();
        for (WarlordsEntity nearEntity : enemies) {
            stats.playersHit++;
            float damageMultiplier = convertToMultiplicationDecimal(
                    (nearEntity.getCurrentHealth() / nearEntity.getMaxBaseHealth()) > damageIncreaseHealthThreshold / 100f
                    ? damageIncrease
                    : 0
            );
            nearEntity.addInstance(InstanceBuilder
                    .damage()
                    .ability(this)
                    .source(wp)
                    .min(damageValues.curseDamage.getMinValue() * damageMultiplier)
                    .max(damageValues.curseDamage.getMaxValue() * damageMultiplier)
                    .crit(damageValues.curseDamage)
            );
            if (inPve && nearEntity instanceof WarlordsNPC warlordsNPC) {
                warlordsNPC.setStunTicks(blindDurationInTicks);
            } else {
                nearEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, blindDurationInTicks, 0, true, false));
            }
            nearEntity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, blindDurationInTicks, 0, true, false));
            if (pveMasterUpgrade) {
                EffectUtils.playFirework(newLoc, FireworkEffect.builder().withColor(Color.RED).withColor(Color.BLACK).with(FireworkEffect.Type.BALL_LARGE).build(), 1);
                nearEntity.getCooldownManager().removeCooldown(IncendiaryCurse.class, false);
                nearEntity.getCooldownManager()
                          .addCooldown(new RegularCooldown<>(name, "INCEN", IncendiaryCurse.class, new IncendiaryCurse(), wp, CooldownTypes.LOW_LEVEL_DEBUFF, cooldownManager -> {
                          }, 5 * 20
                          ) {

                              @Override
                              public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                                  return currentDamageValue * 1.3f;
                              }
                          });
            } else if (pveMasterUpgrade2) {
                EffectUtils.displayParticle(Particle.DUST, nearEntity.getLocation().add(0, 1.2, 0), 3, 0.3, 0.2, 0.3, 0, new Particle.DustOptions(Color.fromRGB(255, 255, 0), 2));
            }
        }
        if (pveMasterUpgrade2) {
            wp.addEnergy(wp, "Unforseen Curse", Math.min(200, enemies.size() * 10));
        }
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitbox;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public IncendiaryCurseStats getAbilityStats() {
        return stats;
    }

    public int getBlindDurationInTicks() {
        return blindDurationInTicks;
    }

    public void setBlindDurationInTicks(int blindDurationInTicks) {
        this.blindDurationInTicks = blindDurationInTicks;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable curseDamage = new Value.RangedValueCritable(408, 552, 20, 175);

        private List<Value> values = List.of(curseDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.curseDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("curseDamage"), Value.RangedValueCritable.class);
            this.values = List.of(curseDamage);
        }

        public Value.RangedValueCritable getCurseDamage() {
            return curseDamage;
        }

    }

    public static class IncendiaryCurseStats extends AbstractAbilityStats<IncendiaryCurse, IncendiaryCurseStats> {

        @Field("targets_hit")
        private int playersHit = 0;

        @Override
        public Class<IncendiaryCurseStats> getClazz() {
            return IncendiaryCurseStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Hit", playersHit));
            return statsDisplay;
        }

        @Override
        public IncendiaryCurseStats merge(IncendiaryCurseStats other, int multiplier) {
            IncendiaryCurseStats stats = super.merge(other, multiplier);
            stats.playersHit = this.playersHit + other.playersHit * multiplier;
            return stats;
        }

        @Override
        public IncendiaryCurseStats create() {
            return new IncendiaryCurseStats();
        }

    }

}
