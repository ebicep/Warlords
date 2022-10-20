package com.ebicep.warlords.achievements;

import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.general.Specializations;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;

import java.time.Instant;

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
        EASY,
        MEDIUM,
        HARD,
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
