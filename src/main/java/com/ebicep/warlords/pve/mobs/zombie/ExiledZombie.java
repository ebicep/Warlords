package com.ebicep.warlords.pve.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

public class ExiledZombie extends AbstractZombie implements EliteMob {

    public ExiledZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Scrupulous",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.SCULK_CORRUPTION),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 10, 50, 130),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 10, 50, 130),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 10, 50, 130),
                        Weapons.AMARANTH.getItem()
                ),
                10000,
                0.28f,
                0,
                1600,
                2000
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 2);
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

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.ZOMBIE_DEATH, 2, 0.4f);
    }
}
