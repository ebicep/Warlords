package com.ebicep.warlords.pve.newitems.setbonus.sets;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.player.ingame.instances.InstanceFlags;
import com.ebicep.warlords.pve.mobs.flags.BossLike;
import com.ebicep.warlords.pve.newitems.setbonus.BaseSet;
import com.ebicep.warlords.pve.newitems.setbonus.SetBonus;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Objects;

public class TruePurpose extends BaseSet {

    @Override
    public Bonus create() {
        return new Bonus();
    }

    @Override
    public List<Object> getVariables() {
        return List.of();
    }

    @Override
    public String getConfigFieldName() {
        return "truePurpose";
    }

    public class Bonus implements SetBonus.Bonus {

        @Override
        public void apply(WarlordsPlayer warlordsPlayer) {
            warlordsPlayer.getGame().registerEvents(new Listener() {

                @EventHandler
                public void onDamageHeal(WarlordsDamageHealingEvent event) {
                    if (!Objects.equals(event.getSource(), warlordsPlayer)) {
                        return;
                    }
                    if (event.getWarlordsEntity() instanceof WarlordsNPC warlordsNPC) {
                        if (warlordsNPC.getMob() instanceof BossLike) {
                            return;
                        }
                        event.getFlags().add(InstanceFlags.PIERCE);
                    }
                }

            });
        }

    }

}
