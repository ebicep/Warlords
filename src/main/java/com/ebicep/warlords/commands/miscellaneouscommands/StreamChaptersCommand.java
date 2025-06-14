package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.database.DatabaseManager;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.player.general.Specializations;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@CommandAlias("streamchapters")
@CommandPermission("group.streamer")
public class StreamChaptersCommand extends BaseCommand {

    public static final HashMap<UUID, Instant> PLAYER_TIME_START = new HashMap<>();
    public static final HashMap<UUID, List<GameTime>> GAME_TIMES = new HashMap<>();

    @Subcommand("start")
    @Description("Mark start of stream")
    public void start(Player player) {
        PLAYER_TIME_START.put(player.getUniqueId(), Instant.now());
        GAME_TIMES.put(player.getUniqueId(), new ArrayList<>());
        player.sendMessage(Component.text("Began recording game time", NamedTextColor.GREEN));
    }

    @Subcommand("startoffset")
    @Description("Mark start of stream with offset (how long ago stream started)")
    public void startOffset(Player player, Integer hour, Integer minute, Integer second) {
        PLAYER_TIME_START.put(player.getUniqueId(), Instant.now().minus(hour, ChronoUnit.HOURS).minus(minute, ChronoUnit.MINUTES).minus(second, ChronoUnit.SECONDS));
        GAME_TIMES.put(player.getUniqueId(), new ArrayList<>());
        player.sendMessage(Component.text("Began recording game time", NamedTextColor.GREEN));
    }

    @Subcommand("get")
    @Description("Prints stream chapters")
    public void get(Player player) {
        print(player.getUniqueId());
    }

    public static void print(UUID uuid) {
        print(uuid, PLAYER_TIME_START.get(uuid), GAME_TIMES.get(uuid));
    }

    public static void print(UUID uuid, Instant startTime, List<GameTime> gameTimes) {
        StringBuilder chapters = new StringBuilder("00:00:00 - Lobby");
        gameTimes.forEach(gameTime -> {
            Instant gameStartTime = gameTime.getStart();
            Instant gameEndTime = gameTime.getEnd();
            if (gameEndTime != null && ChronoUnit.SECONDS.between(gameStartTime, gameEndTime) > 10) {
                appendTime(chapters, startTime, gameStartTime);
                chapters.append(" - ")
                        .append(gameTime.getMap().getMapName())
                        .append(" - ")
                        .append(gameTime.getSpec().name)
                        .append(" - ")
                        .append(gameTime.getPlayerCount())
                        .append(" players");
                appendTime(chapters, startTime, gameEndTime);
                chapters.append(" - Lobby");
            }
        });
        System.out.println(chapters);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.sendMessage(Component.text(chapters.toString(), NamedTextColor.GREEN));
        }
        BotManager.getTextChannelCompsByName("bot-testing").ifPresent(textChannel -> {
            textChannel.sendMessage(chapters.toString()).queue();
        });
    }

    public static void appendTime(StringBuilder chapters, Instant start, Instant end) {
        long hours = ChronoUnit.HOURS.between(start, end) % 24;
        long minutes = ChronoUnit.MINUTES.between(start, end) % 60;
        long seconds = ChronoUnit.SECONDS.between(start, end) % 60;
        chapters.append("\n");
        if (hours < 10) {
            chapters.append("0");
        }
        chapters.append(hours).append(":");
        if (minutes < 10) {
            chapters.append("0");
        }
        chapters.append(minutes).append(":");
        if (seconds < 10) {
            chapters.append("0");
        }
        chapters.append(seconds);
    }

    @Subcommand("get")
    @Description("Prints stream chapters with offset")
    public void get(Player player, Integer hour, Integer minute, Integer second) {
        DatabaseManager.getPlayer(player.getUniqueId(), databasePlayer -> {
                    Instant startTime = Instant.now().minus(hour, ChronoUnit.HOURS).minus(minute, ChronoUnit.MINUTES).minus(second, ChronoUnit.SECONDS);
                    List<GameTime> gameLogs = databasePlayer.getGameLogs();
                    List<GameTime> gameTimes = gameLogs.stream().filter(gameTime -> gameTime.start.isAfter(startTime)).toList();
                    print(player.getUniqueId(), startTime, gameTimes);
                }
        );
    }

    @HelpCommand
    public void help(CommandIssuer issuer, CommandHelp help) {
        help.getHelpEntries().sort(Comparator.comparing(HelpEntry::getCommand));
        help.showHelp();
    }

    public static class GameTime {

        private Instant start;
        private Instant end;
        private GameMap map;
        private Specializations spec;
        private int playerCount;

        public GameTime(Instant start, GameMap map, Specializations spec, int playerCount) {
            this.start = start;
            this.map = map;
            this.spec = spec;
            this.playerCount = playerCount;
        }

        public Instant getStart() {
            return start;
        }

        public Instant getEnd() {
            return end;
        }

        public void setEnd(Instant end) {
            this.end = end;
        }

        public GameMap getMap() {
            return map;
        }

        public Specializations getSpec() {
            return spec;
        }

        public int getPlayerCount() {
            return playerCount;
        }

    }


}
