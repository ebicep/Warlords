package com.ebicep.warlords.pve.newitems.attributes;

import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public interface Attribute {

    String getDatabaseName();

    TextColor getTextColor();

    Component formatValue(float value);

    void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, float value);

}
