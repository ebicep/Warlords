package com.ebicep.warlords.util;

import org.bukkit.Bukkit;

public class CalculateSpeed {
    private int Duration;
    private float Speed;
    private int DurationStatic;
    private float currentSpeed;
    private float speed = .35f;
    private float slowness = .184f;
    private float savedSpeed;
    private boolean speedCap;
    private boolean slownessCap;
    private float defaultspeed;

    public CalculateSpeed(float defaultSpeed) {
        this.currentSpeed = defaultSpeed;
        this.defaultspeed = defaultSpeed;
    }

    public float getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(int Duration, float Speed, int DurationStatic) {
        this.Duration = Duration;
        this.Speed = Speed;
        this.DurationStatic = DurationStatic * 20 - 10;

        // TODO: wip, caps still need fixing but base works
        // Add the speed
        if (this.Duration >= this.DurationStatic) {

            // Save current speed for later use
            this.savedSpeed = this.currentSpeed;

            // Add speed to counter
            this.currentSpeed += Speed;

            // If current speed is higher than the speedcap set speed to cap
            if (this.currentSpeed > this.speed) {
                this.currentSpeed = this.speed;
                speedCap = true;

            // If current speed is lower than the slownesscap set speed to that cap
            } else if (this.currentSpeed < this.slowness) {
                this.currentSpeed = this.slowness;
                slownessCap = true;

            // If speed is fine, dont do anything
            } else {
                speedCap = false;
                slownessCap = false;
            }

        // Dont change speed
        } else if (this.Duration > 1) {
            this.currentSpeed += 0;

        // Remove the speed
        } else if (this.Duration == 1) {

            // If speed hit the cap, reset to value before the cap was set
            if (speedCap) {
                if (this.savedSpeed == this.speed) {
                    this.currentSpeed = this.savedSpeed - (float) 0.025;
                } else {
                    this.currentSpeed = this.savedSpeed;
                }
                speedCap = false;

            // If slowness hit the cap, reset to value before the cap was set
            } else if (slownessCap) {
                if (this.savedSpeed == this.slowness) {
                    this.currentSpeed = this.savedSpeed;
                } else {
                    this.currentSpeed = this.savedSpeed - (float) 0.02;
                }
                slownessCap = false;

            // If speed was fine, just remove it
            } else {
                this.currentSpeed -= Speed;
            }
        }
    }
}