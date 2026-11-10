package com.ebicep.warlords.util.warlords;

import com.ebicep.warlords.util.java.DateUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

public final class ServerRestartReminders {

    private record Reminder(Duration beforeRestart, String message) {}

    private static final List<Reminder> REMINDERS = List.of(
            new Reminder(Duration.ofHours(1), "The server will restart in 1 hour."),
            new Reminder(Duration.ofMinutes(30), "The server will restart in 30 minutes."),
            new Reminder(Duration.ofMinutes(15), "The server will restart in 15 minutes."),
            new Reminder(Duration.ofMinutes(5), "The server will restart in 5 minutes."),
            new Reminder(Duration.ofMinutes(4), "The server will restart in 4 minutes."),
            new Reminder(Duration.ofMinutes(3), "The server will restart in 3 minutes."),
            new Reminder(Duration.ofMinutes(2), "The server will restart in 2 minutes."),
            new Reminder(Duration.ofMinutes(1), "The server will restart in 1 minute.")
    );

    private ServerRestartReminders() {}

    public static void start(JavaPlugin plugin) {
        Instant nextReset = DateUtil.getNextResetDate();
        new BukkitRunnable() {
            int nextIndex = 0;

            @Override
            public void run() {
                if (nextIndex >= REMINDERS.size()) {
                    cancel();
                    return;
                }
                Duration untilRestart = Duration.between(Instant.now(), nextReset);
                while (nextIndex < REMINDERS.size()) {
                    Reminder reminder = REMINDERS.get(nextIndex);
                    if (untilRestart.compareTo(reminder.beforeRestart()) > 0) {
                        break;
                    }
                    Bukkit.broadcast(Component.text(reminder.message(), NamedTextColor.RED));
                    nextIndex++;
                }
                if (nextIndex >= REMINDERS.size()) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20, 20 * 60);
    }
}
