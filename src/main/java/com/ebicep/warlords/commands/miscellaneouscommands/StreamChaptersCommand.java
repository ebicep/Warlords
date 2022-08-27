package com.ebicep.warlords.commands.miscellaneouscommands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.CommandIssuer;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import com.ebicep.jda.BotManager;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.player.general.Specializations;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@CommandAlias("streamchapters")
@CommandPermission("group.adminisrator")
public class StreamChaptersCommand extends BaseCommand {

    public static final HashMap<UUID, Instant> playerTimeStart = new HashMap<>();
    public static final HashMap<UUID, List<GameTime>> gameTimes = new HashMap<>();

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

    @Subcommand("start")
    @Description("Mark start of stream")
    public void start(Player player) {
        playerTimeStart.put(player.getUniqueId(), Instant.now());
        gameTimes.put(player.getUniqueId(), new ArrayList<>());
        player.sendMessage(ChatColor.GREEN + "Began recording game time");
    }

    @Subcommand("get")
    @Description("Prints stream chapters")
    public void get(Player player) {
        StringBuilder chapters = new StringBuilder("00:00:00 - Lobby");
        Instant startTime = playerTimeStart.get(player.getUniqueId());
        gameTimes.get(player.getUniqueId()).forEach(gameTime -> {
            Instant gameStartTime = gameTime.getStart();
            Instant gameEndTime = gameTime.getEnd();
            if (gameEndTime != null && ChronoUnit.SECONDS.between(gameStartTime, gameEndTime) > 10) {
                appendTime(chapters, startTime, gameStartTime);
                chapters.append(" - ").append(gameTime.getMap().getMapName()).append(" - ").append(gameTime.getSpec().name);
                appendTime(chapters, startTime, gameEndTime);
                chapters.append(" - Lobby");
            }
        });
        System.out.println(chapters);
        BotManager.getTextChannelCompsByName("bot-testing").ifPresent(textChannel -> {
            textChannel.sendMessage(chapters.toString()).queue();
        });
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

        public GameTime(Instant start, GameMap map, Specializations spec) {
            this.start = start;
            this.map = map;
            this.spec = spec;
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
    }

}
