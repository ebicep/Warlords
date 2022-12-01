package com.ebicep.warlords.game.option.wavedefense.mobs.slime;

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
        if (ticksElapsed % 10 == 0) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 7, 7, 7)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                we.subtractEnergy(10, false);
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        attacker.getSpec().increaseAllCooldownTimersBy(0.2f);
    }
}
