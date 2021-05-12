package com.ebicep.warlords.util;

import org.bukkit.Bukkit;

public class CalculateSpeed {
    private int nameDuration;
    private float nameSpeed;
    private int duration;
    private float currentSpeed;

    public CalculateSpeed(float defaultSpeed) {
        this.currentSpeed = defaultSpeed;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int nameDuration, float nameSpeed, int duration) {
        this.nameDuration = nameDuration;
        this.nameSpeed = nameSpeed;
        this.duration = duration * 20 - 10;

        // TODO: set max and min speed cap
        if (this.nameDuration >= this.duration) {
            this.currentSpeed += nameSpeed;
        }
        // TODO fix first statement is always true
        else if (this.nameDuration <= this.duration && this.nameDuration > 1) {
            this.currentSpeed += 0;
        } else if (this.nameDuration == 1) {
            this.currentSpeed -= nameSpeed;
        }
    }
}