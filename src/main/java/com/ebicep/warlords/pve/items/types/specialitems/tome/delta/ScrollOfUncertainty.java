package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class ScrollOfUncertainty extends SpecialDeltaTome {

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
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageheal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (ThreadLocalRandom.current().nextDouble() < 0.02) {
                    event.setMin(event.getMax());
                }
            }

        });

    }
}
