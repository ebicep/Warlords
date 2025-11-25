package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.AvengersStrike;
import com.ebicep.warlords.abilities.AvengersWrath;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.List;

public class ArmOfTheAlmighty implements SpecBoostManager.SpecBoost<ArmOfTheAlmighty> {

    private int cleaveTargets;
    private float cleaveRange;
    private float cleaveDamagePercent;
    private int energyStealPerCleave;
    private float wrathDamageBoostPercent;

    @Override
    public void init() {
        this.cleaveTargets = getValue("cleaveTargets", int.class);
        this.cleaveRange = getValue("cleaveRange", float.class);
        this.cleaveDamagePercent = getValue("cleaveDamagePercent", float.class);
        this.energyStealPerCleave = getValue("energyStealPerCleave", int.class);
        this.wrathDamageBoostPercent = getValue("wrathDamageBoostPercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "armOfTheAlmighty";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(cleaveTargets, cleaveRange, cleaveDamagePercent, energyStealPerCleave, wrathDamageBoostPercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public ArmOfTheAlmighty get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    ArmOfTheAlmighty.Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                        if (!(event.getAbility() instanceof AvengersStrike) || !warlordsPlayer.getCooldownManager().hasCooldown(AvengersWrath.AvengersWrathData.class)) {
                            return;
                        }
                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE,
                        getStringName(),
                        AbstractAbility.convertToMultiplicationDecimal(wrathDamageBoostPercent)
                );
                    }
            ));
        }

        @EventHandler
        public void onDamageHealFinalEvent(WarlordsDamageHealingFinalEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof AvengersStrike avengersStrike)) {
                return;
            }
            if (event.getInstanceFlags().contains(InstanceFlags.RECURSIVE)) {
                return;
            }
            boolean wrathActive = warlordsEntity.getCooldownManager().hasCooldown(AvengersWrath.AvengersWrathData.class);
            if (wrathActive) {
                return;
            }
            WarlordsEntity hit = event.getWarlordsEntity();
            PlayerFilter.entitiesAround(hit, cleaveRange, cleaveRange, cleaveRange)
                        .excluding(hit)
                        .aliveEnemiesOf(warlordsEntity)
                        .limit(cleaveTargets)
                        .forEach(target -> {
                            target.addInstance(
                                    InstanceBuilder.damage()
                                                   .ability(avengersStrike)
                                                   .source(warlordsEntity)
                                                   .value(event.getValue() * cleaveDamagePercent / 100f)
                                                   .showAsCrit(event.isCrit())
                                                   .flags(InstanceFlags.RECURSIVE)
                            );
                            AvengersStrike.AvengersStrikeStats stats = avengersStrike.getAbilityStats();
                            stats.setEnergyStole(stats.getEnergyStole() + target.subtractEnergy(getStringName(), energyStealPerCleave, true));
                        });
        }

    }

}
