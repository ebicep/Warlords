package com.ebicep.warlords.game.option.pve.wavedefense.waves;

import com.ebicep.warlords.player.ingame.WarlordsNPC;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import org.bukkit.Location;

import java.util.Random;
import java.util.function.BiFunction;

public interface WaveList {

    Wave getWave(int wave, Random random);

    // TODO Move to AbstractMob, will do later
    default WaveList prependMapper(BiFunction<Wave, Integer, Wave> mapper) {
        return (wave, random) -> mapper.apply(this.getWave(wave, random), wave);
    }

    default WaveList prependEntityMapper(BiFunction<WarlordsNPC, Integer, WarlordsNPC> mapper) {
        return prependMapper((w, counter) -> new DelegatingWave(w) {
            @Override
            public AbstractMob<?> spawnRandomMonster(Location loc) {
                return super.spawnRandomMonster(loc).prependOperation(e -> mapper.apply(e, counter));
            }

        });
    }

}
