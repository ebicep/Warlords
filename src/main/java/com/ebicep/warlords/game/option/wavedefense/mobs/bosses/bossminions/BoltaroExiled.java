package com.ebicep.warlords.game.option.wavedefense.mobs.bosses.bossminions;

import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.game.option.wavedefense.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public class BoltaroExiled extends AbstractZombie implements EliteMob {

    public BoltaroExiled(Location spawnLocation) {
        super(spawnLocation,
                "Exiled Apostate",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.RED_EYE),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 0, 15),
                        Weapons.GEMINI.getItem()
                ),
                3000,
                0.3f,
                0,
                100,
                200
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
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        Utils.playGlobalSound(deathLocation, Sound.ENDERMAN_DEATH, 2, 1.3f);
    }
}
