package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class MasterworksFairEntry {

    private Instant time;
    @Field("rarity")
    private WeaponsPvE rarity;
    @Field("item_submission")
    private boolean itemSubmission;
    private int placement;
    private float score;
    @Field("fair_number")
    private Integer fairNumber;

    public MasterworksFairEntry() {
    }

    public MasterworksFairEntry(Instant time, WeaponsPvE rarity, int placement, float score, Integer fairNumber) {
        this(time, rarity, false, placement, score, fairNumber);
    }

    public MasterworksFairEntry(Instant time, WeaponsPvE rarity, boolean itemSubmission, int placement, float score, Integer fairNumber) {
        this.rarity = rarity;
        this.itemSubmission = itemSubmission;
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
                ", itemSubmission=" + itemSubmission +
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

    public boolean isItemSubmission() {
        return itemSubmission;
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
