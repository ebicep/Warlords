package com.ebicep.warlords.achievements.types;

import com.ebicep.warlords.achievements.Achievement;
import com.ebicep.warlords.events.WarlordsDamageHealingFinalEvent;
import com.ebicep.warlords.game.GameMode;
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

public enum ChallengeAchievements implements Achievement {

    REJUVENATION("Rejuvenation",
            "Heal your flag carrier from below 1k health to their maximum health capacity or above in 3 seconds.",
            GameMode.CAPTURE_THE_FLAG,
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
    public GameMode gameMode;
    public Predicate<WarlordsPlayer> warlordsPlayerPredicate;

    ChallengeAchievements(String name, String description, GameMode gameMode, Predicate<WarlordsPlayer> warlordsPlayerPredicate) {
        this.name = name;
        this.description = description;
        this.gameMode = gameMode;
        this.warlordsPlayerPredicate = warlordsPlayerPredicate;
    }


    @Override
    public void sendAchievementUnlockMessage(Player player) {
        TextComponent message = new TextComponent(ChatColor.GREEN + ">>  Achievement Unlocked: " + ChatColor.GOLD + name + ChatColor.GREEN + "  <<");
        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(ChatColor.GREEN + description).create()));
        ChatUtils.sendMessageToPlayer(player, Collections.singletonList(message), ChatColor.GREEN, true);
    }

    @Override
    public void sendAchievementUnlockMessageToOthers(WarlordsPlayer warlordsPlayer) {

    }

    public static class ChallengeAchievementRecord extends AbstractAchievementRecord {

        private ChallengeAchievements achievement;

        public ChallengeAchievementRecord(ChallengeAchievements achievement) {
            super();
            this.achievement = achievement;
        }

        public ChallengeAchievements getAchievement() {
            return achievement;
        }

    }
}
