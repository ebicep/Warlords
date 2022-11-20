package com.ebicep.warlords.game.option.wavedefense.mobs.skeleton;

import com.ebicep.warlords.abilties.FlameBurst;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.game.option.wavedefense.mobs.MobTier;
import com.ebicep.warlords.game.option.wavedefense.mobs.mobtypes.EliteMob;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

public class VoidSkeleton extends AbstractSkeleton implements EliteMob {

    private int voidRadius = 7;

    public VoidSkeleton(Location spawnLocation) {
        super(
                spawnLocation,
                "Void Mesmer",
                MobTier.ELITE,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.DEMON_SKELETON),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 0, 0, 0),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 0, 0, 0),
                        Weapons.ARMBLADE.getItem()
                ),
                5500,
                0.05f,
                10,
                0,
                0
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        EffectUtils.strikeLightning(warlordsNPC.getLocation(), true);

        FlameBurst flameBurst = new FlameBurst();
        flameBurst.setCritChance(-1);
        warlordsNPC.getSpec().setRed(flameBurst);
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 60 == 0) {
            warlordsNPC.getRedAbility().onActivate(warlordsNPC, null);
        }

        if (ticksElapsed % 100 == 0) {
            EffectUtils.playHelixAnimation(warlordsNPC.getLocation(), voidRadius, ParticleEffect.SMOKE_NORMAL, 1, 30);
            for (WarlordsEntity wp : PlayerFilter
                    .entitiesAround(warlordsNPC, voidRadius, voidRadius, voidRadius)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                wp.addDamageInstance(warlordsNPC, "Void Shred", 450, 900, 0, 100, true);
                wp.addSpeedModifier(warlordsNPC, "Void Slowness", -30, 10, "BASE");
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {
    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDeath(WarlordsEntity killer, Location deathLocation, WaveDefenseOption option) {
        super.onDeath(killer, deathLocation, option);
        FireWorkEffectPlayer.playFirework(deathLocation, FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BURST)
                .withTrail()
                .build());
        Utils.playGlobalSound(deathLocation, Sound.SKELETON_DEATH, 2, 0.2f);
    }
}
