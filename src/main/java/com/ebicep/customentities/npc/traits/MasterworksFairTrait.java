package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.pve.events.MasterworksFairManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.util.Date;

import static com.ebicep.warlords.pve.events.MasterworksFairManager.currentFair;

public class MasterworksFairTrait extends Trait {

    private Date date = new Date();
    private long tickCounter = 0;

    public MasterworksFairTrait() {
        super("MasterworksFairTrait");
    }

    @Override
    public void run() {
        //only update every 30 mins
        if (tickCounter++ % 36000 == 0) {
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK");
            hologramTrait.setLine(1, ChatColor.GREEN + "The Masterworks Fair");

            if (currentFair == null) return;
            Date endDate = new Date(currentFair.getStartDate().getTime() + (Timing.WEEKLY.secondDuration * 1000));
            long timeDifference = endDate.getTime() - date.getTime();
            long days = timeDifference / (1000 * 60 * 60 * 24);
            long hours = (timeDifference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            String timeLeft;
            if (days <= 0) {
                timeLeft = hours + " hour" + (hours != 1 ? "s" : "");
            } else {
                timeLeft = days + " day" + (days != 1 ? "s and " : "and ") + hours + " hour" + (hours != 1 ? "s" : "");
            }
            hologramTrait.setLine(2, ChatColor.GOLD.toString() + ChatColor.BOLD + timeLeft + ChatColor.BOLD + " left");
        }
    }

    @EventHandler
    public void onRightClick(NPCRightClickEvent event) {
        if (this.getNPC() == event.getNPC()) {
            if (!Warlords.getInstance().isEnabled()) {
                // Fix old NPC standing around on Windows + plugin reload after new deployment
                this.getNPC().destroy();
                return;
            }
            MasterworksFairManager.openMasterworksFairMenu(event.getClicker());
        }
    }


}
