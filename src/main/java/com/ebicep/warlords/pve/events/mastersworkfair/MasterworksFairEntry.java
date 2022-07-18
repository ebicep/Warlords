package com.ebicep.warlords.pve.events.mastersworkfair;

import com.ebicep.warlords.pve.weapons.WeaponsPvE;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class MasterworksFairEntry {

    @Field("rarity")
    private WeaponsPvE rarity;
    private int placement;
    private Instant time;

    public MasterworksFairEntry(WeaponsPvE rarity, int placement, Instant time) {
        this.rarity = rarity;
        this.placement = placement;
        this.time = time;
    }
}
