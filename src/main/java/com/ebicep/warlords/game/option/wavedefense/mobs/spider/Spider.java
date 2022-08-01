package com.ebicep.warlords.game.option.wavedefense.mobs.spider;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BasicMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;

public class Spider extends AbstractSpider implements BasicMob {

    public Spider(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Venari",
                null,
                2200,
                0.45f,
                0,
                300,
                450
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

    @Override
    public void onDeath(Location deathLocation, WaveDefenseOption waveDefenseOption) {

    }
}
