package com.ebicep.warlords.pve.mobs.magmacube;

import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.IntermediateMob;
import org.bukkit.Location;

public class BabyMagmaCube extends AbstractMob implements IntermediateMob {

    public BabyMagmaCube(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Illuminati",
                3200,
                0.35f,
                0,
                50,
                100
        );
    }

    @Override
    public Mob getMobRegistry() {
        return null;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

}
