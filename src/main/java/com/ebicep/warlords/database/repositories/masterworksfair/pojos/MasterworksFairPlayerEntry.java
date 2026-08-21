package com.ebicep.warlords.database.repositories.masterworksfair.pojos;

import com.ebicep.warlords.pve.newitems.NewItem;
import com.ebicep.warlords.pve.weapons.AbstractWeapon;
import org.springframework.data.annotation.Id;

import java.util.UUID;

public class MasterworksFairPlayerEntry {

    @Id
    protected String id;
    private UUID uuid;
    private AbstractWeapon weapon;
    private NewItem item;

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
        this.item = null;
    }

    public NewItem getItem() {
        return item;
    }

    public void setItem(NewItem item) {
        this.item = item;
        this.weapon = null;
    }
}
