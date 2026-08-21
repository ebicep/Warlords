package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.warlords.pve.newitems.tiers.NewItemTier;
import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class MasterworksFairEntry {

    private Instant time;
    @Field("rarity")
    private WeaponsPvE rarity;
    @Field("item_submission")
    private boolean itemSubmission;
    @Field("item_tier")
    private NewItemTier itemTier;
    private int placement;
    private float score;
    @Field("fair_number")
    private Integer fairNumber;

    public MasterworksFairEntry() {
    }

    public MasterworksFairEntry(Instant time, WeaponsPvE rarity, int placement, float score, Integer fairNumber) {
        this(time, rarity, false, null, placement, score, fairNumber);
    }

    public MasterworksFairEntry(Instant time, NewItemTier itemTier, int placement, float score, Integer fairNumber) {
        this(time, null, true, itemTier, placement, score, fairNumber);
    }

    private MasterworksFairEntry(
            Instant time,
            WeaponsPvE rarity,
            boolean itemSubmission,
            NewItemTier itemTier,
            int placement,
            float score,
            Integer fairNumber
    ) {
        this.rarity = rarity;
        this.itemSubmission = itemSubmission;
        this.itemTier = itemTier;
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
                ", itemTier=" + itemTier +
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

    public NewItemTier getItemTier() {
        return itemTier;
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
