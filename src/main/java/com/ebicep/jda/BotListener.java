package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.miscellaneouscommands.DiscordCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.party.RegularGamesMenu;
import com.ebicep.warlords.player.Specializations;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.warlords.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotListener extends ListenerAdapter implements Listener {

    private static BukkitTask onGoingBalance;

    private static void cancelOnGoingBalance() {
        if (onGoingBalance != null) {
            onGoingBalance.cancel();
        }
    }

    @EventHandler
    public static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (BotManager.getCompGamesServer() == null) return;
//        Warlords.newChain()
//                .asyncFirst(() -> BotManager.getCompGamesServer().findMembers(m -> m.getEffectiveName().equalsIgnoreCase(player.getName())).get())
//                .asyncLast(members -> {
//                    if (!members.isEmpty()) {
//                        Member member = members.get(0);
//                        BotManager.getCompGamesServer().addRoleToMember(member, Objects.requireNonNull(BotManager.jda.getRoleById("912620490877706260"))).queue();
//                    }
//                }).execute();
    }

    @EventHandler
    public static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (BotManager.getCompGamesServer() == null) return;
//        Warlords.newChain()
//                .asyncFirst(() -> BotManager.getCompGamesServer().findMembers(m -> m.getEffectiveName().equalsIgnoreCase(player.getName())).get())
//                .asyncLast(members -> {
//                    if (!members.isEmpty()) {
//                        Member member = members.get(0);
//                        BotManager.getCompGamesServer().removeRoleFromMember(member, Objects.requireNonNull(BotManager.jda.getRoleById("912620490877706260"))).queue();
//                    }
//                }).execute();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (Warlords.onCustomServer()) return;
        Member member = event.getMember();
        Message message = event.getMessage();
        if (event.isFromType(ChannelType.PRIVATE)) {
            if (event.getAuthor().isBot()) {
                return;
            }
            try {
                Long key = Long.parseLong(message.getContentRaw());
                if (DiscordCommand.playerLinkKeys.containsValue(key)) {
                    UUID uuid = DiscordCommand.playerLinkKeys.getKey(key);
                    if (DatabaseManager.playerService == null) return;
                    Warlords.newChain()
                            .asyncFirst(() -> DatabaseManager.playerService.findByUUID(uuid))
                            .syncLast(databasePlayer -> {
                                Long id = event.getAuthor().getIdLong();
                                databasePlayer.setDiscordID(id);
                                DatabaseManager.updatePlayerAsync(databasePlayer);
                                event.getPrivateChannel().sendMessage("You linked **" + Bukkit.getOfflinePlayer(uuid).getName() + "** to your discord account (" + id + ").").queue();
                                if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                                    Bukkit.getOfflinePlayer(uuid).getPlayer().sendMessage(ChatColor.GREEN + "Your account was linked to the discord account " + event.getAuthor().getAsTag() + " (" + id + ").");
                                }

                                BotManager.sendDebugMessage(
                                        new EmbedBuilder()
                                                .setColor(3066993)
                                                .setTitle("Player Linked - " + id)
                                                .setDescription("UUID: " + uuid + "\n" + "IGN: " + databasePlayer.getName() + "\n" + "KEY: " + key)
                                                .build()
                                );
                            }).execute();
                }
            } catch (Exception e) {
                System.out.println(message);
                System.out.println("Could not parseLong from direct message");
                e.printStackTrace();
            }
        } else if (event.isFromType(ChannelType.TEXT)) {
            TextChannel textChannel = event.getTextChannel();
            switch (textChannel.getName().toLowerCase()) {
                case "gs-teams":
                case "bot-teams": {
                    if (message.getContentRaw().contains(", Balance Cancelled")) {
                        cancelOnGoingBalance();
                    } else if (!message.getEmbeds().isEmpty() && message.getEmbeds().get(0).getFields().size() == 2) {
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
                                        playerNames.add(player.substring(0, player.indexOf('-')));
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
                    if (!message.getEmbeds().isEmpty() && message.getEmbeds().get(0).getFields().size() == 2) {
                        cancelOnGoingBalance();
                        MessageEmbed embed = message.getEmbeds().get(0);
                        boolean isExperimental = embed.getTitle().contains("*");
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
                                        String name = player.substring(0, player.indexOf('-'));
                                        String spec = player.substring(player.indexOf('-') + 1);
                                        blueTeam.add(ChatColor.BLUE + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + spec);
                                    }
                                } else if (fieldName.contains("Red Team")) {
                                    redTeam.add(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Red Team" + ChatColor.DARK_GRAY + " - ");
                                    for (String player : players) {
                                        String name = player.substring(0, player.indexOf('-'));
                                        String spec = player.substring(player.indexOf('-') + 1);
                                        redTeam.add(ChatColor.RED + name + ChatColor.GRAY + " - " + ChatColor.YELLOW + spec);
                                    }
                                }
                            }
                        }
                        AtomicBoolean resetMenu = new AtomicBoolean(true);
                        for (MessageEmbed.Field field : embed.getFields()) {
                            String fieldName = field.getName();
                            String fieldValue = field.getValue();
                            String[] players;
                            if (fieldName != null && fieldValue != null) {
                                boolean isBlueTeam = fieldName.contains("Blue Team");
                                boolean isRedTeam = fieldName.contains("Red Team");
                                players = fieldValue
                                        .replace("```", "")
                                        .replace(" ", "")
                                        .split("\n");
                                try {
                                    Bukkit.getScheduler().callSyncMethod(Warlords.getInstance(), () -> {
                                        for (String player : players) {
                                            String name = player.substring(0, player.indexOf('-'));
                                            String spec = player.substring(player.indexOf('-') + 1);
                                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(name);
                                            if (offlinePlayer == null) continue;
                                            UUID uuid = offlinePlayer.getUniqueId();
                                            if (resetMenu.get()) {
                                                Warlords.partyManager.getPartyFromAny(uuid).ifPresent(party -> party.getRegularGamesMenu().reset());
                                                resetMenu.set(false);
                                            }
                                            //includes offline players
                                            if (isBlueTeam) {
                                                Warlords.getPlayerSettings(uuid).setWantedTeam(Team.BLUE);
                                            } else if (isRedTeam) {
                                                Warlords.getPlayerSettings(uuid).setWantedTeam(Team.RED);
                                            }
                                            if (!spec.isEmpty()) {
                                                Warlords.getPlayerSettings(uuid).setSelectedSpec(Specializations.getSpecFromName(spec));
                                                DatabasePlayer databasePlayer = DatabaseManager.playerService.findByUUID(uuid);
                                                if (databasePlayer != null) {
                                                    databasePlayer.setLastSpec(Specializations.getSpecFromName(spec));
                                                    DatabaseManager.updatePlayerAsync(databasePlayer);
                                                }
                                                // TODO: fix
                                                if (!isExperimental) {
                                                    Warlords.partyManager.getPartyFromAny(uuid).ifPresent(party -> {
                                                        party.getRegularGamesMenu().getRegularGamePlayers().add(
                                                                new RegularGamesMenu.RegularGamePlayer(uuid, isBlueTeam ? Team.BLUE : Team.RED, Specializations.getSpecFromName(spec))
                                                        );
                                                    });
                                                }
                                            } else {
                                                // TODO: fix
                                                if (!isExperimental) {
                                                    Warlords.partyManager.getPartyFromAny(uuid).ifPresent(party -> {
                                                        party.getRegularGamesMenu().getRegularGamePlayers().add(
                                                                new RegularGamesMenu.RegularGamePlayer(uuid, isBlueTeam ? Team.BLUE : Team.RED, Specializations.PYROMANCER)
                                                        );
                                                    });
                                                }
                                            }
                                            if (!isExperimental) {
                                                Warlords.partyManager.getPartyFromAny(uuid).ifPresent(party -> {
                                                    if (offlinePlayer.isOnline()) {
                                                        offlinePlayer.getPlayer().getInventory().setItem(7,
                                                                new ItemBuilder((isBlueTeam ? Team.BLUE : Team.RED).getItem()).name("§aTeam Builder")
                                                                        .get());
                                                    }
                                                });
                                            }
                                            //only send messages to online
                                            if (offlinePlayer.isOnline()) {
                                                Player targetPlayer = offlinePlayer.getPlayer();
                                                targetPlayer.sendMessage(ChatColor.DARK_BLUE + "---------------------------------------");
                                                if (isBlueTeam) {
                                                    targetPlayer.sendMessage(ChatColor.GREEN + "You were automatically put into the " + ChatColor.BLUE + "BLUE" + ChatColor.GREEN + " team!");
                                                } else if (isRedTeam) {
                                                    targetPlayer.sendMessage(ChatColor.GREEN + "You were automatically put into the " + ChatColor.RED + "RED" + ChatColor.GREEN + " team!");
                                                }
                                                if (!spec.isEmpty()) {
                                                    PacketUtils.sendTitle(targetPlayer,
                                                            ChatColor.GREEN + spec,
                                                            isBlueTeam ? ChatColor.BLUE.toString() + ChatColor.BOLD + "BLUE"
                                                                    : isRedTeam ? ChatColor.RED.toString() + ChatColor.BOLD + "RED"
                                                                    : "",
                                                            0, 100, 40);
                                                    targetPlayer.sendMessage(ChatColor.GREEN + "Your spec was automatically changed to " + ChatColor.YELLOW + spec + ChatColor.GREEN + "!");
                                                }
                                                targetPlayer.sendMessage("");
                                                blueTeam.forEach(s -> {
                                                    if (s.contains(name)) {
                                                        targetPlayer.sendMessage(ChatColor.GREEN + s.substring(2, s.indexOf('-') - 2) + s.substring(s.indexOf('-') - 2));
                                                    } else {
                                                        targetPlayer.sendMessage(s);
                                                    }
                                                });
                                                redTeam.forEach(s -> {
                                                    if (s.contains(name)) {
                                                        targetPlayer.sendMessage(ChatColor.GREEN + s.substring(2, s.indexOf('-') - 2) + s.substring(s.indexOf('-') - 2));
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
            }
        }
    }


}
