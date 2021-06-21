package com.ebicep.warlords.player;

public class Cooldown {

    private Class cooldownClass;
    private String name;
    private float timeLeft;
    private WarlordsPlayer from;
    private CooldownTypes cooldownType;

    public Cooldown(Class ability, String name, float timeLeft, WarlordsPlayer from, CooldownTypes cooldownType) {
        this.cooldownClass = ability;
        this.name = name;
        this.timeLeft = timeLeft;
        this.from = from;
        this.cooldownType = cooldownType;
    }

    public Class getCooldownClass() {
        return cooldownClass;
    }

    public String getName() {
        return name;
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public void subtractTime(float amount) {
        if (this.timeLeft - amount <= 0) {
            timeLeft = 0;
        } else {
            this.timeLeft -= amount;
        }
    }

    public WarlordsPlayer getFrom() {
        return from;
    }

    public CooldownTypes getCooldownType() {
        return cooldownType;
    }

}
