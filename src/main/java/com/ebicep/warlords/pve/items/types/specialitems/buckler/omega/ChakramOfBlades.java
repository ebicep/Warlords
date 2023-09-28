package com.ebicep.warlords.pve.items.types.specialitems.buckler.omega;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Objects;
import java.util.Set;

public class ChakramOfBlades extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public ChakramOfBlades() {
    }

    public ChakramOfBlades(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Chakram of Blades";
    }

    @Override
    public String getBonus() {
        return "For every mob on the field, increase your damage by 1%.";
    }

    @Override
    public String getDescription() {
        return "A sword AND a shield? This is revolutionary!";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (event.isHealingInstance()) {
                    return;
                }
                float damageBoost = getDamageBoost();
                event.setMin(event.getMin() * damageBoost);
                event.setMax(event.getMax() * damageBoost);
            }

            private float getDamageBoost() {
                int mobCount = pveOption.mobCount();
                return 1 + (mobCount * .01f);
            }
        });
    }

}
