package com.ebicep.customentities.npc.traits;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.*;

public class MasterworksFairTrait extends Trait {

    public static final AtomicBoolean PAUSED = new AtomicBoolean(false);
    public static Instant startTime;
    private long tickCounter = 0;

    public MasterworksFairTrait() {
        super("MasterworksFairTrait");
    }

    public static String getTimeTill(Instant endDate, boolean includeDays, boolean includeHours, boolean includeMinutes, boolean includeSeconds) {
        Instant currentDate = Instant.now();

        String timeLeft = "";
        if (includeDays) {
            long days = ChronoUnit.DAYS.between(currentDate, endDate);
            if (days > 0) {
                timeLeft += days + (days == 1 ? " day " : " days ");
            }
        }
        if (includeHours) {
            long hours = ChronoUnit.HOURS.between(currentDate, endDate) % 24;
            if (hours > 0) {
                timeLeft += hours + (hours == 1 ? " hour " : " hours ");
            }
        }
        if (includeMinutes) {
            long minutes = ChronoUnit.MINUTES.between(currentDate, endDate) % 60;
            if (minutes > 0) {
                timeLeft += minutes + (minutes == 1 ? " minute " : " minutes ");
            }
        }
        if (includeSeconds) {
            long seconds = ChronoUnit.SECONDS.between(currentDate, endDate) % 60;
            if (seconds > 0) {
                timeLeft += seconds + (seconds == 1 ? " second " : " seconds ");
            }
        }

        if (timeLeft.isEmpty()) {
            return "0 seconds";
        } else {
            return timeLeft.substring(0, timeLeft.length() - 1);
        }
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
                        MasterworksFair newFair = new MasterworksFair();
                        initializeFair(newFair);

                        createFair(newFair);
                    }
                } else {
                    PAUSED.set(true);
                    Warlords.newChain()
                            .asyncFirst(() -> DatabaseManager.masterworksFairService.findFirstByOrderByStartDateDesc())
                            .asyncLast(masterworksFair -> {
                                if (masterworksFair == null) {
                                    System.out.println("[MasterworksFairManager] Could not find masterworks fair in database");
                                    System.out.println("[MasterworksFairManager] Creating new masterworks fair.");
                                    MasterworksFair newFair = new MasterworksFair();
                                    initializeFair(newFair);
                                    createFair(newFair);
                                } else {
                                    checkForReset(masterworksFair);
                                }
                            })
                            .execute();
                    return;
                }
            }
            updateHologram();
        }
    }

    public void checkForReset(MasterworksFair masterworksFair) {
        //check if week past
        long minutesBetween = ChronoUnit.MINUTES.between(masterworksFair.getStartDate(), Instant.now());
        //System.out.println("[MasterworksFairManager] Masterworks Fair Reset Time Minute: " + minutesBetween + " > " + Timing.WEEKLY.minuteDuration);
        if (minutesBetween > 0 && minutesBetween > Timing.WEEKLY.minuteDuration) {
            System.out.println("[MasterworksFairManager] Masterworks Fair reset time has passed");
            resetFair(masterworksFair);
        } else {
            initializeFair(masterworksFair);
        }
    }

    public void updateHologram() {
        HologramTrait hologramTrait = npc.getOrAddTrait(HologramTrait.class);
        hologramTrait.setLine(0, ChatColor.YELLOW.toString() + ChatColor.BOLD + "RIGHT-CLICK");
        hologramTrait.setLine(1, ChatColor.GREEN + "The Masterworks Fair");
        if (currentFair == null) {
            if (startTime != null) {
                hologramTrait.setLine(2, ChatColor.GOLD + "Starts in " + getTimeTill(startTime, true, true, true, true));
            } else {
                hologramTrait.setLine(2, ChatColor.RED + "Currently closed!");
            }
            return;
        }
        Instant endDate = currentFair.getStartDate().plus(7, ChronoUnit.DAYS);
        hologramTrait.setLine(2, ChatColor.GOLD.toString() + ChatColor.BOLD + getTimeTill(endDate, true, true, true, true) + ChatColor.BOLD + " left");
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
