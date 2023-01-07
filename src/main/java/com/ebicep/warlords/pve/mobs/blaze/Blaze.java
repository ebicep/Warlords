package com.ebicep.warlords.pve.mobs.blaze;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FallingBlockWaveEffect;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

public class Blaze extends AbstractBlaze implements EliteMob {

    private final double kindleRadius = 6;

    public Blaze(Location spawnLocation) {
        super(
                spawnLocation,
                "Illusion Kindle",
                MobTier.ELITE,
                null,
                4000,
                0,
                10,
                100,
                200
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), kindleRadius, ParticleEffect.FLAME, 1, 20);
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.FIREWORK_LARGE_BLAST, 2, 0.5f);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 160 == 0) {
            Location loc = warlordsNPC.getLocation();
            EffectUtils.playSphereAnimation(loc, kindleRadius, ParticleEffect.FLAME, 1);
            Utils.playGlobalSound(loc, "mage.inferno.activation", 2, 0.2f);
            new FallingBlockWaveEffect(
                    loc,
                    kindleRadius,
                    1.2,
                    Material.FIRE,
                    (byte) 0
            ).play();

            for (WarlordsEntity target : PlayerFilter
                    .entitiesAround(warlordsNPC, kindleRadius, kindleRadius, kindleRadius)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                target.addDamageInstance(
                        warlordsNPC,
                        "Kindle Wave",
                        518,
                        805,
                        -1,
                        100,
                        false
                );
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(warlordsNPC.getLocation(), Sound.FIRE_IGNITE, 2, 0.2f);
        EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), kindleRadius, ParticleEffect.FLAME, 1, 10);
    }
}
