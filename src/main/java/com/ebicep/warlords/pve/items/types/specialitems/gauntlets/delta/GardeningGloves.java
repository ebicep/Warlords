package com.ebicep.warlords.pve.items.types.specialitems.gauntlets.delta;

import com.ebicep.warlords.events.player.ingame.pve.drops.WarlordsDropItemEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Classes;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.statpool.BasicStatPool;
import com.ebicep.warlords.pve.items.types.AbstractItem;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import com.ebicep.warlords.pve.items.types.specialitems.gauntlets.omega.NaturesClaws;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.MobTier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class GardeningGloves extends SpecialDeltaGauntlet implements AppliesToWarlordsPlayer {

    public GardeningGloves(Set<BasicStatPool> statPool) {
        super(statPool);
    }

    public GardeningGloves() {

    }

    @Override
    public String getName() {
        return "Gardening Gloves";
    }

    @Override
    public String getBonus() {
        return "Increases your chances of finding Items when killing ELITE enemies.";
    }

    @Override
    public String getDescription() {
        return "Save the Earth, ride a... dolphin?";
    }

    @Override
    public Classes getClasses() {
        return Classes.SHAMAN;
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {
        warlordsPlayer.getGame().registerEvents(new Listener() {

            @EventHandler
            public void onItemDrop(WarlordsDropItemEvent event) {
                AbstractMob<?> deadMob = event.getDeadMob();
                if (deadMob.getMobTier() != MobTier.ILLUSION) {
                    return;
                }
                event.addModifier(.1);
            }
        });

    }

    @Override
    public AbstractItem getCraftsInto(Set<BasicStatPool> statPool) {
        return new NaturesClaws(statPool);
    }
}
