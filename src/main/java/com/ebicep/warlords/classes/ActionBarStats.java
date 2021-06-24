package com.ebicep.warlords.classes;

import com.ebicep.warlords.WarlordsPlayer;

public class ActionBarStats {

    private final WarlordsPlayer warlordsPlayer;
    private String name;
    private int timeLeft;

    public ActionBarStats(WarlordsPlayer warlordsPlayer, String name, int timeLeft) {
        this.warlordsPlayer = warlordsPlayer;
        this.name = name;
        this.timeLeft = timeLeft;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public boolean subtractTime() {
        timeLeft--;
        return timeLeft == 0;
    }
}
