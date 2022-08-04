package com.ebicep.warlords.database.repositories.masterworksfair.pojos;

import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public class MasterworksFairPlayerEntry {

    @Id
    protected String id;
    private UUID uuid;
    private AbstractWeapon weapon;

    public MasterworksFairPlayerEntry() {
    }

    public MasterworksFairPlayerEntry(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public void setWeapon(AbstractWeapon weapon) {
        this.weapon = weapon;
    }
}
