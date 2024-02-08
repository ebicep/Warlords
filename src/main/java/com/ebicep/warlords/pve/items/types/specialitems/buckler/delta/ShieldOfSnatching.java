package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.buckler.omega.ChakramOfBlades;

import java.util.Set;

public class ShieldOfSnatching extends SpecialDeltaBuckler implements CraftsInto {

    public ShieldOfSnatching() {
    }

    public ShieldOfSnatching(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Kinda looks like a hand... Is that my wallet?";
    }

    @Override
    public String getBonus() {
        return "For every player below 75% health, you heal 8% more health.";
    }

    @Override
    public String getName() {
        return "Shield of Pillaging";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                ShieldOfSnatching.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float modifyHealingFromAttacker(WarlordsDamageHealingEvent event, float currentHealValue) {
                return currentHealValue * (1 + playersBelowThreshold() * 0.08f);
            }

            private long playersBelowThreshold() {
                return warlordsPlayer.getGame()
                                     .warlordsPlayers()
                                     .filter(p -> p.getCurrentHealth() / p.getMaxHealth() < 0.75)
                                     .count();
            }
        });
    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new ChakramOfBlades(statPool);
    }
}
