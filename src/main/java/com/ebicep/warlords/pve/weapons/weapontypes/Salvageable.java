package com.ebicep.warlords.pve.weapons.weapontypes;

import com.ebicep.warlords.util.java.Utils;
import org.bukkit.ChatColor;

public interface Salvageable {

    int getMinSalvageAmount();

    int getMaxSalvageAmount();

    default int getSalvageAmount() {
        return Utils.generateRandomValueBetweenInclusive(getMinSalvageAmount(), getMaxSalvageAmount());
    }

    default String getSalvageRewardMessage() {
        return ChatColor.AQUA.toString() + getMinSalvageAmount() + "-" + getMaxSalvageAmount() + " Synthetic Shards" + ChatColor.GRAY + ".";
    }

}
