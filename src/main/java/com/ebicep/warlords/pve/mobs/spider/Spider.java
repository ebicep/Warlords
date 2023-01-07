package com.ebicep.warlords.pve.mobs.spider;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BasicMob;
import org.bukkit.Location;

public class Spider extends AbstractSpider implements BasicMob {

    public Spider(Location spawnLocation) {
        super(
                spawnLocation,
                "Lunar Venari",
                MobTier.BASE,
                null,
                2200,
                0.45f,
                0,
                300,
                450
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {

    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

}
