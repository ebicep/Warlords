package com.ebicep.warlords.pve.weapons.weaponaddons;

import com.ebicep.warlords.util.java.JavaUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public interface Salvageable {

    default int getSalvageAmount() {
        return JavaUtils.generateRandomValueBetweenInclusive(getMinSalvageAmount(), getMaxSalvageAmount());
    }

    int getMinSalvageAmount();

    int getMaxSalvageAmount();

    default Component getSalvageRewardMessage() {
        return Component.textOfChildren(
                Component.text(getMinSalvageAmount() + "-" + getMaxSalvageAmount() + " Synthetic Shards", NamedTextColor.WHITE),
                Component.text(".", NamedTextColor.GRAY)
        );
    }

}
