package com.ebicep.warlords.pve.items.types.specialitems.tome.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.cooldowns.CooldownTypes;
import com.ebicep.warlords.player.ingame.cooldowns.cooldowns.PermanentCooldown;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;

import java.util.Set;

public class ScrollOfScripts extends SpecialOmegaTome implements AppliesToWarlordsPlayer {
    public static int numberOfPlayersAbove75(WarlordsPlayer warlordsPlayer) {
        return warlordsPlayer.getGame()
                             .warlordsPlayers()
                             .filter(player -> player.getCurrentHealth() > player.getMaxHealth() * .75)
                             .mapToInt(player -> 1)
                             .sum();
    }

    public ScrollOfScripts() {

    }

    public ScrollOfScripts(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getDescription() {
        return "Bwahahahaha!!!!";
    }

    @Override
    public String getBonus() {
        return "For every player that is above 75% health, deal 5% more damage.";
    }

    @Override
    public String getName() {
        return "Scroll of Sanguinity";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getCooldownManager().addCooldown(new PermanentCooldown<>(
                getName(),
                null,
                ScrollOfScripts.class,
                null,
                warlordsPlayer,
                CooldownTypes.ITEM,
                cooldownManager -> {

                },
                false
        ) {
            @Override
            public float modifyDamageBeforeInterveneFromAttacker(WarlordsDamageHealingEvent event, float currentDamageValue) {
                return currentDamageValue * (1 + .05f * numberOfPlayersAbove75(warlordsPlayer));
            }
        });
    }

}
