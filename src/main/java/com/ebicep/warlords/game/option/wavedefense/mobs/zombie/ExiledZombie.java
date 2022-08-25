package com.ebicep.warlords.game.option.wavedefense.mobs.zombie;

import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class ExiledZombie extends AbstractZombie implements EliteMob {

    public ExiledZombie(Location spawnLocation) {
        super(
                spawnLocation,
                "Exiled Scrupulous",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.FACELESS_BANDIT),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
                        new ItemStack(Material.GOLDEN_CARROT)
                ),
                16000,
                0.42f,
                10,
                2200,
                2800
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), false, 2);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 30 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation(),
                    8,
                    new CircumferenceEffect(ParticleEffect.FIREWORKS_SPARK, ParticleEffect.FIREWORKS_SPARK).particlesPerCircumference(0.75),
                    new DoubleLineEffect(ParticleEffect.SPELL)
            ).playEffects();
        }

        if (ticksElapsed % 200 == 0) {
            for (int i = 0; i < 20; i++) {
                EffectUtils.strikeLightningInCylinder(warlordsNPC.getLocation(), i * 2, false, i + 1, warlordsNPC.getGame());
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
        Utils.playGlobalSound(receiver.getLocation(), Sound.AMBIENCE_THUNDER, 2, 0.7f);
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
