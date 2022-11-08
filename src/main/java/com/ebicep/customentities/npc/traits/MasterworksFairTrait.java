package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairMenu;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.*;

public class MasterworksFairTrait extends WarlordsTrait {

    public static final AtomicBoolean PAUSED = new AtomicBoolean(false);
    public static Instant startTime;
    private long tickCounter = 0;

    public MasterworksFairTrait() {
        super("MasterworksFairTrait");
    }

    @Override
    public void run() {
        if (PAUSED.get()) {
            return;
        }
        if (tickCounter++ % 10 == 0) {
            if (currentFair == null) {
                if (startTime != null) {
                    if (Instant.now().isAfter(startTime)) {
                        startTime = null;
                        //create new fair
                        MasterworksFairManager.createFair();
                    }
                } else {
                    PAUSED.set(true);
                    Warlords.newChain()
                            .asyncFirst(() -> DatabaseManager.masterworksFairService.findFirstByOrderByStartDateDesc())
                            .asyncLast(masterworksFair -> {
                                if (masterworksFair == null) {
                                    ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Could not find masterworks fair in database");
                                    ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Creating new masterworks fair.");
                                    MasterworksFairManager.createFair();
                                } else {
                                    checkForReset(masterworksFair);
                                }
                            })
                            .execute();
                    return;
                }
            } else {
                //checking for reset
                long secondsBetween = ChronoUnit.SECONDS.between(currentFair.getStartDate(), Instant.now());
                if (secondsBetween > 0 && secondsBetween > Timing.WEEKLY.secondDuration) {
                    ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Masterworks Fair reset time has passed");
                    resetFair(currentFair);
                }
            }
            updateHologram();
        }
    }

    public void checkForReset(MasterworksFair masterworksFair) {
        //check if week past
        long minutesBetween = ChronoUnit.MINUTES.between(masterworksFair.getStartDate(), Instant.now());
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Masterworks Fair Reset Time Minute: " + minutesBetween + " > " + Timing.WEEKLY.minuteDuration);
        if (masterworksFair.isEnded()) {
            ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Masterworks Fair Ended");
            MasterworksFairManager.createFair();
        } else if (minutesBetween > 0 && minutesBetween > Timing.WEEKLY.minuteDuration) {
            ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Masterworks Fair reset time has passed");
            resetFair(masterworksFair);
        } else {
            initializeFair(masterworksFair);
        }
    }

    public void updateHologram() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        int fairNumber = currentFair == null ? 0 : currentFair.getFairNumber();
        hologramTrait.setLine(1, ChatColor.GREEN + "The Masterworks Fair" + (fairNumber != 0 ? " #" + fairNumber : ""));
        if (currentFair == null) {
            if (startTime != null) {
                hologramTrait.setLine(2, ChatColor.GOLD + "Starts in " + DateUtil.getTimeTill(startTime, true, true, true, true));
            } else {
                hologramTrait.setLine(2, ChatColor.RED + "Currently closed!");
            }
            return;
        }
        Instant endDate = currentFair.getStartDate().plus(7, ChronoUnit.DAYS);
        hologramTrait.setLine(2,
                ChatColor.GOLD.toString() + ChatColor.BOLD + DateUtil.getTimeTill(endDate,
                        true,
                        true,
                        true,
                        true
                ) + ChatColor.BOLD + " left"
        );
        if (fairNumber != 0 && fairNumber % 10 == 0) {
            hologramTrait.setLine(3, ChatColor.RED + "10x REWARDS!");
        }
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        MasterworksFairMenu.openMasterworksFairMenu(event.getClicker());
    }


}
