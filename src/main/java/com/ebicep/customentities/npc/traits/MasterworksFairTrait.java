package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.pve.events.MasterworksFairManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.ebicep.warlords.pve.events.MasterworksFairManager.currentFair;

public class MasterworksFairTrait extends Trait {

    private long tickCounter = 0;

    public MasterworksFairTrait() {
        super("MasterworksFairTrait");
    }

    @Override
    public void run() {
        //only update every 10 mins
        if (tickCounter++ % 12000 == 0) {
            HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
            hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "CLICK");
            hologramTrait.setLine(1, ChatColor.GREEN + "The Masterworks Fair");

            if (currentFair == null) return;
            Instant currentDate = Instant.now();
            Instant endDate = currentFair.getStartDate().plus(7, ChronoUnit.DAYS);
            long days = ChronoUnit.DAYS.between(currentDate, endDate);
            long hours = (ChronoUnit.HOURS.between(currentDate, endDate) % 24) + 1;
            //long minutes = ChronoUnit.MINUTES.between(currentDate, endDate) % 60;
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
