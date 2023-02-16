package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.customentities.npc.traits.MasterworksFairTrait;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.masterworksfair.pojos.MasterworksFair;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class MasterworksFairManager {

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("LLL dd yyyy z")
            .withZone(ZoneId.of("America/New_York"));
    public static boolean enabled = true;
    public static MasterworksFair currentFair;
    public static AtomicBoolean updateFair = new AtomicBoolean(false);
    public static BukkitTask runnable;

    public static void resetFair(MasterworksFair masterworksFair) {
        resetFair(masterworksFair, true, 5);
    }

    public static void resetFair(MasterworksFair masterworksFair, boolean throughRewardsInventory, int minutesTillStart) {
        if (masterworksFair == null) {
            ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Supplied fair is null. Cannot reset fair.");
            return;
        }
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendMasterworksFairMessage(onlinePlayer, ChatColor.GREEN + "Masterworks Fair #" + masterworksFair.getFairNumber() + " has just ended!");
        }
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Resetting fair");
        masterworksFair.setEnded(true);
        //give out rewards
        currentFair = null;
        Warlords.newChain()
                .async(() -> DatabaseManager.masterworksFairService.update(masterworksFair))
                .sync(() -> masterworksFair.sendRewards(throughRewardsInventory))
                .execute();
        //reset fair
        MasterworksFairTrait.startTime = Instant.now().plus(minutesTillStart, ChronoUnit.MINUTES);
        MasterworksFairTrait.PAUSED.set(false);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.getOpenInventory().getTitle().equals("Masterworks Fair")) {
                onlinePlayer.closeInventory();
            }
        }
    }

    public static void sendMasterworksFairMessage(Player player, String message) {
        player.sendMessage(ChatColor.GOLD + "Masterworks Fair" + ChatColor.DARK_GRAY + " > " + message);
    }

    public static void sendMasterworksFairMessage(Player player, Component component) {
        player.sendMessage(Component.text(ChatColor.GOLD + "Masterworks Fair" + ChatColor.DARK_GRAY + " > ").append(component));
    }

    public static void createFair() {
        MasterworksFair newFair = new MasterworksFair();
        initializeFair(newFair);
        createFair(newFair);
    }

    public static void initializeFair(MasterworksFair masterworksFair) {
        ChatUtils.MessageTypes.MASTERWORKS_FAIR.sendMessage("Initialize masterworks fair: " + masterworksFair.getStartDate());
        currentFair = masterworksFair;
        MasterworksFairTrait.PAUSED.set(false);
        //runnable that updates fair every 30 seconds if there has been a change
        if (runnable != null) {
            runnable.cancel();
        }
        runnable = new BukkitRunnable() {

            @Override
            public void run() {
                if (updateFair.get() && currentFair != null) {
                    updateFair.set(false);
                    Warlords.newChain()
                            .async(() -> DatabaseManager.masterworksFairService.update(currentFair))
                            .execute();
                }
            }
        }.runTaskTimer(Warlords.getInstance(), 60, 20 * 30);
    }

    private static void createFair(MasterworksFair masterworksFair) {
        Warlords.newChain()
                .asyncFirst(() -> {
                    DatabaseManager.masterworksFairService.create(masterworksFair);
                    return DatabaseManager.masterworksFairService.count();
                })
                .syncLast(count -> {
                    int size = Math.toIntExact(count);
                    masterworksFair.setFairNumber(size);
                    DatabaseManager.masterworksFairService.update(masterworksFair);
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        sendMasterworksFairMessage(onlinePlayer, ChatColor.GREEN + "Masterworks Fair #" + size + " has just started!" +
                                (size % 10 == 0 ? ChatColor.RED + " 10x REWARDS!" : ""));
                    }
                })
                .execute();
    }

}
