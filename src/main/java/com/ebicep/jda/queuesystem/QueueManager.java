package com.ebicep.jda.queuesystem;

import com.ebicep.jda.BotManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class QueueManager {

    public static final List<UUID> QUEUE = new ArrayList<>();
    public static final List<FutureQueuePlayer> FUTURE_QUEUE = new ArrayList<>();
    public static final Map<BotManager.DiscordServer, Message> QUEUE_MESSAGE = new HashMap<>();
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
                    textChannel.sendMessageEmbeds(QueueManager.getQueueDiscord()).queueAfter(500, TimeUnit.MILLISECONDS, m -> QUEUE_MESSAGE.put(discordServer, m));
                    return;
                }
                Message message = QUEUE_MESSAGE.get(discordServer);
                if (message == null) {
                    textChannel.sendMessageEmbeds(QueueManager.getQueueDiscord()).queueAfter(500, TimeUnit.MILLISECONDS, m -> QUEUE_MESSAGE.put(discordServer, m));
                } else if (textChannel.getLatestMessageId().equals(message.getId())) {
                    message.editMessageEmbeds(QueueManager.getQueueDiscord()).queue();
                } else {
                    message.delete().queue();
                    textChannel.sendMessageEmbeds(QueueManager.getQueueDiscord()).queueAfter(500, TimeUnit.MILLISECONDS, m -> QUEUE_MESSAGE.put(discordServer, m));
                }
            });
        }
    }

    public static MessageEmbed getQueueDiscord() {
        StringBuilder queue = new StringBuilder();
        for (int i = 0; i < QueueManager.QUEUE.size(); i++) {
            UUID uuid = QueueManager.QUEUE.get(i);
            queue.append("    ").append(i + 1).append(". ").append(Bukkit.getOfflinePlayer(uuid).getName()).append("\n");
        }
        StringBuilder futureQueue = new StringBuilder();
        QueueManager.FUTURE_QUEUE.forEach(futureQueuePlayer -> {
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

    public static Component getQueue() {
        TextComponent.Builder queueList = Component.text("Queue -", NamedTextColor.GREEN)
                                                   .append(Component.newline())
                                                   .toBuilder();
        for (int i = 0; i < QUEUE.size(); i++) {
            UUID uuid = QUEUE.get(i);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            queueList.append(Component.text((i + 1) + ". ", NamedTextColor.YELLOW))
                     .append(Component.text("" + (offlinePlayer.getName() == null ? uuid : offlinePlayer.getName()), NamedTextColor.AQUA))
                     .append(Component.newline());
        }
        if (!FUTURE_QUEUE.isEmpty()) {
            queueList.append(Component.newline());
            queueList.append(Component.text("Future Queue -", NamedTextColor.GREEN)
                                      .append(Component.newline()));
            for (int i = 0; i < FUTURE_QUEUE.size(); i++) {
                FutureQueuePlayer futureQueuePlayer = FUTURE_QUEUE.get(i);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(futureQueuePlayer.uuid());
                queueList.append(Component.text("    - ", NamedTextColor.YELLOW))
                         .append(Component.text("" + (offlinePlayer.getName() == null ? futureQueuePlayer.uuid() : offlinePlayer.getName()), NamedTextColor.AQUA))
                         .append(Component.text(" (" + futureQueuePlayer.timeString() + ")", NamedTextColor.GRAY));
                if (i != FUTURE_QUEUE.size() - 1) {
                    queueList.append(Component.newline());
                }
            }
        }
        return queueList.asComponent();
    }

    public static void sendQueue() {
        sendQueue = true;
    }

    public static void addPlayerToQueue(String name, boolean atBeginning) {
        if (name == null) {
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
        if (player == null) {
            return;
        }
        addPlayerToQueue(player.getUniqueId(), atBeginning);
    }

    public static void addPlayerToQueue(UUID uuid, boolean atBeginning) {
        QUEUE.remove(uuid);
        if (atBeginning) {
            QUEUE.add(0, uuid);
        } else {
            QUEUE.add(uuid);
        }
    }

    public static void removePlayerFromQueue(String name) {
        if (name == null) {
            return;
        }
        OfflinePlayer player = Bukkit.getOfflinePlayerIfCached(name);
        if (player == null) {
            return;
        }
        removePlayerFromQueue(player.getUniqueId());
    }

    public static void removePlayerFromQueue(UUID uuid) {
        QUEUE.remove(uuid);
        removePlayerFromFutureQueue(uuid);
    }

    public static void removePlayerFromFutureQueue(UUID uuid) {
        FUTURE_QUEUE.removeIf(futureQueuePlayer -> {
            if (futureQueuePlayer.uuid().equals(uuid)) {
                futureQueuePlayer.task().cancel();
                return true;
            }
            return false;
        });
    }

    public static void addPlayerToFutureQueue(String name, String timeString, BukkitTask task) {
        addPlayerToFutureQueue(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(name)).getUniqueId(), timeString, task);
    }

    public static void addPlayerToFutureQueue(UUID uuid, String timeString, BukkitTask task) {
        if (FUTURE_QUEUE.stream().noneMatch(futureQueuePlayer -> futureQueuePlayer.uuid().equals(uuid))) {
            FUTURE_QUEUE.add(new FutureQueuePlayer(uuid, timeString, task));
        }
    }

    public static void removePlayerFromFutureQueue(String name) {
        removePlayerFromFutureQueue(Objects.requireNonNull(Bukkit.getOfflinePlayerIfCached(name)).getUniqueId());
    }

    public record FutureQueuePlayer(UUID uuid, String timeString, BukkitTask task) {
    }
}
