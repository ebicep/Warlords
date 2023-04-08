package com.ebicep.warlords.pve.items.types.specialitems.buckler.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.util.warlords.GameRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class PridwensBulwark extends SpecialDeltaBuckler {

    @Override
    public String getName() {
        return "Pridwen's Bulwark";
    }

    @Override
    public String getBonus() {
        return "+10% chance for Seismic Wave to IMMOBILIZE enemies for 0.5 seconds.";
    }

    @Override
    public String getDescription() {
        return "Say hello to Mary!";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeall(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (event.getWarlordsEntity() instanceof WarlordsNPC) {
                    WarlordsNPC warlordsNPC = (WarlordsNPC) event.getWarlordsEntity();
                    if (!Objects.equals(event.getAbility(), "Seismic Wave")) {
                        return;
                    }
                    if (ThreadLocalRandom.current().nextDouble() > 0.1) {
                        return;
                    }
                    //delayed to account for wave kb
                    new GameRunnable(warlordsNPC.getGame()) {

                        @Override
                        public void run() {
                            warlordsNPC.setStunTicks(10);
                        }
                    }.runTaskLater(3);
                }
            }

        });
    }

}
