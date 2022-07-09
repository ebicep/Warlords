package com.ebicep.warlords.pve.weapons;

import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AbstractBetterWeapon extends AbstractWeapon {

    @Field("speed_bonus")
    protected int speedBonus;

    public AbstractBetterWeapon() {
    }

    public AbstractBetterWeapon(UUID uuid) {
        super(uuid);
    }

    @Override
    public List<String> getLore() {
        return Collections.singletonList(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + speedBonus + "%");
    }
}
