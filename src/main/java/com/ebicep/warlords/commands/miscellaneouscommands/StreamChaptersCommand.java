package com.ebicep.warlords.commands.miscellaneouscommands;

import com.ebicep.jda.BotManager;
import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.game.GameMap;
import com.ebicep.warlords.player.Specializations;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class StreamChaptersCommand implements CommandExecutor {

    public static final HashMap<UUID, Instant> playerTimeStart = new HashMap<>();
    public static final HashMap<UUID, List<GameTime>> gameTimes = new HashMap<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!commandSender.isOp()) {
            return true;
        }

        if (args.length == 0) {
            return true;
        }

        String commandName = args[0];
        Player player = (Player) commandSender;
        switch (commandName.toLowerCase()) {
            case "start":
                playerTimeStart.put(player.getUniqueId(), Instant.now());
                gameTimes.put(player.getUniqueId(), new ArrayList<>());
                player.sendMessage(ChatColor.GREEN + "Began recording game time");
                break;
            case "get":
                StringBuilder chapters = new StringBuilder("00:00:00 - Lobby");
                Instant startTime = playerTimeStart.get(player.getUniqueId());
                gameTimes.get(player.getUniqueId()).forEach(gameTime -> {
                    Instant gameStartTime = gameTime.getStart();
                    Instant gameEndTime = gameTime.getEnd();
                    if (gameEndTime != null && ChronoUnit.SECONDS.between(gameStartTime, gameEndTime) > 10) {
                        appendTime(chapters, startTime, gameStartTime);
                        chapters.append(" - ").append(gameTime.getMap().getMapName()).append(" - ").append(gameTime.getSpec().name).append(" - ").append(gameTime.getPlayerCount()).append(" players");
                        appendTime(chapters, startTime, gameEndTime);
                        chapters.append(" - Lobby");
                    }
                });
                System.out.println(chapters);
                BotManager.getTextChannelCompsByName("bot-testing").ifPresent(textChannel -> {
                    textChannel.sendMessage(chapters.toString()).queue();
                });

                break;
        }


        return true;
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

    public void register(Warlords instance) {
        instance.getCommand("streamchapters").setExecutor(this);
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
