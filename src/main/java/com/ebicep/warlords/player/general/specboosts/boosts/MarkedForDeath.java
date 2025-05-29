package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.HolyRadianceAvenger;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsAddCooldownEvent;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.AbstractCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.type.DamageInstance;
import org.bukkit.event.EventHandler;

import java.util.List;

public class MarkedForDeath implements SpecBoostManager.SpecBoost<MarkedForDeath> {

    private float avengerMarkDamage;
    private float avengerMarkSlowPercent;
    private float avengerMarkIncreaseDamagePercent;
    private int holyRadianceCooldownReductionTicks;
    private float holyRadianceEnergyCost;

    @Override
    public void init() {
        this.avengerMarkDamage = getValue("avengerMarkDamage", float.class);
        this.avengerMarkSlowPercent = getValue("avengerMarkSlowPercent", float.class);
        this.avengerMarkIncreaseDamagePercent = getValue("avengerMarkIncreaseDamagePercent", float.class);
        this.holyRadianceCooldownReductionTicks = getValue("holyRadianceCooldownReductionTicks", int.class);
        this.holyRadianceEnergyCost = getValue("holyRadianceEnergyCost", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "markedForDeath";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(avengerMarkDamage, avengerMarkSlowPercent, avengerMarkIncreaseDamagePercent, holyRadianceCooldownReductionTicks, holyRadianceEnergyCost);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public MarkedForDeath get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(HolyRadianceAvenger.class).forEach(holyRadiance -> {
                holyRadiance.getCooldown().addAdditiveModifier("Spec Boost", -holyRadianceCooldownReductionTicks / holyRadiance.getCooldownValue());
                holyRadiance.getEnergyCost().addOverridingModifier("Spec Boost", holyRadianceEnergyCost);
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onCooldownAddEvent(WarlordsAddCooldownEvent event) {
            AbstractCooldown<?> cooldown = event.getAbstractCooldown();
            if (!(cooldown instanceof RegularCooldown<?> regularCooldown)) {
                return;
            }
            if (!(cooldown.getName().equals("Avenger's Mark") || !cooldown.getFrom().equals(warlordsEntity))) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            target.addInstance(InstanceBuilder
                    .damage()
                    .cause(getStringName())
                    .source(warlordsEntity)
                    .value(100)
            );
            target.addSpeedModifier(warlordsEntity, getStringName(), -avengerMarkSlowPercent, regularCooldown);
            regularCooldown.addExtraDamageInstance(new DamageInstance() {
                @Override
                public float modifyDamageBeforeInterveneFromSelf(WarlordsDamageHealingEvent event, float currentDamageValue) {
                    return currentDamageValue * AbstractAbility.convertToMultiplicationDecimal(avengerMarkIncreaseDamagePercent);
                }
            });

        }

    }

}
