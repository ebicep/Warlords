package com.ebicep.warlords.achievements;

import com.ebicep.warlords.database.repositories.player.pojos.general.DatabasePlayer;
import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.player.PlayerStatisticsSecond;
import com.ebicep.warlords.player.WarlordsPlayer;
import com.ebicep.warlords.util.ChatUtils;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum Achievements {

    GAMES_PLAYED_50("Play 50 Games", "", databasePlayer -> databasePlayer.getPubStats().getPlays() > 50, null),
    GAMES_PLAYED_100("Play 100 Games", "", databasePlayer -> databasePlayer.getPubStats().getPlays() > 100, null),
    GAMES_PLAYED_250("Play 250 Games", "", databasePlayer -> databasePlayer.getPubStats().getPlays() > 250, null),
    GAMES_PLAYED_500("Play 500 Games", "", databasePlayer -> databasePlayer.getPubStats().getPlays() > 500, null),
    GAMES_PLAYED_1000("Play 1000 Games", "", databasePlayer -> databasePlayer.getPubStats().getPlays() > 1000, null),
    GAMES_WON_25("Win 25 Games", "", databasePlayer -> databasePlayer.getPubStats().getWins() > 25, null),
    GAMES_WON_50("Win 50 Games", "", databasePlayer -> databasePlayer.getPubStats().getWins() > 50, null),
    GAMES_WON_125("Win 125 Games", "", databasePlayer -> databasePlayer.getPubStats().getWins() > 125, null),
    GAMES_WON_250("Win 250 Games", "", databasePlayer -> databasePlayer.getPubStats().getWins() > 250, null),
    GAMES_WON_500("Win 500 Games", "", databasePlayer -> databasePlayer.getPubStats().getWins() > 500, null),
    GAMES_WON_CTF_10("Win 10 CTF Games", "", databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 10, null),
    GAMES_WON_CTF_25("Win 25 CTF Games", "", databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 25, null),
    GAMES_WON_CTF_50("Win 50 CTF Games", "", databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 50, null),
    GAMES_WON_CTF_75("Win 75 CTF Games", "", databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 75, null),
    GAMES_WON_CTF_100("Win 100 CTF Games", "", databasePlayer -> databasePlayer.getPubStats().getCtfStats().getWins() > 100, null),
    GAMES_WON_TDM_10("Win 10 TDM Games", "", databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 10, null),
    GAMES_WON_TDM_25("Win 25 TDM Games", "", databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 25, null),
    GAMES_WON_TDM_50("Win 50 TDM Games", "", databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 50, null),
    GAMES_WON_TDM_75("Win 75 TDM Games", "", databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 75, null),
    GAMES_WON_TDM_100("Win 100 TDM Games", "", databasePlayer -> databasePlayer.getPubStats().getTdmStats().getWins() > 100, null),

    REJUVENATION("Rejuvenation",
            "Heal your flag carrier from below 1k health to their maximum health capacity or above in 3 seconds.",
            null,
            warlordsPlayer -> {
                for (WarlordsPlayer player : warlordsPlayer.getGame().warlordsPlayers()
                        .filter(wp -> wp.getTeam() == warlordsPlayer.getTeam())
                        .filter(wp -> wp != warlordsPlayer)
                        .collect(Collectors.toList())
                ) {
                    List<PlayerStatisticsSecond.Entry> entries = player.getSecondStats().getEntries();
                    List<WarlordsDamageHealingFinalEvent> events = new ArrayList<>();
                    if (entries.size() > 3) {
                        events.addAll(entries.get(entries.size() - 4).getEvents());
                    }
                    if (entries.size() > 2) {
                        events.addAll(entries.get(entries.size() - 3).getEvents());
                    }
                    if (entries.size() > 1) {
                        events.addAll(entries.get(entries.size() - 2).getEvents());
                    }
                    if (!entries.isEmpty()) {
                        events.addAll(entries.get(entries.size() - 1).getEvents());
                    }
                    int below1000Index = -1;
                    int fullHealthIndex = -1;
                    for (int j = 0; j < events.size(); j++) {
                        if (events.get(j).getFinalHealth() <= 1000) {
                            below1000Index = j + 1;
                            break;
                        }
                    }
                    if (below1000Index != -1) {
                        for (int j = below1000Index; j < events.size(); j++) {
                            if (events.get(j).getFinalHealth() >= player.getMaxHealth()) {
                                fullHealthIndex = j;
                                break;
                            }
                        }
                    } else {
                        return false;
                    }
                    if (fullHealthIndex != -1) {
                        List<WarlordsDamageHealingFinalEvent> healingEvents = new ArrayList<>();
                        for (int j = below1000Index; j < fullHealthIndex; j++) {
                            healingEvents.add(events.get(j));
                        }
                        if ((float) healingEvents.stream()
                                .filter(WarlordsDamageHealingFinalEvent::isHealingInstance)
                                .filter(warlordsDamageHealingFinalEvent -> warlordsDamageHealingFinalEvent.getAttacker() == warlordsPlayer)
                                .map(WarlordsDamageHealingFinalEvent::getValue)
                                .mapToDouble(Float::doubleValue)
                                .sum() >= 2000
                        ) {
                            return true;
                        }
                    } else {
                        return false;
                    }
                }
                return false;
            }),


    ;


    public String name;
    public String description;
    public Predicate<DatabasePlayer> databasePlayerPredicate;
    public Predicate<WarlordsPlayer> warlordsPlayerPredicate;

    Achievements(String name, String description, Predicate<DatabasePlayer> databasePlayerPredicate, Predicate<WarlordsPlayer> warlordsPlayerPredicate) {
        this.name = name;
        this.description = description;
        this.databasePlayerPredicate = databasePlayerPredicate;
        this.warlordsPlayerPredicate = warlordsPlayerPredicate;
    }

    public static void sendAchievementUnlockMessage(Achievements achievements, Player player) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  Achievement Unlocked: " + ChatColor.GOLD + achievements.name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + achievements.description).create()));
        ChatUtils.sendMessageToPlayer(player, Collections.singletonList(message), ChatColor.GREEN, true);
    }

    public void giveAchievements(DatabasePlayer databasePlayer) {
        if (databasePlayerPredicate.test(databasePlayer)) {

        }
    }
}
