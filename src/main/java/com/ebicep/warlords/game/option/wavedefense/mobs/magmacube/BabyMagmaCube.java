package com.ebicep.warlords.game.option.wavedefense.mobs.magmacube;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import org.bukkit.Location;

public class BabyMagmaCube extends AbstractMagmaCube {

    public BabyMagmaCube(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Illuminati",
                MobTier.ELITE,
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
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, String ability) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity mob, WarlordsEntity attacker) {

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
