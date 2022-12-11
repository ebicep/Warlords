package com.ebicep.warlords.game.option.wavedefense.mobs.slime;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;

public class VoidSlime extends AbstractSlime implements EliteMob {
    public VoidSlime(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Chess",
                MobTier.BASE,
                null,
                10000,
                0.1f,
                30,
                0,
                0
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        this.entity.get().setSize(10);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 20 == 0) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 10, 10, 10)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.subtractEnergy(10, true);
            }

            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 100, 100, 100)
                    .aliveEnemiesOf(warlordsNPC)
                    .closestFirst(warlordsNPC)
                    .limit(1)
            ) {
                EffectUtils.playParticleLinkAnimation(warlordsNPC.getLocation(), we.getLocation(), ParticleEffect.DRIP_LAVA);
                we.subtractEnergy(10, true);
                we.getSpeed().addSpeedModifier(warlordsNPC, "Blob Slowness", -20, 10);
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        attacker.getSpec().increaseAllCooldownTimersBy(1);
    }
}
