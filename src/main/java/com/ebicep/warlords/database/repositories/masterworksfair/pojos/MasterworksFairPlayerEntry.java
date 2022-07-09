package com.ebicep.warlords.database.repositories.masterworksfair.pojos;

import com.ebicep.warlords.pve.weapons.AbstractWeapon;

public class MasterworksFairPlayerEntry {

    private String uuid;
    private AbstractWeapon weapon;

    public MasterworksFairPlayerEntry() {
    }

    public MasterworksFairPlayerEntry(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public AbstractWeapon getWeapon() {
        return weapon;
    }

    public void setWeapon(AbstractWeapon weapon) {
        this.weapon = weapon;
    }
}
