package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.RemedicChains;
import com.ebicep.warlords.abilities.SoothingElixir;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.RegularCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;
import org.bukkit.event.EventHandler;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class AuraOfRestoration implements SpecBoostManager.SpecBoost<AuraOfRestoration> {

    private float soothingElixirCooldownIncreaseSeconds;
    private float puddleHealingIncreasePercent;
    private float remedicChainsBreakRadiusIncrease;

    @Override
    public void init() {
        this.soothingElixirCooldownIncreaseSeconds = getValue("soothingElixirCooldownIncreaseSeconds", float.class);
        this.puddleHealingIncreasePercent = getValue("puddleHealingIncreasePercent", float.class);
        this.remedicChainsBreakRadiusIncrease = getValue("remedicChainsBreakRadiusIncrease", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "auraOfRestoration";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(
                soothingElixirCooldownIncreaseSeconds,
                puddleHealingIncreasePercent,
                remedicChainsBreakRadiusIncrease
        );
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public AuraOfRestoration get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        private WarlordsEntity warlordsEntity;

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            this.warlordsEntity = warlordsPlayer;
            warlordsPlayer.getAbilitiesMatching(SoothingElixir.class).forEach(soothingElixir -> {
                soothingElixir.getCooldown().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost", soothingElixirCooldownIncreaseSeconds);
                soothingElixir.getHealValues().getElixirDOTHealing()
                              .forEachValue(floatModifiable -> floatModifiable.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_ADDITIVE,
                                      "Spec Boost", puddleHealingIncreasePercent / 100
                              ));
            });
            warlordsPlayer.getAbilitiesMatching(RemedicChains.class).forEach(remedicChains -> {
                remedicChains.setLinkBreakRadius((int) (remedicChains.getLinkBreakRadius() + remedicChainsBreakRadiusIncrease));
            });
        }

        @EventHandler(ignoreCancelled = true)
        public void onDamageHeal(WarlordsDamageHealingEvent event) {
            if (!event.getSource().equals(warlordsEntity)) {
                return;
            }
            if (!(event.getAbility() instanceof SoothingElixir soothingElixir)) {
                return;
            }
            WarlordsEntity target = event.getWarlordsEntity();
            EnumSet<InstanceFlags> flags = event.getFlags();
            boolean initialHeal = !flags.contains(InstanceFlags.DOT);
            if (initialHeal) {
                target.getCooldownManager().addCooldown(new RegularCooldown<>(
                        getStringName(),
                        null,
                        Boost.class,
                        null,
                        warlordsEntity,
                        CooldownTypes.SPEC_BOOST,
                        cooldownManager -> {
                            target.addInstance(InstanceBuilder
                                    .healing()
                                    .ability(soothingElixir)
                                    .source(warlordsEntity)
                                    .value(soothingElixir.getHealValues().getElixirDOTHealing())
                                    .flags(InstanceFlags.DOT, InstanceFlags.AURA_OF_RESTORATION_SOOTHING_ELIXIR)
                            );
                        },
                        soothingElixir.getPuddleTickDuration(),
                        Collections.singletonList((cooldown, ticksLeft, ticksElapsed) -> {
                            if (ticksElapsed % 20 == 0 && ticksElapsed != 0) {
                                target.addInstance(InstanceBuilder
                                        .healing()
                                        .ability(soothingElixir)
                                        .source(warlordsEntity)
                                        .value(soothingElixir.getHealValues().getElixirDOTHealing())
                                        .flags(InstanceFlags.DOT, InstanceFlags.AURA_OF_RESTORATION_SOOTHING_ELIXIR)
                                );
                            }
                        })
                ));
            } else {
                if (target.getCooldownManager().hasCooldown(Boost.class) && !flags.contains(InstanceFlags.AURA_OF_RESTORATION_SOOTHING_ELIXIR)) {
                    event.setCancelled(true);
                }
            }
        }

    }

}
