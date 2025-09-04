package com.ebicep.warlords.game.option.pve.treasurehunt.artifacts;


import com.ebicep.warlords.game.option.pve.treasurehunt.TreasureHuntOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import libsdisg.shaded.net.kyori.adventure.text.Component;

public interface Artifact {

    String id();

    Component name();

    String description();
}