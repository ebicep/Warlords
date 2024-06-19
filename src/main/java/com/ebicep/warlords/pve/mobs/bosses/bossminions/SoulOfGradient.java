package com.ebicep.warlords.pve.mobs.bosses.bossminions;

import com.ebicep.warlords.abilities.internal.DamageCheck;
import com.ebicep.warlords.abilities.internal.Damages;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.FireWorkEffectPlayer;
import com.ebicep.warlords.effects.circle.CircleEffect;
import com.ebicep.warlords.effects.circle.CircumferenceEffect;
import com.ebicep.warlords.effects.circle.DoubleLineEffect;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.abilities.AbstractPveAbility;
import com.ebicep.warlords.pve.mobs.abilities.RemoveTarget;
import com.ebicep.warlords.pve.mobs.tiers.BossMinionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class SoulOfGradient extends AbstractMob implements BossMinionMob {

    public SoulOfGradient(Location spawnLocation) {
        super(spawnLocation,
                "Soul of Gradient",
                25000,
                0.15f,
                0,
                2000,
                2500,
                new RemoveTarget(20),
                new TormentingMark()
        );
    }

    public SoulOfGradient(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new RemoveTarget(20),
                new TormentingMark()
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.SOUL_OF_GRADIENT;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
        FireWorkEffectPlayer.playFirework(warlordsNPC.getLocation(), FireworkEffect.builder()
                                                                                   .withColor(Color.WHITE)
                                                                                   .with(FireworkEffect.Type.BALL_LARGE)
                                                                                   .build());
    }

    private static class TormentingMark extends AbstractPveAbility implements Damages<TormentingMark.DamageValues> {

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
                    we.addInstance(InstanceBuilder
                            .damage()
                            .ability(this)
                            .source(wp)
                            .value(damageValues.markDamage)
                    );
                }
            }
            return true;
        }

        private final DamageValues damageValues = new DamageValues();

        public DamageValues getDamageValues() {
            return damageValues;
        }

        public static class DamageValues implements Value.ValueHolder {

            private final Value.SetValue markDamage = new Value.SetValue(1000);
            private final List<Value> values = List.of(markDamage);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }
    }
}
