package com.ebicep.warlords.pve.weapons;

import com.ebicep.warlords.Warlords;
import com.ebicep.warlords.player.ingame.WarlordsPlayer;

public class LegendaryWeapon extends AbstractWeapon {

    protected int energyPerSecondBonus;
    protected int energyPerHitBonus;
    protected int speedBonus;

    public LegendaryWeapon(WarlordsPlayer warlordsPlayer) {
        this.skillBoost = Warlords.getPlayerSettings(warlordsPlayer.getUuid()).getSkillBoostForClass();
    }

    @Override
    public void generateStats() {

    }
}
