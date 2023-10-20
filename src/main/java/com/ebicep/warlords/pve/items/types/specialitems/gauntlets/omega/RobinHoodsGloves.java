package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega;

import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropItemEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

public class RobinHoodsGloves extends SpecialOmegaGauntlet implements AppliesToWarlordsPlayer {
    public RobinHoodsGloves() {

    }

    public RobinHoodsGloves(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    @Override
    public String getName() {
        return "Robin Hood's Gloves";
    }

    @Override
    public String getBonus() {
        return "Each Item rarity now has a 1% overall chance to drop when killing a boss.";
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
                AbstractMob deadMob = event.getDeadMob();
                if (Arrays.stream(Mob.BOSSES).noneMatch(mobs -> Objects.equals(mobs.mobClass, deadMob.getClass()))) {
                    return;
                }
                event.getDropRate().set(.01);
                event.setModifier(1);
            }
        });
    }

}
