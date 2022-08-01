package com.ebicep.warlords.game.option.wavedefense2.waves2;

import java.util.Random;

public interface WaveList {

    Wave getWave(int wave, Random random);

//    default WaveList prependMapper(BiFunction<Wave, Integer, Wave> mapper) {
//        return (wave, random) -> mapper.apply(this.getWave(wave, random), wave);
//    }
//
//    default WaveList prependEntityMapper(BiFunction<WarlordsEntity, Integer, WarlordsEntity> mapper) {
//        return prependMapper((w, counter) -> new DelegatingWave(w) {
//            @Override
//            public AbstractMob<?> spawnRandomMonster(Location loc) {
//                return super.spawnRandomMonster(loc).prependOperation(e -> mapper.apply(e, counter));
//            }
//
//        });
//    }

}
