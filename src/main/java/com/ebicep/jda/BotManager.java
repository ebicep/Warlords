package com.ebicep.jda;

import com.ebicep.jda.queuesystem.QueueListener;
import com.ebicep.jda.queuesystem.QueueManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.ServerStatusCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.Option;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.util.warlords.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.security.auth.login.LoginException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class BotManager {

    public static final List<DiscordServer> DISCORD_SERVERS = new ArrayList<>();
    public static JDA jda;
    public static String botToken;
    public static BukkitTask task;
    public static int numberOfMessagesSentLast30Sec = 0;

    public static void connect() throws LoginException {
        if (botToken != null) {
            jda = JDABuilder.createLight(botToken)
                            .enableIntents(GatewayIntent.GUILD_MEMBERS)
                            .addEventListeners(new BotListener(), new QueueListener())
                            .build();

            task = new BukkitRunnable() {

                int counter = 0;

                @Override
                public void run() {
                    if (jda.getStatus() != JDA.Status.CONNECTED) {
                        return;
                    }
                    if (counter == 0) {
                        for (DiscordServer discordServer : DISCORD_SERVERS) {
                            discordServer.setServer(jda.getGuildById(discordServer.getId()));
                            if (discordServer.getQueueChannel() == null) {
                                continue;
                            }
                            discordServer.getTextChannelByName(discordServer.getQueueChannel()).ifPresent(textChannel -> {
                                textChannel.getIterableHistory()
                                           .takeAsync(1000)
                                           .thenAccept(textChannel::purgeMessages)
                                           .thenAccept(unused -> QueueManager.sendQueue());
                            });
                        }
                    }
                    if (counter % 10 == 0) {
                        if (QueueManager.sendQueue) {
                            QueueManager.sendQueue = false;
                            QueueManager.sendNewQueue();
                        }
                    }
                    if (counter % 30 == 0 && ServerStatusCommand.enabled) {
                        sendStatusMessage(false);
                    }
                    if (counter % 3 == 0) {
                        if (numberOfMessagesSentLast30Sec > 0) {
                            numberOfMessagesSentLast30Sec--;
                        }
                    }

                    counter++;
                }
            }.runTaskTimer(Warlords.getInstance(), 100, 20);
        }
    }

    public static void sendStatusMessage(boolean onQuit) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("Server Status", null)
                .setColor(3066993)
                .setTimestamp(new Date().toInstant());
        eb.setDescription("**Players Online**: " + (onQuit ? Bukkit.getOnlinePlayers().size() - 1 : Bukkit.getOnlinePlayers().size()) + "\n");
        eb.appendDescription("**Players In Game**: " + Warlords.getGameManager().getPlayerCount(null) + "\n");
        eb.appendDescription("**Players Waiting in lobby**: " + Warlords.getGameManager().getPlayerCountInLobby(null) + "\n");
        for (GameHolder holder : Warlords.getGameManager().getGames()) {
            Game game = holder.getGame();
            if (game == null) {
                continue;
            }
            if (game.getState() instanceof PreLobbyState) {
                PreLobbyState state = (PreLobbyState) game.getState();
                if (!state.hasEnoughPlayers()) {
                    eb.appendDescription("**Game**: " + game.getGameMode().abbreviation + " - " + game.getMap()
                                                                                                      .getMapName() + " Lobby - Waiting for players\n");
                } else {
                    eb.appendDescription("**Game**: " + game.getGameMode().abbreviation + " - " + game.getMap()
                                                                                                      .getMapName() + " Lobby - " + state.getTimeLeftString() + " Left" + "\n");
                }
            } else if (game.getState() instanceof PlayingState) {
                OptionalInt timeLeft = WinAfterTimeoutOption.getTimeRemaining(game);
                String time = Utils.formatTimeLeft(timeLeft.isPresent() ? timeLeft.getAsInt() : (System.currentTimeMillis() - game.createdAt()) / 1000);
                String word = timeLeft.isPresent() ? " Left" : " Elapsed";
                boolean pve = false;
                for (Option option : game.getOptions()) {
                    if (!(option instanceof PveOption)) {
                        continue;
                    }
                    pve = true;
                    eb.appendDescription("**Game**: " + game.getGameMode().name + " - " +
                            game.getMap().getMapName() + " - " +
                            time + word);
                    if (option instanceof WaveDefenseOption) {
                        WaveDefenseOption waveDefenseOption = (WaveDefenseOption) option;
                        eb.appendDescription(" - " +
                                waveDefenseOption.getDifficulty().getName() + " - Wave " + waveDefenseOption.getWaveCounter() + "\n");
                    } else {
                        eb.appendDescription("\n");
                    }
                    break;
                }
                if (!pve) {
                    eb.appendDescription("**Game**: " + game.getGameMode().abbreviation + " - " + game.getMap()
                                                                                                      .getMapName() + " - " + time + word + " - " + game.getPoints(
                            Team.BLUE) + ":" + game.getPoints(Team.RED) + "\n");
                }
            }
        }
        StringBuilder stringBuilder = new StringBuilder("**Parties**: ");
        PartyManager.PARTIES.forEach(party -> stringBuilder.append(party.getLeaderName()).append(" (").append(party.getPartyPlayers().size()).append("), "));
        stringBuilder.setLength(stringBuilder.length() - 1);
        eb.appendDescription(stringBuilder);

        MessageEmbed messageEmbed = eb.build();

        for (DiscordServer discordServer : DISCORD_SERVERS) {
            if (discordServer.getStatusChannel() == null) {
                continue;
            }
            Message statusMessage = discordServer.getStatusMessage();
            discordServer.getTextChannelByName(discordServer.getStatusChannel()).ifPresent(textChannel -> {
                try {
                    textChannel.getLatestMessageId();
                } catch (Exception e) {
                    textChannel.sendMessageEmbeds(messageEmbed).queue(discordServer::setStatusMessage);
                    return;
                }
                if (statusMessage == null) {
                    textChannel.sendMessageEmbeds(messageEmbed).queue(discordServer::setStatusMessage);
                } else if (textChannel.getLatestMessageId().equals(statusMessage.getId())) {
                    statusMessage.editMessageEmbeds(messageEmbed).queue();
                } else {
                    statusMessage.delete().queue();
                    textChannel.sendMessageEmbeds(messageEmbed).queue(discordServer::setStatusMessage);
                }
            });
        }
    }

    public static Optional<TextChannel> getTextChannelCompsByName(String name) {
        return getServer("comps").getTextChannelByName(name);
    }

    public static DiscordServer getServer(String name) {
        return DISCORD_SERVERS.stream()
                              .filter(discordServer -> discordServer.getName().equals(name))
                              .findFirst()
                              .orElse(null);
    }

    public static void sendDebugMessage(String message) {
        if (jda == null) {
            return;
        }
        getTextChannelGeneralByName("admin-log").ifPresent(textChannel -> textChannel.sendMessage(message).queue());
    }

    public static Optional<TextChannel> getTextChannelGeneralByName(String name) {
        return getServer("general").getTextChannelByName(name);
    }

    public static void sendDebugMessage(MessageEmbed embed) {
        if (jda == null) {
            return;
        }
        getTextChannelGeneralByName("admin-log").ifPresent(textChannel -> textChannel.sendMessageEmbeds(embed).queue());
    }

    public static void sendMessageToStatusChannel(String message) {
        if (jda == null) {
            return;
        }
        if (numberOfMessagesSentLast30Sec > 15) {
            return;
        }
        for (DiscordServer discordServer : DISCORD_SERVERS) {
            if (discordServer.getStatusChannel() == null) {
                continue;
            }
            discordServer.getTextChannelByName(discordServer.getStatusChannel()).ifPresent(textChannel -> {
                textChannel.sendMessage(message).queue();
                numberOfMessagesSentLast30Sec++;
            });
        }
    }

    public static void deleteStatusMessage() {
        for (DiscordServer discordServer : DISCORD_SERVERS) {
            if (discordServer.getStatusMessage() == null) {
                continue;
            }
            discordServer.getStatusMessage().delete().complete();
        }
    }

    public static class DiscordServer {

        private final String name;
        private final String id;
        private final String statusChannel;
        private final String queueChannel;
        private final HashMap<String, TextChannel> channelCache = new HashMap<>();
        private Guild server;
        private Message statusMessage;

        public DiscordServer(String name, String id, String statusChannel, String queueChannel) {
            this.name = name;
            this.id = id;
            this.statusChannel = statusChannel;
            this.queueChannel = queueChannel;
        }

        public String getName() {
            return name;
        }

        public Guild getServer() {
            return server;
        }

        public void setServer(Guild server) {
            this.server = server;
        }

        public String getId() {
            return id;
        }

        public String getStatusChannel() {
            return statusChannel;
        }

        public String getQueueChannel() {
            return queueChannel;
        }

        public HashMap<String, TextChannel> getChannelCache() {
            return channelCache;
        }

        public Message getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(Message statusMessage) {
            this.statusMessage = statusMessage;
        }

        public Optional<TextChannel> getTextChannelByName(String name) {
            if (jda == null) {
                return Optional.empty();
            }
            if (channelCache.containsKey(name)) {
                return Optional.of(channelCache.get(name));
            }
            Optional<TextChannel> textChannel = jda.getTextChannelsByName(name, true).stream().findFirst();
            textChannel.ifPresent(channel -> channelCache.put(name, channel));
            return textChannel;
        }
    }

}
