package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilties.internal.DamageCheck;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.ParticleEffect;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.wavedefense.WaveDefenseOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.mobtypes.BossMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class SoulOfGradient extends AbstractZombie implements BossMob {

    public SoulOfGradient(Location spawnLocation) {
        super(spawnLocation,
                "Soul of Gradient",
                MobTier.BOSS,
                new Utils.SimpleEntityEquipment(
                        SkullUtils.getSkullFrom(SkullID.GRADIENT_SOUL),
                        Utils.applyColorTo(Material.LEATHER_CHESTPLATE, 30, 30, 30),
                        Utils.applyColorTo(Material.LEATHER_LEGGINGS, 30, 30, 30),
                        Utils.applyColorTo(Material.LEATHER_BOOTS, 30, 30, 30),
                        Weapons.TENDERIZER.getItem()
                ),
                10000,
                0.15f,
                0,
                2000,
                2500
        );
    }

    @Override
    public void onSpawn(WaveDefenseOption option) {
        FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                .withColor(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .build());
    }

    @Override
    public void whileAlive(int ticksElapsed, WaveDefenseOption option) {
        if (ticksElapsed % 400 == 0) {
            warlordsNPC.getMob().removeTarget();
        }

        if (ticksElapsed % 10 == 0) {
            new CircleEffect(
                    warlordsNPC.getGame(),
                    warlordsNPC.getTeam(),
                    warlordsNPC.getLocation().add(0, 0.25, 0),
                    6,
                    new CircumferenceEffect(ParticleEffect.SPELL_WITCH, ParticleEffect.FIREWORKS_SPARK).particlesPerCircumference(1),
                    new DoubleLineEffect(ParticleEffect.SPELL)
            ).playEffects();

            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(warlordsNPC, 6, 6, 6)
                    .aliveEnemiesOf(warlordsNPC)
            ) {
                if (we.getCooldownManager().hasCooldown(DamageCheck.class)) {
                    we.addDamageInstance(
                            warlordsNPC,
                            "Tormenting Mark",
                            1000,
                            1000,
                            -1,
                            100,
                            true
                    );
                }
            }
        }
    }

    @Override
    public void onAttack(WarlordsEntity attacker, WarlordsEntity receiver, WarlordsDamageHealingEvent event) {

    }

    @Override
    public void onDamageTaken(WarlordsEntity self, WarlordsEntity attacker, WarlordsDamageHealingEvent event) {

    }
}
