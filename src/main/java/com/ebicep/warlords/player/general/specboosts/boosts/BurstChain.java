package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.Inferno;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.*;

public class BurstChain implements SpecBoostManager.SpecBoost<BurstChain> {

    private int healthDecrease;
    private float baseSpeedIncreasePercent;
    private float velocityIncreasePercentage;
    private int guaranteedCrit;
    private float damageIncreasePercent;
    private float infernoDamageIncreasePercent;
    private Set<String> damageReductionAbilities;

    @Override
    public void init() {
        this.healthDecrease = getValue("healthDecrease", int.class);
        this.baseSpeedIncreasePercent = getValue("baseSpeedIncreasePercent", float.class);
        this.velocityIncreasePercentage = getValue("velocityIncreasePercentage", float.class);
        this.guaranteedCrit = getValue("guaranteedCrit", int.class);
        this.damageIncreasePercent = getValue("damageIncreasePercent", float.class);
        this.infernoDamageIncreasePercent = getValue("infernoDamageIncreasePercent", float.class);
        this.damageReductionAbilities = new HashSet<>(getListValue("damageReductionAbilities", String.class));
    }

    @Override
    public String getConfigFieldName() {
        return "burstChain";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                healthDecrease,
                baseSpeedIncreasePercent,
                velocityIncreasePercentage,
                guaranteedCrit,
                damageIncreasePercent,
                infernoDamageIncreasePercent
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public BurstChain get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private final Map<UUID, Integer> flameBurstHit = new HashMap<>();
        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost (Base)", -healthDecrease);
            warlordsPlayer.getSpeed().addBaseModifier(baseSpeedIncreasePercent);
            warlordsPlayer.getAbilitiesMatching(FlameBurst.class).forEach(flameBurst -> {
                flameBurst.getProjectileSpeed().addModifier(FloatModifiable.ModifierType.ADDITIVE_MULTIPLIER, "Spec Boost", (velocityIncreasePercentage + 100) / 100);
            });
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                        WarlordsEntity victim = event.getWarlordsEntity();
                        if (victim.getCooldownManager()
                                  .getCooldowns()
                                  .stream()
                                  .map(AbstractCooldown::getName)
                                  .noneMatch(damageReductionAbilities::contains)
                        ) {
                            boolean hasInferno = warlordsPlayer.getCooldownManager().hasCooldown(Inferno.class);
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLIER, getStringName(),
                                    AbstractAbility.convertToMultiplicationDecimal(hasInferno ? infernoDamageIncreasePercent :
                                                                                   damageIncreasePercent)
                            );
                        }
                    }
            ));
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamageHeal(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!event.getCause().equals("Flame Burst")) {
                return;
            }
            UUID projectileId = event.getUUID();
            Integer hitCount = flameBurstHit.get(projectileId);
            if (hitCount != null && hitCount >= guaranteedCrit) {
                return;
            }
            int newCount = (hitCount == null ? 0 : hitCount) + 1;
            flameBurstHit.put(projectileId, newCount);
            if (newCount <= guaranteedCrit) {
                event.getCritChance().addModifier(FloatModifiable.ModifierType.OVERRIDING, getStringName(), 100);
            }
            if (flameBurstHit.size() > 64) {
                flameBurstHit.entrySet().removeIf(e -> e.getValue() >= guaranteedCrit);
            }
        }

        @Override
        public void unapply(WarlordsPlayer warlordsPlayer) {
            flameBurstHit.clear();
        }

    }

}