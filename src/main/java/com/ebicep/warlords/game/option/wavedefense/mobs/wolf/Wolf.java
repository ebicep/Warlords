package com.ebicep.warlords.game.option.wavedefense.mobs.wolf;

import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;

public class Wolf extends AbstractWolf implements BasicMob {

    public Wolf(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Hound",
                MobTier.BASE,
                null,
                900,
                0.5f,
                0,
                600,
                800
        );
    }

    @Override
    public void onSpawn() {

    }

    @Override
    public void whileAlive() {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver) {

    }

}
