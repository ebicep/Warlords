package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.abilities.Soulbinding;
import com.ebicep.warlords.abilities.SpiritLink;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownFilter;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PersistentCooldown;
import com.ebicep.warlords.player.ingame.instances.type.Modifier;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class PermeatingLink implements SpecBoostManager.SpecBoost<PermeatingLink> {

    private float meleeDamageIncreasePercent;
    private float spiritLinkDamageReductionDecreasePercent;
    private float spiritLinkDamageToSoulboundIncreasePercent;

    @Override
    public void init() {
        this.meleeDamageIncreasePercent = getValue("meleeDamageIncreasePercent", float.class);
        this.spiritLinkDamageReductionDecreasePercent = getValue("spiritLinkDamageReductionDecreasePercent", float.class);
        this.spiritLinkDamageToSoulboundIncreasePercent = getValue("spiritLinkDamageToSoulboundIncreasePercent", float.class);
    }

    @Override
    public String getConfigFieldName() {
        return "permeatingLink";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(meleeDamageIncreasePercent, spiritLinkDamageReductionDecreasePercent, spiritLinkDamageToSoulboundIncreasePercent);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public PermeatingLink get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getAbilitiesMatching(SpiritLink.class).forEach(spiritLink -> {
                spiritLink.setDamageReduction(spiritLink.getDamageReduction() - spiritLinkDamageReductionDecreasePercent);
            });

            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {},
                    false
            ).addModifier(Modifier.MODIFY_OUTGOING_DAMAGE_BEFORE_INTERVENE, (event, currentDamageValue) -> {
                        if (event.getCause().isEmpty()) {
                            currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE,
                                    getStringName(),
                                    AbstractAbility.convertToMultiplicationDecimal(meleeDamageIncreasePercent)
                            );
                            return;
                        }
                        if (event.getAbility() instanceof SpiritLink) {
                            boolean boundPlayer = new CooldownFilter<>(warlordsPlayer, PersistentCooldown.class)
                                    .filterCooldownClassAndMapToObjectsOfClass(Soulbinding.SoulbindingData.class)
                                    .anyMatch(soulbindingData -> soulbindingData.hasBoundPlayer(event.getWarlordsEntity()));
                            if (boundPlayer) {
                                currentDamageValue.addModifier(FloatModifiable.ModifierType.MULTIPLICATIVE_MULTIPLICATIVE, getStringName(),
                                        AbstractAbility.convertToMultiplicationDecimal(spiritLinkDamageToSoulboundIncreasePercent)
                                );
                            }
                        }
                    }
            ));
        }

    }

}
