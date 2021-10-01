package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.Classes;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.concurrent.ExecutionException;

public class BotListener extends ListenerAdapter implements Listener {

    private static int lastPlayerCount = 0;

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        int size = Bukkit.getOnlinePlayers().size();
        if(size % 5 == 0 && size != lastPlayerCount) {
            BotManager.sendMessageToNotificationChannel("[SERVER] **" + size + "** players are now on the server!");
            lastPlayerCount = size;
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        int size = Bukkit.getOnlinePlayers().size();
        if((size - 1) % 5 == 0 && size != lastPlayerCount) {
            BotManager.sendMessageToNotificationChannel("[SERVER] **" + (size - 1)+ "** players are now on the server!");
            lastPlayerCount = size;
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        TextChannel textChannel = event.getTextChannel();
        if (textChannel.getName().equalsIgnoreCase("teams")) {
            if (message.getEmbeds().size() != 0) {
                MessageEmbed embed = message.getEmbeds().get(0);
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
                                    Bukkit.getOnlinePlayers().stream().filter(p -> p.getName().equalsIgnoreCase(name)).findFirst().ifPresent(targetPlayer -> {
                                        targetPlayer.sendMessage(ChatColor.DARK_BLUE + "---------------------------------------");
                                        if (fieldName.contains("Blue Team")) {
                                            Warlords.getPlayerSettings(targetPlayer.getUniqueId()).setWantedTeam(Team.BLUE);
                                            targetPlayer.sendMessage(ChatColor.GREEN + "You were automatically put into the " + ChatColor.BLUE + "BLUE" + ChatColor.GREEN + " team!");
                                        } else if (fieldName.contains("Red Team")) {
                                            Warlords.getPlayerSettings(targetPlayer.getUniqueId()).setWantedTeam(Team.RED);
                                            targetPlayer.sendMessage(ChatColor.GREEN + "You were automatically put into the " + ChatColor.RED + "RED" + ChatColor.GREEN + " team!");
                                        }
                                        if(!spec.isEmpty()) {
                                            Warlords.getPlayerSettings(targetPlayer.getUniqueId()).setSelectedClass(Classes.getClass(spec));
                                            targetPlayer.sendMessage(ChatColor.GREEN + "Your spec was automatically changed to " + ChatColor.YELLOW + spec + ChatColor.GREEN + "!");
                                        }
                                        targetPlayer.sendMessage(ChatColor.DARK_BLUE + "---------------------------------------");
                                    });
                                }
                                return null;
                            }).get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

}
