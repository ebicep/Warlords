package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.ArcaneShield;
import com.ebicep.warlords.abilities.FlameBurst;
import com.ebicep.warlords.abilities.Inferno;
import com.ebicep.warlords.abilities.TimeWarpPyromancer;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import org.bukkit.event.EventHandler;

import java.util.*;

public class BurstChain implements SpecBoostManager.SpecBoost<BurstChain> {

    private int healthDecrease;
    private float baseSpeedIncreasePercent;
    private float timeWarpCooldownReductionSeconds;
    private float arcaneShieldCooldownReductionSeconds;
    private float velocityIncreasePercentage;
    private int guaranteedCrit;
    private float damageIncreasePercent;
    private float infernoDamageIncreasePercent;
    private Set<String> damageReductionAbilities;

    @Override
    public void init() {
        this.healthDecrease = getValue("healthDecrease", int.class);
        this.baseSpeedIncreasePercent = getValue("baseSpeedIncreasePercent", float.class);
        this.timeWarpCooldownReductionSeconds = getValue("timeWarpCooldownReductionSeconds", float.class);
        this.arcaneShieldCooldownReductionSeconds = getValue("arcaneShieldCooldownReductionSeconds", float.class);
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
                timeWarpCooldownReductionSeconds,
//                arcaneShieldCooldownReductionSeconds,
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
            warlordsPlayer.getHealth().addAdditiveModifier("Spec Boost (Base)", -healthDecrease);
            warlordsPlayer.getSpeed().addBaseModifier(baseSpeedIncreasePercent);
            warlordsPlayer.getAbilitiesMatching(TimeWarpPyromancer.class).forEach(timeWarp -> {
                timeWarp.getCooldown().addAdditiveModifier("Spec Boost", -timeWarpCooldownReductionSeconds);
            });
            warlordsPlayer.getAbilitiesMatching(ArcaneShield.class).forEach(arcaneShield -> {
                arcaneShield.getCooldown().addAdditiveModifier("Spec Boost", -arcaneShieldCooldownReductionSeconds);
            });
            warlordsPlayer.getAbilitiesMatching(FlameBurst.class).forEach(flameBurst -> {
                flameBurst.getProjectileSpeed().addMultiplicativeModifierAdd("Spec Boost", (velocityIncreasePercentage + 100) / 100);
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
            ) {

                @Override
                public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    WarlordsEntity victim = event.getWarlordsEntity();
                    if (victim.getCooldownManager()
                              .getCooldowns()
                              .stream()
                              .map(AbstractCooldown::getName)
                              .noneMatch(damageReductionAbilities::contains)
                    ) {
                        boolean hasInferno = warlordsPlayer.getCooldownManager().hasCooldown(Inferno.class);
                        return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(hasInferno ? infernoDamageIncreasePercent : damageIncreasePercent);
                    }
                    return currentDamageValue;
                }

            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamageHeal(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!event.getCause().equals("Flame Burst")) {
                return;
            }
            Integer hitCount = flameBurstHit.get(event.getUUID());
            if (hitCount != null && hitCount >= guaranteedCrit) {
                return;
            }
            flameBurstHit.put(event.getUUID(), (hitCount == null ? 0 : hitCount) + 1);
            event.setCritChance(100);
        }

    }

}