package com.ebicep.warlords.player;

public class Cooldown {

    private String name;
    private final Class cooldownClass;
    private final Object cooldownObject;
    private String actionBarName;
    private float timeLeft;
    private final WarlordsPlayer from;
    private final CooldownTypes cooldownType;
    private boolean hidden;

    public Cooldown(String name, Class ability, Object cooldownObject, String actionBarName, float timeLeft, WarlordsPlayer from, CooldownTypes cooldownType) {
        this.name = name;
        this.cooldownClass = ability;
        this.cooldownObject = cooldownObject;
        this.actionBarName = actionBarName;
        this.timeLeft = timeLeft;
        this.from = from;
        this.cooldownType = cooldownType;
        this.hidden = false;
    }

    @Override
    public String toString() {
        return "Cooldown{" +
                "name='" + name + '\'' +
                ", cooldownClass=" + cooldownClass +
                ", cooldownObject=" + cooldownObject +
                ", actionBarName='" + actionBarName + '\'' +
                ", timeLeft=" + timeLeft +
                ", from=" + from +
                ", cooldownType=" + cooldownType +
                ", hidden=" + hidden +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getCooldownClass() {
        return cooldownClass;
    }

    public Object getCooldownObject() {
        return cooldownObject;
    }

    public String getActionBarName() {
        return actionBarName;
    }

    public void setActionBarName(String actionBarName) {
        this.actionBarName = actionBarName;
    }

    public float getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(float timeLeft) {
        this.timeLeft = timeLeft;
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
