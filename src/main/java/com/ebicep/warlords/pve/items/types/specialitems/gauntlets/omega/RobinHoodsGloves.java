package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropItemEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mobs;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class RobinHoodsGloves extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public RobinHoodsGloves(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public RobinHoodsGloves() {

    }

    @Override
    public String getName() {
        return "Robin Hood's Gloves";
    }

    @Override
    public String getBonus() {
        return "5% overall chance to drop an Item when killing a boss.";
    }

    @Override
    public String getDescription() {
        return "Finally, all this thieving is paying off.";
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onRewardDrop(WarlordsDropItemEvent event) {
                AbstractMob<?> deadMob = event.getDeadMob();
                if (Arrays.stream(Mobs.BOSSES).noneMatch(mobs -> Objects.equals(mobs.mobClass, deadMob.getClass()))) {
                    return;
                }
                event.getDropRate().set(.0125);
                event.setModifier(1);
            }
        });
    }

}
