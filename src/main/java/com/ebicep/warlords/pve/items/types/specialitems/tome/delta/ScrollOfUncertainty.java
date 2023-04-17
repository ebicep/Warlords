package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.specialitems.CraftsInto;
import com.ebicep.warlords.pve.items.types.specialitems.tome.omega.TomeOfTheft;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class ScrollOfUncertainty extends SpecialDeltaTome implements CraftsInto {

    public ScrollOfUncertainty(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public ScrollOfUncertainty() {

    }

    @Override
    public String getName() {
        return "Scroll of Uncertainty";
    }

    @Override
    public String getBonus() {
        return "+2% chance to deal the max amount of damage any attack can do.";
    }

    @Override
    public String getDescription() {
        return "I'm positive this is worth the read.";
    }

    @Override
    public Classes getClasses() {
        return Classes.ROGUE;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() < 0.02) {
                    event.setMin(event.getMax());
                }
            }

        });

    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new TomeOfTheft(statPool);
    }
}
