package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.NaturesClaws;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GardeningGloves extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {

    public GardeningGloves() {

    }

    public GardeningGloves(Set<BasicStatPool> statPool) {
        super(statPool);
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                GardeningGloves.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                if (ThreadLocalRandom.current().nextDouble() <= .15) {
                    return currentHealValue * 1.3f;
                }
                return currentHealValue;
            }
        });
    }

    @Override
    public String getName() {
        return "Gardening Gloves";
    }

    @Override
    public String getBonus() {
        return "15% to heal +30% more health.";
    }

    @Override
    public String getDescription() {
        return "Save the Earth, ride a... dolphin?";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new NaturesClaws(statPool);
    }
}
