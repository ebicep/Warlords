package com.ebicep.warlords.achievements;

import com.ebicep.warlords.game.GameMode;
import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import java.util.Date;

public interface Achievement {

    void sendAchievementUnlockMessage(Player player);

    void sendAchievementUnlockMessageToOthers(WarlordsPlayer warlordsPlayer);

    abstract class AbstractAchievementRecord<T extends Enum<T>> {

        private T achievement;
        private Date date;

        public AbstractAchievementRecord() {
        }

        public AbstractAchievementRecord(T achievement) {
            this.achievement = achievement;
            this.date = new Date();
        }

        public AbstractAchievementRecord(T achievement, Date date) {
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

        public Date getDate() {
            return date;
        }

    }
}
