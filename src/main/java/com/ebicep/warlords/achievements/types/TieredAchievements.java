package com.ebicep.warlords.achievements.types;

import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.bukkit.WordWrap;
import com.ebicep.warlords.util.chat.ChatUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.Collections;
import java.util.function.Predicate;

import static com.ebicep.warlords.game.GameMode.*;

public enum TieredAchievements implements Achievement {

    //GENERAL
    GAMES_PLAYED_50("Play 50 Games",
            "Play 50 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getPlays() >= 50
    ),
    GAMES_PLAYED_100("Play 100 Games",
            "Play 100 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getPlays() >= 100
    ),
    GAMES_PLAYED_250("Play 250 Games",
            "Play 250 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getPlays() >= 250
    ),
    GAMES_PLAYED_500("Play 500 Games",
            "Play 500 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getPlays() >= 500
    ),
    GAMES_PLAYED_1000("Play 1000 Games",
            "Play 1000 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getPlays() >= 1000
    ),
    GAMES_WON_25("Win 25 Games",
            "Win 25 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getWins() >= 25
    ),
    GAMES_WON_50("Win 50 Games",
            "Win 50 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getWins() >= 50
    ),
    GAMES_WON_125("Win 125 Games",
            "Win 125 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getWins() >= 125
    ),
    GAMES_WON_250("Win 250 Games",
            "Win 250 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getWins() >= 250
    ),
    GAMES_WON_500("Win 500 Games",
            "Win 500 Games",
            null,
            false,
            databasePlayer -> databasePlayer.getPubStats().getWins() >= 500
    ),

    //CTF
    GAMES_WON_CTF_10("Win 10 CTF Games",
            "Win 10 CTF Games",
            CAPTURE_THE_FLAG,
            false,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() >= 10
    ),
    GAMES_WON_CTF_25("Win 25 CTF Games",
            "Win 25 CTF Games",
            CAPTURE_THE_FLAG,
            false,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() >= 25
    ),
    GAMES_WON_CTF_50("Win 50 CTF Games",
            "Win 50 CTF Games",
            CAPTURE_THE_FLAG,
            false,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() >= 50
    ),
    GAMES_WON_CTF_75("Win 75 CTF Games",
            "Win 75 CTF Games",
            CAPTURE_THE_FLAG,
            false,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() >= 75
    ),
    GAMES_WON_CTF_100("Win 100 CTF Games",
            "Win 100 CTF Games",
            CAPTURE_THE_FLAG,
            false,
            databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() >= 100
    ),

    //TDM
    GAMES_WON_TDM_10("Win 10 TDM Games",
            "Win 10 TDM Games",
            TEAM_DEATHMATCH,
            false,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() >= 10
    ),
    GAMES_WON_TDM_25("Win 25 TDM Games",
            "Win 25 TDM Games",
            TEAM_DEATHMATCH,
            false,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() >= 25
    ),
    GAMES_WON_TDM_50("Win 50 TDM Games",
            "Win 50 TDM Games",
            TEAM_DEATHMATCH,
            false,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() >= 50
    ),
    GAMES_WON_TDM_75("Win 75 TDM Games",
            "Win 75 TDM Games",
            TEAM_DEATHMATCH,
            false,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() >= 75
    ),
    GAMES_WON_TDM_100("Win 100 TDM Games",
            "Win 100 TDM Games",
            TEAM_DEATHMATCH,
            false,
            databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() >= 100
    ),

    //PVE
    GAMES_PLAYED_PVE_1("Warped World",
            "Enter a PvE game for the first time.",
            WAVE_DEFENSE,
            false,
            databasePlayer -> databasePlayer.getPveStats().getPlays() >= 1
    ),

    ;

    public static final TieredAchievements[] VALUES = values();
    public final String name;
    public final String description;
    public final GameMode gameMode;
    public final boolean isHidden;
    public final Predicate<DatabasePlayer> databasePlayerPredicate;


    TieredAchievements(String name, String description, GameMode gameMode, boolean isHidden, Predicate<DatabasePlayer> databasePlayerPredicate) {
        this.name = name;
        this.description = description;
        this.gameMode = gameMode;
        this.isHidden = isHidden;
        this.databasePlayerPredicate = databasePlayerPredicate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public GameMode getGameMode() {
        return gameMode;
    }

    @Override
    public Specializations getSpec() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return isHidden;
    }

    @Override
    public void sendAchievementUnlockMessage(Player player) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  Achievement Unlocked: " + ChatColor.GOLD + name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + description).create()));
        ChatUtils.sendMessageToPlayer(player, Collections.singletonList(message), ChatColor.GREEN, true);
    }

    @Override
    public void sendAchievementUnlockMessageToOthers(WarlordsEntity warlordsPlayer) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  " + ChatColor.AQUA + warlordsPlayer.getName() + ChatColor.GREEN + " unlocked: " + ChatColor.GOLD + name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder(WordWrap.wrapWithNewlineWithColor(description, 200, ChatColor.GREEN)).create()
        ));
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

        public TieredAchievementRecord(TieredAchievements achievement, Instant date) {
            super(achievement, date);
        }

    }
}
