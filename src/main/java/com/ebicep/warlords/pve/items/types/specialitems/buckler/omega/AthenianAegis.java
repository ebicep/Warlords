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

public class AthenianAegis extends SpecialOmegaBuckler implements AppliesToWarlordsPlayer {

    public AthenianAegis() {
    }

    public AthenianAegis(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Athenian Aegis";
    }

    @Override
    public String getBonus() {
        return "For every mob on the field, increase your ability to heal by 1%.";
    }

    @Override
    public String getDescription() {
        return "It's covered in olive oil. No, it doesn't come off.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onDamageHeal(WarlordsDamageHealingEvent event) {
                if (!Objects.equals(event.getAttacker(), warlordsPlayer)) {
                    return;
                }
                if (event.isDamageInstance()) {
                    return;
                }
                float healingBoost = getHealingBoost();
                event.setMin(event.getMin() * healingBoost);
                event.setMax(event.getMax() * healingBoost);
            }

            private float getHealingBoost() {
                int mobCount = pveOption.mobCount();
                return 1 + (mobCount * .01f);
            }
        });
    }

}
