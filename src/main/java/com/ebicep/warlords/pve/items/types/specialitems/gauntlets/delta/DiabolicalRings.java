package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class DiabolicalRings extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {

    public DiabolicalRings() {

    }

    public DiabolicalRings(Set<BasicStatPool> statPool) {
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
                CooldownTypes.WEAPON,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public void damageDoBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer) && ThreadLocalRandom.current().nextDouble() <= .02) {
                    event.setMin(event.getMax());
                }
            }
        });
    }

    @Override
    public String getName() {
        return "Diabolical Rings";
    }

    @Override
    public String getBonus() {
        return "+2% chance to deal the max amount of damage when hitting any target.";
    }

    @Override
    public String getDescription() {
        return "I wonder if I could make a pentagram with enough of these...";
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return null;
    }
}
