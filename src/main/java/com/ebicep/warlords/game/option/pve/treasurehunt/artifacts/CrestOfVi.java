package com.ebicep.warlords.game.option.pve.treasurehunt.artifacts;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.game.option.pve.treasurehunt.TreasureHuntOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;
import com.ebicep.warlords.pve.items.types.AppliesToWarlordsPlayer;
import libsdisg.shaded.net.kyori.adventure.text.Component;

public class CrestOfVi implements Artifact, AppliesToWarlordsPlayer {

    @Override
    public String id() {
        return "crest_of_vi";
    }

    @Override
    public Component name() {
        return Component.text("Crest of Vi");
    }

    @Override
    public void applyToWarlordsPlayer(WarlordsPlayer warlordsPlayer, PveOption pveOption) {

    }


    @Override
    public String description() {
        return "Increases ability damage by 10%, but abilities cost 5% more energy.";
    }
}
