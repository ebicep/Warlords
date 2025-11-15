package com.ebicep.warlords.abilities;

import com.ebicep.warlords.abilities.internal.*;
import com.ebicep.warlords.abilities.internal.icon.OrangeAbilityIcon;
import com.ebicep.warlords.database.repositories.config.ConfigManager;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.events.player.ingame.pve.WarlordsApplyBurnEffectEvent;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.pve.mobs.events.boltarobonanza.EventBoltaroShadow;
import com.ebicep.warlords.pve.upgrades.AbilityTree;
import com.ebicep.warlords.pve.upgrades.AbstractUpgradeBranch;
import com.ebicep.warlords.pve.upgrades.mage.pyromancer.InfernoBranch;
import com.ebicep.warlords.util.warlords.Utils;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.annotation.Nonnull;
import java.util.*;

public class Inferno extends AbstractAbility implements OrangeAbilityIcon, Duration, AbilityStats<Inferno, Inferno.InfernoStats> {

    private final InfernoStats stats = new InfernoStats();
    private int tickDuration = 360;
    private int critChanceIncrease = 30;
    private int critMultiplierIncrease = 30;

    public Inferno() {
        super(AbstractAbilityBuilder.create("inferno").pvp());
    }

    public Inferno(AbstractAbilityBuilder builder) {
        super(builder);
    }

    @Override
    public void init(AbstractAbilityBuilder builder) {
        super.init(builder);
        this.tickDuration = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("tickDuration"), int.class);
        this.critChanceIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("critChanceIncrease"), int.class);
        this.critMultiplierIncrease = ConfigManager.getAbilityConfigValue(builder.getNamespaces(), builder.getAppendedFieldName("critMultiplierIncrease"), int.class);
    }

    @Override
    protected boolean onActivateInternal(@Nonnull WarlordsEntity wp) {
        Utils.playGlobalSound(wp.getLocation(), "mage.inferno.activation", 2, 1);
        List<FloatModifiable.FloatModifier> modifiers;
        if (pveMasterUpgrade) {
            wp.getCooldownManager().removeCooldown(Inferno.class, false);
            modifiers = wp.getAbilitiesMatching(Fireball.class).stream().map(ability -> ability.getEnergyCost().addAdditiveModifier(name + " Master", -5)).toList();
        } else {
            modifiers = Collections.emptyList();
        }
        final Map<WarlordsEntity, Integer> hitCount = new HashMap<>();
        wp.getCooldownManager().addCooldown(new RegularCooldown<>(name, "INFR", Inferno.class, null, wp, CooldownTypes.ABILITY, cooldownManager -> {
        }, cooldownManager -> {
            modifiers.forEach(FloatModifiable.FloatModifier::forceEnd);
        }, tickDuration, Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
            if (ticksElapsed % 3 == 0) {
                Location loc = wp.getLocation().add(0, 1.2, 0);
                EffectUtils.displayParticle(Particle.DRIPPING_LAVA, loc, 1, 0.5, 0.3, 0.5, 0.4);
                EffectUtils.displayParticle(Particle.FLAME, loc, 1, 0.5, 0.3, 0.5, 0.0001);
                EffectUtils.displayParticle(Particle.CRIT, loc, 1, 0.5, 0.3, 0.5, 0.0001);
            }
        })
        ) {

            @Override
            protected Listener getListener() {
                return new Listener() {

                    @EventHandler
                    public void onWarlordsApplyBurnEffect(WarlordsApplyBurnEffectEvent event) {
                        if (pveMasterUpgrade) {
                            event.setTickPeriod(10);
                        }
                    }
                };
            }

            @Override
            public boolean distinct() {
                return true;
            }

            @Override
            public void onDeathFromEnemies(WarlordsDamageHealingEvent event, float currentDamageValue, boolean isCrit, boolean isKiller) {
                if (pveMasterUpgrade2 && isKiller) {
                    wp.addEnergy(wp, "Inferno", event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC && warlordsNPC.getMob() instanceof EventBoltaroShadow ? 10 : 30);
                }
            }
        }.addModifier(Modifier.DAMAGE_BEFORE_INTERVENE_ATTACKER, (event, currentDamageValue) -> {
            if (pveMasterUpgrade) {
                WarlordsEntity hit = event.getWarlordsEntity();
                int oldHitCount = hitCount.computeIfAbsent(hit, k -> 0);
                hitCount.put(hit, oldHitCount + 1);
                currentDamageValue.addMultiplicativeModifierMult(name, convertToMultiplicationDecimal(Math.min(50, 5 * oldHitCount)));
            } else if (pveMasterUpgrade2) {
                currentDamageValue.addMultiplicativeModifierMult(name, 1.2f);
            }
                }
        ).addModifier(Modifier.DAMAGE_CRIT_CHANCE_ATTACKER, (event, currentCritChance) -> {
                    if (event.getCause().isEmpty()) {
                        return;
                    }
                    stats.hitsAmplified++;
                    currentCritChance.addAdditiveModifier(name, critChanceIncrease);
                }
        ).addModifier(Modifier.DAMAGE_CRIT_MULTIPLIER_ATTACKER, (event, currentCritMultiplier) -> {
                    if (event.getCause().isEmpty()) {
                        return;
                    }
                    stats.hitsAmplified++;
                    currentCritMultiplier.addAdditiveModifier(name, critMultiplierIncrease);
                }
        ).addModifier(Modifier.DAMAGE_BEFORE_VARIABLE_SET_ATTACKER, event -> {
                    if (pveMasterUpgrade2 && event.getCause().equals("Ignite")) {
                        event.setMinForce(event.getMin() * 2);
                        event.setMaxForce(event.getMax() * 2);
                    }
                }
        ));
        return true;
    }

    @Override
    public void updateDescription(Player player) {
        description = AbilityDescriptionBuilder.create("Combust into a molten inferno, increasing your Crit Chance by ")
                                               .percent(critChanceIncrease, NamedTextColor.RED)
                                               .text(" and your Crit Multiplier by ")
                                               .percent(critMultiplierIncrease, NamedTextColor.RED)
                                               .text(". Lasts ")
                                               .durationTicks(tickDuration)
                                               .text(".")
                                               .build();
    }

    @Override
    public AbstractUpgradeBranch<?> getUpgradeBranch(AbilityTree abilityTree) {
        return new InfernoBranch(abilityTree, this);
    }

    @Override
    public int getTickDuration() {
        return tickDuration;
    }

    @Override
    public void setTickDuration(int tickDuration) {
        this.tickDuration = tickDuration;
    }

    @Override
    public InfernoStats getAbilityStats() {
        return stats;
    }

    public int getHitsAmplified() {
        return stats.hitsAmplified;
    }

    public int getCritChanceIncrease() {
        return critChanceIncrease;
    }

    public void setCritChanceIncrease(int critChanceIncrease) {
        this.critChanceIncrease = critChanceIncrease;
    }

    public int getCritMultiplierIncrease() {
        return critMultiplierIncrease;
    }

    public void setCritMultiplierIncrease(int critMultiplierIncrease) {
        this.critMultiplierIncrease = critMultiplierIncrease;
    }

    public static class InfernoStats extends AbstractAbilityStats<Inferno, InfernoStats> {

        @Field("hits_amplified")
        private int hitsAmplified = 0;

        @Override
        public Class<InfernoStats> getClazz() {
            return InfernoStats.class;
        }

        @Override
        public List<AbilityStatDisplay> getStatsDisplay() {
            List<AbilityStatDisplay> statsDisplay = new ArrayList<>(super.getStatsDisplay());
            statsDisplay.add(new AbilityStatDisplay("Hits Amplified", hitsAmplified));
            return statsDisplay;
        }

        @Override
        public InfernoStats merge(InfernoStats other, int multiplier) {
            InfernoStats stats = super.merge(other, multiplier);
            stats.hitsAmplified = this.hitsAmplified + other.hitsAmplified * multiplier;
            return stats;
        }

        @Override
        public InfernoStats create() {
            return new InfernoStats();
        }

    }

}
