package com.ebicep.warlords.player.general.specboosts.boosts;

import com.ebicep.warlords.player.general.specboosts.SpecBoostManager;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.util.warlords.modifiablevalues.FloatModifiable;

import java.util.List;

public class VitalityBoost implements SpecBoostManager.SpecBoost<VitalityBoost> {

    private int healthIncrease;
    private int passiveRegen;

    @Override
    public void init() {
        this.healthIncrease = getValue("healthIncrease", int.class);
        this.passiveRegen = getValue("passiveRegen", int.class);
    }

    @Override
    public String getConfigFieldName() {
        return "vitalityBoost";
    }

    @Override
    public List<Object> getVariables() {
        return List.of(healthIncrease, passiveRegen);
    }

    @Override
    public SpecBoostManager.Boost create() {
        return new Boost();
    }

    @Override
    public VitalityBoost get() {
        return this;
    }

    public class Boost implements SpecBoostManager.Boost {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getHealth().addModifier(FloatModifiable.ModifierType.ADDITIVE, "Spec Boost (Base)", healthIncrease);
            warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                    getStringName(),
                    null,
                    Boost.class,
                    null,
                    warlordsPlayer,
                    CooldownTypes.SPEC_BOOST,
                    cooldownManager -> {
                    },
                    false,
                    (cooldown, ticksElapsed) -> {
                        if (ticksElapsed % 20 == 0) {
                            warlordsPlayer.addInstance(InstanceBuilder
                                    .healing()
                                    .cause(getConfigFieldName())
                                    .source(warlordsPlayer)
                                    .value(passiveRegen)
                                    .flags(InstanceFlags.NO_MESSAGE, InstanceFlags.NO_HIT_SOUND, InstanceFlags.TRUE_HEALING)
                            );
                        }
                    }
            ));
        }

    }

}
