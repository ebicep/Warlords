package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class MasterworksFairEntry {

    private Instant time;
    @Field("rarity")
    private WeaponsPvE rarity;
    private int placement;
    private float score;
    @Field("fair_number")
    private Integer fairNumber;

    public MasterworksFairEntry(Instant time, WeaponsPvE rarity, int placement, float score, Integer fairNumber) {
        this.rarity = rarity;
        this.placement = placement;
        this.time = time;
        this.score = score;
        if (fairNumber == null) {
            fairNumber = 1;
        }
        this.fairNumber = fairNumber;
    }

    @Override
    public String toString() {
        return "MasterworksFairEntry{" +
                "rarity=" + rarity +
                ", placement=" + placement +
                ", score=" + score +
                ", fairNumber=" + fairNumber +
                '}';
    }

    public Instant getTime() {
        return time;
    }

    public WeaponsPvE getRarity() {
        return rarity;
    }

    public int getPlacement() {
        return placement;
    }

    public float getScore() {
        return score;
    }

    public int getFairNumber() {
        return fairNumber;
    }
}
