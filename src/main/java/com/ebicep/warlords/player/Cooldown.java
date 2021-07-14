package com.ebicep.warlords.player;

public class Cooldown {

    private Class cooldownClass;
    private Object cooldownObject;
    private String name;
    private float timeLeft;
    private WarlordsPlayer from;
    private CooldownTypes cooldownType;
    private boolean hidden;

    public Cooldown(Class ability, Object cooldownObject, String name, float timeLeft, WarlordsPlayer from, CooldownTypes cooldownType) {
        this.cooldownClass = ability;
        this.cooldownObject = cooldownObject;
        this.name = name;
        this.timeLeft = timeLeft;
        this.from = from;
        this.cooldownType = cooldownType;
        this.hidden = false;
    }

    public Class getCooldownClass() {
        return cooldownClass;
    }

    public Object getCooldownObject() {
        return cooldownObject;
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

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }
}
