package com.ebicep.warlords.game.option.wavedefense.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class VoidZombie extends AbstractZombie implements EliteMob {

    private int voidRadius = 5;

    public VoidZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Singularity",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
                        new ItemStack(Material.GOLDEN_CARROT)
                ),
                10000,
                0.1f,
                10,
                1500,
                2000
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 2);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 10 == 0) {
            EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), voidRadius, ParticleEffect.SMOKE_NORMAL, 1, 30);
            for (WarlordsEntity wp : PlayerFilter
                    .entitiesAround(warlordsNPC, voidRadius, voidRadius, voidRadius)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                wp.addDamageInstance(warlordsNPC, "Void Shred", 200, 300, -1, 100, true);
                wp.getSpeed().addSpeedModifier("Void Slowness", -70, 10);
            }
        }

        if (ticksElapsed % 5 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    voidRadius,
                    new CircumferenceEffect(ParticleEffect.FIREWORKS_SPARK, ParticleEffect.FIREWORKS_SPARK).particlesPerCircumference(0.75),
                    new DoubleLineEffect(ParticleEffect.SPELL)
            ).playEffects();
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.AMBIENCE_THUNDER, 2, 0.7f);
        receiver.getSpeed().addSpeedModifier("Envoy Slowness", -20, 2 * 20);
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
