package com.ebicep.warlords.achievements;

import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.entity.Player;

import java.time.Instant;

public interface Achievement {

    String getName();

    String getDescription();

    GameMode getGameMode();

    void sendAchievementUnlockMessage(Player player);

    void sendAchievementUnlockMessageToOthers(WarlordsEntity warlordsPlayer);

    abstract class AbstractAchievementRecord<T extends Enum<T>> {

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

        public abstract String getName();

        public abstract String getDescription();

        public abstract GameMode getGameMode();

        public abstract T[] getAchievements();

        public T getAchievement() {
            return achievement;
        }

        public Instant getDate() {
            return date;
        }

    }
}
