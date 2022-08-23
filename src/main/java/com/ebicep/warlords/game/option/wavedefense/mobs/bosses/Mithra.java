package com.ebicep.warlords.game.option.wavedefense.mobs.bosses;

import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
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

public class Mithra extends AbstractZombie implements BossMob {

    public Mithra(Location spawnLocation) {
        super(spawnLocation,
                "Mithra",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.IRON_QUEEN),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 200, 200, 200),
                        Weapons.SILVER_PHANTASM_SWORD_3.getItem()
                ),
                18000,
                0.2f,
                20,
                1000,
                1300
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
