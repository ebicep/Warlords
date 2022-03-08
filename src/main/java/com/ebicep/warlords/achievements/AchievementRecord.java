package com.ebicep.warlords.achievements;

import com.ebicep.warlords.achievements.Achievements;

import java.util.Date;

public class AchievementRecord {

    private Achievements achievement;
    private Date date;

    public AchievementRecord() {
    }

    public AchievementRecord(Achievements achievement) {
        this.achievement = achievement;
        this.date = new Date();
    }

    public AchievementRecord(Achievements achievement, Date date) {
        this.achievement = achievement;
        this.date = date;
    }

    public Achievements getAchievement() {
        return achievement;
    }

    public Date getDate() {
        return date;
    }
}
