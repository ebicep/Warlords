package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta.GardeningGloves;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.MysticksManualVol23H;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class BruisedBook extends SpecialDeltaTome implements CraftsInto {

    public BruisedBook() {

    }

    public BruisedBook(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Wouldn't a book bruise a person, not the other way around?";
    }

    @Override
    public String getBonus() {
        return "All your healing has a 40% chance to heal for max health, and a 60% chance to heal for no health.";
    }

    @Override
    public String getName() {
        return "Bruised Book";
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
            public void healingDoBeforeVariableSetFromAttacker(WarlordsDamageHealingEvent event) {
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
        return new MysticksManualVol23H(statPool);
    }
}
