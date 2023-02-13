package com.ebicep.jda;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.miscellaneouscommands.DiscordCommand;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.party.Party;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.party.PartyPlayer;
import com.ebicep.warlords.party.RegularGamesMenu;
import com.ebicep.warlords.player.general.PlayerSettings;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.util.bukkit.ItemBuilder;
import com.ebicep.warlords.util.bukkit.PacketUtils;
import com.ebicep.warlords.util.java.Pair;
import com.ebicep.warlords.util.warlords.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class BotListener extends ListenerAdapter implements Listener {

    private static BukkitTask onGoingBalance;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (BotManager.DISCORD_SERVERS.stream().noneMatch(discordServer -> Objects.equals(discordServer.getServer(), event.getGuild()))) {
            return;
        }
        Member member = event.getMember();
        Message message = event.getMessage();
        switch (event.getChannelType()) {
            case PRIVATE:
                parsePrivateLinkMessage(event, message);
                break;
            case TEXT:
                readBalanceStatuses(event, message);
                break;
        }
    }

    private void parsePrivateLinkMessage(MessageReceivedEvent event, Message message) {
        if (event.getAuthor().isBot()) {
            return;
        }
        try {
            Long key = Long.parseLong(message.getContentRaw());
            if (DiscordCommand.playerLinkKeys.containsValue(key)) {
                UUID uuid = DiscordCommand.playerLinkKeys.getKey(key);
                DatabaseManager.updatePlayer(uuid, databasePlayer -> {
                    Long id = event.getAuthor().getIdLong();
                    databasePlayer.setDiscordID(id);
                    event.getPrivateChannel()
                         .sendMessage("You linked **" + Bukkit.getOfflinePlayer(uuid).getName() + "** to your discord account (" + id + ").")
                         .queue();
                    if (Bukkit.getOfflinePlayer(uuid).isOnline()) {
                        Bukkit.getOfflinePlayer(uuid)
                              .getPlayer()
                              .sendMessage(ChatColor.GREEN + "Your account was linked to the discord account " + event.getAuthor()
                                                                                                                      .getAsTag() + " (" + id + ").");
                    }

                    BotManager.sendDebugMessage(
                            new EmbedBuilder()
                                    .setColor(3066993)
                                    .setTitle("Player Linked - " + id)
                                    .setDescription("UUID: " + uuid + "\n" + "IGN: " + databasePlayer.getName() + "\n" + "KEY: " + key)
                                    .build()
                    );
                });
            }
        } catch (Exception e) {
            System.out.println(message);
            System.out.println("Could not parseLong from direct message");
            e.printStackTrace();
        }
    }

    private void readBalanceStatuses(MessageReceivedEvent event, Message message) {
        TextChannel textChannel = event.getTextChannel();
        switch (textChannel.getName().toLowerCase()) {
            case "gs-teams":
            case "bot-teams": {
                readOnGoingBalance(message);
                break;
            }
            case "teams": {
                readNewTeamPosted(message);
                break;
            }
        }
    }

    private void readOnGoingBalance(Message message) {
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
                                    0, 5, 0
                            );
                        }
                    });
                    //auto cancel after 15 seconds
                    if (counter++ > 20 * 15) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(Warlords.getInstance(), 10, 0);

        }
    }

    private void readNewTeamPosted(Message message) {
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
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayerIfCached(name);
                                if (offlinePlayer == null) {
                                    continue;
                                }
                                UUID uuid = offlinePlayer.getUniqueId();
                                Pair<Party, PartyPlayer> partyPlayerPair = PartyManager.getPartyAndPartyPlayerFromAny(uuid);
                                if (resetMenu.get()) {
                                    if (partyPlayerPair != null) {
                                        partyPlayerPair.getA().getRegularGamesMenu().reset();
                                    }
                                    resetMenu.set(false);
                                }
                                //includes offline players
                                if (isBlueTeam) {
                                    PlayerSettings.getPlayerSettings(uuid).setWantedTeam(Team.BLUE);
                                } else if (isRedTeam) {
                                    PlayerSettings.getPlayerSettings(uuid).setWantedTeam(Team.RED);
                                }
                                if (!spec.isEmpty()) {
                                    PlayerSettings.getPlayerSettings(uuid).setSelectedSpec(Specializations.getSpecFromName(spec));
                                    DatabaseManager.updatePlayer(uuid, databasePlayer -> {
                                        databasePlayer.setLastSpec(Specializations.getSpecFromName(spec));
                                    });
                                    if (!isExperimental) {
                                        if (partyPlayerPair != null) {
                                            partyPlayerPair.getA().getRegularGamesMenu().getRegularGamePlayers().add(
                                                    new RegularGamesMenu.RegularGamePlayer(uuid,
                                                            isBlueTeam ? Team.BLUE : Team.RED,
                                                            Specializations.getSpecFromName(spec)
                                                    )
                                            );
                                        }
                                    }
                                } else {
                                    if (!isExperimental) {
                                        if (partyPlayerPair != null) {
                                            partyPlayerPair.getA().getRegularGamesMenu().getRegularGamePlayers().add(
                                                    new RegularGamesMenu.RegularGamePlayer(uuid, isBlueTeam ? Team.BLUE : Team.RED, Specializations.PYROMANCER)
                                            );
                                        }
                                    }
                                }
                                if (!isExperimental) {
                                    if (partyPlayerPair != null && offlinePlayer.isOnline()) {
                                        offlinePlayer.getPlayer().getInventory().setItem(7,
                                                new ItemBuilder((isBlueTeam ? Team.BLUE : Team.RED).getItem())
                                                        .name("§aTeam Builder")
                                                        .get()
                                        );

                                    }
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
                                                0, 100, 40
                                        );
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
    }

    private static void cancelOnGoingBalance() {
        if (onGoingBalance != null) {
            onGoingBalance.cancel();
        }
    }


}
