package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;

public interface Salvageable {

    int getMinSalvageAmount();

    int getMaxSalvageAmount();

    default int getSalvageAmount() {
        return Utils.generateRandomValueBetweenInclusive(getMinSalvageAmount(), getMaxSalvageAmount());
    }

    default String getSalvageRewardMessage() {
        return ChatColor.WHITE.toString() + getMinSalvageAmount() + "-" + getMaxSalvageAmount() + " Synthetic Shards" + ChatColor.GRAY + ".";
    }

}
