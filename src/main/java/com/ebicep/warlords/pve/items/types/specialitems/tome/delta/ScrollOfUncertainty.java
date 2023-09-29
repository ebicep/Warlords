package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.TomeOfTheft;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ScrollOfUncertainty extends SpecialDeltaTome implements CraftsInto {

    public ScrollOfUncertainty() {

    }

    public ScrollOfUncertainty(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Scroll of Uncertainty";
    }

    @Override
    public String getBonus() {
        return "All your attacks have a 40% chance to do max damage, and a 60% chance to do no damage.";
    }

    @Override
    public String getDescription() {
        return "I'm positive this is worth the read.";
    }


    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                ScrollOfUncertainty.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public void damageDoBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
                if (!event.getWarlordsEntity().equals(warlordsPlayer)) {
                    if (ThreadLocalRandom.current().nextDouble() <= .4) {
                        event.setMin(event.getMax());
                    } else {
                        event.setMin(0);
                        event.setMax(0);
                    }
                }
            }
        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new TomeOfTheft(statPool);
    }
}
