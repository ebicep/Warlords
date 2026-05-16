package com.ebicep.jda;

import com.ebicep.jda.queuesystem.QueueListener;
import com.ebicep.jda.queuesystem.QueueManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.commands.debugcommands.misc.ServerStatusCommand;
import com.ebicep.warlords.game.Game;
import com.ebicep.warlords.game.GameManager.GameHolder;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.game.Team;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.win.WinAfterTimeoutOption;
import com.ebicep.warlords.game.state.PlayingState;
import com.ebicep.warlords.game.state.PreLobbyState;
import com.ebicep.warlords.party.PartyManager;
import com.ebicep.warlords.util.chat.ChatUtils;
import com.ebicep.warlords.util.java.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import javax.security.auth.login.LoginException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public class BotManager {

    public enum BotChannel {
        STATUS("status"),
        QUEUE("queue"),
        GAMES_BACKLOG("gamesBacklog"),
        BOT_TEAMS("botTeams"),
        GS_TEAMS("gsTeams"),
        TEAMS("teams"),
        BOT_TESTING("botTesting"),
        ADMIN_LOG("adminLog"),
        ERRORS("errors");

        private final String configKey;

        BotChannel(String configKey) {
            this.configKey = configKey;
        }

        public String getConfigKey() {
            return configKey;
        }
    }

    public static final List<DiscordServer> DISCORD_SERVERS = new ArrayList<>();
    public static JDA jda;
    public static String botToken;
    public static BukkitTask task;
    public static int numberOfMessagesSentLast30Sec = 0;

    public static void connect() throws LoginException {
        if (botToken == null) {
            ChatUtils.MessageType.DISCORD_BOT.sendMessage("No bot token found, not connecting to discord.");
            return;
        }
        jda = JDABuilder.createLight(botToken)
                        .enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
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
                        discordServer.rebindGuild(jda);
                        if (discordServer.getChannelName(BotChannel.QUEUE).isEmpty()) {
                            continue;
                        }
                        discordServer.getChannel(BotChannel.QUEUE).ifPresent(textChannel -> {
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
            if (game.getGameMode() == GameMode.LOBBY) {
                continue;
            }
            if (game.getState() instanceof PreLobbyState state) {
                if (!state.hasEnoughPlayers()) {
                    eb.appendDescription("**Game**: " + game.getGameMode().abbreviation + " - " + game.getMap()
                                                                                                      .getMapName() + " Lobby - Waiting for players\n");
                } else {
                    eb.appendDescription("**Game**: " + game.getGameMode().abbreviation + " - " + game.getMap()
                                                                                                      .getMapName() + " Lobby - " + state.getTimeLeftString() + " Left" + "\n");
                }
            } else if (game.getState() instanceof PlayingState) {
                OptionalInt timeLeft = WinAfterTimeoutOption.getTimeRemaining(game);
                String time = StringUtils.formatTimeLeft(timeLeft.isPresent() ? timeLeft.getAsInt() : (System.currentTimeMillis() - game.createdAt()) / 1000);
                String word = timeLeft.isPresent() ? " Left" : " Elapsed";
                game.getOption(PveOption.class).stream().findFirst().ifPresentOrElse(pveOption -> {
                    eb.appendDescription("**Game**: " + game.getGameMode().name + " - " +
                            game.getMap().getMapName() + " - " +
                            time + word);
                    if (pveOption instanceof WaveDefenseOption waveDefenseOption) {
                        eb.appendDescription(" - " +
                                waveDefenseOption.getDifficulty().getName() + " - Wave " + waveDefenseOption.getWaveCounter() + "\n");
                    } else {
                        eb.appendDescription("\n");
                    }
                }, () -> {
                    eb.appendDescription("**Game**: " + game.getGameMode().abbreviation + " - " +
                            game.getMap().getMapName() + " - " +
                            time + word + " - " +
                            game.getPoints(Team.BLUE) + ":" + game.getPoints(Team.RED) + "\n");
                });
            }
        }
        StringBuilder stringBuilder = new StringBuilder("**Parties**: ");
        PartyManager.PARTIES.forEach(party -> stringBuilder.append(party.getLeaderName()).append(" (").append(party.getPartyPlayers().size()).append("), "));
        stringBuilder.setLength(stringBuilder.length() - 1);
        eb.appendDescription(stringBuilder);

        MessageEmbed messageEmbed = eb.build();

        for (DiscordServer discordServer : DISCORD_SERVERS) {
            if (discordServer.getChannelName(BotChannel.STATUS).isEmpty()) {
                continue;
            }
            Message statusMessage = discordServer.getStatusMessage();
            discordServer.getChannel(BotChannel.STATUS).ifPresent(textChannel -> {
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
        DiscordServer main = getServer("main");
        if (main == null) {
            return;
        }
        main.getChannel(BotChannel.ADMIN_LOG).ifPresent(textChannel -> textChannel.sendMessage(message).queue());
    }

    public static void sendDebugMessage(MessageEmbed embed) {
        if (jda == null) {
            return;
        }
        DiscordServer main = getServer("main");
        if (main == null) {
            return;
        }
        main.getChannel(BotChannel.ADMIN_LOG).ifPresent(textChannel -> textChannel.sendMessageEmbeds(embed).queue());
    }

    public static void sendMessageToStatusChannel(String message) {
        if (jda == null) {
            return;
        }
        if (numberOfMessagesSentLast30Sec > 15) {
            return;
        }
        for (DiscordServer discordServer : DISCORD_SERVERS) {
            if (discordServer.getChannelName(BotChannel.STATUS).isEmpty()) {
                continue;
            }
            discordServer.getChannel(BotChannel.STATUS).ifPresent(textChannel -> {
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
        private final Map<BotChannel, String> channels;
        private final HashMap<String, TextChannel> channelCache = new HashMap<>();
        private Guild server;
        private Message statusMessage;

        public DiscordServer(String name, String id, Map<BotChannel, String> channels) {
            this.name = name;
            this.id = id;
            this.channels = channels;
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

        public Map<BotChannel, String> getChannels() {
            return channels;
        }

        public Optional<String> getChannelName(BotChannel channel) {
            return Optional.ofNullable(channels.get(channel)).filter(s -> !s.isEmpty());
        }

        public Optional<TextChannel> getChannel(BotChannel channel) {
            return getChannelName(channel).flatMap(this::getTextChannelByName);
        }

        public Message getStatusMessage() {
            return statusMessage;
        }

        public void setStatusMessage(Message statusMessage) {
            this.statusMessage = statusMessage;
        }

        public void clearChannelCache() {
            channelCache.clear();
        }

        public void rebindGuild(JDA jda) {
            setServer(jda.getGuildById(id));
            clearChannelCache();
        }

        public Optional<TextChannel> getTextChannelByName(String name) {
            if (jda == null) {
                return Optional.empty();
            }
            if (channelCache.containsKey(name)) {
                return Optional.of(channelCache.get(name));
            }
            Optional<TextChannel> textChannel;
            if (server != null) {
                textChannel = server.getTextChannelsByName(name, true).stream().findFirst();
            } else {
                textChannel = jda.getTextChannelsByName(name, true).stream().findFirst();
            }
            textChannel.ifPresent(channel -> channelCache.put(name, channel));
            return textChannel;
        }
    }

}
