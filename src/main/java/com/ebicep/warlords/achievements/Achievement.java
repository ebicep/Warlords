package com.ebicep.warlords.achievements;

import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.util.function.Function;

public interface Achievement {

    String getName();

    String getDescription();

    GameMode getGameMode();

    Specializations getSpec();

    boolean isHidden();

    Difficulty getDifficulty();

    void sendAchievementUnlockMessage(Player player);

    void sendAchievementUnlockMessageToOthers(WarlordsEntity warlordsPlayer);

    enum Difficulty {
        EASY("Easy",
                ChatColor.GREEN,
                integer -> (int) Math.round(Math.pow(integer, 1 / 4.0))
        ),
        MEDIUM("Medium",
                ChatColor.GOLD,
                integer -> (int) Math.round(Math.pow(integer, 1 / 3.5))
        ),
        HARD("Hard",
                ChatColor.RED,
                integer -> (int) Math.round(Math.pow(integer, 1 / 3))
        ),

        ;

        public static final Difficulty[] VALUES = values();
        public final String name;
        public final ChatColor chatColor;
        public final Function<Integer, Integer> weightFunction;

        Difficulty(String name, ChatColor chatColor, Function<Integer, Integer> weightFunction) {
            this.name = name;
            this.chatColor = chatColor;
            this.weightFunction = weightFunction;
        }

        public String getColoredName() {
            return chatColor + name;
        }
    }

    abstract class AbstractAchievementRecord<T extends Enum<T> & Achievement> {

        private T achievement;
        private Instant date;

        public AbstractAchievementRecord() {
        }

        public AbstractAchievementRecord(T achievement) {
            this.achievement = achievement;
            this.date = Instant.now();
        }

        public AbstractAchievementRecord(T achievement, Instant date) {
            this.achievement = achievement;
            this.date = date;
        }

        public T getAchievement() {
            return achievement;
        }

        public Instant getDate() {
            return date;
        }

    }
}
