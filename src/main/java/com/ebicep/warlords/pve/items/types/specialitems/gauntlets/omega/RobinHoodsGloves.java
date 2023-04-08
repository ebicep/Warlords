package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.events.player.ingame.pve.WarlordsDropRewardEvent;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mobs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Objects;

public class RobinHoodsGloves extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    @Override
    public String getName() {
        return "Robin Hood's Gloves";
    }

    @Override
    public String getBonus() {
        return "5% chance to drop an Item when killing a boss.";
    }

    @Override
    public String getDescription() {
        return "Finally, all this thieving is paying off.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onRewardDrop(WarlordsDropRewardEvent event) {
                if (event.getRewardType() != WarlordsDropRewardEvent.RewardType.ITEM) {
                    return;
                }
                AbstractMob<?> deadMob = event.getDeadMob();
                if (Arrays.stream(Mobs.BOSSES).noneMatch(mobs -> Objects.equals(mobs.mobClass, deadMob.getClass()))) {
                    return;
                }
                event.getDropRate().set(.05);
                event.setModifier(1);
            }
        });
    }

}
