package com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions;

import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.BossMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;

public class NarmerAcolyte extends AbstractZombie implements BossMob {

    public NarmerAcolyte(Location spawnLocation) {
        super(spawnLocation,
                "Acolyte of Narmer",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.RED_EYE),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Weapons.DEMONBLADE.getItem()
                ),
                5600,
                0.35f,
                15,
                540,
                765
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {

    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 400 == 0) {
            warlordsNPC.getMob().removeTarget();
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
