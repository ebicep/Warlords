package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.arcanist.sentinel.SanctuaryBranch;
import com.ebicep.warlords.util.java.Priority;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class Sanctuary extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<Sanctuary, Sanctuary.SanctuaryStats> {

    private final SanctuaryStats stats = new SanctuaryStats();
    private int hexTickDurationIncrease = 40;
    private int additionalDamageReduction = 4;
    private int tickDuration = 240;

    public Sanctuary() {
        super(AbstractAbilityBuilder.create("sanctuary").pvp());
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    public int getHexTickDurationIncrease() {
        return hexTickDurationIncrease;
    }

    public void setHexTickDurationIncrease(int hexTickDurationIncrease) {
        this.hexTickDurationIncrease = hexTickDurationIncrease;
    }

    public int getAdditionalDamageReduction() {
        return additionalDamageReduction;
    }

    public void setAdditionalDamageReduction(int additionalDamageReduction) {
        this.additionalDamageReduction = additionalDamageReduction;
    }

    @Override
    public SanctuaryStats getAbilityStats() {
        return stats;
    }

    @Override
    protected void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.hexTickDurationIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("hexTickDurationIncrease"), int.class);
        this.additionalDamageReduction = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("additionalDamageReduction"), int.class);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Summon your full protective power, increasing ")
                                               .text("FHEX", NamedTextColor.DARK_GREEN)
                                               .text(" duration by ")
                                               .durationTicks(hexTickDurationIncrease)
                                               .text(" and causing Guardian Beam to not consume ")
                                               .text("FHEX", NamedTextColor.DARK_GREEN)
                                               .text(" stacks.")
                                               .emptyLine()
                                               .text("All allies with max stacks of ")
                                               .text("FHEX", NamedTextColor.DARK_GREEN)
                                               .text(" gain an additional ")
                                               .percent(additionalDamageReduction, AbilityDescriptionBuilder.COLOR_BROWN)
                                               .text(" damage reduction per stack and reflect the reduced damage back to the dealer. Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public boolean onActivate(@Nonnull WarlordsEntity wp) {
        Location loc = wp.getLocation();
        Utils.playGlobalSound(wp.getLocation(), "warrior.laststand.activation", 2, 1.8f);
        Utils.playGlobalSound(loc, "arcanist.sanctuary.activation", 2, 0.55f);
        EffectUtils.playCircularShieldAnimation(loc, Particle.END_ROD, 5, 0.8, 2);
        EffectUtils.playCircularShieldAnimation(loc, Particle.DRIPPING_WATER, 3, 0.6, 1.2);
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade2) {
            modifiers = wp.getAbilitiesMatching(GuardianBeam.class).stream().map(ability -> ability.getCooldown().addMultiplicativeModifierMult(name + " Master", 0.55f)).toList();
        } else {
            modifiers = Collections.emptyList();
        }
        PlayerFilter.playingGame(wp.getGame()).teammatesOf(wp).forEach(teammate -> {
            new CooldownFilter<>(teammate, RegularCooldown.class).filterCooldownClass(FortifyingHex.class).filterCooldownFrom(wp).forEach(cd -> {
                cd.setTicksLeft(cd.getTicksLeft() + hexTickDurationIncrease);
                stats.hexesProlonged++;
            });
            boolean isSelf = wp == teammate;
            teammate.getCooldownManager()
                    .addCooldown(new RegularCooldown<>(name, isSelf ? "SANCTUARY" : null, Sanctuary.class, new Sanctuary(), wp, CooldownTypes.ABILITY, cooldownManager -> {
                    }, cooldownManager -> {
                        if (isSelf) {
                            modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
                        }
                    }, false, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                        if (wp.isDead()) {
                            cooldown.setTicksLeft(0);
                        }
                    })
                    ) {

                        @Override
                        @Priority(-10)
                        public float modifyDamageAfterInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                            EnumSet<InstanceFlags> flags = event.getFlags();
                            if (flags.contains(InstanceFlags.RECURSIVE)) {
                                return currentDamageValue;
                            }
                            int hexStacks = (int) new CooldownFilter<>(event.getWarlordsEntity(), RegularCooldown.class).filterCooldownFrom(wp)
                                                                                                                        .filterCooldownClass(FortifyingHex.FortifyingHexData.class)
                                                                                                                        .stream()
                                                                                                                        .count();
                            if (hexStacks < FortifyingHex.getFromHex(wp).getMaxStacks()) {
                                return currentDamageValue;
                            }
                            FortifyingHex fromHex = FortifyingHex.getFromHex(wp);
                            float damageToReflect = (float) (currentDamageValue * (1 - Math.pow(convertToDivisionDecimal(fromHex.getDamageReduction()
                                                                                                                                .getCalculatedValue() + additionalDamageReduction),
                                    3
                            )));
                            Utils.playGlobalSound(wp.getLocation(), Sound.ENTITY_VEX_HURT, 1, 1.9f);
                            event.getSource()
                                 .addInstance(InstanceBuilder.damage()
                                                             .cause(name)
                                                             .source(wp)
                                                             .value(damageToReflect)
                                                             .flags(InstanceFlags.RECURSIVE, InstanceFlags.REFLECTIVE_DAMAGE)
                                                             .flag(InstanceFlags.TRUE_DAMAGE, pveMasterUpgrade));
                            stats.totalDamageReflected += damageToReflect;
                            return (float) (currentDamageValue * Math.pow(convertToDivisionDecimal(additionalDamageReduction), 3));
                        }

                        @Override
                        protected Listener getListener() {
                            if (!isSelf) {
                                return null;
                            }
                            return new Listener() {

                                @EventHandler(priority = EventPriority.LOWEST)
                                private void onAddCooldown(WarlordsAddCooldownEvent event) {
                                    AbstractCooldown<?> cooldown = event.getAbstractCooldown();
                                    if (!Objects.equals(cooldown.getFrom(), wp) || !(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                                        return;
                                    }
                                    Object cdObject = cooldown.getCooldownObject();
                                    if (cdObject instanceof FortifyingHex) {
                                        regularCooldown.setTicksLeft(regularCooldown.getTicksLeft() + hexTickDurationIncrease);
                                        stats.hexesProlonged++;
                                    }
                                    if (pveMasterUpgrade2 && !event.getWarlordsEntity().equals(wp) && cdObject instanceof GuardianBeam.GuardianBeamShield guardianBeamShield) {
                                        float oldShieldPercent = guardianBeamShield.getShieldPercent() / 100f;
                                        float newShieldPercent = oldShieldPercent + .15f;
                                        float newShieldHealth = guardianBeamShield.getMaxShieldHealth() / oldShieldPercent * newShieldPercent;
                                        guardianBeamShield.setMaxShieldHealth(newShieldHealth);
                                        guardianBeamShield.setShieldHealth(newShieldHealth);
                                    }
                                }
                            };
                        }
                    });
        });
        return true;
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new SanctuaryBranch(abilityTree, this);
    }

    public static class SanctuaryStats extends AbstractAbilityStats<Sanctuary, SanctuaryStats> {

        @Field("hexes_prolonged")
        private int hexesProlonged = 0;

        @Field("hexes_not_consumed")
        private int hexesNotConsumed = 0;

        @Field("total_damage_reflected")
        private float totalDamageReflected = 0;

        @Override
        public Class<SanctuaryStats> getClazz() {
            return SanctuaryStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Hexes Prolonged", hexesProlonged));
            statsDisplay.add(new AbilityStatDisplay("Total Damage Reflected", totalDamageReflected));
            statsDisplay.add(new AbilityStatDisplay("Hexes Not Consumed", hexesNotConsumed));
            return statsDisplay;
        }

        @Override
        public SanctuaryStats merge(SanctuaryStats other, int multiplier) {
            SanctuaryStats stats = super.merge(other, multiplier);
            stats.hexesProlonged = this.hexesProlonged + other.hexesProlonged * multiplier;
            stats.hexesNotConsumed = this.hexesNotConsumed + other.hexesNotConsumed * multiplier;
            stats.totalDamageReflected = this.totalDamageReflected + other.totalDamageReflected * multiplier;
            return stats;
        }

        @Override
        public SanctuaryStats create() {
            return new SanctuaryStats();
        }

        public int getHexesNotConsumed() {
            return hexesNotConsumed;
        }

        public void setHexesNotConsumed(int hexesNotConsumed) {
            this.hexesNotConsumed = hexesNotConsumed;
        }

    }

}
