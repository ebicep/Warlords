package com.ebicep.warlords.game.option.wavedefense.mobs.magmacube;

import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;

public class BabyMagmaCube extends AbstractMagmaCube {

    public BabyMagmaCube(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Illuminati",
                null,
                2500,
                0.35f,
                0,
                50,
                100
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
    public int dropRate() {
        return 0;
    }

    @Override
    public int commonDropChance() {
        return 0;
    }

    @Override
    public int rareDropChance() {
        return 0;
    }

    @Override
    public int epicDropChance() {
        return 0;
    }
}
