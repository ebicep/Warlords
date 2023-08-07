package com.ebicep.warlords.pve.mobs.magmacube;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import org.bukkit.Location;

public class BabyMagmaCube extends AbstractMagmaCube implements EliteMob {

    public BabyMagmaCube(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Illuminati",
                MobTier.ILLUSION,
                null,
                3200,
                0.35f,
                0,
                50,
                100
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);

    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {

    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
