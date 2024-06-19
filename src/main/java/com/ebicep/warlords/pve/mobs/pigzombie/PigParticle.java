package com.ebicep.warlords.pve.mobs.pigzombie;

import com.ebicep.warlords.abilities.PrismGuard;
import com.ebicep.warlords.abilities.internal.AbstractAbility;
import com.ebicep.warlords.abilities.internal.Heals;
import com.ebicep.warlords.abilities.internal.Value;
import com.ebicep.warlords.effects.EffectUtils;
import com.ebicep.warlords.game.option.pve.PveOption;
import com.ebicep.warlords.player.ingame.WarlordsEntity;
import com.ebicep.warlords.player.ingame.instances.InstanceBuilder;
import com.ebicep.warlords.pve.mobs.AbstractMob;
import com.ebicep.warlords.pve.mobs.Mob;
import com.ebicep.warlords.pve.mobs.tiers.ChampionMob;
import com.ebicep.warlords.util.warlords.PlayerFilter;
import org.bukkit.Location;
import org.bukkit.Particle;

import javax.annotation.Nonnull;
import java.util.List;

public class PigParticle extends AbstractMob implements ChampionMob {

    public PigParticle(Location spawnLocation) {
        super(
                spawnLocation,
                "Pig Particle",
                8000,
                0.2f,
                10,
                450,
                600,
                new VoidHealing(), new PrismGuard(20)
        );
    }

    public PigParticle(
            Location spawnLocation,
            String name,
            int maxHealth,
            float walkSpeed,
            float damageResistance,
            float minMeleeDamage,
            float maxMeleeDamage
    ) {
        super(
                spawnLocation,
                name,
                maxHealth,
                walkSpeed,
                damageResistance,
                minMeleeDamage,
                maxMeleeDamage,
                new VoidHealing(), new PrismGuard(20)
        );
    }

    @Override
    public Mob getMobRegistry() {
        return Mob.PIG_ALLEVIATOR;
    }

    @Override
    public void onSpawn(PveOption option) {
        super.onSpawn(option);
    }

    @Override
    public void whileAlive(int ticksElapsed, PveOption option) {
        if (ticksElapsed % 30 == 0) {
            EffectUtils.playCylinderAnimation(warlordsNPC.getLocation(), 6, Particle.CLOUD, 1);
        }
    }

    private static class VoidHealing extends AbstractAbility implements Heals<VoidHealing.HealingValues> {

        public VoidHealing() {
            super("Void Healing", 200, 200, .5f, 100);
        }

        @Override
        public boolean onActivate(@Nonnull WarlordsEntity wp) {
            for (WarlordsEntity we : PlayerFilter
                    .entitiesAround(wp, 6, 6, 6)
                    .aliveTeammatesOfExcludingSelf(wp)
            ) {
                we.addInstance(InstanceBuilder
                        .healing()
                        .ability(this)
                        .source(wp)
                        .value(healingValues.voidHealing)
                );
            }
            return true;
        }

        private final HealingValues healingValues = new HealingValues();

        @Override
        public HealingValues getHealValues() {
            return healingValues;
        }

        public static class HealingValues implements Value.ValueHolder {

            private final Value.SetValue voidHealing = new Value.SetValue(200);
            private final List<Value> values = List.of(voidHealing);

            @Override
            public List<Value> getValues() {
                return values;
            }

        }

    }
}
