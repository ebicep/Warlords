package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.RedAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.marker.FlagHolder;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.warrior.revenant.RecklessChargeBranch;
import com.ebicep.warlords.util.bukkit.LocationUtils;
import com.ebicep.warlords.util.warlords.GameRunnable;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RecklessCharge extends AbstractAbility implements RedAbilityIcon, HitBox, Damages<RecklessCharge.DamageValues>, AbilityStats<RecklessCharge,
        RecklessCharge.RecklessChargeStats>, CanReduceCooldowns {

    private final RecklessChargeStats stats = new RecklessChargeStats();
    private final DamageValues damageValues = new DamageValues();
    private FloatModifiable hitbox = new FloatModifiable(2.5f);
    private int stunTimeInTicks = 10;
    private float additionalBlocks = 0;
    private boolean verticalMovement = false;
    private int maxChargeDuration = 5;
    private int flagBlockReduction;

    public RecklessCharge() {
        super(AbstractAbilityBuilder.create("recklessCharge").pvp());
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.stunTimeInTicks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("stunTimeInTicks"), int.class);
        this.additionalBlocks = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("additionalBlocks"), int.class);
        this.flagBlockReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("flagBlockReduction"), int.class);
        this.hitbox = new FloatModifiable(ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hitBox"), float.class));
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "warrior.seismicwave.activation", 2, 1);
        if (pveMasterUpgrade || pveMasterUpgrade2) {
            wp.getCooldownManager()
              .addCooldown(new RegularCooldown<>(name,
                      pveMasterUpgrade ? "Reckless Rampage" : "Reverberation",
                      RecklessCharge.class,
                      null,
                      wp,
                      CooldownTypes.ABILITY,
                      cooldownManager -> {
                      },
                      2 * 20
              ) {

                  @Override
                  public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                      return currentDamageValue * 0.2f;
                  }
              });
        }
        Location location = wp.getLocation();
        boolean horizontal = Math.abs(location.getPitch()) < 50;
        if (!verticalMovement || horizontal) {
            location.setPitch(0);
        }
        Location chargeLocation = location.clone();
        List<WarlordsEntity> playersHit = new ArrayList<>();
        playersHit.add(wp);
        boolean inAir;
        double chargeDistance;
        if (location.getWorld().getBlockAt(location.clone().add(0, -.01, 0)).getType() == Material.AIR) {
            inAir = true;
            //travels 5 blocks
            chargeDistance = 5;
        } else {
            inAir = false;
            //travels 7 at peak jump
            chargeDistance = Math.max(Math.min(LocationUtils.getDistance(wp, .1) * 5, 7.2), 6.3);
        }
        chargeDistance += additionalBlocks;
        if (wp.hasFlag()) {
            chargeDistance -= flagBlockReduction;
        }
        double chargeDistanceSquared = (float) (chargeDistance * chargeDistance);
        float hitboxValue = hitbox.getCalculatedValue();
        new GameRunnable(wp.getGame()) {

            //safety precaution
            int chargeDuration = maxChargeDuration;

            @Override
            public void run() {
                if (chargeDuration == maxChargeDuration) {
                    Vector vector;
                    if (inAir) {
                        vector = location.getDirection().multiply(1.75);
                    } else {
                        vector = location.getDirection().multiply(2.5);
                    }
                    if (!horizontal) {
                        vector.multiply(3);
                    }
                    if (horizontal || FlagHolder.isPlayerHolderFlag(wp)) {
                        vector.setY(.2);
                    }
                    wp.setVelocity(name, vector, true);
                }
                //cancel charge if hit a block, making the player stand still
                boolean reachedMaxDistance = wp.getLocation().distanceSquared(chargeLocation) > chargeDistanceSquared;
                boolean hitWall = (!verticalMovement) && wp.getEntity().getVelocity().getX() == 0 && wp.getEntity().getVelocity().getZ() == 0;
                if (reachedMaxDistance || hitWall || chargeDuration <= 0) {
                    wp.setVelocity(name, new Vector(0, 0, 0), true);
                    this.cancel();
                }
                for (int i = 0; i < 4; i++) {
                    EffectUtils.displayParticle(
                            Particle.DUST,
                            wp.getLocation().clone().add((Math.random() * 1.5) - .75, .5 + (Math.random() * 2) - 1, (Math.random() * 1.5) - .75),
                            1,
                            0,
                            0,
                            0,
                            0,
                            new Particle.DustOptions(Color.fromRGB(255, 0, 0), 1)
                    );
                }
                PlayerFilter.entitiesAround(wp, hitboxValue, hitboxValue + 2.5, hitboxValue).excluding(playersHit).forEach(otherPlayer -> {
                    playersHit.add(otherPlayer);
                    if (otherPlayer.isEnemyAlive(wp)) {
                        stats.targetsCharged++;
                        float damageMultiplier = pveMasterUpgrade2 && otherPlayer.getCooldownManager().hasCooldown(CripplingStrike.class) ? 1.75f : 1;
                        otherPlayer.addInstance(InstanceBuilder.damage()
                                                               .ability(RecklessCharge.this)
                                                               .source(wp)
                                                               .min(damageValues.chargeDamage.getMinValue() * damageMultiplier)
                                                               .max(damageValues.chargeDamage.getMaxValue() * damageMultiplier)
                                                               .crit(damageValues.chargeDamage));
                        boolean stunned = otherPlayer.setStunTicks(getStunTimeInTicks());
                        if (stunned) {
                            otherPlayer.getEntity()
                                       .showTitle(Title.title(Component.empty(),
                                               Component.text("IMMOBILIZED", NamedTextColor.LIGHT_PURPLE),
                                               Title.Times.times(Ticks.duration(0), Ticks.duration(stunTimeInTicks), Ticks.duration(0))
                                       ));
                        }
                        if (pveMasterUpgrade) {
                            otherPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                    "Reckless Rampage",
                                    "RECK",
                                    RecklessCharge.class,
                                    null,
                                    wp,
                                    CooldownTypes.ABILITY,
                                    cooldownManager -> {
                                    },
                                    getStunTimeInTicks()
                            ).addModifier(Modifier.DAMAGE_BEFORE_INTERVENE_SELF, (event, currentDamageValue) -> {
                                        if (event.getCause().contains("Strike")) {
                                            currentDamageValue.addMultiplicativeModifierMult("Reckless Rampage", 1.25f);
                                        }
                                    }
                            ));
                        }
                    } else if (pveMasterUpgrade2 && otherPlayer.isTeammateAlive(wp)) {
                        otherPlayer.getCooldownManager().addCooldown(new RegularCooldown<>(
                                "Probiotic",
                                "PROBIO",
                                RecklessCharge.class,
                                null,
                                wp,
                                CooldownTypes.ABILITY,

                                cooldownManager -> {
                                },
                                8 * 20
                        ).addModifier(Modifier.HEALING_MODIFY_SELF, (event, currentHealValue) -> {
                                    currentHealValue.addMultiplicativeModifierMult(name, 1.5f);
                                }
                        ));
                        new CooldownFilter<>(otherPlayer, RegularCooldown.class).filter(cd -> cd.getCooldownType() != CooldownTypes.LOW_LEVEL_DEBUFF)
                                                                                .forEach(cd -> cd.setTicksLeft(cd.getTicksLeft() + 40));
                        EffectUtils.displayParticle(Particle.HEART, otherPlayer.getLocation().add(0, 2, 0), 10, .5, .25, .5, 0);
                    }
                });
                chargeDuration--;
            }
        }.runTaskTimer(1, 0);
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Charge forward, dealing ")
                                               .damage(damageValues.chargeDamage)
                                               .text(" damage to all enemies you pass through. Enemies hit are ")
                                               .text("IMMOBILIZED", NamedTextColor.DARK_PURPLE)
                                               .text(", preventing movement for ")
                                               .durationTicks(stunTimeInTicks)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new RecklessChargeBranch(abilityTree, this);
    }

    public int getStunTimeInTicks() {
        return stunTimeInTicks;
    }

    public void setStunTimeInTicks(int stunTimeInTicks) {
        this.stunTimeInTicks = stunTimeInTicks;
    }

    @Override
    public DamageValues getDamageValues() {
        return damageValues;
    }

    @Override
    public boolean canReduceCooldowns() {
        return pveMasterUpgrade2;
    }

    @Override
    public RecklessChargeStats getAbilityStats() {
        return stats;
    }

    @Override
    public FloatModifiable getHitBoxRadius() {
        return hitbox;
    }

    public float getAdditionalBlocks() {
        return additionalBlocks;
    }

    public void setAdditionalBlocks(float additionalBlocks) {
        this.additionalBlocks = additionalBlocks;
    }

    public boolean isVerticalMovement() {
        return verticalMovement;
    }

    public void setVerticalMovement(boolean verticalMovement) {
        this.verticalMovement = verticalMovement;
    }

    public int getMaxChargeDuration() {
        return maxChargeDuration;
    }

    public void setMaxChargeDuration(int maxChargeDuration) {
        this.maxChargeDuration = maxChargeDuration;
    }

    public static class DamageValues implements Value.ValueHolder {

        private Value.RangedValueCritable chargeDamage = new Value.RangedValueCritable(457, 601, 20, 200);

        private List<Value> values = List.of(chargeDamage);

        @Override
        public List<Value> getValues() {
            return values;
        }

        @Override
        public void init(AbstractAbilityBuilder builder) {
            this.chargeDamage = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldNameDamage("chargeDamage"), Value.RangedValueCritable.class);
            this.values = List.of(chargeDamage);
        }

        public Value.RangedValueCritable getChargeDamage() {
            return chargeDamage;
        }

    }

    public static class RecklessChargeStats extends AbstractAbilityStats<RecklessCharge, RecklessChargeStats> {

        @Field("targets_charged")
        private int targetsCharged = 0;

        @Override
        public Class<RecklessChargeStats> getClazz() {
            return RecklessChargeStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Targets Charged", targetsCharged));
            return statsDisplay;
        }

        @Override
        public RecklessChargeStats merge(RecklessChargeStats other, int multiplier) {
            RecklessChargeStats stats = super.merge(other, multiplier);
            stats.targetsCharged = this.targetsCharged + other.targetsCharged * multiplier;
            return stats;
        }

        @Override
        public RecklessChargeStats create() {
            return new RecklessChargeStats();
        }

    }

}
