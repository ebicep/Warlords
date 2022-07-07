package com.ebicep.warlords.pve.weapons;

import org.bukkit.ChatColor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Collections;
import java.util.List;

public abstract class AbstractBetterWeapon extends AbstractWeapon {

    @Field("speed_bonus")
    protected int speedBonus;

    public AbstractBetterWeapon() {
        super();
    }

    @Override
    public List<String> getLore() {
        return Collections.singletonList(ChatColor.GRAY + "Speed: " + ChatColor.GREEN + "+" + speedBonus + "%");
    }

}
