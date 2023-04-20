package com.ebicep.jda.queuesystem;

import com.ebicep.jda.BotManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QueueManager {

    public static final List<UUID> queue = new ArrayList<>();
    public static final List<FutureQueuePlayer> futureQueue = new ArrayList<>();
    public static Message queueMessage = null;
    public static boolean sendQueue = true;

    public static void sendNewQueue() {
        for (BotManager.DiscordServer discordServer : BotManager.DISCORD_SERVERS) {
            if (discordServer.getQueueChannel() == null) {
                continue;
            }
            discordServer.getTextChannelByName(discordServer.getQueueChannel()).ifPresent(textChannel -> {
                try {
                    textChannel.getLatestMessageId();
                } catch (Exception e) {
                    textChannel.sendMessageEmbeds(QueueManager.getQueueDiscord()).queueAfter(500, TimeUnit.MILLISECONDS, m -> queueMessage = m);
                    return;
                }
                if (queueMessage == null) {
                    textChannel.sendMessageEmbeds(QueueManager.getQueueDiscord()).queueAfter(500, TimeUnit.MILLISECONDS, m -> queueMessage = m);
                } else if (textChannel.getLatestMessageId().equals(queueMessage.getId())) {
                    queueMessage.editMessageEmbeds(QueueManager.getQueueDiscord()).queue();
                } else {
                    queueMessage.delete().queue();
                    textChannel.sendMessageEmbeds(QueueManager.getQueueDiscord()).queueAfter(500, TimeUnit.MILLISECONDS, m -> queueMessage = m);
                }
            });
        }
    }

    public static Component getQueue() {
        TextComponent.Builder queueList = Component.text("Queue -", NamedTextColor.GREEN)
                                                   .append(Component.newline())
                                                   .toBuilder();
        for (int i = 0; i < queue.size(); i++) {
            UUID uuid = queue.get(i);
            queueList.append(Component.text("    " + i + 1 + ". ", NamedTextColor.YELLOW))
                     .append(Component.text(Objects.requireNonNull(Bukkit.getOfflinePlayer(uuid).getName()), NamedTextColor.AQUA))
                     .append(Component.newline());
        }
        if (!futureQueue.isEmpty()) {
            queueList.append(Component.newline());
            queueList.append(Component.text("Future Queue -", NamedTextColor.GREEN)
                                      .append(Component.newline()));
            futureQueue.forEach(futureQueuePlayer -> {
                queueList.append(Component.text("    - ", NamedTextColor.YELLOW))
                         .append(Component.text(Objects.requireNonNull(Bukkit.getOfflinePlayer(futureQueuePlayer.uuid()).getName()), NamedTextColor.AQUA))
                         .append(Component.text(" (" + futureQueuePlayer.timeString() + ")", NamedTextColor.GRAY))
                         .append(Component.newline());
            });
        }
        return queueList.asComponent();
    }

    public static MessageEmbed getQueueDiscord() {
        StringBuilder queue = new StringBuilder();
        for (int i = 0; i < QueueManager.queue.size(); i++) {
            UUID uuid = QueueManager.queue.get(i);
            queue.append("    ").append(i + 1).append(". ").append(Bukkit.getOfflinePlayer(uuid).getName()).append("\n");
        }
        StringBuilder futureQueue = new StringBuilder();
        QueueManager.futureQueue.forEach(futureQueuePlayer -> {
            futureQueue.append("    ")
                       .append("- ")
                       .append(Bukkit.getOfflinePlayer(futureQueuePlayer.uuid()).getName())
                       .append(" (")
                       .append(futureQueuePlayer.timeString())
                       .append(")")
                       .append("\n");
        });

        return new EmbedBuilder()
                .setColor(3066993)
                .setTimestamp(new Date().toInstant())
                .addField("Current Queue", queue.toString(), true)
                .addField("\u200B", "\u200B", true)
                .addField("Future Queue", futureQueue.toString(), true)
                .setFooter("Usage: /queue")
                .build();
    }

    public static void sendQueue() {
        sendQueue = true;
    }

    public static void addPlayerToQueue(String name, boolean atBeginning) {
        addPlayerToQueue(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(name)).getUniqueId(), atBeginning);
    }

    public static void addPlayerToQueue(UUID uuid, boolean atBeginning) {
        queue.remove(uuid);
        if (atBeginning) {
            queue.add(0, uuid);
        } else {
            queue.add(uuid);
        }
    }

    public static void removePlayerFromQueue(String name) {
        removePlayerFromQueue(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(name)).getUniqueId());
    }

    public static void removePlayerFromQueue(UUID uuid) {
        queue.remove(uuid);
        removePlayerFromFutureQueue(uuid);
    }

    public static void addPlayerToFutureQueue(String name, String timeString, BukkitTask task) {
        addPlayerToFutureQueue(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(name)).getUniqueId(), timeString, task);
    }

    public static void addPlayerToFutureQueue(UUID uuid, String timeString, BukkitTask task) {
        if (futureQueue.stream().noneMatch(futureQueuePlayer -> futureQueuePlayer.uuid().equals(uuid))) {
            futureQueue.add(new FutureQueuePlayer(uuid, timeString, task));
        }
    }

    public static void removePlayerFromFutureQueue(String name) {
        removePlayerFromFutureQueue(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(name)).getUniqueId());
    }

    public static void removePlayerFromFutureQueue(UUID uuid) {
        futureQueue.removeIf(futureQueuePlayer -> {
            if (futureQueuePlayer.uuid().equals(uuid)) {
                futureQueuePlayer.task().cancel();
                return true;
            }
            return false;
        });
    }

    public record FutureQueuePlayer(UUID uuid, String timeString, BukkitTask task) {
    }
}
