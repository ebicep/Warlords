package com.ebicep.warlords.pve.items.types.specialitems.tome.delta;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;

public class AGuideToMMA extends SpecialDeltaTome {

    @Override
    public String getName() {
        return "mmmpffgfg: A Guide to MMA";
    }

    @Override
    public String getBonus() {
        return "Bosses take 25% more damage.";
    }

    @Override
    public String getDescription() {
        return "Dana White approved!";
    }

    @Override
    public Classes getClasses() {
        return Classes.WARRIOR;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageheal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (event.getWarlordsEntity() instanceof WarlordsNPC) {
                    WarlordsNPC warlordsNPC = (WarlordsNPC) event.getWarlordsEntity();
                    if (warlordsNPC.getMobTier() == MobTier.BOSS) {
                        event.setMin(event.getMin() * 1.25f);
                        event.setMax(event.getMax() * 1.25f);
                    }
                }
            }

        });

    }
}
