package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.Classes;
import com.ebicep.warlords.queuesystem.QueueManager;
import com.ebicep.warlords.util.PacketUtils;
import com.ebicep.warlords.util.Utils;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class BotListener extends ListenerAdapter implements Listener {

    private static int lastPlayerCount = 0;
    private static BukkitTask onGoingBalance;

    private static void cancelOnGoingBalance() {
        if (onGoingBalance != null) {
            onGoingBalance.cancel();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Member member = event.getMember();
        Message message = event.getMessage();
        TextChannel textChannel = event.getTextChannel();
        switch (textChannel.getName().toLowerCase()) {
            case "bot-teams": {
                if (message.getContentRaw().contains(", Balance Cancelled")) {
                    cancelOnGoingBalance();
                } else if (message.getEmbeds().size() != 0 && message.getEmbeds().get(0).getFields().size() == 2) {
                    cancelOnGoingBalance();
                    MessageEmbed embed = message.getEmbeds().get(0);
                    List<String> playerNames = new ArrayList<>();
                    for (MessageEmbed.Field field : embed.getFields()) {
                        String fieldName = field.getName();
                        String fieldValue = field.getValue();
                        if (fieldName != null && fieldValue != null) {
                            String[] players = fieldValue
                                    .replace("```", "")
                                    .replace(" ", "")
                                    .split("\n");
                            if (fieldName.contains("Blue Team") || fieldName.contains("Red Team")) {
                                for (String player : players) {
                                    playerNames.add(player.substring(0, player.indexOf("-")));
                                }
                            }
                        }
                    }
                    onGoingBalance = new BukkitRunnable() {
                        int counter = 0;

                        @Override
                        public void run() {
                            playerNames.forEach(name -> {
                                Player player = Bukkit.getPlayer(name);
                                if (player != null) {
                                    Random random = new Random();
                                    PacketUtils.sendTitle(player,
                                            ChatColor.GREEN + Utils.specsOrdered[random.nextInt(Utils.specsOrdered.length)],
                                            random.nextInt(2) == 0 ? ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE" : ChatColor.RED.toString() + ChatColor.BOLD + "RED",
                                            0, 5, 0);
                                }
                            });
                            //auto cancel after 15 seconds
                            if (counter++ > 20 * 15) {
                                this.cancel();
                            }
                        }
                    }.runTaskTimer(Warlords.getInstance(), 10, 0);

                }
                break;
            }
            case "teams": {
                if (message.getEmbeds().size() != 0 && message.getEmbeds().get(0).getFields().size() == 2) {
                    cancelOnGoingBalance();
                    MessageEmbed embed = message.getEmbeds().get(0);
                    List<String> blueTeam = new ArrayList<>();
                    List<String> redTeam = new ArrayList<>();
                    for (MessageEmbed.Field field : embed.getFields()) {
                        String fieldName = field.getName();
                        String fieldValue = field.getValue();
                        String[] players;
                        if (fieldName != null && fieldValue != null) {
                            players = fieldValue
                                    .replace("```", "")
                                    .split("\n");

                            if (fieldName.contains("Blue Team")) {
                                blueTeam.add(ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "Blue Team" + ChatColor.DARK_GRAY + " - ");
                                for (String player : players) {
                                    String name = player.substring(0, player.indexOf("-"));
                                    String spec = player.substring(player.indexOf("-") + 1);
                                    blueTeam.add(ChatColor.BLUE + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + spec);
                                }
                            } else if (fieldName.contains("Red Team")) {
                                redTeam.add(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Red Team" + ChatColor.DARK_GRAY + " - ");
                                for (String player : players) {
                                    String name = player.substring(0, player.indexOf("-"));
                                    String spec = player.substring(player.indexOf("-") + 1);
                                    redTeam.add(ChatColor.RED + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + spec);
                                }
                            }
                        }
                    }
                    for (MessageEmbed.Field field : embed.getFields()) {
                        String fieldName = field.getName();
                        String fieldValue = field.getValue();
                        String[] players;
                        if (fieldName != null && fieldValue != null) {
                            players = fieldValue
                                    .replace("```", "")
                                    .replace(" ", "")
                                    .split("\n");
                            try {
                                Bukkit.getScheduler().callSyncMethod(Warlords.getInstance(), () -> {
                                    for (String player : players) {
                                        String name = player.substring(0, player.indexOf("-"));
                                        String spec = player.substring(player.indexOf("-") + 1);
                                        Player targetPlayer = Bukkit.getPlayer(name);
                                        if (targetPlayer != null) {
                                            targetPlayer.sendMessage(ChatColor.DARK_BLUE + "---------------------------------------");
                                            if (fieldName.contains("Blue Team")) {
                                                Warlords.getPlayerSettings(targetPlayer.getUniqueId()).setWantedTeam(Team.BLUE);
                                                targetPlayer.sendMessage(ChatColor.GREEN + "You were automatically put into the " + ChatColor.BLUE + "BLUE" + ChatColor.GREEN + " team!");
                                            } else if (fieldName.contains("Red Team")) {
                                                Warlords.getPlayerSettings(targetPlayer.getUniqueId()).setWantedTeam(Team.RED);
                                                targetPlayer.sendMessage(ChatColor.GREEN + "You were automatically put into the " + ChatColor.RED + "RED" + ChatColor.GREEN + " team!");
                                            }
                                            if (!spec.isEmpty()) {
                                                PacketUtils.sendTitle(targetPlayer,
                                                        ChatColor.GREEN + spec,
                                                        fieldName.contains("Blue Team") ? ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE"
                                                                : fieldName.contains("Red Team") ? ChatColor.RED.toString() + ChatColor.BOLD + "RED"
                                                                : "",
                                                        0, 100, 40);

                                                Warlords.getPlayerSettings(targetPlayer.getUniqueId()).setSelectedClass(Classes.getClass(spec));
                                                targetPlayer.sendMessage(ChatColor.GREEN + "Your spec was automatically changed to " + ChatColor.YELLOW + spec + ChatColor.GREEN + "!");
                                            }
                                            targetPlayer.sendMessage("");
                                            blueTeam.forEach(s -> {
                                                if (s.contains(name)) {
                                                    targetPlayer.sendMessage(ChatColor.GREEN + s.substring(2, s.indexOf("-") - 2) + s.substring(s.indexOf("-") - 2));
                                                } else {
                                                    targetPlayer.sendMessage(s);
                                                }
                                            });
                                            redTeam.forEach(s -> {
                                                if (s.contains(name)) {
                                                    targetPlayer.sendMessage(ChatColor.GREEN + s.substring(2, s.indexOf("-") - 2) + s.substring(s.indexOf("-") - 2));
                                                } else {
                                                    targetPlayer.sendMessage(s);
                                                }
                                            });
                                            targetPlayer.sendMessage(ChatColor.DARK_BLUE + "---------------------------------------");
                                        }
                                    }
                                    return null;
                                }).get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                break;
            }
            case "waiting": {
                if (member != null && member.getUser().isBot()) {
                    return;
                }
                String queueCommand = message.getContentRaw();
                String[] args = queueCommand.substring(1).split(" ");
                //System.out.println(Arrays.toString(args));
                if (member != null) {
                    String playerName = member.getEffectiveName();
                    if (queueCommand.equalsIgnoreCase("-queue")) {
                        textChannel.sendMessage(QueueManager.getQueueDiscord()).queue(QueueManager.queueMessages::add);
                    } else if (queueCommand.startsWith("-queue") && args.length > 0) {
                        switch (args[1]) {
                            case "join": {
                                if (QueueManager.queue.stream().anyMatch(uuid -> uuid.equals(Bukkit.getOfflinePlayer(playerName).getUniqueId())) || QueueManager.futureQueue.stream().anyMatch(futureQueuePlayer -> futureQueuePlayer.getUuid().equals(Bukkit.getOfflinePlayer(playerName).getUniqueId()))) {
                                    message.reply("You are already in the queue!").queue();
                                    break;
                                }
                                if (args.length == 3) { //adding to queue for future time
                                    try {
                                        String futureTime = args[2];
                                        SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
                                        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
                                        hourFormat.setTimeZone(TimeZone.getTimeZone("EST"));
                                        minuteFormat.setTimeZone(TimeZone.getTimeZone("EST"));
                                        Date date = new Date();
                                        int currentHour = Integer.parseInt(hourFormat.format(date));
                                        int currentMinute = Integer.parseInt(minuteFormat.format(date));
                                        int hourDiff = Integer.parseInt(args[1].substring(0, args[1].indexOf(":"))) - currentHour;
                                        int minuteDiff = Integer.parseInt(args[1].substring(args[1].indexOf(":") + 1)) - currentMinute;
                                        if (hourDiff > 5) {
                                            textChannel.sendMessage("You cannot join the queue 3+ hours ahead").queue();
                                        } else if (hourDiff == 0 && minuteDiff < 20) {
                                            textChannel.sendMessage("You cannot join the queue within 20 minutes. Join the server and type **/joinqueue** to join the queue now").queue();
                                        } else if (hourDiff >= 0) {
                                            long futureTimeMillis = System.currentTimeMillis();
                                            futureTimeMillis += hourDiff * 3600000L;
                                            futureTimeMillis += minuteDiff * 60000L;
                                            long diff = futureTimeMillis - System.currentTimeMillis();
                                            message.reply("You will join the queue in **" + TimeUnit.MILLISECONDS.toMinutes(diff) + "** minutes. Make sure you are online at that time or you will be automatically removed if there is an open party spot!").queue();
                                            QueueManager.addPlayerToFutureQueue(playerName, futureTime, new BukkitRunnable() {

                                                @Override
                                                public void run() {
                                                    QueueManager.addPlayerToQueue(playerName, false);
                                                    QueueManager.futureQueue.removeIf(futureQueuePlayer -> futureQueuePlayer.getUuid().equals(Bukkit.getOfflinePlayer(member.getEffectiveName()).getUniqueId()));
                                                }
                                            }.runTaskLater(Warlords.getInstance(), TimeUnit.MILLISECONDS.toSeconds(diff) * 20));
                                        } else {
                                            message.reply("Invalid Time - HOUR:MINUTE").queue();
                                        }
                                    } catch (Exception e) {
                                        message.reply("Invalid Time - HOUR:MINUTE").queue();
                                    }
                                } else { //adding to queue normally
                                    QueueManager.addPlayerToQueue(member.getEffectiveName(), false);
                                }

                                break;
                            }
                            case "leave": {
                                if (QueueManager.queue.stream().anyMatch(uuid -> uuid.equals(Bukkit.getOfflinePlayer(playerName).getUniqueId()))) {
                                    QueueManager.removePlayerFromQueue(playerName);
                                    message.reply("You left the queue!").queue();
                                    break;
                                } else if (QueueManager.futureQueue.stream().anyMatch(futureQueuePlayer -> futureQueuePlayer.getUuid().equals(Bukkit.getOfflinePlayer(playerName).getUniqueId()))) {
                                    QueueManager.removePlayerFromFutureQueue(playerName);
                                    message.reply("You left the future queue!").queue();
                                    break;
                                }
                                break;
                            }
                        }
                        QueueManager.sendNewQueue();
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int size = Bukkit.getOnlinePlayers().size();
        if (size % 5 == 0 && size != lastPlayerCount) {
            BotManager.sendMessageToNotificationChannel("[SERVER] **" + size + "** players are now on the server!");
            lastPlayerCount = size;
        }

        List<Member> members = BotManager.getCompGamesServer().findMembers(m -> m.getEffectiveName().equalsIgnoreCase(player.getName())).get();
        if (!members.isEmpty()) {
            Member member = members.get(0);
            BotManager.getCompGamesServer().addRoleToMember(member, Objects.requireNonNull(BotManager.jda.getRoleById("912620490877706260"))).queue();
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int size = Bukkit.getOnlinePlayers().size();
        if ((size - 1) % 5 == 0 && size != lastPlayerCount && size > 4) {
            BotManager.sendMessageToNotificationChannel("[SERVER] **" + (size - 1) + "** players are now on the server!");
            lastPlayerCount = size;
        }

        List<Member> members = BotManager.getCompGamesServer().findMembers(m -> m.getEffectiveName().equalsIgnoreCase(player.getName())).get();
        if (!members.isEmpty()) {
            Member member = members.get(0);
            BotManager.getCompGamesServer().removeRoleFromMember(member, Objects.requireNonNull(BotManager.jda.getRoleById("912620490877706260"))).queue();
        }
    }


}
