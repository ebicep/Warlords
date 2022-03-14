package com.ebicep.warlords.achievements.types;

import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Date;
import java.util.function.Predicate;

public enum TieredAchievements implements Achievement {

    //GENERAL
    GAMES_PLAYED_50("Play 50 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getPlays() > 50),
    GAMES_PLAYED_100("Play 100 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getPlays() > 100),
    GAMES_PLAYED_250("Play 250 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getPlays() > 250),
    GAMES_PLAYED_500("Play 500 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getPlays() > 500),
    GAMES_PLAYED_1000("Play 1000 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getPlays() > 1000),
    GAMES_WON_25("Win 25 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getWins() > 25),
    GAMES_WON_50("Win 50 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getWins() > 50),
    GAMES_WON_125("Win 125 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getWins() > 125),
    GAMES_WON_250("Win 250 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getWins() > 250),
    GAMES_WON_500("Win 500 Games",
            "",
            null,
            databasePlayer -> databasePlayer.getPubStats().getWins() > 500),

    //CTF
    GAMES_WON_CTF_10("Win 10 CTF Games",
            "",
            GameMode.CAPTURE_THE_FLAG,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 10),
    GAMES_WON_CTF_25("Win 25 CTF Games",
            "",
            GameMode.CAPTURE_THE_FLAG,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 25),
    GAMES_WON_CTF_50("Win 50 CTF Games",
            "",
            GameMode.CAPTURE_THE_FLAG,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 50),
    GAMES_WON_CTF_75("Win 75 CTF Games",
            "",
            GameMode.CAPTURE_THE_FLAG,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 75),
    GAMES_WON_CTF_100("Win 100 CTF Games",
            "",
            GameMode.CAPTURE_THE_FLAG,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 100),

    //TDM
    GAMES_WON_TDM_10("Win 10 TDM Games",
            "",
            GameMode.TEAM_DEATHMATCH,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 10),
    GAMES_WON_TDM_25("Win 25 TDM Games",
            "",
            GameMode.TEAM_DEATHMATCH,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 25),
    GAMES_WON_TDM_50("Win 50 TDM Games",
            "",
            GameMode.TEAM_DEATHMATCH,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 50),
    GAMES_WON_TDM_75("Win 75 TDM Games",
            "",
            GameMode.TEAM_DEATHMATCH,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 75),
    GAMES_WON_TDM_100("Win 100 TDM Games",
            "",
            GameMode.TEAM_DEATHMATCH,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 100),

    ;

    public String name;
    public String description;
    public GameMode gameMode;
    public Predicate<DatabasePlayer> databasePlayerPredicate;


    TieredAchievements(String name, String description, GameMode gameMode, Predicate<DatabasePlayer> databasePlayerPredicate) {
        this.name = name;
        this.description = description;
        this.gameMode = gameMode;
        this.databasePlayerPredicate = databasePlayerPredicate;
    }

    @Override
    public void sendAchievementUnlockMessage(Player player) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  Achievement Unlocked: " + ChatColor.GOLD + name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + description).create()));
        ChatUtils.sendMessageToPlayer(player, Collections.singletonList(message), ChatColor.GREEN, true);
    }

    @Override
    public void sendAchievementUnlockMessageToOthers(WarlordsPlayer warlordsPlayer) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  " + ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GREEN + " unlocked: " + ChatColor.GOLD + name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(WordWrap.wrapWithNewlineWithColor(description, 200, ChatColor.GREEN)).create()));
        warlordsPlayer.getGame().warlordsPlayers()
                //.filter(wp -> wp.getTeam() == warlordsPlayer.getTeam())
                .filter(wp -> wp != warlordsPlayer)
                .filter(wp -> wp.getEntity() instanceof Player)
                .map(wp -> (Player) wp.getEntity())
                .forEachOrdered(player -> ChatUtils.sendMessageToPlayer(player, Collections.singletonList(message), ChatColor.GREEN, true));
    }

    public static class TieredAchievementRecord extends AbstractAchievementRecord<TieredAchievements> {

        public TieredAchievementRecord() {
        }

        public TieredAchievementRecord(TieredAchievements achievement) {
            super(achievement);
        }

        public TieredAchievementRecord(TieredAchievements achievement, Date date) {
            super(achievement, date);
        }

        @Override
        public String getName() {
            return getAchievement().name;
        }

        @Override
        public String getDescription() {
            return getAchievement().description;
        }

        @Override
        public GameMode getGameMode() {
            return getAchievement().gameMode;
        }

        @Override
        public TieredAchievements[] getAchievements() {
            return TieredAchievements.values();
        }

    }
}
