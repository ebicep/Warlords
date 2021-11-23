package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.maps.Team;
import com.ebicep.warlords.player.Classes;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class BotListener extends ListenerAdapter implements Listener {

    private static int lastPlayerCount = 0;

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int size = Bukkit.getOnlinePlayers().size();
        if(size % 5 == 0 && size != lastPlayerCount) {
            BotManager.sendMessageToNotificationChannel("[SERVER] **" + size + "** players are now on the server!");
            lastPlayerCount = size;
        }

        List<Member> members = BotManager.getCompGamesServer().findMembers(m -> m.getEffectiveName().equalsIgnoreCase(player.getName())).get();
        if(!members.isEmpty()) {
            Member member = members.get(0);
            BotManager.getCompGamesServer().addRoleToMember(member, Objects.requireNonNull(BotManager.jda.getRoleById("912620490877706260"))).queue();
        }
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        int size = Bukkit.getOnlinePlayers().size();
        if((size - 1) % 5 == 0 && size != lastPlayerCount && size > 4) {
            BotManager.sendMessageToNotificationChannel("[SERVER] **" + (size - 1) + "** players are now on the server!");
            lastPlayerCount = size;
        }

        List<Member> members = BotManager.getCompGamesServer().findMembers(m -> m.getEffectiveName().equalsIgnoreCase(player.getName())).get();
        if(!members.isEmpty()) {
            Member member = members.get(0);
            BotManager.getCompGamesServer().removeRoleFromMember(member, Objects.requireNonNull(BotManager.jda.getRoleById("912620490877706260"))).queue();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        TextChannel textChannel = event.getTextChannel();
        if (textChannel.getName().equalsIgnoreCase("teams")) {
            if (message.getEmbeds().size() != 0) {
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

                        if(fieldName.contains("Blue Team")) {
                            blueTeam.add(ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "Blue Team" + ChatColor.DARK_GRAY + " - ");
                            for (String player : players) {
                                String name = player.substring(0, player.indexOf("-"));
                                String spec = player.substring(player.indexOf("-") + 1);
                                blueTeam.add(ChatColor.BLUE + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + spec);
                            }
                        } else if(fieldName.contains("Red Team")) {
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
                                        targetPlayer.sendMessage("");
                                        blueTeam.forEach(s -> targetPlayer.sendMessage(s));
                                        redTeam.forEach(s -> targetPlayer.sendMessage(s));
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
