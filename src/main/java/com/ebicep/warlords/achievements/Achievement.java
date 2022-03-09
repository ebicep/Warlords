package com.ebicep.warlords.achievements;

import com.ebicep.warlords.player.WarlordsPlayer;
import org.bukkit.entity.Player;

import java.util.Date;

public interface Achievement {

    void sendAchievementUnlockMessage(Player player);

    void sendAchievementUnlockMessageToOthers(WarlordsPlayer warlordsPlayer);

    abstract class AbstractAchievementRecord {

        private Date date;

        public AbstractAchievementRecord() {
            this.date = new Date();
        }

        public Date getDate() {
            return date;
        }

    }
}
