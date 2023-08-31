package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.events.player.ingame.WarlordsDamageHealingEvent;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.general.Weapons;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.pve.mobs.MobTier;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.pve.mobs.zombie.AbstractZombie;
import com.ebicep.warlords.util.pve.SkullID;
import com.ebicep.warlords.util.pve.SkullUtils;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import com.ebicep.warlords.util.warlords.Utils;
import org.bukkit.*;

import javax.annotation.Nonnull;

public class SoulOfGradient extends AbstractZombie implements BossMinionMob {

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
                25000,
                0.15f,
                0,
                2000,
                2500,
                new RemoveTarget(20),
                new TormentingMark()
        );
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                                                                   .withColor(Color.WHITE)
                                                                                   .with(FireworkEffect.Type.BALL_LARGE)
                                                                                   .build());
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

    private static class TormentingMark extends AbstractPveAbility {

        public TormentingMark() {
            super(
                    "Tormenting Mark",
                    1000,
                    1000,
                    .5f,
                    50,
                    0,
                    100
            );
        }

        @Override
        public boolean onPveActivate(@Nonnull WarlordsEntity wp, PveOption pveOption) {
            wp.subtractEnergy(energyCost, false);

            new CircleEffect(
                    wp.getGame(),
                    wp.getTeam(),
                    wp.getLocation().add(0, 0.25, 0),
                    6,
                    new CircumferenceEffect(Particle.SPELL_WITCH, Particle.FIREWORKS_SPARK).particlesPerCircumference(1),
                    new DoubleLineEffect(Particle.SPELL)
            ).playEffects();

            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, 6, 6, 6)
                    .aliveEnemiesOf(wp)
            ) {
                if (we.getCooldownManager().hasCooldown(DamageCheck.class)) {
                    we.addDamageInstance(
                            wp,
                            name,
                            minDamageHeal,
                            maxDamageHeal,
                            critChance,
                            critMultiplier
                    );
                }
            }
            return true;
        }
    }
}
