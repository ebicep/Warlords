package com.ebicep.customentities.npc.traits;

import com.ebicep.customentities.npc.HasNPCLabelHologram;
import com.ebicep.customentities.npc.NPCLabelHologram;
import com.ebicep.customentities.npc.WarlordsTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.database.repositories.timings.pojos.Timing;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager;
import com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairMenu;
import com.ebicep.warlords.util.bukkit.ComponentBuilder;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.DateUtil;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.ebicep.warlords.pve.events.mastersworkfair.MasterworksFairManager.*;

public class MasterworksFairTrait extends WarlordsTrait implements HasNPCLabelHologram {

    public static final AtomicBoolean PAUSED = new AtomicBoolean(false);
    public static Instant startTime;
    private final NPCLabelHologram labelHologram = new NPCLabelHologram("lobby-masterworks-fair");
    private long tickCounter = 0;

    public MasterworksFairTrait() {
        super("MasterworksFairTrait");
    }

    @Override
    public NPCLabelHologram getLabelHologram() {
        return labelHologram;
    }

    @Override
    public void onSpawn() {
        updateHologram();
    }

    @Override
    public void run() {
        if (PAUSED.get()) {
            return;
        }
        tickCounter++;
        if (tickCounter % 10 != 0) {
            return;
        }
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
                                ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Could not find masterworks fair in database");
                                ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Creating new masterworks fair.");
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
                ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Masterworks Fair reset time has passed");
                resetFair(currentFair);
            }
        }
        if (tickCounter % 1200 == 0) {
            updateHologram();
        }
    }

    public void checkForReset(MasterworksFair masterworksFair) {
        //check if week past
        long minutesBetween = ChronoUnit.MINUTES.between(masterworksFair.getStartDate(), Instant.now());
        ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Masterworks Fair Reset Time Minute: " + minutesBetween + " > " + Timing.WEEKLY.minuteDuration);
        if (masterworksFair.isEnded()) {
            ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Masterworks Fair Ended");
            MasterworksFairManager.createFair();
        } else if (minutesBetween > 0 && minutesBetween > Timing.WEEKLY.minuteDuration) {
            ChatUtils.MessageType.MASTERWORKS_FAIR.sendMessage("Masterworks Fair reset time has passed");
            resetFair(masterworksFair);
        } else {
            initializeFair(masterworksFair);
        }
    }

    public void updateHologram() {
        int fairNumber = currentFair == null ? 0 : currentFair.getFairNumber();
        String title = "The Masterworks Fair" + (fairNumber != 0 ? " #" + fairNumber : "");
        if (currentFair == null) {
            ComponentBuilder componentBuilder;
            if (startTime != null) {
                componentBuilder = ComponentBuilder.create(
                        "Starts in " + DateUtil.getTimeTill(startTime, true, true, true, true),
                        NamedTextColor.GOLD
                ).newLine(title, NamedTextColor.GREEN);
            } else {
                componentBuilder = ComponentBuilder.create("Currently closed!", NamedTextColor.RED)
                        .newLine(title, NamedTextColor.GREEN);
            }
            labelHologram.update(npc, componentBuilder.build());
            return;
        }
        Instant endDate = currentFair.getStartDate().plus(7, ChronoUnit.DAYS);
        ComponentBuilder componentBuilder;
        if (fairNumber != 0 && fairNumber % 5 == 0) {
            componentBuilder = ComponentBuilder.create("2x REWARDS!", NamedTextColor.RED)
                    .newLine(DateUtil.getTimeTill(endDate, true, true, true, true) + " left", NamedTextColor.GOLD, TextDecoration.BOLD)
                    .newLine(title, NamedTextColor.GREEN);
        } else {
            componentBuilder = ComponentBuilder.create(
                    DateUtil.getTimeTill(endDate, true, true, true, true) + " left",
                    NamedTextColor.GOLD,
                    TextDecoration.BOLD
            ).newLine(title, NamedTextColor.GREEN);
        }
        labelHologram.update(npc, componentBuilder.build());
    }

    @Override
    public void rightClick(NPCRightClickEvent event) {
        MasterworksFairMenu.openMasterworksFairMenu(event.getClicker());
    }


}
